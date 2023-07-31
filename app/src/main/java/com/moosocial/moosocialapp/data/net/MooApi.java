package com.moosocial.moosocialapp.data.net;

import com.moosocial.moosocialapp.MooApplication;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MooApi {
    public static String BASE_URL = "http://192.168.168.7/moolab/2.2.2/api/";
    /*  Api url for getting token and refeshing token     */
    public static  String URL_AUTH_TOKEN ;
    /* Api url for getting user detail */
    public static  String URL_USER_ME ;
    public static  String URL_SEARCH;
    public static  String URL_GET_NOTIFICATION_COUNT;
    public static  String URL_LIST_NOTIFICATION;
    public static  String URL_NOTIFICATION_MARK_READ;
    public static  String URL_NOTIFICATION_DELETE;
    public static  String URL_GCM;
    public static  String URL_SIGNUP;
    public static  String URL_FORGOT;
    public static  String URL_LIST_FRIEND;
    public static  String URL_FILE;
    public static  String URL_STATUS;
    public static  String URL_FETCH_LINK;
    public static  String URL_COMMENT_ITEM;
    public static  String URL_COMMENT_ACTIVITY;
    protected MooApplication app;
    public MooApi(MooApplication app){
        this.app = app;
    }

    public MooApi(String baseURL) {
        BASE_URL = baseURL+"/api/";
        URL_AUTH_TOKEN = BASE_URL + "auth/token";
        URL_USER_ME = BASE_URL + "user/me";
        URL_SEARCH = BASE_URL + "search";
        URL_LIST_NOTIFICATION = BASE_URL + "notification/me/show";
        URL_GET_NOTIFICATION_COUNT = BASE_URL + "notification/me";
        URL_NOTIFICATION_MARK_READ = BASE_URL + "notification/";
        URL_NOTIFICATION_DELETE = BASE_URL + "notification/me/delete";
        URL_GCM = BASE_URL + "user/me/gcm/token";
        URL_SIGNUP = BASE_URL + "user/register";
        URL_FORGOT = BASE_URL + "user/forgot";
        URL_LIST_FRIEND = BASE_URL + "friend/list";
        URL_FILE = BASE_URL + "file";
        URL_STATUS = BASE_URL + "activity";
        URL_FETCH_LINK = BASE_URL + "activity/fetch-link";
        URL_COMMENT_ITEM = BASE_URL + "%s/comment";
        URL_COMMENT_ACTIVITY = BASE_URL + "activity/%s/comment";
    }

    public void get(){}
    public void post(){

    }
    public void put(){}
    public void delete(){}
}
