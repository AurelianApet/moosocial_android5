package com.moosocial.moosocialapp.presentation.view.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.interactor.NotificationResult;
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
public class SettingActivity extends MooActivityToken {
    public ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_bar_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(getResources().getString(R.string.text_setting));

        final UtilsConfig configApp = MooGlobals.getInstance().getConfig();
        if (configApp.languages.length() > 0) {
            TextView tLanguages = (TextView) findViewById(R.id.language);
            final String sLocation = getLanguage();
            JSONObject jLangnue = configApp.getLanguages(sLocation);
            try {
                tLanguages.setText(jLangnue.getString("label"));
            } catch (JSONException e) {
            }
            tLanguages.setVisibility(View.VISIBLE);
            TableRow tSetting = (TableRow)findViewById(R.id.tableRow_Setting);
            tSetting.setOnClickListener(new View.OnClickListener() {
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
                    new MaterialDialog.Builder(new ContextThemeWrapper(SettingActivity.this, R.style.AppThemeMaterialDialog))
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
        Switch sNotification = (Switch)findViewById(R.id.switch_notification);

        final SharedPreferences sharedSettings = getApplicationContext().getSharedPreferences(MooGlobals.MOO_SHARED_GLOBAL, MODE_PRIVATE);
        String sSettingNotification = sharedSettings.getString(MooGlobals.MOO_SETTING_NOTIFICATION,"1");
        if (sSettingNotification.equals("1"))
        {
            sNotification.setChecked(true);
        }

        sNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked)
                {
                    sharedSettings.edit().putString(MooGlobals.MOO_SETTING_NOTIFICATION,"1").apply();
                }
                else
                {
                    sharedSettings.edit().putString(MooGlobals.MOO_SETTING_NOTIFICATION,"0").apply();
                }
            }
        });
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
