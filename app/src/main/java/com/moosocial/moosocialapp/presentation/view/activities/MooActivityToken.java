package com.moosocial.moosocialapp.presentation.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.moosocial.moosocialapp.MooApplication;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MooActivityToken extends MooActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getToken().getAccess_token().isEmpty())
        {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                setupAdAtBottom();
            }
        };

        handler.postDelayed(r, 1000);
    }
}
