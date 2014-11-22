package com.elfadili.wheresmyplaces.constants;

import android.app.Application;
import android.content.res.Configuration;

import com.elfadili.wheresmyplaces.MainActivity;
import com.elfadili.wheresmyplaces.WelcomeActivity;
import com.parse.Parse;
import com.parse.PushService;

public class GlobalState extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, IMarocPlaceExplorerConstants.PUSH_APP_ID, IMarocPlaceExplorerConstants.PUSH_CLIENT_KEY);
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}