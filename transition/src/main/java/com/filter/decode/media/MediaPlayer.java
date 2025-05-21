package com.filter.decode.media;

import android.content.Context;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

public class MediaPlayer {

    private static final String TAG = MediaPlayer.class.getSimpleName();

    private static final long BUFFER_LOW_WATER_MARK_US = 2000000; // 2 seconds; NOTE: make sure this is below DashMediaExtractor's mMinBufferTimeUs

    /**
     * Pass as track index to tell the player that no track should be selected.
     */
    public static final int TRACK_INDEX_NONE = -1;
    /**
     * Pass as track index to tell the player to automatically select the first fitting track.
     */
    public static final int TRACK_INDEX_AUTO = -2;

    public void setTimeStart(int timeStart) {
        if (timeStartTrim != 0) {
            this.timeStart = timeStartTrim + timeStart;
        } else {
            this.timeStart = timeStart;
        }
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getTimeEnd() {
        if (timeEnd == -1) {
            return -1;
        }
        return timeEnd + timeDelay + timeStart;
    }

    public int getDuration() {
        return timeEnd + timeStart;
    }

    public long getIdMedia() {
        return idMedia;
    }

    public void setIdMedia(long idMedia) {
        this.idMedia = idMedia;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    public int getTimeDelay() {
        return timeDelay;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean isMute) {
        this.isMute = isMute;
    }

    public void setTimeStartTrim(int timeStartTrim) {
        this.timeStartTrim = timeStartTrim;
    }

    public int getTimeStartTrim() {
        return this.timeStartTrim;
    }

    public boolean isExistAudio() {
        return isExistAudio;
    }

    public void setExistAudio(boolean existAudio) {
        isExistAudio = existAudio;
    }

    public enum SeekMode {
        /**
         * Seeks to the previous sync point.
         * This mode exists for backwards compatibility and is the same as {@link #FAST_TO_PREVIOUS_SYNC}.
         */
        @Deprecated
        FAST(MediaExtractor.SEEK_TO_PREVIOUS_SYNC),

        /**
         * Seeks to the previous sync point.
         * This seek mode equals Android MediaExtractor's {@link android.media.MediaExtractor#SEEK_TO_PREVIOUS_SYNC}.
         */
        FAST_TO_PREVIOUS_SYNC(MediaExtractor.SEEK_TO_PREVIOUS_SYNC),

        /**
         * Seeks to the next sync point.
         * This seek mode equals Android MediaExtractor's {@link android.media.MediaExtractor#SEEK_TO_NEXT_SYNC}.
         */
        FAST_TO_NEXT_SYNC(MediaExtractor.SEEK_TO_NEXT_SYNC),

        /**
         * Seeks to to the closest sync point.
         * This seek mode equals Android MediaExtractor's {@link android.media.MediaExtractor#SEEK_TO_CLOSEST_SYNC}.
         */
        FAST_TO_CLOSEST_SYNC(MediaExtractor.SEEK_TO_CLOSEST_SYNC),

        /**
         * Seeks to the exact frame if the seek time equals the frame time, else
         * to the following frame; this means that it will often seek one frame too far.
         */
        PRECISE(MediaExtractor.SEEK_TO_PREVIOUS_SYNC),

        /**
         * Default mode.
         * Always seeks to the exact frame. Can cost maximally twice the time than the PRECISE mode.
         */
        EXACT(MediaExtractor.SEEK_TO_PREVIOUS_SYNC),

        /**
         * Always seeks to the exact frame by skipping the decoding of all frames between the sync
         * and target frame, because of which it can result in block artifacts.
         */
        FAST_EXACT(MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

        private final int baseSeekMode;

        SeekMode(int baseSeekMode) {
            this.baseSeekMode = baseSeekMode;
        }

        public int getBaseSeekMode() {
            return baseSeekMode;
        }
    }

    private enum State {
        IDLE,
        INITIALIZED,
        PREPARING,
        PREPARED,
        STOPPED,
        RELEASING,
        RELEASED,
        ERROR
    }

    private final SeekMode mSeekMode = SeekMode.EXACT;
    private MediaExtractor mVideoExtractor;
    private MediaExtractor mAudioExtractor;


    private int mAudioTrackIndex;
    private int mAudioSessionId;
    private float mVolumeLeft = 1, mVolumeRight = 1;

    private PlaybackThread mPlaybackThread;
    private long mCurrentPosition;
    private long mSeekTargetTime;
    private boolean mSeeking;
    private final TimeBase mTimeBase;

    private boolean mScreenOnWhilePlaying;
    private AudioPlayback mAudioPlayback;
    private Decoders mDecoders;
    private boolean mBuffering;
    private final Timeline mCueTimeline;
    private int timeStart = 0;
    private int timeEnd = 0;
    private int timeDelay = 0;
    private long idMedia;

    private State mCurrentState;
    private boolean isMute = true;
    private boolean isExistAudio = true;
    private int timeStartTrim = 0;
    private boolean isMuteAll = false;
    /**
     * A lock to sync release() with the actual releasing on the playback thread. This lock makes
     * sure that release() waits until everything has been released before returning to the caller,
     * and thus makes the async release look synchronized to an API caller.
     */
    private Object mReleaseSyncLock;

    public void checkVolume() {
        setVolume(1, 1);
        if (isMute) {
            setVolume(0, 0);
        }
        if (isMuteAll) {
            setVolume(0, 0);
        }
    }

    public MediaPlayer() {
        mPlaybackThread = null;
        mTimeBase = new TimeBase();
        mCurrentState = State.IDLE;
        mAudioSessionId = 0;
        mCueTimeline = new Timeline();
        setVolume(0);
    }

    /**
     * Sets the media source and track indices. The track indices can either be actual track indices
     * that have been determined externally, {@link #TRACK_INDEX_AUTO} to automatically select
     * the first fitting track index, or {@link #TRACK_INDEX_NONE} to not select any track.
     *
     * @param source          the media source
     * @param audioTrackIndex an audio track index or one of the TRACK_INDEX_* constants
     */
    public void setDataSource(MediaSource source, int audioTrackIndex)
            throws IOException, IllegalStateException {
        if (mCurrentState != State.IDLE) {
            throw new IllegalStateException();
        }

        releaseMediaExtractors();

        mVideoExtractor = source.getVideoExtractor();
        mAudioExtractor = source.getAudioExtractor();

        if (mVideoExtractor != null && mAudioExtractor == null) {
            mAudioExtractor = mVideoExtractor;
        }

        switch (audioTrackIndex) {
            case TRACK_INDEX_AUTO:
                mAudioTrackIndex = getTrackIndex(mAudioExtractor, "audio/");
                break;
            case TRACK_INDEX_NONE:
                mAudioTrackIndex = MediaCodecDecoder.INDEX_NONE;
                break;
            default:
                mAudioTrackIndex = audioTrackIndex;
        }

        // Select audio track
        if (mAudioTrackIndex != MediaCodecDecoder.INDEX_NONE) {
            mAudioExtractor.selectTrack(mAudioTrackIndex);
            MediaFormat mAudioFormat = mAudioExtractor.getTrackFormat(mAudioTrackIndex);
            Log.d(TAG, "selected audio track #" + mAudioTrackIndex + " " + mAudioFormat.toString());
        }

        mVideoExtractor = null;

        if (mAudioTrackIndex == MediaCodecDecoder.INDEX_NONE) {
            throw new IOException("invalid data source, no supported stream found");
        }

        mCurrentState = State.INITIALIZED;
    }

    private void releaseMediaExtractors() {
        // Audio and video extractors could be the same object,
        // but calling release twice does not hurt.
        if (mAudioExtractor != null) {
            mAudioExtractor.release();
            mAudioExtractor = null;
        }

        if (mVideoExtractor != null) {
            mVideoExtractor.release();
            mVideoExtractor = null;
        }
    }

    /**
     * Sets the media source and automatically selects fitting tracks.
     *
     * @param source the media source
     */
    public void setDataSource(MediaSource source) throws IOException, IllegalStateException {
        setDataSource(source, TRACK_INDEX_AUTO);
    }

    private int getTrackIndex(MediaExtractor mediaExtractor, String mimeType) {
        if (mediaExtractor == null) {
            return MediaCodecDecoder.INDEX_NONE;
        }

        for (int i = 0; i < mediaExtractor.getTrackCount(); ++i) {
            MediaFormat format = mediaExtractor.getTrackFormat(i);
            Log.d(TAG, format.toString());
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime != null && mime.startsWith(mimeType)) {
                return i;
            }
        }

        return MediaCodecDecoder.INDEX_NONE;
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(Context, Uri, Map)
     * @deprecated only for compatibility with Android API
     */
    @Deprecated
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException {
        setDataSource(new UriSource(context, uri, headers));
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(Context, Uri)
     * @deprecated only for compatibility with Android API
     */
    @Deprecated
    public void setDataSource(Context context, Uri uri) throws IOException {
        setDataSource(context, uri, null);
    }

    private void prepareInternal() throws IOException, IllegalStateException {
        mCueTimeline.reset();

        MediaCodecDecoder.OnDecoderEventListener decoderEventListener = decoder -> {
            if (mPlaybackThread != null && !mPlaybackThread.isPaused()
                    && !mBuffering
                    && mDecoders.getCachedDuration() < BUFFER_LOW_WATER_MARK_US
                    && !mDecoders.hasCacheReachedEndOfStream()) {
                mBuffering = true;
            }
        };

        if (mCurrentState == State.RELEASING) {
            return;
        }

        mDecoders = new Decoders();

//        if (mVideoTrackIndex != MediaCodecDecoder.INDEX_NONE) {
//            try {
//                MediaCodecDecoder vd = new MediaCodecVideoDecoder(mVideoExtractor, false, mVideoTrackIndex,
//                        decoderEventListener, mSurface, mVideoRenderTimingMode.isRenderModeApi21());
//                mDecoders.addDecoder(vd);
//            } catch (Exception e) {
//                Log.e(TAG, "cannot create video decoder: " + e.getMessage());
//            }
//        }

        if (mAudioTrackIndex != MediaCodecDecoder.INDEX_NONE) {
            mAudioPlayback = new AudioPlayback();
            mAudioPlayback.setAudioSessionId(mAudioSessionId);
            setVolume(mVolumeLeft, mVolumeRight); // sets the volume on mAudioPlayback

            try {
                boolean passive = (mAudioExtractor == mVideoExtractor || mAudioExtractor == null);
                MediaCodecDecoder ad = new MediaCodecAudioDecoder(mAudioExtractor != null ? mAudioExtractor : mVideoExtractor,
                        passive, mAudioTrackIndex, decoderEventListener, mAudioPlayback);
                mDecoders.addDecoder(ad);
                mDecoders.setTimeEnd(timeEnd);
                mDecoders.setTimeStart(timeStart);
            } catch (Exception e) {
                Log.e(TAG, "cannot create audio decoder: " + e.getMessage());
                mAudioPlayback = null;
            }
        }

        if (mDecoders.getDecoders() == null) {
            throw new IOException("cannot decode any stream");
        }

        if (mAudioPlayback != null) {
            mAudioSessionId = mAudioPlayback.getAudioSessionId();
        }

        if (mCurrentState == State.RELEASING) {
            return;
        }

        mDecoders.decodeFrame(false);
        if (mAudioPlayback != null) mAudioPlayback.pause(true);
        mDecoders.seekTo(SeekMode.FAST_TO_PREVIOUS_SYNC, 0);
    }

    public void setTimeToDecoder() {
        if (mDecoders != null) {
            mDecoders.setTimeEnd(timeEnd);
            mDecoders.setTimeStart(timeStart);
        }
    }

    /**
     * @see android.media.MediaPlayer#prepareAsync()
     */
    public void prepareAsync() throws IllegalStateException {
        if (mCurrentState != State.INITIALIZED && mCurrentState != State.STOPPED) {
            throw new IllegalStateException();
        }

        mCurrentState = State.PREPARING;

        // Create the playback loop handler thread
        mPlaybackThread = new PlaybackThread();
        mPlaybackThread.start();

        // Execute prepare asynchronously on playback thread
        mPlaybackThread.prepare();
    }


    public void start() {
        if (timeEnd == -1) {
            return;
        }
        if (mCurrentState != State.PREPARED) {
            mCurrentState = State.ERROR;
            return;
        }
        checkVolume();
        mPlaybackThread.play();
    }

    public void pause() {
        if (timeEnd == -1) {
            return;
        }
        if (mCurrentState != State.PREPARED) {
            mCurrentState = State.ERROR;
            return;
        }

        mPlaybackThread.pause();
    }

    public void seekTo(long usec) {
        if (mCurrentState.ordinal() < State.PREPARED.ordinal()) {
            mCurrentState.ordinal();
            State.RELEASING.ordinal();
        }

        mSeeking = true;
        mSeekTargetTime = usec;
        if (mPlaybackThread != null) {
            mPlaybackThread.seekTo(mSeekTargetTime);
        }
    }

    public void seekTo(int msec) {
        if (timeEnd == -1) {
            return;
        }
        seekTo(msec * 1000L);
    }

    public void seekToStart() {
        if (timeEnd == -1) {
            return;
        }
        seekTo(timeStart * 1000L);
    }

    public boolean isMuteAll() {
        return isMuteAll;
    }

    public void setMuteAll(boolean muteAll) {
        isMuteAll = muteAll;
    }

    public boolean isPlaying() {
        if (mCurrentState.ordinal() >= State.RELEASING.ordinal()) {
            mCurrentState = State.ERROR;
            throw new IllegalStateException();
        }

        return mPlaybackThread != null && !mPlaybackThread.isPaused();
    }

    /**
     * Stops the player and releases the playback thread. The player will consume minimal resources
     * after calling this method. To continue playback, the player must first be prepared with
     */
    public void stop() {
        if (mPlaybackThread != null) {
            // Create a new lock object for this release cycle
            mReleaseSyncLock = new Object();

            synchronized (mReleaseSyncLock) {
                try {
                    // Schedule release on the playback thread
                    boolean awaitingRelease = mPlaybackThread.release();
                    mPlaybackThread = null;

                    // Wait for the release on the playback thread to finish
                    if (awaitingRelease) {
                        mReleaseSyncLock.wait();
                    }
                } catch (InterruptedException e) {
                    // nothing to do here
                }
            }

            mReleaseSyncLock = null;
        }

        mAudioPlayback = null;

        mCurrentState = State.STOPPED;
    }

    /**
     * Resets the player to its initial state, similar to a freshly created instance. To reuse the
     * player instance, set a data source and call  {@link #prepareAsync()}.
     */
    public void reset() {
        stop();
        mCurrentState = State.IDLE;
    }

    /**
     * Stops the player and releases all resources (e.g. memory, codecs, event listeners). Once
     * the player instance is released, it cannot be used any longer.
     * Call this method as soon as you're finished using the player instance, and latest when
     * destroying the activity or fragment that contains this player. Not releasing the player can
     * lead to memory leaks.
     */
    public void release() {
        if (mCurrentState == State.RELEASING || mCurrentState == State.RELEASED) {
            return;
        }

        mCurrentState = State.RELEASING;
        stop();
        releaseMediaExtractors();
        mCurrentState = State.RELEASED;


    }

    /**
     * @see android.media.MediaPlayer#setScreenOnWhilePlaying(boolean)
     */
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            if (screenOn) {
                Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            mScreenOnWhilePlaying = screenOn;
        }
    }


    public int getCurrentPosition() {
        if (mCurrentState.ordinal() >= State.RELEASING.ordinal()) {
            mCurrentState = State.ERROR;
            throw new IllegalStateException();
        }
        return (int) ((mSeeking ? mSeekTargetTime : mCurrentPosition) / 1000);
    }

    /**
     * @see android.media.MediaPlayer#setVolume(float, float)
     */
    public void setVolume(float leftVolume, float rightVolume) {
        mVolumeLeft = leftVolume;
        mVolumeRight = rightVolume;

        if (mAudioPlayback != null) {
            mAudioPlayback.setStereoVolume(leftVolume, rightVolume);
        }
    }

    /**
     * This API method in the Android MediaPlayer is hidden, but may be unhidden in the future. Here
     * it can already be used.
     * see android.media.MediaPlayer#setVolume(float)
     */
    public void setVolume(float volume) {
        isMute = volume == 0;
        setVolume(volume, volume);
    }

    private class PlaybackThread extends HandlerThread implements Handler.Callback {

        private static final int PLAYBACK_PREPARE = 1;
        private static final int PLAYBACK_PLAY = 2;
        private static final int PLAYBACK_PAUSE = 3;
        private static final int PLAYBACK_LOOP = 4;
        private static final int PLAYBACK_SEEK = 5;
        private static final int PLAYBACK_RELEASE = 6;
        private static final int PLAYBACK_PAUSE_AUDIO = 7;

        static final int DECODER_SET_SURFACE = 100;

        private Handler mHandler;
        private boolean mPaused;
        private boolean mReleasing;
        private MediaCodecDecoder.FrameInfo mVideoFrameInfo;
        private double mPlaybackSpeed;
        private long mLastCueEventTime;

        public PlaybackThread() {
            super(TAG + "#" + PlaybackThread.class.getSimpleName(), Process.THREAD_PRIORITY_AUDIO);
            mPaused = true;
            mReleasing = false;
            mLastCueEventTime = 0;
        }

        @Override
        public synchronized void start() {
            super.start();

            // Create the handler that will process the messages on the handler thread
            mHandler = new Handler(this.getLooper(), this);

            Log.d(TAG, "PlaybackThread started");
        }

        public void prepare() {
            mHandler.sendEmptyMessage(PLAYBACK_PREPARE);
        }

        public void play() {
            mPaused = false;
            mHandler.sendEmptyMessage(PLAYBACK_PLAY);
        }

        public void pause() {
            mPaused = true;
            mHandler.sendEmptyMessage(PLAYBACK_PAUSE);
        }

        public void setPlaybackSpeed(float speed) {
            if (speed < 0) {
                throw new IllegalArgumentException("speed cannot be negative");
            }

            mTimeBase.setSpeed(speed);
            mTimeBase.startAt(mCurrentPosition);
        }


        public boolean isPaused() {
            return mPaused;
        }

        public void seekTo(long usec) {
            // When multiple seek requests come in, e.g. when a user slides the finger on a
            // seek bar in the UI, we don't want to process all of them and can therefore remove
            // all requests from the queue and only keep the most recent one.
            mHandler.removeMessages(PLAYBACK_SEEK); // remove any previous requests
            mHandler.obtainMessage(PLAYBACK_SEEK, usec).sendToTarget();
        }

        private boolean release() {
            if (!isAlive()) {
                return false;
            }

            mPaused = true;
            mReleasing = true;
            mHandler.sendEmptyMessage(PLAYBACK_RELEASE);

            return true;
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            try {
                if (mReleasing) {
                    releaseInternal();
                    return true;
                }
                switch (msg.what) {
                    case PLAYBACK_PREPARE:
                        prepareInternal();
                        return true;
                    case PLAYBACK_PLAY:
                        playInternal();
                        return true;
                    case PLAYBACK_PAUSE:
                        pauseInternal();
                        return true;
                    case PLAYBACK_PAUSE_AUDIO:
                        pauseInternalAudio();
                        return true;
                    case PLAYBACK_LOOP:
                        loopInternal();
                        return true;
                    case PLAYBACK_SEEK:
                        seekInternal((Long) msg.obj);
                        return true;
                    case PLAYBACK_RELEASE:
                        releaseInternal();
                        return true;
                    case DECODER_SET_SURFACE:
                        return true;
                    default:
                        Log.d(TAG, "unknown/invalid message");
                        return false;
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "decoder interrupted", e);
            } catch (IllegalStateException e) {
                Log.e(TAG, "decoder error, too many instances?", e);
            } catch (IOException e) {
                Log.e(TAG, "decoder error, codec can not be created", e);
            }

            // Release after an exception
            releaseInternal();
            return true;
        }

        private void prepareInternal() {
            try {
                MediaPlayer.this.prepareInternal();
                mCurrentState = MediaPlayer.State.PREPARED;

                // This event is only triggered after a successful async prepare (not after the sync prepare!)
            } catch (IOException e) {
                Log.e(TAG, "prepareAsync() failed: cannot decode stream(s)", e);
                releaseInternal();
            } catch (IllegalStateException e) {
                Log.e(TAG, "prepareAsync() failed: something is in a wrong state", e);
                releaseInternal();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "prepareAsync() failed: surface might be gone", e);
                releaseInternal();
            }
        }

        private void playInternal() throws IOException, InterruptedException {
            if (mDecoders.isEOS()) {
                mCurrentPosition = 0;
                mDecoders.seekTo(SeekMode.FAST_TO_PREVIOUS_SYNC, 0);
                mCueTimeline.setPlaybackPosition(0);
            }

            // reset time (otherwise playback tries to "catch up" time after a pause)
            mTimeBase.startAt(mDecoders.getCurrentDecodingPTS());

            if (mAudioPlayback != null) {
                mHandler.removeMessages(PLAYBACK_PAUSE_AUDIO);
                mAudioPlayback.play();
            }

            mPlaybackSpeed = mTimeBase.getSpeed();
            // Sync audio playback speed to playback speed (to account for speed changes during pause)
            if (mAudioPlayback != null) {
                mAudioPlayback.setPlaybackSpeed((float) mPlaybackSpeed);
            }

            mHandler.removeMessages(PLAYBACK_LOOP);
            loopInternal();
        }

        private void pauseInternal(boolean drainAudioPlayback) {
            mHandler.removeMessages(PLAYBACK_LOOP);
            if (mAudioPlayback != null) {
                if (drainAudioPlayback) {
                    mHandler.sendEmptyMessageDelayed(PLAYBACK_PAUSE_AUDIO,
                            (mAudioPlayback.getQueueBufferTimeUs() + mAudioPlayback.getPlaybackBufferTimeUs()) / 1000 + 1);
                } else {
                    mAudioPlayback.pause(false);
                }
            }
        }

        private void pauseInternal() {
            pauseInternal(false);
        }

        private void pauseInternalAudio() {
            if (mAudioPlayback != null) {
                mAudioPlayback.pause();
            }
        }

        private void loopInternal() throws IOException, InterruptedException {

            long cachedDuration = mDecoders.getCachedDuration();
            if (mBuffering && cachedDuration > -1 && cachedDuration < BUFFER_LOW_WATER_MARK_US && !mDecoders.hasCacheReachedEndOfStream()) {
                mHandler.sendEmptyMessageDelayed(PLAYBACK_LOOP, 100);
                return;
            }

            long startTime = SystemClock.elapsedRealtime();
            if (mBuffering) {
                mBuffering = false;
                mTimeBase.startAt(mDecoders.getCurrentDecodingPTS());
            }

            if (mVideoFrameInfo != null && mTimeBase.getOffsetFrom(mVideoFrameInfo.presentationTimeUs) > 60000) {
                mHandler.sendEmptyMessageDelayed(PLAYBACK_LOOP, 50);
                return;
            }

            // Update the current position of the player
            mCurrentPosition = mDecoders.getCurrentDecodingPTS();

            // fire cue events
            // Rate limited to 10 Hz (every 100ms)
            if (mCueTimeline.count() > 0 && startTime - mLastCueEventTime > 100) {
                mLastCueEventTime = startTime;
            }

            if (mAudioPlayback != null) {
                if (mPlaybackSpeed != mTimeBase.getSpeed()) {
                    mPlaybackSpeed = mTimeBase.getSpeed();
                    mAudioPlayback.setPlaybackSpeed((float) mPlaybackSpeed);
                }

                // Sync timebase to audio timebase when there is audio data available
                long currentAudioPTS = mAudioPlayback.getCurrentPresentationTimeUs();
                if (currentAudioPTS > AudioPlayback.PTS_NOT_SET) {
                    mTimeBase.startAt(currentAudioPTS);
                }
            }

            // Handle EOS
            if (mDecoders.isEOS()) {
                mPaused = true;
                pauseInternal(true); // pause but play remaining buffered audio
            } else {
                // Get next frame
                mVideoFrameInfo = mDecoders.decodeFrame(false);
            }

            if (!mPaused) {
                // Static delay time until the next call of the playback loop
                long delay = 10;
                // Scale delay by playback speed to avoid limiting framerate
                delay = (long) (delay / mTimeBase.getSpeed());
                // Calculate the duration taken for the current call
                long duration = (SystemClock.elapsedRealtime() - startTime);
                // Adjust the delay by the time taken
                delay = delay - duration;
                if (delay > 0) {
                    // Sleep for some time and then continue processing the loop
                    // This replaces the very unreliable and jittery Thread.sleep in the old decoder thread
                    mHandler.sendEmptyMessageDelayed(PLAYBACK_LOOP, delay);
                } else {
                    // The current call took too much time; there is no time left for delaying, call instantly
                    mHandler.sendEmptyMessage(PLAYBACK_LOOP);
                }
            }
        }

        private void seekInternal(long usec) throws IOException, InterruptedException {

            // Clear the audio cache
            if (mAudioPlayback != null) mAudioPlayback.pause(true);

            // Seek to the target time
            mDecoders.seekTo(mSeekMode, usec);

            // Reset time to keep frame rate constant
            // (otherwise it's too fast on back seeks and waits for the PTS time on fw seeks)
            mTimeBase.startAt(mDecoders.getCurrentDecodingPTS());

            // Check if another seek has been issued in the meantime
            boolean newSeekWaiting = mHandler.hasMessages(PLAYBACK_SEEK);

            // Render seek target frame (if no new seek is waiting to be processed)
            if (newSeekWaiting) {
                mDecoders.dismissFrames();
            } else {
                mDecoders.renderFrames();
            }

            // When there are no more seek requests in the queue, notify of finished seek operation
            if (!newSeekWaiting) {
                // Set the final seek position as the current position
                // (the final seek position may be off the initial target seek position)
                mCurrentPosition = mDecoders.getCurrentDecodingPTS();
                mSeeking = false;

                if (!mPaused) {
                    playInternal();
                }

                mCueTimeline.setPlaybackPosition((int) (mCurrentPosition / 1000));
            }
        }

        private void releaseInternal() {
            interrupt();

            quit();


            if (mDecoders != null) {
                mDecoders.release();
            }
            if (mAudioPlayback != null) mAudioPlayback.stopAndRelease();

            MediaPlayer.this.releaseMediaExtractors();

            Log.d(TAG, "PlaybackThread destroyed");

            // Notify #release() that it can now continue because #releaseInternal is finished
            if (mReleaseSyncLock != null) {
                synchronized (mReleaseSyncLock) {
                    mReleaseSyncLock.notify();
                    mReleaseSyncLock = null;
                }
            }
        }
    }
}
