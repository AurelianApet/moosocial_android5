package com.moosocial.moosocialapp.domain.interactor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.moosocial.moosocialapp.domain.Token;
import com.moosocial.moosocialapp.domain.TokenDeserializer;
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
public class IdentifyingUser extends UseCase{
    private MooApplication app;
    private LoginNewActivity loginActivity;
    private MainActivity mainActivity;
    private UseCase uUseCase;
    private String sLanguageCode;
    public static final String MOO_TOKEN = "Moo.App.Token";
    public String token;
    private String username;
    private String password;
    public String sUrlLoad;
    private Boolean forceLogin = false,forceRefeshToken=false;
    private MooApi api ;

    public IdentifyingUser(MooApplication app,Activity aActivity){
        super(aActivity);
        this.app = app;
        this.sLanguageCode = app.getBaseContext().getResources().getConfiguration().locale.getISO3Language();
        SharedPreferences settings = MooGlobals.getInstance().getSharedSettings();
        token = settings.getString(MOO_TOKEN,"");
        api = new MooApi(MooGlobals.getInstance().getConfig().urlHost);
        Log.d("moodebug", "Booting " + token);
    }
    public IdentifyingUser setLoginData(String username,String password){
        this.username = username;
        this.password = password;
        forceLogin = true;
        return this;
    }

    public IdentifyingUser setUrlLoad(String sUrlLoad)
    {
        this.sUrlLoad = sUrlLoad;
        return this;
    }

    public IdentifyingUser setUseCase(UseCase uUseCase)
    {
        this.uUseCase = uUseCase;
        return this;
    }


    @Override
    public void execute() {
        final IdentifyingUser iIdentifyingUser = this;
        if(forceLogin){
            loginActivity.showLoading();
            // Request a string response
            GsonRequest<Token> gsObjRequest = new GsonRequest<Token>(Request.Method.POST, api.URL_AUTH_TOKEN,Token.class,null,
                    new Response.Listener<Token>() {
                        @Override
                        public void onResponse(Token response) {


                            SharedPreferences.Editor editor = MooGlobals.getInstance().getSharedSettings().edit();
                            editor.putString(MOO_TOKEN, MooGlobals.getInstance().getGson().toJson(response));
                            editor.commit();
                            MooGlobals.getInstance().setToken(response);
                            MooGlobals.getInstance().setIsLooged(true);
                            Log.d("moodebug", "Call Api" + MooGlobals.getInstance().getGson().toJson(response));

                            Intent iIntent = new Intent(loginActivity, MainActivity.class);
                            iIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (iIdentifyingUser.sUrlLoad != null && !iIdentifyingUser.sUrlLoad.isEmpty())
                            {
                                iIntent.putExtra("load_url",iIdentifyingUser.sUrlLoad);
                                iIdentifyingUser.sUrlLoad = "";
                            }
                            loginActivity.startActivity(iIntent);
                            loginActivity.finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if(response != null && response.data != null){
                        switch(response.statusCode){
                            case 404:
                            case 401:
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
                    loginActivity.hideLoading();
                }
            },new GsonBuilder().registerTypeAdapter(Token.class, new TokenDeserializer()).create()){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username",username);
                    params.put("password", password);
                    params.put("language",sLanguageCode);
                    return params;
                }
            };

            // Add the request to the queue
            MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
            // Hacking for logout
            forceLogin = false;
        }else if(forceRefeshToken){
            if (uUseCase != null)
            {
                //nothing
            }else if (mainActivity != null) {
                mainActivity.hideWebview();
            }else{
                loginActivity.showLoading();
            }

            GsonRequest<Token> gsObjRequest = new GsonRequest<Token>(Request.Method.POST, api.URL_AUTH_TOKEN,Token.class,null,
                    new Response.Listener<Token>() {
                        @Override
                        public void onResponse(Token response) {
                            SharedPreferences.Editor editor = MooGlobals.getInstance().getSharedSettings().edit();
                            editor.putString(MOO_TOKEN, MooGlobals.getInstance().getGson().toJson(response));
                            editor.commit();
                            MooGlobals.getInstance().setToken(response);
                            MooGlobals.getInstance().setIsLooged(true);
                            MooGlobals.getInstance().setWaitingRefeshToken(false);
                            if (mainActivity != null) {
                                //reload page on webview
                                try
                                {
                                    mainActivity.loadUrl(MooGlobals.getInstance().getConfig().urlHost + MooGlobals.getInstance().getConfig().jListUrls.getString("home_everyone"));
                                }catch (Exception e)
                                {

                                }
                                mainActivity.startUpdateNotification();
                            } if (uUseCase != null)
                            {
                                uUseCase.onRefesh();
                                uUseCase = null;
                            }
                            else{
                                loginActivity.startActivity(new Intent(loginActivity, MainActivity.class));
                                loginActivity.finish();
                            }
                            Log.d("moodebug", "Call Refesh" + MooGlobals.getInstance().getGson().toJson(response));

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
                                Log.d("moodebug", "Refeshing token " + new String(response.data));
                                Toast.makeText(app.getApplicationContext(), err.getMessage(), Toast.LENGTH_LONG).show();

                                return;
                        }
                    }else{
                        Log.e("moodebug", "Something went wrong!", error);
                    }
                    MooGlobals.getInstance().setWaitingRefeshToken(false);
                    MooGlobals.getInstance().setIsLooged(false);
                    MooGlobals.getInstance().setIsLooged(false);
                    MooGlobals.getInstance().getSharedSettings().edit().clear().commit();
                    if (mainActivity != null) {
                        mainActivity.startActivity(new Intent(mainActivity, LangingActivity.class));
                        mainActivity.finish();
                        mainActivity = null;
                    }
                    if (uUseCase != null)
                    {
                        uUseCase = null;
                        Activity aActitivy = uUseCase.getActivity();
                        aActitivy.startActivity(new Intent(aActitivy, LangingActivity.class));
                        aActitivy.finish();
                    }
                    else if(loginActivity != null)
                    {
                        loginActivity.hideLoading();
                    }

                    // Hacking when the refesh token is invalid
                    forceRefeshToken = false;

                }
            },new GsonBuilder().registerTypeAdapter(Token.class, new TokenDeserializer()).create()){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("grant_type","refresh_token");
                    params.put("refresh_token", MooGlobals.getInstance().getToken().getRefresh_token());
                    params.put("language",sLanguageCode);
                    return params;
                }
            };
            MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
        }else{

            if(!token.isEmpty()){

                MooGlobals.getInstance().setToken(MooGlobals.getInstance().getGson().fromJson(token, Token.class));
                MooGlobals.getInstance().setIsLooged(true);
                if(MooGlobals.getInstance().getToken().getTime_expired() > System.currentTimeMillis()){ // >
                    // do nothing
                }else{
                    MooGlobals.getInstance().setWaitingRefeshToken(true);
                }
            }
        }

    }

    public IdentifyingUser setLoginActivity(LoginNewActivity loginActivity) {
        this.loginActivity = loginActivity;
        return this;
    }

    public IdentifyingUser setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        return this;
    }

    public IdentifyingUser setIsRefeshToken(Boolean status) {
        this.forceRefeshToken = status;
        return this;
    }
}
