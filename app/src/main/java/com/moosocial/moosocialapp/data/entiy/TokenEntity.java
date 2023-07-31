package com.moosocial.moosocialapp.data.entiy;

import com.moosocial.moosocialapp.domain.Token;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class TokenEntity extends Token {
    public TokenEntity(String access_token, String token_type, String expires_in, String refresh_token, String scope, String gdm_token) {
        super(access_token, token_type, expires_in, refresh_token, scope,gdm_token);
    }
}
