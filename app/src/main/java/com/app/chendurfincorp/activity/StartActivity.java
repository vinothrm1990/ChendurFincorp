package com.app.chendurfincorp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.chendurfincorp.R;
import com.app.chendurfincorp.helper.ChendurFincorp;
import com.app.chendurfincorp.helper.Constants;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import spencerstudios.com.bungeelib.Bungee;

public class StartActivity extends AppCompatActivity implements InternetConnectivityListener{

    RelativeLayout startLayout;
    InternetAvailabilityChecker internetAvailabilityChecker;
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);

        startLayout = findViewById(R.id.start_layout);
        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("CF",MODE_PRIVATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        internetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (!isConnected) {
            Snackbar snack = Snackbar.make(startLayout, "Check your Internet Connection", Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.RED);
            snack.show();
        } else if (isConnected){
            Snackbar snack = Snackbar.make(startLayout, "Connected to the Internet", Snackbar.LENGTH_SHORT);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.GREEN);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Constants.pref.getBoolean("isLogged", false)) {
                        startActivity(new Intent(StartActivity.this, HomeActivity.class));
                        Bungee.shrink(StartActivity.this);
                        finish();
                    }else {
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                        Bungee.fade(StartActivity.this);
                        finish();
                    }


                }
            }, SPLASH_TIME_OUT);
        }
    }
}
