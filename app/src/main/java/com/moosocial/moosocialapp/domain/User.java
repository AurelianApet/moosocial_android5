package com.moosocial.moosocialapp.domain;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class User {
    protected final int userId;
    protected String name;
    protected Object avatar;
    protected int photo_count;
    protected int friend_count;
    protected int blog_count;
    protected int topic_count;
    protected int video_count;
    protected String gender;
    protected String birthday;
    protected String timezone;
    protected String about;
    protected String cover;
    protected String profile_url;
    private Token cToken ;
    public User(int userId) {
        this.userId = userId;
    }


    public Token getcToken() {
        return cToken;
    }

    public void setcToken(Token cToken) {
        this.cToken = cToken;
    }

}
