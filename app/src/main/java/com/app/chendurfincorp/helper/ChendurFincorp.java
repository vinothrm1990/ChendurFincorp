package com.app.chendurfincorp.helper;

import android.app.Application;
import android.content.Context;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(mailTo = "shadowwsvinothkumar@gmail.com")
public class ChendurFincorp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        InternetAvailabilityChecker.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        InternetAvailabilityChecker.getInstance().removeAllInternetConnectivityChangeListeners();
    }
}
