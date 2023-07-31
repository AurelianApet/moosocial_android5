package com.moosocial.moosocialapp.presentation.view.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.ItemSearch;
import com.moosocial.moosocialapp.domain.interactor.SeachingResult;
import com.moosocial.moosocialapp.presentation.view.items.search.SearchExpandableListAdapter;
import com.moosocial.moosocialapp.presentation.view.items.search.SearchGroup;

import java.util.HashMap;
import java.util.List;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SearchResultsActivity extends MooActivityToken {
    public ActionBar ab;
    public SeachingResult sResult;
    public SearchExpandableListAdapter listAdapter;
    public ProgressBar pProgressBar;
    public TextView tTextNoResult;
    public ExpandableListView expListView;
    public SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_bar_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(getResources().getString(R.string.toolbar_title_search));

        pProgressBar = (ProgressBar)findViewById(R.id.search_progress);
        pProgressBar.setVisibility(View.GONE);

        tTextNoResult = (TextView)findViewById(R.id.text_no_result);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.list_result);

        sResult = new SeachingResult((MooApplication)getApplication(),this);
        sResult.setExpandableListView(expListView);
        sResult.setProgressBar(pProgressBar);
        sResult.setTextNoResult(tTextNoResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconified(false);
        searchView.setActivated(true);
        searchView.setQueryHint(getResources().getString(R.string.moo_search_hint));


        final SearchView.SearchAutoComplete mSearchSrcTextView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                CharSequence text = mSearchSrcTextView.getText();
                if (TextUtils.isEmpty(text)) {
                    finish();
                }
                else{
                    mSearchSrcTextView.setText("");
                    mSearchSrcTextView.requestFocus();
                }
            }
       });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                pProgressBar.setVisibility(View.VISIBLE);
                tTextNoResult.setVisibility(View.GONE);
                expListView.setVisibility(View.GONE);

                sResult.setKeySearch(query);
                sResult.execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
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
