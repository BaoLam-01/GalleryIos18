package com.example.galleryios18.utils;

import android.content.Context;

import com.arthenica.ffmpegkit.FFmpegKit;

import java.io.File;
import java.util.Calendar;

import timber.log.Timber;

public class ConvertAudioLocal {

    private File audioConvert;
    private OnConvertAudioListener onConvertAudioListener;

    public ConvertAudioLocal(OnConvertAudioListener onConvertAudioListener) {
        this.onConvertAudioListener = onConvertAudioListener;
    }

    public void convertAudio(Context context, String path, long start, long durationCut, int durationAudio) {
        Timber.e("HaiPd: convertAudio: " + path + " " + start + " " + durationCut + " " + durationAudio);
        durationCut += 100;
        File folder = new File(context.getCacheDir(), "Audio");
        if (!folder.exists()) {
            folder.mkdirs();
        } else {
            File[] files = folder.listFiles();
            for (File file : files) {
                file.delete();
            }
        }

        audioConvert = new File(folder, Calendar.getInstance().getTimeInMillis() + ".m4a");
        Timber.e("HaiPd: convertAudio: " + audioConvert.getAbsolutePath());
        String ss = convertTime(start);
        String t;
        long timeDuration = 0;
        if (start + durationCut < durationAudio) {
            timeDuration = durationCut;

        } else {
            timeDuration = durationAudio - start;
        }
        if (timeDuration < 0)
            timeDuration = 8;
        t = convertTime(timeDuration);
//        String timeTrade = convertTime(start+3*timeDuration/4);
//        String timeTradeEnd = convertTime(timeDuration/4);
        String pathStringFinal = "'".concat(path).concat("'");
//        String fade = "afade=t=out:st="+timeTrade+":d="+timeTradeEnd;

        String[] cmd = new String[]{"-y", "-i", pathStringFinal, "-ss", ss, "-t", t, "-preset", "ultrafast", "-b:a", "128k", "-maxrate", "128k", "-c:v", "copy", audioConvert.getAbsolutePath()};
        for (String s : cmd) {
            Timber.e("HaiPd: convertAudio: " + s);
        }
        FFmpegKit.executeAsync(MethodUtils.argumentsToString(cmd), (session) -> {
            Timber.e("HaiPd: convertAudio: " + session.getReturnCode());
            if (session.getReturnCode().isValueSuccess()) {
                if (onConvertAudioListener != null) {
                    onConvertAudioListener.onConvertSuccess(audioConvert.getAbsolutePath());
                }
            } else if (session.getReturnCode().isValueCancel()) {

            } else {
                if (onConvertAudioListener != null) {
                    onConvertAudioListener.onConvertFailed(audioConvert.getAbsolutePath());
                }
            }
        });
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
        void onConvertSuccess(String path);

        void onConvertFailed(String path);
    }
}
