package com.filter.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return displaymetrics.heightPixels;
    }

    public static String saveMusicToCache(Context context, String path) {
        File file;
        File outFile = null;
        try {
            InputStream in;
            OutputStream out;
            String[] nameMusic = path.split(File.separator);
            String root = context.getCacheDir() + File.separator + "" + "music_moart";
            File myDir = new File(root);
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            String songMojo = nameMusic[nameMusic.length - 1];
            file = new File(root);
            if (!file.exists()) {
                file.mkdirs();
            }

            in = context.getAssets().open(path);
            outFile = new File(file, songMojo);
            if (outFile.exists()) {
                return outFile.getAbsolutePath();
            }
            out = new FileOutputStream(outFile);
            out.flush();
            copyFile(in, out);
            out.close();
        } catch (Exception ignored) {
        }
        if (outFile != null) {
            return outFile.getAbsolutePath();
        } else {
            return "";
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
