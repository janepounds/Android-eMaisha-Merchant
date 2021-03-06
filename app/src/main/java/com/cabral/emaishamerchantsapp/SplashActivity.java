package com.cabral.emaishamerchantsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.auth.Login;

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
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, splashTimeOut);
    }

//    public class MyTask extends  AsyncTask<String, Void, String>{
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//
//
//        @Override
//        protected String doInBackground(String... strings) {
//            // Check for Internet Connection from the static method of Helper class
//            if (haveNetworkConnection() || ) {
//
//
//
//
//
//                return "1";
//            } else {
//
//                return "0";
//            }
//
//        }
//    }
//
//    private boolean haveNetworkConnection() {
//        boolean haveConnectedWifi = false;
//        boolean haveConnectedMobile = false;
//
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//                if (ni.isConnected())
//                    haveConnectedWifi = true;
//            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//                if (ni.isConnected())
//                    haveConnectedMobile = true;
//        }
//        return haveConnectedWifi || haveConnectedMobile;
//    }

}

