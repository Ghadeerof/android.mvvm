package com.app.androidtest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.app.androidtest.view.activity.MainActivity;
import com.app.androidtest.view.interfaces.progressBarOperations;

import java.util.HashMap;
import java.util.Locale;


public class AppSession extends Application {
    public static String imageURL;
    private static Context context;
    private static progressBarOperations ProgressBarOperations;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AppSession.imageURL = getSharedPreferences("userdata", MODE_PRIVATE).getString("imageURL","");

    }

    public static void setProgressBarOperations(progressBarOperations operations) {
        AppSession.ProgressBarOperations = operations;
    }

    public static void show() {
        AppSession.ProgressBarOperations.show();
    }

    public static void hide() {
        AppSession.ProgressBarOperations.hide();
    }


}
