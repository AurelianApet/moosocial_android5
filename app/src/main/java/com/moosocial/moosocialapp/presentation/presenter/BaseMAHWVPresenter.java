package com.moosocial.moosocialapp.presentation.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.domain.interactor.GCMManage;
import com.moosocial.moosocialapp.presentation.view.activities.BaseMooActivityHasWebView;
import com.moosocial.moosocialapp.presentation.view.activities.LangingActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginNewActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MainActivity;
import com.moosocial.moosocialapp.util.GCM.QuickstartPreferences;
import com.moosocial.moosocialapp.util.MooGlobals;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class BaseMAHWVPresenter extends AppPresenter {
    public BaseMAHWVPresenter(Activity activity) {
        super(activity);
    }
    public void onRefeshToken() {
        MooGlobals.getInstance().getIdentifying().setMainActivity((MainActivity)activity).setIsRefeshToken(true).execute();
    }
    public void onClickLogout(){
        Intent intent = new Intent(activity, LangingActivity.class); // this is the starting activity for your application
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // tells Android to finish all other activities in the stack
        intent.putExtra("logout","1");
        activity.startActivity(intent);
        activity.finish();
    }
}
