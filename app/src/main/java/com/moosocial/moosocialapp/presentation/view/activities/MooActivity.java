package com.moosocial.moosocialapp.presentation.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.Token;
import com.moosocial.moosocialapp.domain.interactor.GCMManage;
import com.moosocial.moosocialapp.domain.interactor.IdentifyingUser;
import com.moosocial.moosocialapp.util.GCM.QuickstartPreferences;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.moosocial.moosocialapp.util.UtilsConfig;

import java.util.Locale;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MooActivity extends AppCompatActivity {
    protected Boolean bHasAds = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Token getToken()
    {
        Gson gson = new Gson();
        SharedPreferences sharedSettings = getApplicationContext().getSharedPreferences(MooGlobals.MOO_APP, MODE_PRIVATE);
        String token = sharedSettings.getString(IdentifyingUser.MOO_TOKEN, "");

        if (!token.isEmpty())
        {
            return gson.fromJson(token, Token.class);
        }

        return new Token("","","0","","","");
    }

    public String getGMCToken()
    {
        SharedPreferences sharedSettings = getApplicationContext().getSharedPreferences(MooGlobals.MOO_APP, MODE_PRIVATE);
        return  sharedSettings.getString(QuickstartPreferences.GCM_TOKEN, "");
    }

    public String getLanguage()
    {
        UtilsConfig utilsConfig = MooGlobals.getInstance().getConfig();
        SharedPreferences sharedSettings = getApplicationContext().getSharedPreferences(MooGlobals.MOO_SHARED_GLOBAL, MODE_PRIVATE);
        return  sharedSettings.getString(MooGlobals.MOO_LANGUAGE,utilsConfig.defaultLanguage);
    }

    public void setLanguage(String localeCode){
        SharedPreferences sharedSettings = getApplicationContext().getSharedPreferences(MooGlobals.MOO_SHARED_GLOBAL, MODE_PRIVATE);
        sharedSettings.edit().putString(MooGlobals.MOO_LANGUAGE, localeCode).apply();
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        MooGlobals.getInstance().getConfig().updateMenuLanguage(localeCode);

        //add to server
        String sGcmToken = getGMCToken();
        if (!sGcmToken.isEmpty()) {
            GCMManage gcm = new GCMManage((MooApplication) getApplication(), this, null);
            gcm.setToken(sGcmToken);
            gcm.execute();
        }

        restartActivity();
    }

    public String getLanguageCode()
    {
        Configuration config = getBaseContext().getResources().getConfiguration();
        return config.locale.getISO3Language();
    }
    public void restartActivity() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private FrameLayout content;
    private void setSpaceForAd(int height) {

        // content.getChildAt(0).setPadding(0, 0, 0, 50);
        View child0 = content.getChildAt(0);
        FrameLayout.LayoutParams layoutparams = (android.widget.FrameLayout.LayoutParams) child0
                .getLayoutParams();
        layoutparams.bottomMargin = height;
        child0.setLayoutParams(layoutparams);

    }

    @SuppressLint("NewApi")
    public void setupAdAtBottom() {
        if (MooGlobals.getInstance().getsAdmodBottomId().isEmpty() || bHasAds || !MooGlobals.getInstance().getbAdBottom())
        {
            return;
        }

        bHasAds = true;
        content = (FrameLayout) findViewById(android.R.id.content);
        // inflate ad layout and set it to bottom by layouparams
        final LinearLayout ad = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.ad_layout, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        ad.setLayoutParams(params);

        // adding viewtreeobserver to get height of ad layout , so that
        // android.R.id.content will set margin of that height
        ViewTreeObserver vto = ad.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    ad.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    ad.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width = ad.getMeasuredWidth();
                int height = ad.getMeasuredHeight();
                Log.i("ad hight", height + "");
                setSpaceForAd(height);

            }

        });
        addLayoutToContent(ad);



    }

    private void addLayoutToContent(LinearLayout ad) {
        // content.addView(ad);
        content.addView(ad);
        AdView mAdView = new AdView(getBaseContext());
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(MooGlobals.getInstance().getsAdmodBottomId());
        ad.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void initBackground()
    {
        UtilsConfig utilsConfig = MooGlobals.getInstance().getConfig();
        if (!utilsConfig.enableSlider)
        {
            LinearLayout lFull = (LinearLayout)findViewById(R.id.moo_body);
            lFull.setBackgroundResource(0);

            lFull.setBackgroundColor(getResources().getColor(R.color.background_langing_color));
        }
    }
}
