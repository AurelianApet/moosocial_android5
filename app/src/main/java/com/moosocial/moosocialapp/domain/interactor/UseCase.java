package com.moosocial.moosocialapp.domain.interactor;

import android.app.Activity;
import android.content.Intent;

import com.moosocial.moosocialapp.presentation.view.activities.LangingActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginActivity;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class UseCase {
    protected Activity aActivity;

    public UseCase(Activity aActivtiy)
    {
        this.aActivity = aActivtiy;
    }

    public void execute() {

    }

    public Activity getActivity()
    {
        return aActivity;
    }

    public void onRefesh(){
        Intent intent = new Intent(aActivity, LangingActivity.class); // this is the starting activity for your application
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // tells Android to finish all other activities in the stack
        aActivity.startActivity(intent);
    }
}
