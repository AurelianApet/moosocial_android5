package com.moosocial.moosocialapp.presentation.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.presentation.presenter.LoginPresenter;
import com.moosocial.moosocialapp.util.MooGlobals;

import org.json.JSONException;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 *
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class LoginNewActivity extends MooActivity {
    LoginPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        initBackground();

        presenter = new LoginPresenter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.getString("email") != null) {
                EditText _emailText = (EditText) findViewById(R.id.input_email);
                EditText _passwordText = (EditText) findViewById(R.id.input_password);

                _emailText.setText(extras.getString("email"));
                _passwordText.setText(extras.getString("password"));
                try {
                    presenter.setUrlLoad(MooGlobals.getInstance().getConfig().urlHost + MooGlobals.getInstance().getConfig().jListUrls.getString("link_create_account"));
                } catch (JSONException e) {
                }
                presenter.onLogin();
            }

            if (extras.getString("refresh") != null)
            {
                MooGlobals.getInstance().getIdentifying().setLoginActivity(this).setIsRefeshToken(true).execute();
            }
        }

        TextView tTextViewForgot = (TextView)findViewById(R.id.link_forgot);
        tTextViewForgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.onForgotPassword();
            };
        });

        Button bLogin = (Button)findViewById(R.id.login);
        bLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.onLogin();
            };
        });

        ImageView iBack = (ImageView)findViewById(R.id.img_back);
        iBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            };
        });
    }

    public void showLoading()
    {
        RelativeLayout rProgressBar = (RelativeLayout)findViewById(R.id.login_progress);
        rProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading()
    {
        RelativeLayout rProgressBar = (RelativeLayout)findViewById(R.id.login_progress);
        rProgressBar.setVisibility(View.GONE);
    }
}
