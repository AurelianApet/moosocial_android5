package com.moosocial.moosocialapp.domain.interactor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.internal.LinkedTreeMap;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.data.net.GsonRequest;
import com.moosocial.moosocialapp.data.net.MooApi;
import com.moosocial.moosocialapp.domain.Error;
import com.moosocial.moosocialapp.domain.Me;
import com.moosocial.moosocialapp.presentation.view.activities.BaseMooActivityHasWebView;
import com.moosocial.moosocialapp.presentation.view.activities.LangingActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.util.MooGlobals;

import org.w3c.dom.Text;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class LoadingOwnerAvatar extends UseCase {
    private MooApplication app;
    private ImageView mImageView;
    private ImageView mCoverView;
    private TextView tAccountName;
    private MooApi api ;
    @Override
    public void execute() {
        String uri = String.format(api.URL_USER_ME+"?access_token=%s",((MooActivity)aActivity).getToken().getAccess_token());
        GsonRequest<Me> gsObjRequest = new GsonRequest<Me>(Request.Method.GET,uri,Me.class,null,
                new Response.Listener<Me>() {
                    @Override
                    public void onResponse(final Me response) {

                        Object avatar = response.getAvatar();
                        String url = (String) ((LinkedTreeMap) avatar).get("100");
                        Glide.with(aActivity).load(url)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mImageView);

                        String cover = response.getCover();
                        Glide.with(aActivity).load(cover)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mCoverView);

                        tAccountName.setText(response.getName());

                        tAccountName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((BaseMooActivityHasWebView) aActivity).loadUrl(response.getProfileUrl());
                                ((BaseMooActivityHasWebView) aActivity).closeDrawer();
                            }
                        });
                        mImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((BaseMooActivityHasWebView) aActivity).loadUrl(response.getProfileUrl());
                                ((BaseMooActivityHasWebView) aActivity).closeDrawer();
                            }
                        });
                        MooGlobals.getInstance().setMe(response);
                        ((BaseMooActivityHasWebView)aActivity).updateNotificationsBadge(Integer.parseInt(response.getNotification_count()));
                        ((BaseMooActivityHasWebView)aActivity).checkMenuHide();

                        MooGlobals.getInstance().setsAdmodBottomId(response.getAdmodBannerId());
                        MooGlobals.getInstance().setsAdmodInterstitialId(response.getAdmodInterstitialId());
                        Boolean bAdFull = false;
                        if (response.getAdsShowFull().equals("1"))
                        {
                            bAdFull = true;
                        }
                        MooGlobals.getInstance().setbAdFull(bAdFull);

                        Boolean bAdBottom = false;
                        if (response.getAdsShowBottom().equals("1"))
                        {
                            bAdBottom = true;
                        }
                        MooGlobals.getInstance().setbAdBottom(bAdBottom);

                        ((BaseMooActivityHasWebView)aActivity).setupAdAtBottom();
                        ((BaseMooActivityHasWebView)aActivity).showFullAds();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:

                            com.moosocial.moosocialapp.domain.Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
                            //json = trimMessage(json, "message");
                            //if(json != null) displayMessage(json);
                            if (err.getMessage().equals("Error parameter : Token is invalid"))
                            {
                                MooGlobals.getInstance().setWaitingRefeshToken(false);
                                MooGlobals.getInstance().setIsLooged(false);
                                MooGlobals.getInstance().setIsLooged(false);
                                MooGlobals.getInstance().getSharedSettings().edit().clear().commit();
                                aActivity.startActivity(new Intent(aActivity, LangingActivity.class));
                                aActivity.finish();
                            }
                            Toast.makeText(app.getApplicationContext(), err.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }else{
                    Log.e("moodebug", "Something went wrong!", error);
                }
            }
        });
        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }
    public LoadingOwnerAvatar(MooApplication app, Activity aActivtiy){
        super(aActivtiy);
        this.app = app;
    }

    public void setmImageView(ImageView mImageView) {
        this.mImageView = mImageView;
    }
    public void setmCoverView(ImageView mCoverView) {
        this.mCoverView = mCoverView;
    }

    public void SettAccountName(TextView tAccountName)
    {
        this.tAccountName = tAccountName;
    }
}
