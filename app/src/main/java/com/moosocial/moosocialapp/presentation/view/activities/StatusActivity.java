package com.moosocial.moosocialapp.presentation.view.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.InputType;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.Me;
import com.moosocial.moosocialapp.presentation.presenter.StatusPresenter;
import com.moosocial.moosocialapp.presentation.view.items.emoji.EmojiGridViewAdapter;
import com.moosocial.moosocialapp.presentation.view.items.emoji.EmojiItem;
import com.moosocial.moosocialapp.util.Mention.mentions.MentionUsers;
import com.moosocial.moosocialapp.util.Mention.mentions.UserMention;
import com.moosocial.moosocialapp.util.Mention.suggestions.SuggestionsResult;
import com.moosocial.moosocialapp.util.Mention.tokenization.QueryToken;
import com.moosocial.moosocialapp.util.Mention.tokenization.interfaces.QueryTokenReceiver;
import com.moosocial.moosocialapp.util.Mention.ui.RichEditorView;
import com.moosocial.moosocialapp.util.MooGlobals;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.utils.FileUtils;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class StatusActivity extends MooActivity implements QueryTokenReceiver {
    public ActionBar ab;
    public RichEditorView statusText;
    public MentionUsers mUsers;
    public LinearLayout lListImage;
    public TextView tUserTag;
    private ArrayList<String> mSelectPath;
    private ArrayList<UserMention> friendSelect;
    private static final int REQUEST_IMAGE = 2;
    private static final int REQUEST_FRIEND = 99;
    private File mTmpFile;
    private static final int REQUEST_CAMERA = 100;
    private static final int LIMIT_PHOTO = 10;
    public ArrayList<UserMention> suggestions;
    public Integer iPrivacy = 0;
    public ImageView iIconPrivacy;
    public TextView tTextPrivacy;
    public Me mUser;
    public StatusPresenter sStatus;
    public String userShareLink = "";
    public LinearLayout lContentLink;
    public ImageView iLinkImage;
    public TextView tLinkTitle;
    public TextView tLinkDescription;
    public MaterialDialog.Builder dialogLink;
    PopupWindow listPopupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mUser = MooGlobals.getInstance().getMe();

        statusText = (RichEditorView) findViewById(R.id.editor);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(statusText, InputMethodManager.SHOW_IMPLICIT);

        statusText.setQueryTokenReceiver(this);
        statusText.setHint(getResources().getString(R.string.text_hint_what_new));
        statusText.setScrollView((ScrollView)findViewById(R.id.scrollView_content));
        statusText.setViewTop((LinearLayout)findViewById(R.id.status_content_top));

        suggestions = new ArrayList<UserMention>();

        ImageView iAvatar = (ImageView)findViewById(R.id.avatar_status);
        Object avatar = mUser.getAvatar();
        String url = (String) ((LinkedTreeMap) avatar).get("100");
        Glide.with(this).load(url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iAvatar);

        mUsers = new MentionUsers(suggestions);

        ImageButton iPicture = (ImageButton) findViewById(R.id.image_status_picture);
        iPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userShareLink.isEmpty())
                    return;

                MultiImageSelector selector = MultiImageSelector.create(StatusActivity.this);
                selector.showCamera(true);
                selector.count(LIMIT_PHOTO);
                selector.multi();
                selector.origin(mSelectPath);
                selector.start(StatusActivity.this, REQUEST_IMAGE);
            }
        });

        ImageButton iCamera = (ImageButton) findViewById(R.id.image_status_camera);
        iCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userShareLink.isEmpty())
                    return;
                showCameraAction();
            }
        });

        ImageButton iTag = (ImageButton) findViewById(R.id.image_status_tag);
        iTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectFriend();
            }
        });

        lListImage = (LinearLayout)findViewById(R.id.list_image);

        tUserTag = (TextView)findViewById(R.id.usertag);

        tUserTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendSelect != null && friendSelect.size() > 0)
                {
                    startSelectFriend();
                }
            }
        });

        initViewTagFriend();

        LinearLayout lPrivacyContent = (LinearLayout)findViewById(R.id.content_privacy);
        iIconPrivacy = (ImageView)findViewById(R.id.icon_privacy);
        tTextPrivacy = (TextView) findViewById(R.id.text_privacy);

        lPrivacyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> listPrivacy = new ArrayList<String>();
                listPrivacy.add(getResources().getString(R.string.text_privacy_public));
                listPrivacy.add(getResources().getString(R.string.text_privacy_friend));
                listPrivacy.add(getResources().getString(R.string.text_privacy_onlyme));

                final CharSequence[] charSequenceItems = listPrivacy.toArray(new CharSequence[listPrivacy.size()]);
                new MaterialDialog.Builder(StatusActivity.this)
                        .items(charSequenceItems)
                        .itemsCallbackSingleChoice(iPrivacy, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which != iPrivacy) {
                                    iPrivacy =  which;
                                    initViewPrivacy();
                                }
                                return true;
                            }
                        })
                        .show();
            }
        });

        ImageView iBack = (ImageView)findViewById(R.id.img_back);
        iBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sStatus = new StatusPresenter(this);
        sStatus.onCreate();

        mSelectPath = new ArrayList<>();

        Button bShare = (Button)findViewById(R.id.share);
        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sStatus.postStatus(statusText,iPrivacy,friendSelect,mSelectPath,userShareLink);
            }
        });

        dialogLink = new MaterialDialog.Builder(this)
                .inputType(InputType.TYPE_CLASS_TEXT )
                .autoDismiss(false)
                .positiveText(getResources().getString(R.string.text_ok))
                .input(getResources().getString(R.string.text_link_hint), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.length() == 0 || !URLUtil.isValidUrl(input.toString())) {
                            Toast.makeText(StatusActivity.this, R.string.text_link_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sStatus.setDialog(dialog);
                        sStatus.fetchLink(input.toString());
                    }
                }).negativeText(getResources().getString(R.string.text_cancel)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        ImageButton iLink = (ImageButton) findViewById(R.id.image_status_link);
        iLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLink.show();
            }
        });

        lContentLink = (LinearLayout) findViewById(R.id.content_link);
        iLinkImage = (ImageView) findViewById(R.id.link_image);
        tLinkTitle = (TextView) findViewById(R.id.link_title);
        tLinkDescription = (TextView) findViewById(R.id.link_description);

        ImageView iRemoveLink = (ImageView)findViewById(R.id.remove_link);
        iRemoveLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userShareLink = "";
                lContentLink.setVisibility(View.GONE);
            }
        });
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
                statusText.addText(item.text);
                listPopupWindow.dismiss();
            }
        });

        //listViewSort.setLayoutParams(new ViewGroup.LayoutParams(500,500));
        listPopupWindow.setContentView(gEmoji);
        listPopupWindow.setOutsideTouchable(true);

        final ImageView iEmoji = (ImageView)findViewById(R.id.image_status_emoji);
        iEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listPopupWindow.isShowing())
                    listPopupWindow.showAsDropDown(iEmoji);
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

    @Override
    public List<String> onQueryReceived(final @NonNull QueryToken queryToken) {
        List<String> buckets = Collections.singletonList("UserMention");
        List<UserMention> suggestions = mUsers.getSuggestions(queryToken);
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
        statusText.onReceiveSuggestionsResult(result, "UserMention");
        return buckets;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FRIEND)
        {
            if(resultCode == RESULT_OK){
                Gson gson = new Gson();
                friendSelect = gson.fromJson(data.getStringExtra(SelectFriendActivity.EXTRA_RESULT),new TypeToken<ArrayList<UserMention>>(){}.getType());

                initViewTagFriend();
            }
        }
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                initImageViewUpload();
            }
        }

        if(requestCode == REQUEST_CAMERA){
            if(resultCode == RESULT_OK) {
                if (mTmpFile != null) {
                    // notify system the image has change
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mTmpFile)));

                    mSelectPath.add(mTmpFile.getPath());
                    initImageViewUpload();
                }
            }else{
                // delete tmp file
                while (mTmpFile != null && mTmpFile.exists()){
                    boolean success = mTmpFile.delete();
                    if(success){
                        mTmpFile = null;
                    }
                }
            }
        }
    }

    public void showCameraAction()
    {
        if (mSelectPath.size() >= LIMIT_PHOTO)
        {
            Toast.makeText(StatusActivity.this, R.string.mis_msg_amount_limit, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                mTmpFile = FileUtils.createTmpFile(StatusActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                Toast.makeText(StatusActivity.this, R.string.mis_error_image_not_exist, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(StatusActivity.this, R.string.mis_msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    public void initImageViewUpload()
    {
        lListImage.removeAllViews();
        for(String path: mSelectPath){
            ImageView imageView = new ImageView(this);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(250,250));
            Glide.with(this)
                    .load(new File(path))
                    .centerCrop()
                    .thumbnail(0.5f)
                    .dontAnimate()
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            lListImage.addView(imageView);
        }
    }

    public void initViewTagFriend()
    {
        String result = "<b>"+mUser.getName()+"</b>";
        String color = "#005999";
        if (friendSelect != null && friendSelect.size() > 0)
        {
            Resources res = getResources();
            if (friendSelect.size() == 1)
            {
                result += " " + (res.getString(R.string.text_status_tag_one_friend) + " " + "<span style='color: "+color+";'>"+friendSelect.get(0).getName()+"</span>");
            }
            else if (friendSelect.size() == 2)
            {
                result += " " + (String.format(res.getString(R.string.text_status_tag_morefriend),"<span style='color: "+color+";'>"+friendSelect.get(0).getName()+"</span>","<span style='color: "+color+";'>"+friendSelect.get(1).getName()+"</span>"));
            }
            else
            {
                Integer size = friendSelect.size() - 1;
                result += " " + (String.format(res.getString(R.string.text_status_tag_morefriend),"<span style='color: "+color+";'>"+friendSelect.get(0).getName()+"</span>","<span style='color: "+color+";'>"+size.toString()+ " " + res.getString(R.string.text_others) +"</span>"));
            }

        }

        tUserTag.setText(Html.fromHtml(result));
    }

    public void initViewPrivacy()
    {
        switch (iPrivacy)
        {
            case 0:
                tTextPrivacy.setText(getResources().getString(R.string.text_privacy_public));
                iIconPrivacy.setImageResource(R.drawable.ic_privacy_public);
                break;
            case 1:
                tTextPrivacy.setText(getResources().getString(R.string.text_privacy_friend));
                iIconPrivacy.setImageResource(R.drawable.ic_privacy_friend);
                break;
            case 2:
                tTextPrivacy.setText(getResources().getString(R.string.text_privacy_onlyme));
                iIconPrivacy.setImageResource(R.drawable.ic_privacy_onlyme);
                break;
        }
    }

    public void startSelectFriend()
    {
        Intent iSelect = new Intent(StatusActivity.this, SelectFriendActivity.class);
        ArrayList<Integer> intList = new ArrayList<>();
        if (friendSelect != null && friendSelect.size() > 0)
        {
            for(UserMention user: friendSelect){
                intList.add(user.getId());
            }
        }

        ArrayList<UserMention> intListUser = new ArrayList<>();
        intListUser.addAll(suggestions);

        if (intListUser.size() > 0)
        {
            for (int index = 0;index<intListUser.size();index++)
            {
                intListUser.get(index).setChecked(false);
                if (intList.contains(intListUser.get(index).getId()))
                {
                    intListUser.get(index).setChecked(true);
                }
            }
        }

        Gson gson = new Gson();
        iSelect.putExtra("friends", gson.toJson(intListUser));
        iSelect.putExtra("title",getResources().getString(R.string.text_tag_friend)) ;
        startActivityForResult(iSelect,REQUEST_FRIEND);
    }

    public void addSuggests(ArrayList<UserMention> aMentions)
    {
        this.suggestions = aMentions;
        mUsers = new MentionUsers(aMentions);
    }

    public void afterFetch(Object response, String link) {
        userShareLink = link;
        lContentLink.setVisibility(View.VISIBLE);
        String image = (String) ((LinkedTreeMap) response).get("image");
        String title = (String) ((LinkedTreeMap) response).get("title");
        String description = (String) ((LinkedTreeMap) response).get("description");
        iLinkImage.setVisibility(View.GONE);
        if (!image.isEmpty())
        {
            iLinkImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(image)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(iLinkImage);
        }

        tLinkTitle.setText(title);
        tLinkDescription.setText(description);
    }

}
