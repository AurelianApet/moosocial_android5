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
import com.moosocial.moosocialapp.domain.Error;
import com.moosocial.moosocialapp.data.net.GsonRequest;
import com.moosocial.moosocialapp.data.net.MooApi;
import com.moosocial.moosocialapp.presentation.presenter.BaseMAHWVPresenter;
import com.moosocial.moosocialapp.presentation.presenter.LoginPresenter;
import com.moosocial.moosocialapp.presentation.view.activities.LangingActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginNewActivity;
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
public class GCMManage extends UseCase {
    private MooApplication app;
    private String sToken;
    private Boolean bRefesh;
    private Boolean bAdd;
    private LoginPresenter pPresenter;
    private MooApi api ;
    public GCMManage(MooApplication app,Activity aActivity,LoginPresenter pPresenter)
    {
        super(aActivity);
        this.app = app;
        this.pPresenter = pPresenter;
        bAdd = true;
        bRefesh = false;
    }
    public void execute()
    {
        if (bAdd)
        {
            Log.wtf("aaa",api.URL_GCM);
            GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.POST, api.URL_GCM,Object.class,null,
                    new Response.Listener<Object>() {
                        @Override
                        public void onResponse(Object response) {
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
                }
            },new GsonBuilder().create()){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("token",sToken);
                    params.put("access_token", MooGlobals.getInstance().getToken().getAccess_token());
                    params.put("language",((MooActivity)aActivity).getLanguageCode());
                    return params;
                }
            };
            MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
        }
        else
        {
            GsonRequest<Object> gsObjRequest;
            if (MooGlobals.getInstance().getConfig().enablePostDelete)
            {
                gsObjRequest = new GsonRequest<Object>(Request.Method.DELETE, api.URL_GCM,Object.class,null,
                        new Response.Listener<Object>() {
                            @Override
                            public void onResponse(Object response) {
                                actionDeleteDone();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        actionDeleteError(error);
                    }
                },new GsonBuilder().create()){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("token",sToken);
                        params.put("access_token", MooGlobals.getInstance().getToken().getAccess_token());
                        params.put("language",((MooActivity)aActivity).getLanguageCode());
                        return params;
                    }
                };
            }
            else
            {
                gsObjRequest = new GsonRequest<Object>(Request.Method.POST, api.URL_GCM + "/delete",Object.class,null,
                        new Response.Listener<Object>() {
                            @Override
                            public void onResponse(Object response) {
                                actionDeleteDone();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        actionDeleteError(error);
                    }
                },new GsonBuilder().create()){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("token",sToken);
                        params.put("access_token", MooGlobals.getInstance().getToken().getAccess_token());
                        params.put("language",((MooActivity)aActivity).getLanguageCode());
                        return params;
                    }
                };
            }

            MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
        }
    }

    public void actionDeleteDone()
    {
        if (!bRefesh) {
            pPresenter.doActionAfterDeleteDone();
        }
        else
        {
            bRefesh = false;
        }
    }

    public void actionDeleteError(VolleyError error)
    {
        String json = null;
        NetworkResponse response = error.networkResponse;
        if(response != null && response.data != null){
            Error err;
            switch(response.statusCode){
                case 400:
                    err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
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
                case 500:
                    err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
                    if (err.getMessage().equals("The access token provided has expired"))
                    {
                        MooGlobals.getInstance().getIdentifying().setLoginActivity((LoginNewActivity) aActivity).setMainActivity(null).setUseCase(GCMManage.this).setIsRefeshToken(true).execute();
                    }
                    break;
            }
        }else{
            Log.e("moodebug", "Something went wrong!", error);
        }
    }

    public void onRefesh(){
        bRefesh = true;
        this.setDelete();
        this.execute();

        pPresenter.doActionAfterDeleteDone();
    }

    public GCMManage setToken(String sToken)
    {
        this.sToken = sToken;
        return this;
    }

    public GCMManage setAdd()
    {
        this.bAdd = true;
        return this;
    }

    public GCMManage setDelete()
    {
        this.bAdd = false;
        return this;
    }
}
