package com.moosocial.moosocialapp.presentation.view.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.presentation.presenter.MainPresenter;
import com.moosocial.moosocialapp.presentation.view.items.menubar.MooSpinner;
import com.moosocial.moosocialapp.util.GCM.QuickstartPreferences;
import com.moosocial.moosocialapp.util.MooGlobals;


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
public class MainActivity extends BaseMooActivityHasWebView {
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    MainPresenter presenter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout swipeRefresh;
    private String access_token;
    public BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MooGlobals.getInstance().isLogged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		presenter = new MainPresenter(this);

        access_token = getToken().getAccess_token();

        //init toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setActionBar(getSupportActionBar());
        initActionBar();

        setSpinner((MooSpinner) findViewById(R.id.spinner_rss));
        initSpinner();
        //init menu
        setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
        setNavigationView((NavigationView)findViewById(R.id.nav_view));
        setupDrawerContent(initNavigationView());
        //init tab
        final ViewPager viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: //everyone
                        try
                        {
                            wWebView.loadUrl("javascript:window.activityAction.setFilter('0')");
                        }catch (Exception e)
                        {

                        }
                        break;
                    case 1: //friend
                        try
                        {
                            wWebView.loadUrl("javascript:window.activityAction.setFilter('1')");
                        }catch (Exception e)
                        {

                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //init web
        setWebView((WebView) findViewById(R.id.webview));
        initWebView();

        // init avatar
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        initAvatar((ImageView)header.findViewById(R.id.navigation_drawer_user_account_picture_profile),
                (ImageView)header.findViewById(R.id.navigation_drawer_user_account_picture_cover),(TextView)header.findViewById(R.id.navigation_drawer_account_information_display_name));
        //initAvatar((ImageView)findViewById(R.id.action_account));
        if (configApp.enableGCM) {
            String sGcmToken = MooGlobals.getInstance().getSharedSettings().getString(QuickstartPreferences.GCM_TOKEN,null);
            if (sGcmToken == null) {
                presenter.initGCM();
            }
            else{
                if (MooGlobals.getInstance().getToken() != null)
                    MooGlobals.getInstance().getToken().setGcmToken(sGcmToken);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void showWebview() {
        super.showWebview();
    }

    private void setupViewPager(ViewPager viewPager)
    {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment(), getResources().getString(R.string.tab_everyone));
        adapter.addFragment(new Fragment(), getResources().getString(R.string.tab_friends));
        viewPager.setAdapter(adapter);
    }



    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        String key = (String) menuItem.getTitleCondensed();
                        if (key.equals("setting"))
                        {
                            Intent iSetting = new Intent(MainActivity.this, SettingActivity.class);
                            startActivity(iSetting);
                            closeDrawer();
                            return true;
                        }

                        JSONObject item = hMenus.get(key);
                        try {
                            if (URLUtil.isValidUrl(item.getString("url")))
                            {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getString("url")));
                                startActivity(browserIntent);
                                if (prevMenuItem != null) {
                                    prevMenuItem.setChecked(false);
                                }
                                return true;
                            }
                        } catch (JSONException e) {

                        }

                        if (prevMenuItem != null) {
                            prevMenuItem.setChecked(false);
                        }

                        prevMenuItem = menuItem;

                        bCheckNoLoadLogiUrl = true;
                        menuItem.setChecked(true);
                        closeDrawer();

                        checkLogicUrl(key,"",true);

                        return true;
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //mDrawerLayout.openDrawer(GravityCompat.START);
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
