package com.moosocial.moosocialapp.presentation.model;

import android.util.Log;

import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.data.entiy.UserEntity;
import com.moosocial.moosocialapp.data.net.UserApi;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class UserModel extends AppModel  {
    private UserEntity entity;
    private UserApi api ;
    public UserModel(MooApplication app) {
        super(app);
        entity = new UserEntity(app);
        api = new UserApi(app);
    }
    public UserEntity find(String username,String password){
        Log.d("mooDebug", "Username : " + username);

        return entity;
    }
    public UserEntity find2(String username,String password){
        Log.d("mooDebug", "Say hello 2");
        return entity;
    }
}
