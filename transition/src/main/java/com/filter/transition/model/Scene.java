package com.filter.transition.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Scene {
    private long id = 0;
    private transient Bitmap previewBitmap;
    @SerializedName("duration")
    @Expose
    private int duration;

    @SerializedName("path")
    @Expose
    private String path = "";
    @SerializedName("currentBg")
    @Expose
    private String currentBg = "#000000";

    @SerializedName("AnimationEffect")
    @Expose
    private AnimationType animationEffect;

    @SerializedName("AnimationFilter")
    @Expose
    private AnimationType animationFilter;

    @SerializedName("AnimationTransition")
    @Expose
    private AnimationType animationTransition;

    @SerializedName("isImage")
    @Expose
    private boolean isImage;

    @SerializedName("default")
    @Expose
    private boolean defaultType;

    @SerializedName("fit")
    @Expose
    private boolean fit = false;

    private boolean isMute = true;

    private int timeStart;
    private int timeMinusEnd;

    private transient List<String> listPathVideo;

    private String name = "";

    private transient Matrix matrix = new Matrix();

    private transient ArrayList<Bitmap> frameReviewVideo = new ArrayList<>();

    private transient Boolean canRead = true;

    public Scene() {
    }

    public ArrayList<Bitmap> getFrameReviewVideo() {
        if (frameReviewVideo == null) {
            frameReviewVideo = new ArrayList<>();
        }
        return frameReviewVideo;
    }

    public void setFrameReviewVideo(ArrayList<Bitmap> frameReviewVideo) {
        this.frameReviewVideo = frameReviewVideo;
    }

    public void setUpFrameReviewVideo() {
        if (listPathVideo != null && !listPathVideo.isEmpty()) {
            if (frameReviewVideo == null) {
                frameReviewVideo = new ArrayList<>();
            }
            if (frameReviewVideo.isEmpty()) {
                float size = duration / 1000f;
                int oneFrame = (int) (listPathVideo.size() / size);
                for (int i = 0; i <= size; i++) {
                    int count = oneFrame * i;
                    if (count < listPathVideo.size()) {
                        frameReviewVideo.add(BitmapFactory.decodeFile(listPathVideo.get(count), null));
                    }
                }
            }
        }
    }


    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Bitmap getPreviewBitmap() {
        return previewBitmap;
    }

    public void setPreviewBitmap(Bitmap previewBitmap) {
        this.previewBitmap = previewBitmap;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRealDuration() {
        return duration - timeStart - timeMinusEnd;
    }

    public String getCurrentBg() {
        return currentBg;
    }

    public void setCurrentBg(String currentBg) {
        this.currentBg = currentBg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AnimationType getAnimationEffect() {
        return animationEffect;
    }

    public void setAnimationEffect(AnimationType animationEffect) {
        this.animationEffect = animationEffect;
    }

    public AnimationType getAnimationFilter() {
        return animationFilter;
    }

    public void setAnimationFilter(AnimationType animationFilter) {
        this.animationFilter = animationFilter;
    }

    public AnimationType getAnimationTransition() {
        return animationTransition;
    }

    public void setAnimationTransition(AnimationType animationTransition) {
        this.animationTransition = animationTransition;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getListPathVideo() {
        return listPathVideo;
    }

    public String getName() {
        return name;
    }

    public void resetListPathVideo() {
        listPathVideo = null;
    }

    public boolean isDefaultType() {
        return defaultType;
    }

    public void setDefaultType(boolean defaultType) {
        this.defaultType = defaultType;
    }

    public void setListPathVideo(File file, String name) {
        this.name = name;
        this.listPathVideo = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String nameFile;

                if (i < 9) {
                    nameFile = name + "_000" + (i + 1) + ".jpeg";
                } else if (i < 99) {
                    nameFile = name + "_00" + (i + 1) + ".jpeg";
                } else if (i < 999) {
                    nameFile = name + "_0" + (i + 1) + ".jpeg";
                } else {
                    nameFile = name + "_" + (i + 1) + ".jpeg";
                }
                this.listPathVideo.add(file.getAbsolutePath() + "/" + nameFile);
            }
        }
    }

    public boolean isFit() {
        return fit;
    }

    public void setFit(boolean fit) {
        this.fit = fit;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public int getTimeMinusEnd() {
        return timeMinusEnd;
    }


    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeMinusEnd(int timeMinusEnd) {
        this.timeMinusEnd = timeMinusEnd;
    }

    public void setListPathVideo(List<String> listPathVideo) {
        this.listPathVideo = listPathVideo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int calVisibleDuration() {
        int durationTransition = 0;
        if (animationTransition != null) {
            durationTransition = animationTransition.getTimeTransition();
        }
        return duration - durationTransition - timeStart - timeMinusEnd;
    }

    public Scene copy() {
        Scene scene = new Scene();
        scene.setPath(path);
        scene.setFit(fit);
        scene.setAnimationTransition(animationTransition);
        scene.setAnimationEffect(animationEffect);
        scene.setAnimationFilter(animationFilter);
        scene.setId(id);
        scene.setImage(isImage);
        scene.setCurrentBg(currentBg);
        scene.setDuration(duration);
        scene.setFrameReviewVideo(frameReviewVideo);
        scene.setDefaultType(defaultType);
        scene.setListPathVideo(listPathVideo);
        scene.setMatrix(matrix);
        scene.setPreviewBitmap(previewBitmap);
        scene.setTimeStart(timeStart);
        scene.setTimeMinusEnd(timeMinusEnd);
        scene.setName(name);
        return scene;
    }

    public Scene copyBitmap() {
        Scene scene = new Scene();
        scene.setId(id);
        scene.setPreviewBitmap(previewBitmap);
        return scene;
    }

    public Scene copyListPathVideo() {
        Scene scene = new Scene();
        scene.setId(id);
        scene.setPath(path);
        scene.setMute(isMute);
        scene.setListPathVideo(listPathVideo);
        return scene;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }
}
