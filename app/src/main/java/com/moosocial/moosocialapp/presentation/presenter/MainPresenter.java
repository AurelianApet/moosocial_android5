package com.moosocial.moosocialapp.presentation.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.domain.interactor.GCMManage;
import com.moosocial.moosocialapp.presentation.view.activities.MainActivity;
import com.moosocial.moosocialapp.util.GCM.QuickstartPreferences;
import com.moosocial.moosocialapp.util.GCM.RegistrationIntentService;
import com.moosocial.moosocialapp.util.MooGlobals;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MainPresenter extends AppPresenter {


    public MainPresenter(Activity activity) {
        super(activity);
    }

    public void initGCM()
    {
        ((MainActivity)activity).mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {;
                SharedPreferences shareSetting = MooGlobals.getInstance().getSharedSettings();
                String sGcmToken = shareSetting.getString(QuickstartPreferences.GCM_TOKEN,null);
                MooGlobals.getInstance().getToken().setGcmToken(sGcmToken);
                //add to server
                GCMManage gcm = new GCMManage((MooApplication)activity.getApplication(),activity,null);
                gcm.setToken(sGcmToken);
                gcm.execute();
            }
        };
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(activity, RegistrationIntentService.class);
            activity.startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 9000)
                        .show();
            } else {
                Log.i("moosocial", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

}
