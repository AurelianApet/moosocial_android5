package com.moosocial.moosocialapp.presentation.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.internal.LinkedTreeMap;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.data.net.MooApi;
import com.moosocial.moosocialapp.domain.Me;
import com.moosocial.moosocialapp.domain.interactor.LoadingOwnerAvatar;
import com.moosocial.moosocialapp.domain.interactor.NotificationUpdate;
import com.moosocial.moosocialapp.presentation.model.NotificationModel;
import com.moosocial.moosocialapp.presentation.presenter.BaseMAHWVPresenter;
import com.moosocial.moosocialapp.presentation.presenter.StatusPresenter;
import com.moosocial.moosocialapp.presentation.view.items.emoji.EmojiGridViewAdapter;
import com.moosocial.moosocialapp.presentation.view.items.emoji.EmojiItem;
import com.moosocial.moosocialapp.presentation.view.items.menubar.MenuBarItem;
import com.moosocial.moosocialapp.presentation.view.items.menubar.MenuBarSpinnerAdapter;
import com.moosocial.moosocialapp.presentation.view.items.menubar.MooSpinner;

import com.moosocial.moosocialapp.util.Mention.mentions.MentionUsers;
import com.moosocial.moosocialapp.util.Mention.mentions.UserMention;
import com.moosocial.moosocialapp.util.Mention.suggestions.SuggestionsResult;
import com.moosocial.moosocialapp.util.Mention.tokenization.QueryToken;
import com.moosocial.moosocialapp.util.Mention.tokenization.interfaces.QueryTokenReceiver;
import com.moosocial.moosocialapp.util.Mention.ui.RichEditorView;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.moosocial.moosocialapp.util.UtilsMoo;
import com.moosocial.moosocialapp.util.UtilsWebViewJS;
import com.moosocial.moosocialapp.util.MediaUtility;
import com.moosocial.moosocialapp.util.UtilsConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class BaseMooActivityHasWebView extends MooActivityToken implements QueryTokenReceiver {
    public final String TAG = "BaseMooActivityHasWebView";
    protected DrawerLayout mDrawerLayout;
    protected NavigationView navigationView;
    protected MenuBuilder mMenus;
    protected UtilsConfig configApp;
    protected HashMap<String,JSONObject> hMenus = new HashMap<String,JSONObject>();
    protected HashMap<String,JSONObject> hAccountMenus = new HashMap<String,JSONObject>();
    protected TabLayout tabLayout;
    protected Toolbar toolbar;
    protected MooSpinner mSpinner;
    protected Integer bFirstSpin;
    protected ActionBar ab;
    protected int mNotificationsCount = 0;
    protected PopupMenu pAccount;
    protected ProgressBar mProgressBar;
    protected SwipeRefreshLayout swipeRefresh;
    protected WebView wWebView;
    protected LoadingOwnerAvatar loadingOwerAvatar;
    protected String sCurrentUrl = "";
    private static final int REQUEST_FILE_PICKER = 1;
    private ValueCallback<Uri> mFilePathCallback4;
    private ValueCallback<Uri[]> mFilePathCallback5;
    private SearchView searchView;
    protected BaseMAHWVPresenter basePresenter;
    protected NotificationUpdate nUpdate;
    private Handler mHandler;
    protected Boolean bCheckNoLoadLogiUrl;
    protected MenuItem prevMenuItem;
    Boolean loadingFinished = true;
    Boolean redirect = false;
    private static AsyncHttpClient client = new AsyncHttpClient();
    static {
        client.setThreadPool(Executors.newSingleThreadExecutor());
    }
    private Boolean checkShowUpload = false;
    ResponseHandlerInterface handlerUpload;
    public Boolean doPostStatus = false;

    private ArrayList<String> aResultImages;
    private StatusPresenter statusPresenter;
    private ProgressBar progressStatusBar;

    private RelativeLayout rCommentImageContent;
    private ImageView iCommentImage;
    private ScrollView lCommentEditContent;
    public RichEditorView rCommentEdit;
    public String sCommentImage = "";
    private static final int REQUEST_IMAGE = 2;
    private MentionUsers mUsers;
    private String sCommentType = "";
    private String sCommentId = "";
    private LinearLayout lContentComment;
    public Boolean doPostComment = false;
    public PopupWindow listPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bCheckNoLoadLogiUrl = false;
        configApp = MooGlobals.getInstance().getConfig();
        basePresenter = new BaseMAHWVPresenter(this);

        nUpdate = new NotificationUpdate((MooApplication)getApplication(),this);
        mHandler = new Handler();
        mStatusChecker.run();

        bFirstSpin = 0;

        statusPresenter = new StatusPresenter(this);
    }

    public BaseMAHWVPresenter getPresenter()
    {
        return basePresenter;
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            nUpdate.execute();
            mHandler.postDelayed(mStatusChecker, MooGlobals.getInstance().getConfig().notificationTime);
        }
    };

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mStatusChecker);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mStatusChecker);
        super.onStop();
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mStatusChecker);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mStatusChecker.run();
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_notifications);

        // Update LayerDrawable's BadgeDrawable
        item.setIcon(buildCounterDrawable(mNotificationsCount, R.drawable.ic_bar_notifications));

        //init account menu
        MenuItem mAccount = (MenuItem)menu.findItem(R.id.action_account);
        SubMenu subAccount = mAccount.getSubMenu();
        int length = configApp.menuAccount.length();
        if (length > 0) {
            try {
                int i = 0;
                for (i = 0; i < configApp.menuAccount.length(); i++) {
                    JSONObject itemJson = configApp.menuAccount.getJSONObject(i);
                    hAccountMenus.put(itemJson.getString("key"), itemJson);
                    MenuItem item_account = subAccount.add(itemJson.getString("label"));
                    item_account.setTitleCondensed(itemJson.getString("key"));
                }
            } catch (Exception e) {

            }
        }

        MenuItem item_account = subAccount.add(getResources().getString(R.string.action_logout));
        item_account.setTitleCondensed("account_logout");
        return true;
    }
    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.notification_counter, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }
    public void stopUpdateNotification()
    {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void startUpdateNotification()
    {
        mStatusChecker.run();
    }

    public void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getExtras() != null) {
            if (intent.getExtras().get("url") != null) {
                loadUrl((String) intent.getExtras().get("url"));
            }

            //handle notification
            if (intent.getExtras().get("notification_url") != null) {
                loadUrl((String) intent.getExtras().get("notification_url"));
            }

            if (intent.getExtras().get(StatusPresenter.EXTRA_STATUS) != null) {
                final Integer iPrivacy = intent.getExtras().getInt(StatusPresenter.EXTRA_PRIVACY);
                final String sMessage = intent.getExtras().getString(StatusPresenter.EXTRA_MESSAGE);
                final String sMessageText = intent.getExtras().getString(StatusPresenter.EXTRA_MESSAGE_TEXT);
                final String sFriends = intent.getExtras().getString(StatusPresenter.EXTRA_FRIEND);
                final String sImages = intent.getExtras().getString(StatusPresenter.EXTRA_IMAGE);
                final String sLink = intent.getExtras().getString(StatusPresenter.EXTRA_LINK);
                progressStatusBar.setVisibility(View.VISIBLE);
                doPostStatus = true;

                final String[] aImages = sImages.split(",");
                aResultImages = new ArrayList<>();
                checkShowUpload = true;
                if (!sImages.isEmpty())
                {
                    handlerUpload = new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                            if (checkShowUpload) {
                                Toast toast = Toast.makeText(BaseMooActivityHasWebView.this, getResources().getString(R.string.text_error_post_image), Toast.LENGTH_SHORT);
                                toast.show();
                                progressStatusBar.setVisibility(View.GONE);
                                checkShowUpload = false;
                                doPostStatus = false;
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                            try {
                                JSONObject jObject = new JSONObject(responseString);
                                aResultImages.add(jObject.getString("photo"));
                                if (aResultImages.size() == aImages.length)
                                {
                                    statusPresenter.postStatusServer(iPrivacy,sMessage,sMessageText,sFriends,aResultImages,sLink);
                                    doPostStatus = false;
                                }
                            }
                            catch (Exception e)
                            {

                            }

                        }
                    };

                    new UploadImagesTask().execute(aImages);

                }
                else
                {
                    statusPresenter.postStatusServer(iPrivacy,sMessage,sMessageText,sFriends,aResultImages,sLink);
                    doPostStatus = false;
                }
            }
        }
        //ab.collapseActionView();
        //now getIntent() should always return the last received intent
    }

    public void afterShare()
    {
        progressStatusBar.setVisibility(View.GONE);
        wWebView.loadUrl("javascript:window.activityAction.fetch(1)");
    }

    private class UploadImagesTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(final String... aImages) {
            try
            {

                RequestParams params = new RequestParams();
                String token = getToken().getAccess_token();

                for (String name : aImages) {
                    /*File compressedImage = new Compressor(BaseMooActivityHasWebView.this)
                            .setMaxWidth(1920)
                            .setMaxHeight(1920)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(new File(name));*/
                    File compressedImage = UtilsMoo.saveBitmapToFile(new File(name));
                    params.put("qqfile", compressedImage);
                    client.post(MooApi.URL_FILE + "?access_token=" + token, params, handlerUpload);
                }

            }catch (Exception e)
            {
                Log.wtf("Test",e.toString());
            }

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }

    @Override
    public List<String> onQueryReceived(final @NonNull QueryToken queryToken) {
        List<String> buckets = Collections.singletonList("UserMention");
        List<UserMention> suggestions = mUsers.getSuggestions(queryToken);
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
        rCommentEdit.onReceiveSuggestionsResult(result, "UserMention");
        return buckets;
    }

    public void addSuggests(ArrayList<UserMention> aMentions)
    {
        mUsers = new MentionUsers(aMentions);
    }

    public void resetComment()
    {
        if (lContentComment != null) {
            lContentComment.setVisibility(View.GONE);
            this.sCommentType = "";
            this.sCommentId = "";
            this.sCommentImage = "";

            rCommentImageContent.setVisibility(View.GONE);
            rCommentEdit.setText("");
            doPostComment = false;
        }
    }

    public void afterComment()
    {
        wWebView.loadUrl("javascript:window.commentAction.refeshAndScrollToBottom()");
        resetComment();
    }

    public void postComment(String sCommentType,String sCommentId, String sLink)
    {
        loadUrl(sLink,true);
        this.sCommentType = sCommentType;
        this.sCommentId = sCommentId;
        lContentComment.setVisibility(View.VISIBLE);
    }

    // For webview
    //---------------------------------------------------------------------
    public String getUrlHost(){
        return configApp.urlHost;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    protected void initWebView(){
        WebSettings webSettings = wWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String sUserAgent = webSettings.getUserAgentString();
        sUserAgent += " mooAndroid/1.0";
        webSettings.setUserAgentString(sUserAgent);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        wWebView.addJavascriptInterface(new UtilsWebViewJS(this),"Android");
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();

        wWebView.setWebChromeClient(new WebChromeClient() {
            public void openFileChooser(ValueCallback<Uri> filePathCallback) {
                mFilePathCallback4 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
            }

            public void openFileChooser(ValueCallback filePathCallback, String acceptType) {
                mFilePathCallback4 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
            }

            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
                mFilePathCallback4 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                mFilePathCallback5 = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                if (fileChooserParams.getMode() == 1)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                if (fileChooserParams.getAcceptTypes().length > 0)
                {
                    String sTypes = fileChooserParams.getAcceptTypes()[0];
                    if (!sTypes.isEmpty())
                    {
                        sTypes = sTypes.replace("/*","");
                        sTypes = sTypes.replace(".","/");
                        //intent.setType(sTypes);
                    }
                }

                startActivityForResult(Intent.createChooser(intent, "File Chooser"), REQUEST_FILE_PICKER);
                return true;
            }
        });

        wWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.indexOf(configApp.urlHost) == -1)
                {
                    return false;
                }
                if (url.indexOf("access_token") == -1)
                {
                    Integer index = url.indexOf("?");
                    if ( index == -1)
                    {
                        url += "?access_token=" + getToken().getAccess_token() + "&language=" + getLanguageCode();
                    }
                    else
                    {
                        url += "&access_token=" + getToken().getAccess_token() + "&language=" + getLanguageCode();
                    }

                    wWebView.loadUrl(url);
                    if (!loadingFinished)
                    {
                        redirect = true;
                    }

                    loadingFinished = false;

                    return true;
                }

                return false;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingFinished = false;
                String key = url.replace("?access_token=" + getToken().getAccess_token() + "&language=" + getLanguageCode(), "");
                String url_full = key = key.replace(configApp.urlHost, "");
                String[] keys = key.split("/");
                if (keys.length < 2)
                {
                    key = "home";
                }
                else
                {
                    key = keys[1];
                }
                checkLogicUrl(key,url_full,false);

                hideWebview();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                bCheckNoLoadLogiUrl = false;
                if (!url.equals("file:///android_asset/offline.html") && !sCurrentUrl.equals(url)) {
                    sCurrentUrl = "";
                }
                if (!redirect)
                {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect)
                {
                    showWebview();
                }
                else
                {
                    redirect = false;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (sCurrentUrl.isEmpty()) {
                    sCurrentUrl = wWebView.getUrl();
                }
                wWebView.loadUrl("file:///android_asset/offline.html");
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.web_progress);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
                if (tabLayout.getVisibility() != View.GONE)
                {
                    wWebView.loadUrl("javascript:window.activityAction.fetch(1)");
                    return;
                }
                if (!sCurrentUrl.isEmpty())
                {
                    wWebView.loadUrl(sCurrentUrl);
                }
                else
                {
                    wWebView.reload();
                }
            }
        });
        //load default url
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            String sUrl = extras.getString("load_url");
            if (!sUrl.isEmpty())
            {
                loadUrl(sUrl);
            }
        }
        else
        {
            NotificationModel nNotification = MooGlobals.getInstance().getNotification();
            if (nNotification != null) {
                MooGlobals.getInstance().setNotification(null);
                if (!nNotification.getUrl().isEmpty()) {
                    loadUrl(nNotification.getUrl());
                }
                else
                {
                    try {
                        loadUrl(configApp.urlHost + configApp.jListUrls.getString("home_everyone"));
                    } catch (Exception e) {

                    }
                }
            } else {
                try {
                    loadUrl(configApp.urlHost + configApp.jListUrls.getString("home_everyone"));
                } catch (Exception e) {

                }

            }
        }

        progressStatusBar = (ProgressBar)findViewById(R.id.progressBarStatus);

        rCommentImageContent = (RelativeLayout)findViewById(R.id.comment_image_content);
        iCommentImage = (ImageView)findViewById(R.id.comment_image);
        lCommentEditContent = (ScrollView)findViewById(R.id.comment_edit_content);
        rCommentEdit = (RichEditorView)findViewById(R.id.comment_editor);

        ImageView iCommentRemoveImage = (ImageView)findViewById(R.id.comment_remove_image);
        iCommentRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sCommentImage = "";
                rCommentImageContent.setVisibility(View.GONE);
            }
        });
        ImageView iCommentImageButton = (ImageView)findViewById(R.id.comment_image_button);
        iCommentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sCommentImage.isEmpty())
                    return;

                MultiImageSelector selector = MultiImageSelector.create(BaseMooActivityHasWebView.this);
                selector.showCamera(true);
                selector.single();
                selector.start(BaseMooActivityHasWebView.this, REQUEST_IMAGE);
            }
        });

        ImageView iPostComment = (ImageView)findViewById(R.id.comment_post);
        iPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doPostComment)
                {
                    return;
                }
                doPostComment = true;
                statusPresenter.postComment(rCommentEdit,sCommentImage,sCommentType,sCommentId);
            }
        });

        lContentComment = (LinearLayout)findViewById(R.id.comment_content);

        mUsers = new MentionUsers(new ArrayList<UserMention>());

        rCommentEdit.setQueryTokenReceiver(this);
        rCommentEdit.setHint(getResources().getString(R.string.text_hint_comment));
        rCommentEdit.setContentComment(lCommentEditContent);

        statusPresenter.onCreate();

        listPopupWindow = new PopupWindow(
                this);
        ArrayList<EmojiItem> aEmoji = new ArrayList<>();
        JSONArray jEmoji = MooGlobals.getInstance().getConfig().jEmojis;
        try {
            for (int i = 0; i < jEmoji.length(); i++) {
                aEmoji.add(new EmojiItem(jEmoji.getJSONObject(i).getString("code"), jEmoji.getJSONObject(i).getString("emoji")));
            }
        }catch (Exception e)
        {

        }
        EmojiGridViewAdapter eAdapter = new EmojiGridViewAdapter(this,aEmoji);
        GridView gEmoji = new GridView(this);
        gEmoji.setNumColumns(GridView.AUTO_FIT);
        gEmoji.setGravity(View.TEXT_ALIGNMENT_CENTER);
        gEmoji.setColumnWidth(100);
        gEmoji.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gEmoji.setAdapter(eAdapter);
        gEmoji.setBackgroundColor(Color.WHITE);
        gEmoji.setPadding(5,5,5,5);

        gEmoji.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                EmojiItem item = (EmojiItem) parent.getAdapter().getItem(position);
                rCommentEdit.addText(item.text);
                listPopupWindow.dismiss();
            }
        });

        //listViewSort.setLayoutParams(new ViewGroup.LayoutParams(500,500));
        listPopupWindow.setContentView(gEmoji);
        listPopupWindow.setOutsideTouchable(true);

        final ImageView iEmoji = (ImageView)findViewById(R.id.comment_emoji);
        iEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listPopupWindow.isShowing())
                    listPopupWindow.showAsDropDown(iEmoji);
            }
        });
    }
    protected void setWebView(WebView wWebView){
        this.wWebView = wWebView;
    }

    public void loadUrl(String url)
    {
        resetComment();
        wWebView.loadUrl(url + "?access_token=" + getToken().getAccess_token() + "&language=" + getLanguageCode());
    }

    public void loadUrl(String url, Boolean bCheckSameUrl)
    {
        if (bCheckSameUrl)
        {
            String webUrl = wWebView.getUrl();
            webUrl = webUrl.replace("?access_token=" + getToken().getAccess_token() + "&language=" + getLanguageCode(), "");
            webUrl = webUrl.replace("?access_token=" + getToken().getAccess_token(), "");
            if (webUrl.equals(url))
            {
                return;
            }
        }
        wWebView.loadUrl(url + "?access_token=" + getToken().getAccess_token() + "&language=" + getLanguageCode());
    }

    public void showWebview() {
        mProgressBar.setVisibility(View.INVISIBLE);
        wWebView.animate().alpha(1.0f)
                .setDuration(300)
                .setStartDelay(150);

        mProgressBar.animate().alpha(0.0f)
                .setDuration(60);
    }

    public void hideWebview() {
        mProgressBar.setAlpha(1.0f);
        mProgressBar.setVisibility(View.VISIBLE);
        //wWebView.setAlpha(0.0f);
    }
    public WebView getwWebView(){
        return wWebView;
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        if(wWebView.canGoBack()){
            resetComment();
            wWebView.goBack();
        }else{
            moveTaskToBack(true);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode==REQUEST_FILE_PICKER)
        {
            if(mFilePathCallback4!=null)
            {
                Uri result = intent==null || resultCode!= Activity.RESULT_OK ? null : intent.getData();
                if(result!=null)
                {
                    String path = MediaUtility.getPath(this, result);
                    Uri uri = Uri.fromFile(new File(path));
                    mFilePathCallback4.onReceiveValue(uri);
                }
                else
                {
                    mFilePathCallback4.onReceiveValue(null);
                }
            }
            if(mFilePathCallback5!=null)
            {
                Uri result = intent==null || resultCode!=Activity.RESULT_OK ? null : intent.getData();
                ClipData clipdata = intent==null || resultCode!=Activity.RESULT_OK ? null : intent.getClipData();

                if(result!=null)
                {
                    String path = MediaUtility.getPath(this, result);
                    try {
                        Uri uri = Uri.fromFile(new File(path));
                        mFilePathCallback5.onReceiveValue(new Uri[]{uri});
                    }catch (Exception e)
                    {
                        mFilePathCallback5.onReceiveValue(null);
                    }
                }
                else if (clipdata != null && clipdata.getItemCount() > 0)
                {
                    List<Uri> uris = new ArrayList<Uri>();
                    for (int i=0; i<clipdata.getItemCount();i++)
                    {
                        String path = MediaUtility.getPath(this, clipdata.getItemAt(i).getUri());
                        try{
                            Uri uri = Uri.fromFile(new File(path));
                            uris.add(uri);
                        }catch (Exception e) {

                        }
                    }
                    Uri[] simpleArray = new Uri[ uris.size() ];
                    mFilePathCallback5.onReceiveValue(uris.toArray( simpleArray ));
                }
                else
                {
                    mFilePathCallback5.onReceiveValue(null);
                }
            }

            mFilePathCallback4 = null;
            mFilePathCallback5 = null;
        }

        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                ArrayList<String> mSelectPath = intent.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                sCommentImage = mSelectPath.get(0);

                Glide.with(this)
                        .load(new File(sCommentImage))
                        .centerCrop()
                        .thumbnail(0.5f)
                        .dontAnimate()
                        .into(iCommentImage);
                rCommentImageContent.setVisibility(View.VISIBLE);
            }
        }
    }
    // End webview
    //-----------------------------------------------------------------------

    // For NavigationView
    //-----------------------------------------------------------------------
    public void setDrawerLayout(DrawerLayout mDL){
        mDrawerLayout = mDL;
    }
    public void setNavigationView(NavigationView nav_view){
        this.navigationView = nav_view;
    }
    public NavigationView initNavigationView(){
        int length = configApp.menuItems.length();
        mMenus = (MenuBuilder) navigationView.getMenu();
        if (length > 0){
            try {
                int i = 0;
                for(i=0;i<configApp.menuItems.length();i++){
                    JSONObject itemJson = configApp.menuItems.getJSONObject(i);
                    hMenus.put(itemJson.getString("key"), itemJson);
                    MenuItem item = mMenus.add(R.id.group_menu,Menu.NONE,0,itemJson.getString("label"));
                    item.setTitleCondensed(itemJson.getString("key"));
                    if (!itemJson.getString("icon").isEmpty()) {
                        item.setIcon(getResources().getIdentifier(itemJson.getString("icon"), "drawable", this.getPackageName()));
                    }
                    item.setCheckable(true);
                }
            }catch (Exception e)
            {

            }
        }

        length = configApp.pages.length();
        //add menu setting
        MenuItem item = mMenus.add(R.id.group_menu_add,Menu.NONE,0,getResources().getString(R.string.text_setting));
        item.setTitleCondensed("setting");
        item.setIcon(getResources().getIdentifier("ic_setting", "drawable", this.getPackageName()));
        item.setCheckable(true);

        if (length > 0){
            try {
                int i = 0;
                for(i=0;i<configApp.pages.length();i++){
                    JSONObject itemJson = configApp.pages.getJSONObject(i);
                    hMenus.put(itemJson.getString("key"), itemJson);
                    item = mMenus.add(R.id.group_menu_add,Menu.NONE,0,itemJson.getString("label"));
                    item.setTitleCondensed(itemJson.getString("key"));
                    if (!itemJson.getString("icon").isEmpty()) {
                        item.setIcon(getResources().getIdentifier(itemJson.getString("icon"), "drawable", this.getPackageName()));
                    }
                    item.setCheckable(true);
                }
            }catch (Exception e)
            {

            }
        }

        /*
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        */
        return navigationView;
    }

    public void openDrawer(){
        if(mDrawerLayout != null){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
    public void closeDrawer(){
        if(mDrawerLayout != null){
            mDrawerLayout.closeDrawers();
        }
    }

    public void checkMenuHide()
    {
        Me mMe = MooGlobals.getInstance().getMe();
        ArrayList aMenus = null;
        if (mMe != null)
        {
            aMenus = (ArrayList)mMe.getMenus();
        }

        if (aMenus == null || aMenus.size() == 0)
        {
            return;
        }

        mMenus = (MenuBuilder) navigationView.getMenu();
        int i;
        int size = mMenus.size();
        ArrayList<Integer> aIdRemove = new ArrayList<Integer>();
        for (i = 0;i<size;i++)
        {
            MenuItem mItem = mMenus.getItem(i);
            for (int j = 0; j<aMenus.size();j++)
            {
                LinkedTreeMap menu = (LinkedTreeMap)aMenus.get(j);
                String name = (String)menu.get("name");
                String value = (String)menu.get("value");
                if (mItem.getTitleCondensed().toString().indexOf(name) != -1 && value.isEmpty())
                {
                    mItem.setVisible(false);
                    break;
                }
            }
        }
    }
    // End NavigationView
    //-----------------------------------------------------------------------


    // For spin
    //-----------------------------------------------------------------------------
    public  void setSpinner(MooSpinner mSpinner){
        this.mSpinner = mSpinner;
    }
    public void initSpinner(){
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.wtf("aaa",bFirstSpin.toString());
                if (bFirstSpin == 0) {
                    MenuBarItem item = (MenuBarItem) parentView.getItemAtPosition(position);
                    bCheckNoLoadLogiUrl = true;
                    loadUrl(configApp.urlHost + item.getUrl());
                }
                else {
                    bFirstSpin--;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }
    // End spin
    //------------------------------------------------------------------------------

    // For actionbar
    //-------------------------------------------------------------------------------
    public void setActionBar(ActionBar ab){
        this.ab = ab;
    }
    public void initActionBar(){
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(getResources().getString(R.string.toolbar_title_home));
    }
    // End actionbar
    //--------------------------------------------------------------------------------

    // For menu
    //----------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String key = (String)item.getTitleCondensed();
        if (!key.isEmpty())
        {
            switch (key)
            {
                case "account_logout":
                    basePresenter.onClickLogout();
                    break;
                /*case "account_profile":
                    basePresenter.onClickProfileInformation();
                    break;
                case "account_picture":
                    basePresenter.onClickChangeProfilePiture();
                    break;
                case "account_invite":
                    basePresenter.onClickInviteFriends();
                    break;*/
                default:
                    JSONObject oMenu = hAccountMenus.get(key);
                    if (oMenu != null) {
                        try {
                            loadUrl(configApp.urlHost + oMenu.getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.action_notifications:
                Intent iNotification = new Intent(this, NotificationActivity.class);
                startActivity(iNotification);
                return true;
            case R.id.action_search:
                Intent iSearch = new Intent(this, SearchResultsActivity.class);
                startActivity(iSearch);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // End menu
    //----------------------------------------------------------------------------------

    // Avatar
    //----------------------------------------------------------------------------------
    public void initAvatar(ImageView mImageView, ImageView mCoverView, TextView tAccountName){ // (ImageView) findViewById(R.id.myImage);
        loadingOwerAvatar = new LoadingOwnerAvatar((MooApplication) this.getApplication(),this);
        loadingOwerAvatar.setmImageView(mImageView);
        loadingOwerAvatar.setmCoverView(mCoverView);
        loadingOwerAvatar.SettAccountName(tAccountName);
        loadingOwerAvatar.execute();
    }
    //----------------------------------------------------------------------------------
    // End avatar

    // Tab logic
    //----------------------------------------------------------------------------------
    public void checkLogicUrl(String key,String url_full,Boolean bLoad)
    {
        if (key.indexOf("?") != -1)
        {
            String tmp = key.substring(key.indexOf("?"),key.length());
            key = key.replace(tmp,"");
        }
        if (bCheckNoLoadLogiUrl) {
            if (!bLoad)
                return;
        }
        if (url_full.indexOf("activities/view") == -1) {
            if (key.equals("activities"))
                key = "home";
        }

        if (key.equals("home")) {
            tabLayout.setVisibility(View.VISIBLE);
            try {
                if (url_full.equals(configApp.jListUrls.getString("home_friend"))) {
                    tabLayout.getTabAt(1).select();
                }else {
                    tabLayout.getTabAt(0).select();
                }
            }catch (Exception e){

            }
        } else {
            tabLayout.setVisibility(View.GONE);
        }
        String text = "";
        for(Map.Entry<String, JSONObject> entry : hMenus.entrySet()) {
            String text_key = entry.getKey();
            JSONObject value = entry.getValue();
            if (text_key.indexOf(key) >= 0)
            {
                text = text_key;
                break;
            }
        }
        if (!text.isEmpty()) {
            try {
                //Init menu
                mMenus = (MenuBuilder) navigationView.getMenu();
                int i;
                for (i = 0;i<mMenus.size();i++)
                {
                    MenuItem mItem = mMenus.getItem(i);
                    if (key.equals(mItem.getTitleCondensed()))
                    {
                        if (prevMenuItem != null) {
                            prevMenuItem.setChecked(false);
                        }
                        prevMenuItem = mItem;
                        mItem.setChecked(true);
                        break;
                    }
                }

                JSONObject oMenu = hMenus.get(text);
                JSONArray submenu = oMenu.getJSONArray("subLinks");
                if (submenu.length() > 0) {
                    bFirstSpin++;
                    mSpinner.setVisibility(View.VISIBLE);
                    ab.setDisplayShowTitleEnabled(false);
                    List<MenuBarItem> spinnerItems = new ArrayList<MenuBarItem>();
                    for (i = 0; i < submenu.length(); i++) {
                        JSONObject itemJson = submenu.getJSONObject(i);
                        spinnerItems.add(new MenuBarItem(itemJson.getString("label"), itemJson.getString("url"), oMenu.getString("label")));
                    }

                    MenuBarSpinnerAdapter adapter = new MenuBarSpinnerAdapter(ab.getThemedContext(), spinnerItems);
                    mSpinner.setAdapter(adapter);
                } else {
                    mSpinner.setVisibility(View.GONE);
                    ab.setDisplayShowTitleEnabled(true);
                    ab.setTitle(oMenu.getString("label"));
                }
                if (bLoad)
                {
                    try {
                        loadUrl(configApp.urlHost + oMenu.getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            switch (key)
            {
                case "conversations":
                    mSpinner.setVisibility(View.GONE);
                    ab.setDisplayShowTitleEnabled(true);
                    ab.setTitle(getResources().getString(R.string.toolbar_title_message));
                    break;
                default:
                    mSpinner.setVisibility(View.GONE);
                    ab.setDisplayShowTitleEnabled(true);
                    ab.setTitle(getResources().getString(R.string.toolbar_title_home));
                    break;
            }
            if (prevMenuItem != null) {
                prevMenuItem.setChecked(false);
            }
            prevMenuItem = null;
        }
    }
    // End Tab
    public void showFullAds()
    {
        if (MooGlobals.getInstance().getsAdmodInterstitialId().isEmpty() || !MooGlobals.getInstance().getbAdFull())
        {
            return;
        }
        // Create the InterstitialAd and set the adUnitId.
        final InterstitialAd mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId(MooGlobals.getInstance().getsAdmodInterstitialId());

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });
    }
}
