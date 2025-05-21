package com.filter.decode.media;

/**
 * Created by Mario on 14.06.2014.
 *
 * A time base in microseconds for media playback.
 */
class TimeBase {

    private long mStartTime;
    private double mSpeed = 1.0;

    public TimeBase() {
        start();
    }

    public void start() {
        startAt(0);
    }

    public void startAt(long mediaTime) {
        mStartTime = microTime() - mediaTime;
    }

    public long getCurrentTime() {
        return microTime() - mStartTime;
    }

    public long getOffsetFrom(long from) {
        return  from - getCurrentTime();
    }

    public double getSpeed() {
        return mSpeed;
    }

    /**
     * Sets the playback speed. Can be used for fast forward and slow motion.
     * speed 0.5 = half speed / slow motion
     * speed 2.0 = double speed / fast forward
     * @param speed
     */
    public void setSpeed(double speed) {
        mSpeed = speed;
    }

    private long microTime() {
        return (long)(System.nanoTime() / 1000 * mSpeed);
    }
}
