package com.moosocial.moosocialapp.presentation.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.presentation.presenter.LoginPresenter;
import com.moosocial.moosocialapp.presentation.view.activities.adapter.SliderAdapter;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.moosocial.moosocialapp.util.UtilsConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 *
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class LangingActivity extends MooActivity {
    LoginPresenter presenter;
    protected UtilsConfig configApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsConfig utilsConfig = MooGlobals.getInstance().getConfig();

        if (!utilsConfig.enableSlider)
        {
            setContentView(R.layout.activity_langing);
        }
        else
        {
            setContentView(R.layout.activity_langing_slider);
        }
        initBackground();

        presenter = new LoginPresenter(this);
        configApp = MooGlobals.getInstance().getConfig();

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.getString("logout") != null)
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
                    Intent intent = new Intent(this, LoginNewActivity.class);
                    intent.putExtra("refresh","1");
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }


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
                    new MaterialDialog.Builder(new ContextThemeWrapper(LangingActivity.this, R.style.AppThemeMaterialDialog))
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

        TextView tLogin = (TextView)findViewById(R.id.login);
        tLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LangingActivity.this, LoginNewActivity.class);
                startActivity(intent);
            }
        });

        TextView tSignup = (TextView)findViewById(R.id.signup);
        tSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LangingActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        String url = null;
        try {
            url = MooGlobals.getInstance().getConfig().urlHost + MooGlobals.getInstance().getConfig().jListUrls.getString("term_service");
        } catch (JSONException e) {
        }
        TextView tTerm = (TextView)findViewById(R.id.link_term);
        final String finalUrl = url;
        tTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
                startActivity(browserIntent);
            }
        });

        try {
            url = MooGlobals.getInstance().getConfig().urlHost + MooGlobals.getInstance().getConfig().jListUrls.getString("privacy");
        } catch (JSONException e) {
        }
        TextView tPrivacy = (TextView)findViewById(R.id.link_privacy);
        final String finalUrlP = url;
        tPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrlP));
                startActivity(browserIntent);
            }
        });

        if (utilsConfig.enableSlider)
        {
            ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
            SliderAdapter adapter = new SliderAdapter(getBaseContext());
            viewPager.setAdapter(adapter);
            viewPager.setVisibility(View.VISIBLE);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliderDots);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setVisibility(View.VISIBLE);
        }
    }
}
