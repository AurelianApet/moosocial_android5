package com.moosocial.moosocialapp.presentation.view.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.presentation.presenter.LoginPresenter;
import com.moosocial.moosocialapp.presentation.view.items.util.KeyValueSpinnerAdapter;
import com.moosocial.moosocialapp.presentation.view.items.util.SpinnerItem;
import com.moosocial.moosocialapp.util.GCM.QuickstartPreferences;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.moosocial.moosocialapp.util.UtilsConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class LoginActivity extends MooActivity {
    protected UtilsConfig configApp;
    LoginPresenter presenter;
    public ActionBar ab;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_bar_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(getResources().getString(R.string.toolbar_title_login));*/

        configApp = MooGlobals.getInstance().getConfig();
        presenter = new LoginPresenter(this);
        //MooGlobals.getInstance().identifying.setLoginActivity(this);

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
            }else if (extras.getString("logout") != null)
            {
                if (MooGlobals.getInstance().getToken() == null)
                {
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                    return;
                }
                presenter.onLogout();
            }
        }
        else
        {
            MooGlobals.getInstance().identifying.execute();

            if (MooGlobals.getInstance().isLogged())
            {
                if(MooGlobals.getInstance().isWaitingRefeshToken()){
                    presenter.onRefeshToken();
                }else{
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }

        TextView tTextView = (TextView)findViewById(R.id.link_signup);
        tTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.onCreateAccount();
            };
        });

        TextView tTextViewForgot = (TextView)findViewById(R.id.link_forgot);
        tTextViewForgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.onForgotPassword();
            };
        });

        //Init languages
        if (configApp.languages.length() > 0) {
            TextView tLanguages = (TextView) findViewById(R.id.link_languages);
            final String sLocation = getLanguage();
            JSONObject jLangnue = configApp.getLanguages(sLocation);
            try {
                tLanguages.setText(jLangnue.getString("label"));
            } catch (JSONException e) {
            }
            tLanguages.setVisibility(View.VISIBLE);
            tLanguages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> listItems = new ArrayList<String>();
                    int index = 0;
                    for (int i=0;i<configApp.languages.length();i++) {
                        try {
                            JSONObject tmp = configApp.languages.getJSONObject(i);
                            if (tmp.getString("key").equals(sLocation))
                            {
                                index = i;
                            }
                            listItems.add(tmp.getString("label"));
                        }catch (Exception e)
                        {

                        }
                    }

                    final CharSequence[] charSequenceItems = listItems.toArray(new CharSequence[listItems.size()]);
                    final int finalIndex = index;
                    new MaterialDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.AppThemeMaterialDialog))
                            .items(charSequenceItems)
                            .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if (which != finalIndex) {
                                        try {
                                            JSONObject tmp = configApp.languages.getJSONObject(which);
                                            setLanguage(tmp.getString("key"));
                                            restartActivity();
                                        } catch (Exception e) {

                                        }

                                    }
                                    return true;
                                }
                            })
                            .show();
                }
            });
        }
    }

    public void showLoading()
    {
        ProgressBar bProgressBar = (ProgressBar)findViewById(R.id.login_progress);
        bProgressBar.setVisibility(View.VISIBLE);

        LinearLayout lLinearLayout = (LinearLayout) findViewById(R.id.login_content);
        lLinearLayout.setVisibility(View.INVISIBLE);
    }

    public void hideLoading()
    {
        ProgressBar bProgressBar = (ProgressBar)findViewById(R.id.login_progress);
        bProgressBar.setVisibility(View.GONE);

        LinearLayout lLinearLayout = (LinearLayout) findViewById(R.id.login_content);
        lLinearLayout.setVisibility(View.VISIBLE);
    }

    public void loginAction(View view) {
        presenter.onLogin();
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}