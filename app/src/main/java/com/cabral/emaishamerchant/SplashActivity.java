package com.cabral.emaishamerchant;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import com.cabral.emaishamerchant.auth.Registration;
import com.cabral.emaishamerchant.network.NetworkStateChecker;

public class SplashActivity extends AppCompatActivity {


    public static int splashTimeOut = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Registration.class);
                startActivity(intent);
                finish();
            }
        }, splashTimeOut);
    }
}

