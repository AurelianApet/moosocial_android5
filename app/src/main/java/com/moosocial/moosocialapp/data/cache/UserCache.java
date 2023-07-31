package com.moosocial.moosocialapp.data.cache;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.moosocial.moosocialapp.data.entiy.TokenEntity;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class UserCache {
    private Context c;
    private TokenEntity token;
    public static final String UserToken = "MooApplication.UserToken";
    public UserCache(Context c) {

        this.c = c;
        SharedPreferences token = this.c.getSharedPreferences(UserToken, this.c.MODE_PRIVATE);
    }
}
