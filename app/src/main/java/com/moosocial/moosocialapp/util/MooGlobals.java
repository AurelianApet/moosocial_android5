package com.moosocial.moosocialapp.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.domain.Me;
import com.moosocial.moosocialapp.domain.Token;
import com.moosocial.moosocialapp.domain.interactor.IdentifyingUser;
import com.moosocial.moosocialapp.presentation.model.NotificationModel;

import java.util.Locale;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 *
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MooGlobals {
    private static Token token;
    public static final String MOO_APP = "MooApplication";
    public static final String MOO_SHARED_GLOBAL = "MooApplicationGlobal";
    public static final String MOO_LANGUAGE = "MooLocation";
    public static final String MOO_SETTING_NOTIFICATION = "MooSettingNotification";
    private static Boolean isLooged = false;
    private static Boolean isWaitingRefeshToken = false;
    public static IdentifyingUser identifying;
    private static RequestQueue mRequestQueue;
    private static SharedPreferences sharedSettings;
    private static Gson gson;
    private static NotificationModel nNotification;
    private static UtilsConfig configApp;
    private static Me mMe;
    public static final String TAG = MooApplication.class
            .getSimpleName();

    public void init(MooApplication app)
    {
        // Configure Gson
        gson = new Gson();
        sharedSettings = app.getApplicationContext().getSharedPreferences(MOO_APP, 0);

        //mInstance = this;
        SharedPreferences sharedGlobalSettings = app.getApplicationContext().getSharedPreferences(MOO_SHARED_GLOBAL, 0);
        this.configApp = new UtilsConfig(app.getBaseContext());

        String sLanguage = sharedGlobalSettings.getString(MOO_LANGUAGE, this.configApp.defaultLanguage);
        Configuration config = app.getBaseContext().getResources().getConfiguration();

        if (! "".equals(sLanguage) && ! config.locale.getLanguage().equals(sLanguage))
        {
            Locale locale = new Locale(sLanguage);
            Locale.setDefault(locale);
            config.locale = locale;
            app.getBaseContext().getResources().updateConfiguration(config, app.getBaseContext().getResources().getDisplayMetrics());
        }

        identifying = new IdentifyingUser(app,null);
        //identifying.execute();

        mRequestQueue = Volley.newRequestQueue(app.getApplicationContext());
    }

    public IdentifyingUser getIdentifying(){
        return identifying;
    }

    public final boolean isLogged(){
        return isLooged;
    }
    public final boolean isWaitingRefeshToken(){
        return isWaitingRefeshToken;
    }
    public final void setWaitingRefeshToken(Boolean status){
        isWaitingRefeshToken = status;
    }
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void setMe(Me mMe)
    {
        this.mMe = mMe;
    }

    public Me getMe()
    {
        return this.mMe;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    public Token getToken() {
        return token;
    }

    public NotificationModel getNotification() {
        return nNotification;
    }

    public void setNotification(NotificationModel nNotification) {
        this.nNotification = nNotification;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setIsLooged(Boolean isLooged) {
        this.isLooged = isLooged;
        // Hacking for logout
        if(!isLooged){
            getIdentifying().token ="";
        }
    }

    public SharedPreferences getSharedSettings() {
        return sharedSettings;
    }

    public Gson getGson() {
        return gson;
    }
    public final UtilsConfig getConfig(){
        return this.configApp;
    }

    private static MooGlobals instance;
    private static String sAdmodBottomId = "";
    private static String sAdmodInterstitialId = "";
    private static Boolean bAdFull;
    private static Boolean bAdBottom;

    public void setbAdFull(Boolean bAdFull)
    {
        MooGlobals.bAdFull = bAdFull;
    }

    public void setbAdBottom(Boolean bAdBottom)
    {
        MooGlobals.bAdBottom = bAdBottom;
    }

    public void setsAdmodBottomId(String sAdmodBottomId)
    {
        MooGlobals.sAdmodBottomId = sAdmodBottomId;
    }

    public void setsAdmodInterstitialId(String sAdmodInterstitialId)
    {
        MooGlobals.sAdmodInterstitialId = sAdmodInterstitialId;
    }

    public Boolean getbAdBottom() {
        return bAdBottom;
    }

    public Boolean getbAdFull() {
        return bAdFull;
    }

    public String getsAdmodBottomId()
    {
        return sAdmodBottomId;
    }

    public String getsAdmodInterstitialId()
    {
        return sAdmodInterstitialId;
    }

    public static synchronized MooGlobals getInstance()
    {
        if (instance == null)
        {
            instance = new MooGlobals();
        }
        return instance;
    }
}
