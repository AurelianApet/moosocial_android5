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
import com.google.gson.internal.LinkedTreeMap;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.data.net.GsonRequest;
import com.moosocial.moosocialapp.data.net.MooApi;
import com.moosocial.moosocialapp.domain.Error;
import com.moosocial.moosocialapp.domain.SignupConfig;
import com.moosocial.moosocialapp.presentation.presenter.SignupPresenter;
import com.moosocial.moosocialapp.presentation.view.activities.LoginActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.presentation.view.activities.SignupActivity;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.moosocial.moosocialapp.util.UtilsMoo;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class GetSignupConfig extends UseCase {
    private MooApplication app;
    private MooApi api ;
    private SignupPresenter sSignupPresenter;
    public GetSignupConfig(Activity aActivtiy, SignupPresenter sSignupPresenter) {
        super(aActivtiy);
        app = (MooApplication)aActivtiy.getApplication();
        this.sSignupPresenter = sSignupPresenter;
    }
    public void execute()
    {
        ((SignupActivity)aActivity).showLoading();
        GsonRequest<SignupConfig> gsObjRequest = new GsonRequest<SignupConfig>(Request.Method.GET, api.URL_SIGNUP,SignupConfig.class,null,
                new Response.Listener<SignupConfig>() {
                    @Override
                    public void onResponse(SignupConfig response) {
                        sSignupPresenter.setSignupConfig(response);
                        ((SignupActivity)aActivity).initLayout(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((SignupActivity)aActivity).showErrorLoadConfig();
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
}
