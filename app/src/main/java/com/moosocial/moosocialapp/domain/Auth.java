package com.moosocial.moosocialapp.domain;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class Auth {
    /* The current user
     */
    private User cUser;
    private static Auth ourInstance = new Auth();

    public static Auth getInstance() {
        return ourInstance;
    }

    private Auth() {
    }


    public User getcUser() {
        return cUser;
    }

    public void setcUser(User cUser) {
        this.cUser = cUser;
    }
}
