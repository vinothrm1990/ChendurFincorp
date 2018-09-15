package com.app.chendurfincorp.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.andrognito.flashbar.Flashbar;
import com.app.chendurfincorp.R;
import com.app.chendurfincorp.helper.Constants;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import org.jetbrains.annotations.NotNull;
import spencerstudios.com.bungeelib.Bungee;

public class StartActivity extends AppCompatActivity implements InternetConnectivityListener {

    private static int SPLASH_TIME_OUT = 2000;
    InternetAvailabilityChecker internetAvailabilityChecker;
    Flashbar flashbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("CF", MODE_PRIVATE);

        flashbar = networkStatus();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        internetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    private Flashbar networkStatus() {
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .titleSizeInSp(18)
                .messageSizeInSp(14)
                .title("Network Status:")
                .message("Check your Internet Connection")
                .titleColorRes(R.color.red)
                .messageColorRes(R.color.red)
                .backgroundColorRes(R.color.translucent_black)
                .showOverlay()
                .titleTypeface(Typeface.createFromAsset(getAssets(),"fonts/lato_bold.ttf"))
                .messageTypeface(Typeface.createFromAsset(getAssets(),"fonts/lato_regular.ttf"))
                .primaryActionTextTypeface(Typeface.createFromAsset(getAssets(),"fonts/lato_bold.ttf"))
                .primaryActionText("Goto")
                .primaryActionTextColorRes(R.color.black)
                .primaryActionTextSizeInSp(10)
                .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                    @Override
                    public void onActionTapped(@NotNull Flashbar bar) {
                        bar.dismiss();
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .build();
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (!isConnected) {
            flashbar.show();
        } else if (isConnected){
            flashbar.dismiss();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (Constants.pref.getBoolean("isLogged", false)) {
                        startActivity(new Intent(StartActivity.this, HomeActivity.class));
                        Bungee.shrink(StartActivity.this);
                        finish();
                    } else {
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                        Bungee.fade(StartActivity.this);
                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);

        }
    }
}
