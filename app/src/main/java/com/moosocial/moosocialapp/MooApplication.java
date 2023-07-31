package com.moosocial.moosocialapp;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.moosocial.moosocialapp.util.MooGlobals;


import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MooApplication extends Application{
    //private static MooApplication mInstance;
    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        MooGlobals.getInstance().init(this);
    }
}
