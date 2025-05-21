package com.base.capva.model;

public class TextGroup {
    private String text;
    private float lineLeft;
    private float lineBaseline;
    private float lineTop;
    private float lineBot;
    private float ascent;
    private float descent;
    private int line;
    private float gap;
    private boolean isMultiLeveList;

//    public TextGroup(String text, float lineLeft, float lineBaseline, int line) {
//        this.text = text;
//        this.lineLeft = lineLeft;
//        this.lineBaseline = lineBaseline;
//        this.line = line;
//    }


//    public TextGroup(String text, float lineLeft, float lineBaseline, float lineTop, float lineBot, int line) {
//        this.text = text;
//        this.lineLeft = lineLeft;
//        this.lineBaseline = lineBaseline;
//        this.lineTop = lineTop;
//        this.lineBot = lineBot;
//        this.line = line;
//    }

    public TextGroup(String text, float lineLeft, float gap, float lineBaseline, float lineTop, float lineBot, float ascent, float descent, int line, boolean isMultiLeveList) {
        this.text = text;
        this.lineLeft = lineLeft;
        this.gap = gap;
        this.lineBaseline = lineBaseline;
        this.lineTop = lineTop;
        this.lineBot = lineBot;
        this.ascent = ascent;
        this.descent = descent;
        this.line = line;
        this.isMultiLeveList = isMultiLeveList;
    }

    public boolean isMultiLeveList() {
        return isMultiLeveList;
    }

    public void setMultiLeveList(boolean multiLeveList) {
        isMultiLeveList = multiLeveList;
    }

    public float getGap() {
        return gap;
    }

    public void setGap(float gap) {
        this.gap = gap;
    }

    public float getAscent() {
        return ascent;
    }

    public void setAscent(float ascent) {
        this.ascent = ascent;
    }

    public float getDescent() {
        return descent;
    }

    public void setDescent(float descent) {
        this.descent = descent;
    }

    public float getLineTop() {
        return lineTop;
    }

    public void setLineTop(float lineTop) {
        this.lineTop = lineTop;
    }

    public float getLineBot() {
        return lineBot;
    }

    public void setLineBot(float lineBot) {
        this.lineBot = lineBot;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getLineLeft() {
        return lineLeft;
    }

    public void setLineLeft(float lineLeft) {
        this.lineLeft = lineLeft;
    }

    public float getLineBaseline() {
        return lineBaseline;
    }

    public void setLineBaseline(float lineBaseline) {
        this.lineBaseline = lineBaseline;
    }

    @Override
    public String toString() {
        return "TextGroup{" +
                "text='" + text + '\'' +
                ", lineLeft=" + lineLeft +
                ", lineBaseline=" + lineBaseline +
                ", lineTop=" + lineTop +
                ", lineBot=" + lineBot +
                ", ascent=" + ascent +
                ", descent=" + descent +
                ", line=" + line +
                '}';
    }
}
