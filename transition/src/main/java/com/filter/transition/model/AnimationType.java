package com.filter.transition.model;


import com.filter.helper.InterpolatorType;
import com.filter.helper.MagicFilterType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnimationType {
    @SerializedName("effect")
    @Expose
    private MagicFilterType effect = MagicFilterType.NONE;

    @SerializedName("timeDelay")
    @Expose
    private int timeDelay;

    @SerializedName("timeAnimation")
    @Expose
    private int timeAnimation;

    @SerializedName("from")
    @Expose
    private float from;

    @SerializedName("to")
    @Expose
    private float to;

    @SerializedName("timeTransition")
    @Expose
    private int timeTransition = 500;
    @SerializedName("valueFilter")
    @Expose
    private float valueFilter = 0.5f;
    @SerializedName("interpolatorType")
    @Expose
    private InterpolatorType interpolatorType = InterpolatorType.NONE;

    private long id = 0L;
    private String name = "";

    public AnimationType() {

    }

    public AnimationType(MagicFilterType effect, int timeDelay, int timeTransition, float from, float to) {
        this.effect = effect;
        this.timeDelay = timeDelay;
        this.timeTransition = timeTransition;
        this.from = from;
        this.to = to;
    }

    public AnimationType(MagicFilterType effect) {
        this.effect = effect;
    }

    public MagicFilterType getEffect() {
        return effect;
    }

    public void setEffect(MagicFilterType effect) {
        this.effect = effect;
    }

    public int getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    public float getFrom() {
        return from;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public int getTimeAnimation() {
        return timeAnimation;
    }

    public void setTimeAnimation(int timeAnimation) {
        this.timeAnimation = timeAnimation;
    }

    public int getTimeTransition() {
        return timeTransition;
    }

    public void setTimeTransition(int timeTransition) {
        this.timeTransition = timeTransition;
    }

    public float getValueFilter() {
        return valueFilter;
    }

    public void setValueFilter(float valueFilter) {
        this.valueFilter = valueFilter;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public InterpolatorType getInterpolatorType() {
        return interpolatorType;
    }

    public void setInterpolatorType(InterpolatorType interpolatorType) {
        this.interpolatorType = interpolatorType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
