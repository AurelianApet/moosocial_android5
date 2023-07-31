package com.moosocial.moosocialapp.domain;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class Me extends User {
    private String email;
    private String last_login;
    private String notification_count;
    private String friend_request_count;
    private String conversation_user_count;
    private String lang;
    protected Object menus;
    private String admod_banner_id;
    private String admod_interstitial_id;
    private String ads_show_full;
    private String ads_show_bottom;
   // private String cover;

    public Me(int userId) {
        super(userId);
    }

    public String getEmail() {
        return email;
    }

    public String getNotification_count() {
        return notification_count;
    }

    public String getFriend_request_count() {
        return friend_request_count;
    }

    public String getConversation_user_count() {
        return conversation_user_count;
    }

    public String getLang() {
        return lang;
    }
    public Object getAvatar(){
        return avatar;
    }

    public String getCover() {
        return cover;
    }

    public String getName()
    {
        return name;
    }

    public String getProfileUrl()
    {
        return profile_url;
    }

    public Object getMenus()
    {
        return menus;
    }

    public String getAdmodBannerId()
    {
        return admod_banner_id;
    }

    public String getAdmodInterstitialId()
    {
        return admod_interstitial_id;
    }

    public String getAdsShowFull()
    {
        return ads_show_full;
    }

    public String getAdsShowBottom()
    {
        return ads_show_bottom;
    }
}
