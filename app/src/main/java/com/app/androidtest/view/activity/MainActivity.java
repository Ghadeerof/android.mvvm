package com.app.androidtest.view.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;


import com.app.androidtest.AppSession;
import com.app.androidtest.R;
import com.app.androidtest.view.interfaces.progressBarOperations;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements progressBarOperations {

    private ProgressBar mProgressDialog;
    private RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        NavController navController = navHost.getNavController();
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.first_nav);
        navController.setGraph(graph);
        mProgressDialog = findViewById(R.id.mProgressDialog);
        progress = findViewById(R.id.progress);

        AppSession.setProgressBarOperations(this);

        AppSession.hide();


    }

    private void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void showDialog() {

        if (mProgressDialog != null && mProgressDialog.getVisibility() == View.GONE) {
            mProgressDialog.setVisibility(View.VISIBLE);
            progress.setBackgroundColor(getResources().getColor(R.color.black_overlay));
        }
    }

    public void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.getVisibility() == View.VISIBLE) {
            mProgressDialog.setVisibility(View.GONE);
            progress.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    public void show() {
        showDialog();
    }

    @Override
    public void hide() {
        hideDialog();
    }
}