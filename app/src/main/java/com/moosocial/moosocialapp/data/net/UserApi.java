package com.moosocial.moosocialapp.data.net;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.data.entiy.TokenEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class UserApi extends MooApi {
    private TokenEntity token;

    public UserApi(MooApplication app) {
        super(app);
    }

    public void me() {
    }

    public void find(final String username , final String password) {

    // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, super.URL_AUTH_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        Log.d("moodebug", response.substring(0, 100));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            json = new String(response.data);
                            Log.d("moodebug", json);
                            //json = trimMessage(json, "message");
                            //if(json != null) displayMessage(json);
                            break;
                    }
                }else{
                    Log.e("moodebug", "Something went wrong!", error);
                }



            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };

    // Add the request to the queue
        Volley.newRequestQueue(app.getApplicationContext()).add(stringRequest);

    }
}
