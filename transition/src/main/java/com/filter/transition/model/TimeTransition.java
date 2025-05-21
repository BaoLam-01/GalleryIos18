package com.filter.transition.model;

public class TimeTransition {
    private int begin;
    private int start;
    private int startVideo;
    private int end;

    public TimeTransition(int begin, int start,int end,int startVideo) {
        this.begin = begin;
        this.start = start;
        this.end = end;
        this.startVideo = startVideo;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStartVideo() {
        return startVideo;
    }

    public void setStartVideo(int startVideo) {
        this.startVideo = startVideo;
    }


    @Override
    public String toString() {
        return "TimeTransition{" +
                "begin=" + begin +
                ", start=" + start +
                ", startVideo=" + startVideo +
                ", end=" + end +
                '}';
    }
}
