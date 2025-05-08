package com.example.galleryios18.manager;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsManager {

    private static AnalyticsManager instance;
    private static FirebaseAnalytics firebaseAnalytics;

    private AnalyticsManager() {
    }

    public static void init(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        instance = new AnalyticsManager();
    }

    public static AnalyticsManager getInstance() {
        if (instance == null) {
            instance = new AnalyticsManager();
        }
        return instance;
    }

    public void logEvent(String s, Bundle bundle) {
        firebaseAnalytics.logEvent(s, bundle);
    }
}
