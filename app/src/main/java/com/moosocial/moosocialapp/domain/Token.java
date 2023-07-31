package com.moosocial.moosocialapp.domain;

import android.util.Log;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class Token {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private long time_created;
    private long time_expired;
    private String gcm_token;
    public Token(String access_token, String token_type, String expires_in, String refresh_token, String scope, String gcm_token) {
        this.set(access_token,token_type,expires_in,refresh_token,scope,gcm_token);
    }


    public void set(String access_token, String token_type, String expires_in, String refresh_token, String scope, String gcm_token){
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
        this.time_created =System.currentTimeMillis();
        this.time_expired =System.currentTimeMillis() + (Long.parseLong(expires_in)*1000);
        this.gcm_token = gcm_token;
        Log.d("moodebug", "Token Deserializer" + time_created);
    }
    public String getAccess_token() {

        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


    public long getTime_created() {
        return time_created;
    }

    public long getTime_expired() {
        return time_expired;
    }

    public void setGcmToken(String gcm_token)
    {
        this.gcm_token = gcm_token;
    }

    public String getGcmToken()
    {
        return this.gcm_token;
    }

}
