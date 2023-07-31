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
import com.moosocial.moosocialapp.domain.*;
import com.moosocial.moosocialapp.domain.Error;
import com.moosocial.moosocialapp.presentation.view.activities.LoginActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginNewActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.presentation.view.activities.SignupActivity;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.moosocial.moosocialapp.util.UtilsMoo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SignupResult extends UseCase {
    private MooApplication app;
    private MooApi api ;
    private String sName;
    private String sPassword;
    private String sEmail;
    private String sGender;
    private String sBirthday;

    public SignupResult(Activity aActivtiy) {
        super(aActivtiy);
        app = (MooApplication)aActivtiy.getApplication();
    }

    public void setData(String sEmail,String sName,String sPassword, String gender, String birthday)
    {
        this.sEmail = sEmail;
        this.sName = sName;
        this.sPassword = sPassword;
        this.sGender = gender;
        this.sBirthday = birthday;
    }

    public void execute()
    {
        ((SignupActivity)aActivity).showLoading();
        GsonRequest<SignupConfig> gsObjRequest = new GsonRequest<SignupConfig>(Request.Method.GET, api.URL_SIGNUP,SignupConfig.class,null,
                new Response.Listener<SignupConfig>() {
                    @Override
                    public void onResponse(SignupConfig response) {
                        String key = response.getKey();
                        doSignup(key);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((SignupActivity)aActivity).hideLoading();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            com.moosocial.moosocialapp.domain.Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
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

    public void doSignup(final String sKey)
    {
        final String sHash = UtilsMoo.md5(MooGlobals.getInstance().getConfig().apiKey + sKey);
        GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.POST, api.URL_SIGNUP,Object.class,null,
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {
                        Intent intent = new Intent(aActivity, LoginNewActivity.class); // this is the starting activity for your application
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // tells Android to finish all other activities in the stack
                        intent.putExtra("email",sEmail);
                        intent.putExtra("password",sPassword);
                        aActivity.startActivity(intent);
                        aActivity.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((SignupActivity)aActivity).hideLoading();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            com.moosocial.moosocialapp.domain.Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
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
                    params.put("password",sPassword);
                    params.put("name",sName);
                    params.put("password2",sPassword);
                    params.put("email",sEmail);
                    params.put("gender", sGender);
                    params.put("language",((MooActivity)aActivity).getLanguageCode());
                    if (!sBirthday.isEmpty())
                    {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
                            Date date = formatter.parse(sBirthday);

                            SimpleDateFormat formatter_sql = new SimpleDateFormat("yyyy-mm-dd");
                            params.put("birthday", formatter_sql.format(date));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }


                    return params;
                }
        };

        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }
}
