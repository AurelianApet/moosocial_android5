package com.moosocial.moosocialapp.presentation.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.internal.LinkedTreeMap;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.data.net.MooApi;
import com.moosocial.moosocialapp.domain.interactor.CommentResult;
import com.moosocial.moosocialapp.domain.interactor.FetchLinkResult;
import com.moosocial.moosocialapp.domain.interactor.GetListFriend;
import com.moosocial.moosocialapp.domain.interactor.StatusResult;
import com.moosocial.moosocialapp.presentation.view.activities.BaseMooActivityHasWebView;
import com.moosocial.moosocialapp.presentation.view.activities.MainActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.presentation.view.activities.StatusActivity;
import com.moosocial.moosocialapp.util.Mention.mentions.MentionSpan;
import com.moosocial.moosocialapp.util.Mention.mentions.MentionsEditable;
import com.moosocial.moosocialapp.util.Mention.mentions.UserMention;
import com.moosocial.moosocialapp.util.Mention.ui.RichEditorView;
import com.moosocial.moosocialapp.util.UtilsMoo;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class StatusPresenter extends AppPresenter
{
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_MESSAGE_TEXT = "message_text";
    public static final String EXTRA_PRIVACY = "privacy";
    public static final String EXTRA_FRIEND = "friend";
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_LINK = "link";
    public MaterialDialog dialog;
    public GetListFriend gList;
    public StatusPresenter(Activity activity) {
        super(activity);
        gList = new GetListFriend(activity,this);
    }

    public void onCreate()
    {
        gList.execute();
    }

    public void onSuccess(Object oResults) {
        ArrayList<Object> aFriends = (ArrayList<Object>) ((LinkedTreeMap) oResults).get("friends");
        ArrayList<UserMention> aMentions = new ArrayList<UserMention>();
        for (Object user : aFriends)
        {
            LinkedTreeMap tmp = (LinkedTreeMap)user;
            Object avatar = tmp.get("image");
            String url = (String) ((LinkedTreeMap) avatar).get("50_square");
            aMentions.add(new UserMention(Integer.valueOf((String)tmp.get("id")),(String)tmp.get("name"),url));
        }
        if (activity.getClass().getSimpleName().equals("StatusActivity"))
            ((StatusActivity)activity).addSuggests(aMentions);
        else if (activity.getClass().getSimpleName().equals("MainActivity"))
            ((MainActivity)activity).addSuggests(aMentions);
    }

    public void postStatus(RichEditorView statusText, Integer iPrivacy, ArrayList<UserMention> friendSelect, ArrayList<String> mSelectPath, String userShareLink) {
        iPrivacy++;
        String sFriends = "";
        String sImages = "";
        List<String> lTmp = new ArrayList<>();

        if (friendSelect != null && friendSelect.size() > 0)
        {
            for (UserMention user : friendSelect)
            {
                lTmp.add(String.valueOf(user.getId()));
            }

            sFriends = TextUtils.join(",", lTmp);
        }

        MentionsEditable mEdit = statusText.getText();
        List<MentionSpan> aMentions = statusText.getMentionSpans();
        String sMessage = mEdit.toString();
        String sMessageText = mEdit.toString();
        int tmp = 0;
        if (aMentions.size() > 0) {
            for (MentionSpan mUser : aMentions) {
                UserMention uUser = (UserMention)mUser.getMention();
                int start = mEdit.getSpanStart(mUser) + tmp;
                int end = mEdit.getSpanStart(mUser) + tmp;
                String sTmp = "@[" + uUser.getId() + ":"+uUser.getName()+"]";
                sMessage = sMessage.substring(0, start) + sTmp + sMessage.substring(end + uUser.getName().length());
                tmp += sTmp.length() - uUser.getName().length();
            }
        }

        lTmp = new ArrayList<>();
        if (mSelectPath != null && mSelectPath.size() > 0)
        {
            for (String image : mSelectPath)
            {
                lTmp.add(String.valueOf(image));
            }

            sImages = TextUtils.join(",", lTmp);
        }

        if (statusText.getText().trim().toString().isEmpty() && mSelectPath.size() == 0 )
        {
            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.text_error_share_status), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Intent mainIntent = new Intent(activity.getBaseContext(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra(StatusPresenter.EXTRA_PRIVACY,iPrivacy);
        mainIntent.putExtra(StatusPresenter.EXTRA_MESSAGE,sMessage);
        mainIntent.putExtra(StatusPresenter.EXTRA_MESSAGE_TEXT,sMessageText);
        mainIntent.putExtra(StatusPresenter.EXTRA_FRIEND,sFriends);
        mainIntent.putExtra(StatusPresenter.EXTRA_IMAGE,sImages);
        mainIntent.putExtra(StatusPresenter.EXTRA_STATUS,true);
        mainIntent.putExtra(StatusPresenter.EXTRA_LINK,userShareLink);

        activity.startActivity(mainIntent);
        activity.finish();
    }

    public void postStatusServer(Integer iPrivacy, String sMessage, String sMessageText, String sFriends, ArrayList<String> sImages, String sLink)
    {
        StatusResult statusResult = new StatusResult(activity);
        statusResult.execute(iPrivacy, sMessage, sMessageText, sFriends, sImages,sLink,"User","0");
    }

    public void fetchLink(String sLink)
    {
        FetchLinkResult fetchLinkResult = new FetchLinkResult(activity);
        fetchLinkResult.execute(sLink,dialog);
    }

    public void postComment(RichEditorView statusText, final String sImage, final String sCommentType, final String sCommentId) {
        if (statusText.getText().trim().toString().isEmpty() && sImage.isEmpty() )
        {
            Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.text_error_comment), Toast.LENGTH_SHORT);
            toast.show();
            ((BaseMooActivityHasWebView)activity).doPostComment = false;
            return;
        }

        MentionsEditable mEdit = statusText.getText();
        List<MentionSpan> aMentions = statusText.getMentionSpans();
        String sMessage = mEdit.toString();
        int tmp = 0;
        if (aMentions.size() > 0) {
            for (MentionSpan mUser : aMentions) {
                UserMention uUser = (UserMention)mUser.getMention();
                int start = mEdit.getSpanStart(mUser) + tmp;
                int end = mEdit.getSpanStart(mUser) + tmp;
                String sTmp = "@[" + uUser.getId() + ":"+uUser.getName()+"]";
                sMessage = sMessage.substring(0, start) + sTmp + sMessage.substring(end + uUser.getName().length());
                tmp += sTmp.length() - uUser.getName().length();
            }
        }
        final String sMessageText = sMessage;

        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        final CommentResult commentResult = new CommentResult(activity);
        if (!sImage.isEmpty())
        {
            TextHttpResponseHandler handlerUpload = new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                        Toast toast = Toast.makeText(activity, activity.getResources().getString(R.string.text_error_post_image), Toast.LENGTH_SHORT);
                        toast.show();
                        ((BaseMooActivityHasWebView)activity).doPostComment = false;
                    }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jObject = new JSONObject(responseString);
                        commentResult.execute(sMessageText,jObject.getString("photo"),sCommentType,sCommentId);
                    }
                    catch (Exception e)
                    {

                    }
                }
            };

            RequestParams params = new RequestParams();
            String token = ((MooActivity)activity).getToken().getAccess_token();
            try {
                File compressedImage = UtilsMoo.saveBitmapToFile(new File(sImage));
                params.put("qqfile", compressedImage);
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(MooApi.URL_FILE + "?access_token=" + token, params, handlerUpload);
            }catch (Exception e)
            {

            }
        }
        else
        {
            commentResult.execute(sMessageText,sImage,sCommentType,sCommentId);
        }
    }

    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }
}
