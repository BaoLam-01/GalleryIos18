package com.filter.utils;

import android.content.Context;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.filter.transition.model.Audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateAudioManager {

    private final OnConvertAudioListener onConvertAudioListener;
    private int countConvert = 0;
    private List<Audio> audioTemplateList;
    private final List<Audio> audioTemplateListConvert = new ArrayList<>();

    public CreateAudioManager(OnConvertAudioListener onConvertAudioListener, Context context) {
        this.onConvertAudioListener = onConvertAudioListener;
    }

    public void convertListAudio(Context context, List<Audio> listAudio, boolean isCustom) {
        this.audioTemplateList = listAudio;
        File folder = new File(context.getCacheDir(), "Audio");
        if (!folder.exists()) {
            folder.mkdirs();
        } else {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
        if (audioTemplateList != null && !audioTemplateList.isEmpty()) {
            for (int i = 0; i < audioTemplateList.size(); i++) {
                Audio audioTemplate = audioTemplateList.get(i);
                convertAudio(folder, audioTemplate, context, i + 1, isCustom);
            }
        }
    }

    //volume = 0.0f ~ 1.0f
    private void convertAudio(File folder, Audio audioTemplate, Context context, int pos, boolean isCustom) {
//        durationCut += 100;

        File audioConvert;
        if (audioTemplate.isAudio()) {
            audioConvert = new File(folder, pos + ".m4a");
        } else {
            audioConvert = new File(folder, pos + ".aac");
        }
        String path;
        if (isCustom) {
            if (audioTemplate.getPath().contains("audio/") && !audioTemplate.getPath().contains("/audio/")) {
                path = Utils.saveMusicToCache(context, audioTemplate.getPath());
            } else {
                path = audioTemplate.getPath();
            }
        } else {
            path = Utils.saveMusicToCache(context, audioTemplate.getPath());
        }
        long start = audioTemplate.getTimeStart();
        long durationCut = audioTemplate.getDuration();
        float volume = audioTemplate.getVolume();
        float fadeIn = audioTemplate.getFadeIn();
        float fadeOut = audioTemplate.getFadeOut();
        float speed = audioTemplate.getSpeed();
        float startFadeOut = durationCut / 1000f - fadeOut;

        String ss = convertTime(start);
        String t;
        t = convertTime(durationCut);
        String pathStringFinal = "'".concat(path).concat("'");
        Audio audio1 = new Audio(audioConvert.getAbsolutePath());
        audio1.setTimeDelay(audioTemplate.getTimeDelay());
        audioTemplateListConvert.add(audio1);
        String[] cmd;
        if (speed == 1f && fadeIn == 0f && fadeOut == 0f) {
            cmd = new String[]{"-y", "-i", pathStringFinal, "-af \"volume = " + volume + "\"", "-ss", ss, "-t", t, "-preset", "ultrafast", "-b:a", "128k", "-maxrate", "128k", "-c:v", "copy", audioConvert.getAbsolutePath()};
        } else {
            cmd = new String[]{"-y", "-i", pathStringFinal, "-af \"volume = " + volume + "\",\"atempo = " + speed + "\",\"afade = t = in:ss = 0.0:d = " + fadeIn + "\",\"afade = t = out:st = " + startFadeOut + ":d = " + fadeOut + "\"", "-ss", ss, "-t", t, "-preset", "ultrafast", "-b:a", "128k", "-maxrate", "128k", "-c:v", "copy", audioConvert.getAbsolutePath()};
        }
        FFmpegKit.executeAsync(argumentsToString(cmd), session -> {
            if (session.getReturnCode().isValueSuccess()) {
                countConvert++;

                if (audioTemplateList != null && !audioTemplateList.isEmpty() && audioTemplateList.size() == countConvert) {
                    if (onConvertAudioListener != null) {
                        onConvertAudioListener.onConvertSuccess(audioTemplateListConvert);
                    }
                }
            } else {
                if (onConvertAudioListener != null) {
                    onConvertAudioListener.onConvertFailed(audioConvert.getAbsolutePath());
                }
            }
        });
    }

    public String argumentsToString(final String[] arguments) {
        if (arguments == null) {
            return "null";
        }


        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(arguments[i]);
        }

        return stringBuilder.toString();
    }

    private String convertTime(long time) {
        int seconds = (int) (time / 1000);

        int se = (int) (time % 1000);

        String ss;
        if (se < 100) {
            ss = seconds + ".0" + se;
        } else {
            ss = seconds + "." + se;
        }
        return ss;
    }

    public interface OnConvertAudioListener {
        void onConvertSuccess(List<Audio> listAudio);

        void onConvertFailed(String path);
    }
}
