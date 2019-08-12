package com.interstellarstudios.weathershare;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(Home.class)
                .withSplashTimeOut(0)
                .withBackgroundColor(Color.parseColor("#FFFFFF"))
                .withLogo(R.drawable.launcher_logo);

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}
