package com.moosocial.moosocialapp.data.net;

import com.moosocial.moosocialapp.MooApplication;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class AuthApi extends MooApi {
    public AuthApi(MooApplication app) {
        super(app);
    }

    public void getToken(){}
    public void refeshToken(){}
    public void validateToken(){}
}
