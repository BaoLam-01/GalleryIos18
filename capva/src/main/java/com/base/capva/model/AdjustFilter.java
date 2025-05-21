package com.base.capva.model;

import com.filter.helper.MagicFilterType;

public class AdjustFilter {
    public static final float DEFAULT_BRIGHTNESS = 0;
    public static final float DEFAULT_CONTRAST = 1;
    public static final float DEFAULT_EXPOSURE = 0;
    public static final float DEFAULT_BRILLIANCE = 1;
    public static final float DEFAULT_HIGHLIGHT = 1;
    public static final float DEFAULT_SHADOW = 0;
    public static final float DEFAULT_SATURATION = 1;
    public static final float DEFAULT_SHARPNESS = 0;
    public static final float DEFAULT_TINT = 0;
    public static final float DEFAULT_BLACK_POINT = 1;
    public static final float DEFAULT_VIBRANCE = 0;

    private MagicFilterType type = MagicFilterType.NONE;
    /**
     * 0 - 1 default 1
     */
    private float intensity = 1;

    /**
     * -0.3 - 0.3 default 0
     */
    private float brightness = DEFAULT_BRIGHTNESS;


    /**
     * 0.5 - 1.5 default 1
     */
    private float contrast = DEFAULT_CONTRAST;

    /**
     * -0.5 - 0.5 default 0
     */
    private float exposure = DEFAULT_EXPOSURE;

    /**
     * 0.5 - 1.5 default 1
     */
    private float brilliance = DEFAULT_BRILLIANCE;

    /**
     * 0 - 1 default 1
     */
    private float highlight = DEFAULT_HIGHLIGHT;

    /**
     * 0 - 1 default 0
     */
    private float shadow = DEFAULT_SHADOW;

    /**
     * 0 - 2 default 1
     */
    private float saturation = DEFAULT_SATURATION;

    /**
     * -0.5 - 0.5 default 0
     */
    private float sharpness = DEFAULT_SHARPNESS;


    /**
     * -1 - 1 default 0
     */
    private float tint = DEFAULT_TINT;

    /**
     * 0 - 2 default 1
     */
    private float blackPoint = DEFAULT_BLACK_POINT;

    /**
     * -1 - 1 default 0
     */
    private float vibrance = DEFAULT_VIBRANCE;

    public float getVibrance() {
        return vibrance;
    }

    public void setVibrance(float vibrance) {
        this.vibrance = vibrance;
    }

    public float getBlackPoint() {
        return blackPoint;
    }

    public void setBlackPoint(float blackPoint) {
        this.blackPoint = blackPoint;
    }

    public float getTint() {
        return tint;
    }

    public void setTint(float tint) {
        this.tint = tint;
    }

    public float getSharpness() {
        return sharpness;
    }

    public void setSharpness(float sharpness) {
        this.sharpness = sharpness;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getShadow() {
        return shadow;
    }

    public void setShadow(float shadow) {
        this.shadow = shadow;
    }

    public float getHighlight() {
        return highlight;
    }

    public void setHighlight(float highlight) {
        this.highlight = highlight;
    }

    public MagicFilterType getType() {
        return type;
    }

    public void setType(MagicFilterType type) {
        this.type = type;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public float getBrilliance() {
        return brilliance;
    }

    public void setBrilliance(float brilliance) {
        this.brilliance = brilliance;
    }

    @Override
    public String toString() {
        return "AdjustFilter{" +
                "type=" + type +
                ", intensity=" + intensity +
                ", brightness=" + brightness +
                ", contrast=" + contrast +
                ", exposure=" + exposure +
                ", brilliance=" + brilliance +
                ", highlight=" + highlight +
                ", shadow=" + shadow +
                ", saturation=" + saturation +
                ", sharpness=" + sharpness +
                ", tint=" + tint +
                ", blackPoint=" + blackPoint +
                ", vibrance=" + vibrance +
                '}';
    }
}
