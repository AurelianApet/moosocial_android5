package com.moosocial.moosocialapp.presentation.view.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.interactor.NotificationResult;
import com.moosocial.moosocialapp.domain.interactor.SeachingResult;
import com.moosocial.moosocialapp.presentation.view.items.search.SearchExpandableListAdapter;

import java.util.List;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class NotificationActivity extends MooActivityToken {
    public ActionBar ab;
    public ProgressBar pProgressBar;
    public NotificationResult nResult;
    public TextView tTextNoResult;
    public ListView lListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_bar_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(getResources().getString(R.string.action_notifications));

        pProgressBar = (ProgressBar)findViewById(R.id.search_progress);
        pProgressBar.setVisibility(View.VISIBLE);

        tTextNoResult = (TextView)findViewById(R.id.text_no_result);

        lListView = (ListView) findViewById(R.id.list_result);
        nResult = new NotificationResult((MooApplication)getApplication(),this);
        nResult.setListView(lListView);
        nResult.setProgressBar(pProgressBar);
        nResult.setTextNoResult(tTextNoResult);
        nResult.execute();
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
