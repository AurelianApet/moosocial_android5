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
import com.moosocial.moosocialapp.presentation.view.activities.BaseMooActivityHasWebView;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.util.MooGlobals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class StatusResult extends UseCase {
    private MooApplication app;
    private MooApi api ;

    public StatusResult(Activity aActivtiy) {
        super(aActivtiy);
        app = (MooApplication)aActivtiy.getApplication();
    }

    public void execute(final Integer iPrivacy, final String sMessage, final String sMessageText, final String sFriends, final ArrayList<String> sImages, final String sLink, final String sType, final String sTargetId) {
        String uri = String.format(api.URL_STATUS + "?access_token=%s",((MooActivity)aActivity).getToken().getAccess_token());
        GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.POST,uri,Object.class,null,
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {
                        ((BaseMooActivityHasWebView)aActivity).afterShare();
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
                params.put("language",((MooActivity)aActivity).getLanguageCode());
                params.put("type",sType);
                params.put("target_id",sTargetId);
                params.put("message",sMessage);
                params.put("messageText",sMessageText);
                params.put("userTagging",sFriends);
                params.put("privacy",iPrivacy.toString());
                params.put("userShareLink",sLink);
                if (sImages.size() > 0) {
                    int i = 0;
                    for (String img : sImages)
                    {
                        params.put("wallPhoto["+i+"]", img);
                        i++;
                    }
                }

                return params;
            }
        };
        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }
}
