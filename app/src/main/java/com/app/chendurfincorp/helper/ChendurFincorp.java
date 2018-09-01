package com.app.chendurfincorp.helper;

import android.app.Application;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;

public class ChendurFincorp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        InternetAvailabilityChecker.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        InternetAvailabilityChecker.getInstance().removeAllInternetConnectivityChangeListeners();
    }
}
