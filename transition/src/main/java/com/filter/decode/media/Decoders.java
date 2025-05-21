package com.filter.decode.media;

import android.util.Log;

import java.io.IOException;

/**
 * Created by Mario on 13.09.2015.
 */
class Decoders {

    private static final String TAG = Decoders.class.getSimpleName();

    private MediaCodecDecoder mDecoders;
    private int timeEnd = 0;
    private int timeStart = 0;

    public Decoders() {
        mDecoders = null;
    }

    public void addDecoder(MediaCodecDecoder decoder) {
        mDecoders = decoder;
    }

    public MediaCodecDecoder getDecoders() {
        return mDecoders;
    }

    public MediaCodecDecoder.FrameInfo decodeFrame(boolean force) throws IOException {
        //Log.d(TAG, "decodeFrame");
        boolean outputEos = false;

        if (mDecoders.getCurrentTime() >= timeEnd + timeStart) {
            return null;
        }

        while (!outputEos) {
            int outputEosCount = 0;
            MediaCodecDecoder.FrameInfo fi;

            while ((fi = mDecoders.dequeueDecodedFrame()) != null) {
                mDecoders.renderFrame(fi, 0);
            }

            while (mDecoders.queueSampleToCodec(false)) {
            }

            if (mDecoders.isOutputEos()) {
                outputEosCount++;
            }

            if (!force) {
                // If we have not decoded a video frame and we're not forcing decoding until a frame
                // becomes available, return null.
                return null;
            }

            outputEos = (outputEosCount == 1);
        }

        Log.d(TAG, "EOS NULL");
        return null; // EOS already reached, no video frame left to return
    }

    /**
     * Releases all decoders. This must be called to free decoder resources when this object is no longer in use.
     */
    public void release() {

        try {
            mDecoders.release();
        } catch (Exception e) {
            Log.e(TAG, "release failed", e);
        }
    }

    public void seekTo(MediaPlayer.SeekMode seekMode, long seekTargetTimeUs) throws IOException {
        mDecoders.seekTo(seekMode, seekTargetTimeUs);
    }

    public void renderFrames() {
        mDecoders.renderFrame();
    }

    public void dismissFrames() {
        mDecoders.dismissFrame();
    }

    public long getCurrentDecodingPTS() {
        long minPTS = Long.MAX_VALUE;
        long pts = mDecoders.getCurrentDecodingPTS();
        if (pts != MediaCodecDecoder.PTS_NONE && minPTS > pts) {
            minPTS = pts;
        }
        return minPTS;
    }

    public boolean isEOS() {
        int eosCount = 0;
        if (mDecoders.isOutputEos()) {
            eosCount++;
        }
        return eosCount == 1;
    }

    public long getCachedDuration() {
        long minCachedDuration;
        minCachedDuration = mDecoders.getCachedDuration();

        if (minCachedDuration == Long.MAX_VALUE) {

            return -1;
        }

        return minCachedDuration;
    }

    /**
     * Returns true only if all decoders have reached the end of stream.
     */
    public boolean hasCacheReachedEndOfStream() {
        if (!mDecoders.hasCacheReachedEndOfStream()) {
            return false;
        }
        return true;
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public int getCurrentTime() {
        return mDecoders.getCurrentTime();
    }
}
