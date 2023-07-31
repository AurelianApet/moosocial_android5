package com.moosocial.moosocialapp.domain.interactor;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.moosocial.moosocialapp.domain.Error;
import com.moosocial.moosocialapp.data.net.GsonRequest;
import com.moosocial.moosocialapp.data.net.MooApi;

import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.presentation.view.activities.BaseMooActivityHasWebView;
import com.moosocial.moosocialapp.presentation.view.activities.MainActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.util.MooGlobals;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class NotificationUpdate extends UseCase {
    private MooApplication app;
    private MooApi api ;

    public NotificationUpdate(MooApplication app, Activity aActivity){
        super(aActivity);
        this.app = app;
        this.aActivity = aActivity;
    }

    @Override
    public void execute() {
        if (((MooActivity)aActivity).getToken() == null)
        {
            return;
        }
        String uri = String.format(api.URL_GET_NOTIFICATION_COUNT + "?access_token=%s",((MooActivity)aActivity).getToken().getAccess_token());
        Log.wtf("noti",uri);
        GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.GET,uri,Object.class,null,
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {
                        ((BaseMooActivityHasWebView)aActivity).updateNotificationsBadge(Integer.parseInt((String) ((LinkedTreeMap) response).get("count_notification")));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

                ((MainActivity)aActivity).stopUpdateNotification();
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
}
