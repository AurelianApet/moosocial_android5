package com.moosocial.moosocialapp.presentation.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.interactor.GCMManage;
import com.moosocial.moosocialapp.presentation.model.UserModel;
import com.moosocial.moosocialapp.presentation.view.activities.ForgotActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LangingActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginActivity;
import com.moosocial.moosocialapp.presentation.view.activities.LoginNewActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MainActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.presentation.view.activities.SignupActivity;
import com.moosocial.moosocialapp.util.MooGlobals;

import java.io.IOException;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class LoginPresenter extends AppPresenter {
    private UserModel user ;
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    TextView _emailTextError;
    TextView _passwordTextError;
    String sUrlLoad;

    public void onRefeshToken(){
        MooGlobals.getInstance().getIdentifying().setIsRefeshToken(true).execute();
    }

    public void setUrlLoad(String sUrlLoad)
    {
        this.sUrlLoad = sUrlLoad;
    }


    public LoginPresenter(Activity activity) {
        super(activity);
		
		user = new UserModel((MooApplication)activity.getApplication());
        _emailText = (EditText)activity.findViewById(R.id.input_email);
        _passwordText = (EditText)activity.findViewById(R.id.input_password);
        _loginButton = (Button)activity.findViewById(R.id.btn_login);
        _signupLink = (TextView)activity.findViewById(R.id.link_signup);
        _emailTextError = (TextView)activity.findViewById(R.id.email_error_message);
        _passwordTextError = (TextView)activity.findViewById(R.id.password_error_message);
    }

    public void onLogin(){
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

        boolean valid = true;

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        //email = "root@local.com";
        //password = "1";
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailTextError.setText(activity.getResources().getString(R.string.login_error_email));
            _emailTextError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            _emailTextError.setText(null);
            _emailTextError.setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            _passwordTextError.setText(activity.getResources().getString(R.string.login_error_password));
            _passwordTextError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            _passwordTextError.setText(null);
            _passwordTextError.setVisibility(View.GONE);
        }

        if (valid)
        {
            MooGlobals.getInstance().getIdentifying().setLoginActivity((LoginNewActivity)activity).setUrlLoad(sUrlLoad).setLoginData(email, password).execute();
        }
		
    }

    public void onLogout()
    {
        String sGcmToken = ((MooActivity)activity).getGMCToken();
        if (sGcmToken != null && !sGcmToken.isEmpty()) {
            GCMManage gcm = new GCMManage((MooApplication) activity.getApplication(), activity, this);
            gcm.setToken(sGcmToken);
            gcm.setDelete();
            gcm.execute();
        }

        doActionAfterDeleteDone();
    }

    public void doActionAfterDeleteDone()
    {

        final String sGcmToken = ((MooActivity)activity).getGMCToken();
        if (sGcmToken != null && !sGcmToken.isEmpty())
        {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        GcmPubSub.getInstance(activity).unsubscribe(sGcmToken, "/topics/global");
                    } catch (IOException | IllegalArgumentException e) {
                    }
                    return null;
                }
            }.execute();
        }
        MooGlobals.getInstance().setIsLooged(false);
        MooGlobals.getInstance().getSharedSettings().edit().clear().commit();
        MooGlobals.getInstance().getToken().setAccess_token(null);
        MooGlobals.getInstance().getToken().setGcmToken(null);

    }

    public void onCreateAccount(){
        Intent intent = new Intent(activity, SignupActivity.class);
        activity.startActivity(intent);
    }

    public void onForgotPassword(){
        Intent intent = new Intent(activity, ForgotActivity.class);
        activity.startActivity(intent);
    }
}
