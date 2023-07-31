package com.moosocial.moosocialapp.domain.interactor;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.data.net.GsonRequest;
import com.moosocial.moosocialapp.data.net.MooApi;
import com.moosocial.moosocialapp.domain.Error;
import com.moosocial.moosocialapp.presentation.view.activities.ForgotActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.moosocial.moosocialapp.util.UtilsMoo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class ForgotResult extends UseCase {
    private MooApplication app;
    private MooApi api ;
    private String sEmail;

    public ForgotResult(Activity aActivtiy) {
        super(aActivtiy);
        app = (MooApplication)aActivtiy.getApplication();
    }

    public void setData(String sEmail)
    {
        this.sEmail = sEmail;
    }

    public void execute()
    {
        ((ForgotActivity)aActivity).showLoading();
        GsonRequest<MooKey> gsObjRequest = new GsonRequest<MooKey>(Request.Method.GET, api.URL_FORGOT,MooKey.class,null,
                new Response.Listener<MooKey>() {
                    @Override
                    public void onResponse(MooKey response) {
                        String key = response.getKey();
                        doForgot(key);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((ForgotActivity)aActivity).hideLoading();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
                            //json = trimMessage(json, "message");
                            //if(json != null) displayMessage(json);
                            Toast.makeText(app.getApplicationContext(), err.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }else{
                    Log.e("moodebug", "Something went wrong!", error);
                }
            }
        },new GsonBuilder().create()){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("language",((MooActivity)aActivity).getLanguageCode());
                return params;
            }
        };
        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }

    public void doForgot(final String sKey)
    {
        final String sHash = UtilsMoo.md5(MooGlobals.getInstance().getConfig().apiKey + sKey);
        GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.POST, api.URL_FORGOT,Object.class,null,
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {
                        ((ForgotActivity)aActivity).showSuccessful();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((ForgotActivity)aActivity).hideLoading();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
                            //json = trimMessage(json, "message");
                            //if(json != null) displayMessage(json);
                            Toast.makeText(app.getApplicationContext(), err.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }else{
                    Log.e("moodebug", "Something went wrong!", error);
                }
            }
            },new GsonBuilder().create()){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("security_token",sHash);
                    params.put("key",sKey);
                    params.put("email",sEmail);
                    params.put("language",((MooActivity)aActivity).getLanguageCode());
                    return params;
                }
        };

        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }
}
