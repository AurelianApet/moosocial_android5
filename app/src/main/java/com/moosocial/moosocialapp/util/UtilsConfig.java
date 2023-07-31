package com.moosocial.moosocialapp.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class UtilsConfig {
    public JSONObject configJson;
    public JSONObject menuJson;
    public String urlHost;
    public String googleAppSenderId;
    public String googleApiKey;
    public Long notificationTime;
    public String apiKey;
    public Boolean enableGCM;
    public String defaultLanguage;
    public JSONArray menuItems;
    private Context context;
    public JSONArray menuAccount;
    public JSONArray pages;
    public JSONArray languages;
    public JSONArray jEmojis;
    public JSONObject jListUrls;
    public Boolean enablePostDelete;
    public Boolean enableSlider;

    public UtilsConfig(Context context) {
        try {
            this.context = context;
            StringBuilder buf = new StringBuilder();
            InputStream json = context.getAssets().open("appConfig.json");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            this.configJson = new JSONObject(buf.toString());
            this.defaultLanguage = this.configJson.getJSONObject("general").getString("defaultLanguage");
            String sLanguage = this.defaultLanguage;

            buf = new StringBuilder();
            if (sLanguage.isEmpty())
            {
                json = context.getAssets().open("appMenus.json");
            }
            else
            {
                try {
                    json = context.getAssets().open("appMenus-" + sLanguage + ".json");
                }catch (Exception e)
                {
                    json = context.getAssets().open("appMenus.json");
                }
            }
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            str = "";
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            this.menuJson = new JSONObject(buf.toString());

            //init value
            this.urlHost = this.configJson.getJSONObject("general").getString("initialUrl");
            this.googleAppSenderId = this.configJson.getJSONObject("general").getString("googleAppSenderId");
            this.googleApiKey = this.configJson.getJSONObject("general").getString("googleApiKey");
            this.enableGCM = this.configJson.getJSONObject("general").getBoolean("enableGCM");
            this.apiKey = this.configJson.getJSONObject("general").getString("apiKey");
            this.enablePostDelete = this.configJson.getJSONObject("general").getBoolean("enablePostDelete");
            this.enableSlider = this.configJson.getJSONObject("general").getBoolean("enableSlider");
            this.notificationTime = this.configJson.getJSONObject("general").getLong("notificationTime");
            this.languages = this.configJson.getJSONArray("languages");

            this.menuItems = this.menuJson.getJSONArray("items");
            this.menuAccount = this.menuJson.getJSONArray("account");
            this.pages = this.menuJson.getJSONArray("pages");
            this.jListUrls = this.configJson.getJSONObject("list_urls");
            this.jEmojis = this.configJson.getJSONArray("emojis");
        }
        catch (Exception $e)
        {
            Log.d("moosocial",$e.getMessage());
        }
    }

    public void updateMenuLanguage(String sLanguage)
    {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json;
            if (sLanguage.isEmpty()) {
                json = context.getAssets().open("appMenus.json");
            } else {
                try {
                    json = context.getAssets().open("appMenus-" + sLanguage + ".json");
                } catch (Exception e) {
                    json = context.getAssets().open("appMenus.json");
                }
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str = "";
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            this.menuJson = new JSONObject(buf.toString());
            this.menuItems = this.menuJson.getJSONArray("items");
            this.pages = this.menuJson.getJSONArray("pages");
            this.menuAccount = this.menuJson.getJSONArray("account");
        }
        catch (Exception e)
        {

        }
    }

    public JSONObject getLanguages(String key){
        for (int i=0;i<this.languages.length();i++)
        {
            try {
                JSONObject tmp = this.languages.getJSONObject(i);
                if (tmp.getString("key").equals(key))
                    return tmp;
            }catch (Exception e)
            {

            }
        }
        return null;
    }
}
