package com.filter.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LoadShaderManager {

    private static LoadShaderManager instance;
    private final static Map<String, String> fontMap = new HashMap<>();

    public static LoadShaderManager getInstance() {
        if (instance == null) {
            instance = new LoadShaderManager();
        }
        return instance;
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private String loadShaderFromAssets(Context context, String file) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream ims = assetManager.open(file);

            String re = convertStreamToString(ims);
            ims.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean addShader(String key, Context context) {
        if (fontMap.get(key) == null) {
            try {
                fontMap.put(key, loadShaderFromAssets(context, key));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private LoadShaderManager() {
    }

    public String getShader(String key) {
        return fontMap.get(key);
    }
}
