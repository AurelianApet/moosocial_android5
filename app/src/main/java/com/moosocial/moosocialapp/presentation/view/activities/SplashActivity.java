package com.moosocial.moosocialapp.presentation.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.presentation.model.NotificationModel;
import com.moosocial.moosocialapp.util.MooGlobals;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SplashActivity extends MooActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Bundle bBundle = getIntent().getExtras();
        if (bBundle != null && bBundle.getString("notification_url") != null)
        {
            NotificationModel nNotification = new NotificationModel((MooApplication)getApplication(),bBundle.getString("notification_id"),bBundle.getString("notification_url"));
            MooGlobals.getInstance().setNotification(nNotification);
        }

        if (checkInternet())
            start();
    }

    protected Boolean checkInternet()
    {
        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            // notify user you are not online
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppThemeDialog));
            builder.setMessage(getResources().getString(R.string.connect_error))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.text_reconnect), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (checkInternet())
                                start();
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
    }

    protected void start()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LangingActivity.class);
                startActivity(i);

                finish();
            }
        }, 1500);
    }

}
