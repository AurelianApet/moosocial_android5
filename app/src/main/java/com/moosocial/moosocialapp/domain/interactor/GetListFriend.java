package com.moosocial.moosocialapp.domain.interactor;


import android.app.Activity;
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
import com.moosocial.moosocialapp.domain.SignupConfig;
import com.moosocial.moosocialapp.presentation.presenter.AppPresenter;
import com.moosocial.moosocialapp.presentation.presenter.SignupPresenter;
import com.moosocial.moosocialapp.presentation.presenter.StatusPresenter;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.presentation.view.activities.SignupActivity;
import com.moosocial.moosocialapp.util.MooGlobals;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class GetListFriend extends UseCase {
    private MooApi api ;
    private AppPresenter sPresenter;
    public GetListFriend(Activity aActivtiy, AppPresenter sPresenter) {
        super(aActivtiy);
        this.sPresenter = sPresenter;
    }
    public void execute()
    {
        if (((MooActivity)aActivity).getToken() == null)
        {
            return;
        }
        String uri = String.format(api.URL_LIST_FRIEND  +"?access_token=%s",((MooActivity)aActivity).getToken().getAccess_token());
        GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.GET, uri,Object.class,null,
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {
                        ((StatusPresenter)sPresenter).onSuccess(response);
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
                            Toast.makeText(aActivity, err.getMessage(), Toast.LENGTH_LONG).show();
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
