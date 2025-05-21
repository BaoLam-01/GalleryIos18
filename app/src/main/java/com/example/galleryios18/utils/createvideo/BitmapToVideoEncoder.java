package com.example.galleryios18.utils.createvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.arthenica.ffmpegkit.FFmpegKit;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitmapToVideoEncoder {

    private StateMuxer mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
    private StateCodec mStateCodec = StateCodec.MUXER_STATE_UNINITIALIZED;
    private String pathAudio;

    private static final float[] TEXTURE_NO_ROTATION_NOR = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    private static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 30;
    private static final int IFRAME_INTERVAL = 10;
    private static final int BIT_RATE = 16000000;

    private int outputWidth;
    private int outputHeight;

    private MediaCodec encoder;
    private CodecInputSurface inputSurface;
    private MediaMuxer muxer;
    private int trackIndex;
    private boolean muxerStarted;

    private MediaCodec.BufferInfo bufferInfo;

    private FloatBuffer glCubeBuffer;
    private FloatBuffer glTextureBuffer;

    private ExecutorService executorService;

    private boolean withAudio = false;
    private boolean abort = false;
    private int dem = 0;
    private final Object frameSync = new Object();
    private boolean keepEncode = true;
    private boolean noMoreFrames = false;
    private CountDownLatch newFrameLatch;
    private Queue<Bitmap> encodeQueue = new ConcurrentLinkedQueue<>();
    private String outputPath;
    private OnBitmapToVideoEncoderListener listener;
    private File tempFileNoAudio;
    private final Context context;
    private RenderFrame renderFrame;
    private boolean endStream = false;

    public BitmapToVideoEncoder(Context context) {
        this.context = context;
    }

    public void setupEncoder(String outputPath, int outputWidth, int outputHeight,
                             OnBitmapToVideoEncoderListener listener) {
        renderFrame = new RenderFrame();
        this.listener = listener;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;

        this.outputPath = outputPath;

        glCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION_NOR.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glCubeBuffer.clear();
        glCubeBuffer.put(CUBE).position(0);
        glTextureBuffer.clear();
        glTextureBuffer.put(TEXTURE_NO_ROTATION_NOR).position(0);

    }

    public void setupAudio(String pathAudio) {
        this.withAudio = true;
        this.pathAudio = pathAudio;
    }

    public Queue<Bitmap> getEncodeQueue() {
        return encodeQueue;
    }

    public int getSizeQueue() {
        return encodeQueue.size();
    }

    private void mixAudioAndVideo() {
//use one of overloaded setDataSource() functions to set your data source
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(tempFileNoAudio.getAbsolutePath()));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long duration = Long.parseLong(time);
            String t = duration / (1000 * 1.0f) + "";
            retriever.release();
            String[] cmd =
                    {"-i", tempFileNoAudio.getAbsolutePath(), "-t", t, "-stream_loop", "-1", "-i", pathAudio, "-shortest", "-c:v", "copy", "-c:a", "aac", outputPath};
            FFmpegKit.executeAsync(String.join(" ", cmd), (session) -> {
                if (session.getReturnCode().isValueSuccess()) {
                    tempFileNoAudio.delete();
                    onSuccess();
                    File fileAudio = new File(pathAudio);
                    if (fileAudio.exists()) {
                        fileAudio.delete();
                    }

                }
            });
        } catch (Exception exception) {
            Log.e("HaiPd", "mixAudioAndVideo: " + exception.getMessage());
            onFail();
        }

    }


    private void onSuccess() {
        if (listener != null) {
            listener.onSuccess(outputPath);
        }
    }

    private void onFailure() {
        deleteOutputError();
        if (listener != null) {
            listener.onFailure();
        }
    }

    private void deleteOutputError() {
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public void startEncoding() {
        if (listener != null) {
            listener.onStartEncoding();
        }
        keepEncode = true;
        noMoreFrames = false;
        abort = false;
        getExecutorService().execute(this::encode);
    }

    public ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        return executorService;
    }

    private long currentTime = 0;

    public void encode() {
        try {
            prepareEncoder();
            inputSurface.makeCurrent();
            if (listener != null) {
                listener.onReadyDrawFrame();
            }
            while (keepEncode) {
                if (noMoreFrames && (encodeQueue.isEmpty())) {
                    break;
                }
                long time = computePresentationTimeNsec(dem);
                currentTime = time / 1000000;
                if (listener != null) {
                    listener.onProcess((int) currentTime);
                }
                Bitmap overlayBitmap = pollBitmapFromQueue();
                if (overlayBitmap == null) {
                    continue;
                }
                drainEncoder(false);
                generateSurfaceFrame(overlayBitmap);
                inputSurface.setPresentationTime(time);
                inputSurface.swapBuffers();
                dem++;
            }
            drainEncoder(true);
        } catch (IOException e) {
            Log.e("HaiPd", "encode: " + e.getMessage());
            e.printStackTrace();
        } finally {
            releaseEncoder();
        }
        executorService.shutdown();

        if (abort) {
            deleteOutputError();
        } else {
            if (withAudio) {
                mixAudioAndVideo();
            } else {
                if (keepEncode) {
                    onSuccess();
                } else {
                    onFailure();
                }
            }
        }
    }

    @Nullable
    private Bitmap pollBitmapFromQueue() {
        Bitmap bitmap = encodeQueue.poll();
        if (bitmap == null) {
            synchronized (frameSync) {
                newFrameLatch = new CountDownLatch(1);
            }

            try {
                newFrameLatch.await();
            } catch (InterruptedException e) {
                Log.e("HaiPd", "pollBitmapFromQueue: " + e.getMessage());
                e.printStackTrace();
                onFailure();
            }

            bitmap = encodeQueue.poll();
        }
        return bitmap;
    }

    public void queueFrame(Bitmap bitmap) {
        if (encoder == null || muxer == null) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            return;
        }
        encodeQueue.add(bitmap);
        synchronized (frameSync) {
            if ((newFrameLatch != null) && (newFrameLatch.getCount() > 0)) {
                newFrameLatch.countDown();
            }
        }
    }

    public void stopEncoding() {
        if (encoder == null || muxer == null) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            return;
        }
        noMoreFrames = true;
        synchronized (frameSync) {
            if ((newFrameLatch != null) && (newFrameLatch.getCount() > 0)) {
                newFrameLatch.countDown();
            }
        }
    }

    public void abortEncoding() {
        if (encoder == null || muxer == null) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            return;
        }
        noMoreFrames = true;
        abort = true;
        encodeQueue = new ConcurrentLinkedQueue<>(); // Drop all frames
        synchronized (frameSync) {
            if ((newFrameLatch != null) && (newFrameLatch.getCount() > 0)) {
                newFrameLatch.countDown();
            }
        }
    }

    private void generateSurfaceFrame(Bitmap overlayBitmap) {
        int texture = renderFrame.loadTexture(overlayBitmap);
        renderFrame.renderTexture(texture, outputWidth, outputHeight, glCubeBuffer, glTextureBuffer);
    }

    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
    @NonNull
    private MediaFormat getMediaFormat() {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, outputWidth, outputHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        return format;
    }

    private boolean onFail() {
        if (listener != null) {
            listener.onFailure();
            return true;
        }
        return false;
    }

    private void encoderStart(MediaFormat format) {
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        inputSurface = new CodecInputSurface(encoder.createInputSurface());
        encoder.start();
    }

    private void prepareEncoder() throws IOException {
        bufferInfo = new MediaCodec.BufferInfo();
        MediaFormat format = getMediaFormat();
        encoder = MediaCodec.createEncoderByType(MIME_TYPE);
        try {
            encoderStart(format);
        } catch (Exception e) {
            Log.e("HaiPd", "prepareEncoder: " + e.getMessage());
        }
        mStateCodec = StateCodec.MUXER_STATE_INITIALIZED;

        String outputFileString = "";
        if (withAudio) {
            tempFileNoAudio = new File(context.getCacheDir(), Calendar.getInstance().getTimeInMillis() + ".mp4");
            try {
                outputFileString = tempFileNoAudio.getCanonicalPath();
            } catch (IOException e) {
                Log.e("HaiPd", "prepareEncoder: " + e.getMessage());
                return;
            }
        } else {
            try {
                outputFileString = new File(outputPath).getCanonicalPath();
            } catch (IOException e) {
                Log.e("HaiPd", "prepareEncoder: " + outputFileString + " " + e.getMessage());
                return;
            }
        }
        try {
            muxer = new MediaMuxer(outputFileString, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mStateMuxer = StateMuxer.MUXER_STATE_INITIALIZED;
        } catch (IOException ioe) {
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }

        trackIndex = -1;
        muxerStarted = false;
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    private void releaseEncoder() {
        if (encoder != null && mStateCodec == StateCodec.MUXER_STATE_INITIALIZED) {
            try {
                encoder.stop();
                encoder.release();
                encoder = null;
            } catch (IllegalStateException ignored) {
                Log.e("HaiPd", "releaseEncoder: " + ignored.getMessage());
            }
            mStateCodec = StateCodec.MUXER_STATE_UNINITIALIZED;
        }
        if (inputSurface != null) {
            inputSurface.release();
            inputSurface = null;
        }
        if (muxer != null && muxerStarted && mStateMuxer == StateMuxer.MUXER_STATE_STARTED) {
            try {
                muxer.stop();
                muxer.release();
                muxer = null;
            } catch (IllegalStateException ignored) {
                Log.e("HaiPd", "releaseEncoder: " + ignored.getMessage());
            }
            mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
        }
    }

    /**
     * Extracts all pending data from the encoder.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (mStateCodec != StateCodec.MUXER_STATE_INITIALIZED) {
            return;
        }
        if (endOfStream) {
            if (!endStream) {
                try {
                    endStream = true;
                    encoder.signalEndOfInputStream();
                } catch (Exception ignored) {
                    Log.e("HaiPd", "drainEncoder: " + ignored.getMessage());
                }
            }
        }

        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        while (mStateCodec == StateCodec.MUXER_STATE_INITIALIZED) {
            int encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = encoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (muxerStarted) {
                    mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
                    break;
                }
                MediaFormat newFormat = encoder.getOutputFormat();

                // now that we have the Magic Goodies, start the muxer
                trackIndex = muxer.addTrack(newFormat);
                if (mStateMuxer == StateMuxer.MUXER_STATE_INITIALIZED) {
                    muxer.start();
                    muxerStarted = true;
                    mStateMuxer = StateMuxer.MUXER_STATE_STARTED;
                }
            } else if (encoderStatus < 0) {
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    bufferInfo.size = 0;
                }

                if (bufferInfo.size != 0) {
                    if (!muxerStarted) {
                        mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
                        if (listener != null) {
                            listener.onFailure();
                        }
                        break;
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(bufferInfo.offset);
                    encodedData.limit(bufferInfo.offset + bufferInfo.size);

                    try {
                        muxer.writeSampleData(trackIndex, encodedData, bufferInfo);
                        mStateMuxer = StateMuxer.MUXER_STATE_STARTED;
                    } catch (Exception ignored) {
                        Log.e("HaiPd", "drainEncoder: " + ignored.getMessage());
                        mStateMuxer = StateMuxer.MUXER_STATE_UNINITIALIZED;
                    }
                }

                encoder.releaseOutputBuffer(encoderStatus, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0 || endStream) {
                    break;      // out of while
                }
            }
        }
    }

    /**
     * Generates the presentation time for frame N, in nanoseconds.
     */
    private static long computePresentationTimeNsec(int frameIndex) {
        final long ONE_BILLION = 1000000000;
        return frameIndex * ONE_BILLION / FRAME_RATE;
    }

    /**
     * Holds state associated with a Surface used for MediaCodec encoder input.
     * <p>
     * The constructor takes a Surface obtained from MediaCodec.createInputSurface(), and uses that
     * to create an EGL window surface.  Calls to eglSwapBuffers() cause a frame of data to be sent
     * to the video encoder.
     * <p>
     * This object owns the Surface -- releasing this will release the Surface too.
     */
    public static class CodecInputSurface {
        private static final int EGL_RECORDABLE_ANDROID = 0x3142;

        private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
        private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

        private Surface mSurface;

        /**
         * Creates a CodecInputSurface from a Surface.
         */
        public CodecInputSurface(Surface surface) {
            if (surface == null) {
                throw new NullPointerException();
            }
            mSurface = surface;

            eglSetup();
        }

        /**
         * Prepares EGL.  We want a GLES 2.0 context and a surface that supports recording.
         */
        private void eglSetup() {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                throw new RuntimeException("unable to get EGL14 display");
            }
            int[] version = new int[2];
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                throw new RuntimeException("unable to initialize EGL14");
            }

            // Configure EGL for recording and OpenGL ES 2.0.
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL_RECORDABLE_ANDROID, 1,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                    numConfigs, 0);
            checkEglError("eglCreateContext RGB888+recordable ES2");

            // Configure context for OpenGL ES 2.0.
            int[] attrib_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
                    attrib_list, 0);
            checkEglError("eglCreateContext");

            // Create a window surface, and attach it to the Surface we received.
            int[] surfaceAttribs = {
                    EGL14.EGL_NONE
            };
            mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurface,
                    surfaceAttribs, 0);
            checkEglError("eglCreateWindowSurface");
        }

        /**
         * Discards all resources held by this class, notably the EGL context.  Also releases the
         * Surface that was passed to our constructor.
         */
        public void release() {
            if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
                EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(mEGLDisplay);
            }

            mSurface.release();

            mEGLDisplay = EGL14.EGL_NO_DISPLAY;
            mEGLContext = EGL14.EGL_NO_CONTEXT;
            mEGLSurface = EGL14.EGL_NO_SURFACE;

            mSurface = null;
        }

        /**
         * Makes our EGL context and surface current.
         */
        public void makeCurrent() {
            EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
            checkEglError("eglMakeCurrent");
        }

        /**
         * Calls eglSwapBuffers.  Use this to "publish" the current frame.
         */
        public boolean swapBuffers() {
            boolean result = EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
            checkEglError("eglSwapBuffers");
            return result;
        }

        /**
         * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
         */
        public void setPresentationTime(long nsecs) {
            EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nsecs);
            checkEglError("eglPresentationTimeANDROID");
        }

        /**
         * Checks for EGL errors.  Throws an exception if one is found.
         */
        private void checkEglError(String msg) {
            int error;
            if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
                throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
            }
        }
    }

    public interface OnBitmapToVideoEncoderListener {
        void onStartEncoding();

        void onReadyDrawFrame();

        void onProcess(int process);

        void onSuccess(String path);

        void onFailure();
    }
}
