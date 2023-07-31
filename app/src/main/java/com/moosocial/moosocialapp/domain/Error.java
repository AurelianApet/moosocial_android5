package com.moosocial.moosocialapp.domain;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class Error {
    private String name;
    private String message;
    private String url;

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
}
