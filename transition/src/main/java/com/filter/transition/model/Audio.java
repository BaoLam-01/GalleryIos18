package com.filter.transition.model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(tableName = "audioTable")
public class Audio {
    @SerializedName("id_category")
    @Expose
    private int idCategory;

    @SerializedName("id_audio")
    @PrimaryKey(autoGenerate = true)
    @Expose
    private int id;

    private boolean downloaded = false;

    @SerializedName("audio_name")
    @Expose
    private String name = "";

    @SerializedName("category_name")
    @Expose
    private String nameCategory = "";

    @SerializedName("audio_link")
    @Expose
    private String path = "";

    private long duration = 0L;

    @SerializedName("audio_time")
    @Expose
    private String convertDuration = "";

    @SerializedName("timeDelay")
    @Expose
    private int timeDelay = 0;

    @SerializedName("timeStart")
    @Expose
    private int timeStart = 0;

    @SerializedName("isAudio")
    @Expose
    private boolean isAudio = false;

    @SerializedName("isFavorite")
    @Expose
    private boolean isFavorite = false;

    transient private boolean isPlaying = false;
    transient private float volume = 1f;
    transient private float fadeIn = 0f;
    transient private float fadeOut = 0f;
    transient private float speed = 1f;

    public Audio() {
    }

    public Audio(String path) {
        this.path = path;
    }

    public Audio(String pathAudio, long duration) {
        this.path = pathAudio;
        this.duration = duration;
    }

    public Audio(String nameAudio, String pathAudio, long toLong) {
        this.name = nameAudio;
        this.path = pathAudio;
        this.duration = toLong;
    }

    public Audio(
            boolean downloaded,
            String name,
            String path,
            Long duration,
            String convertDuration,
            int timeDelay,
            boolean isAudio,
            boolean isFavorite
    ) {
        this.downloaded = downloaded;
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.convertDuration = convertDuration;
        this.timeDelay = timeDelay;
        this.isAudio = isAudio;
        this.isFavorite = isFavorite;
    }

    public Audio(String path, int timeDelay, int timeStart, int durationAudio, float volume, float fadeIn, float fadeOut, float speed, boolean isAudio) {
        this.path = path;
        this.timeDelay = timeDelay;
        this.timeStart = timeStart;
        this.duration = durationAudio;
        this.volume = volume;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.speed = speed;
        this.isAudio = isAudio;
    }

    public Audio(String name, String path, long duration, String convertDuration) {
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.convertDuration = convertDuration;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getConvertDuration() {
        return convertDuration;
    }

    public void setConvertDuration(String convertDuration) {
        this.convertDuration = convertDuration;
    }

    public int getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public void setAudio(boolean audio) {
        isAudio = audio;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getFadeIn() {
        return fadeIn;
    }

    public void setFadeIn(float fadeIn) {
        this.fadeIn = fadeIn;
    }

    public float getFadeOut() {
        return fadeOut;
    }

    public void setFadeOut(float fadeOut) {
        this.fadeOut = fadeOut;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audio audio = (Audio) o;
        return Objects.equals(path, audio.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "Audio{" +
                "idCategory=" + idCategory +
                ", id=" + id +
                ", downloaded=" + downloaded +
                ", name='" + name + '\'' +
                ", nameCategory='" + nameCategory + '\'' +
                ", path='" + path + '\'' +
                ", duration=" + duration +
                ", convertDuration='" + convertDuration + '\'' +
                ", timeDelay=" + timeDelay +
                ", isAudio=" + isAudio +
                ", isFavorite=" + isFavorite +
                ", isPlaying=" + isPlaying +
                '}';
    }
}