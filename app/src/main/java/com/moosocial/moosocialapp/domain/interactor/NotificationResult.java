package com.moosocial.moosocialapp.domain.interactor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.data.net.GsonRequest;
import com.moosocial.moosocialapp.data.net.MooApi;
import com.moosocial.moosocialapp.domain.Error;
import com.moosocial.moosocialapp.domain.ItemNotification;
import com.moosocial.moosocialapp.presentation.view.activities.MainActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.presentation.view.items.notification.NotificationListViewAdapter;
import com.moosocial.moosocialapp.util.MooGlobals;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class NotificationResult extends UseCase {
    private MooApplication app;
    private NotificationListViewAdapter listAdapter;
    private ListView lListView;
    private TextView tTextNoResult;
    private ProgressBar pProgressBar;
    private MooApi api ;
    @Override
    public void execute() {
        if (((MooActivity)aActivity).getToken() == null)
        {
            return;
        }
        String uri = String.format(api.URL_LIST_NOTIFICATION + "?access_token=%s&language=%s",((MooActivity)aActivity).getToken().getAccess_token(),((MooActivity)aActivity).getLanguageCode());
        GsonRequest<ItemNotification[]> gsObjRequest = new GsonRequest<ItemNotification[]>(Request.Method.GET,uri,ItemNotification[].class,null,
                new Response.Listener<ItemNotification[]>() {
                    @Override
                    public void onResponse(ItemNotification[] response) {
                        pProgressBar.setVisibility(View.GONE);
                        final RelativeLayout rLoading = (RelativeLayout)aActivity.findViewById(R.id.content_loading);
                        rLoading.setVisibility(View.VISIBLE);

                        if (response.length == 0)
                        {
                            tTextNoResult.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            rLoading.setVisibility(View.GONE);
                            List<ItemNotification> items = new ArrayList<ItemNotification>();
                            int i;
                            for (i = 0; i < response.length; i++)
                            {
                                items.add(response[i]);
                            }
                            listAdapter = new NotificationListViewAdapter(aActivity,items);
                            lListView.setAdapter(listAdapter);

                            View footerView = aActivity.getLayoutInflater().inflate(R.layout.notification_footer, null);
                            lListView.addFooterView(footerView);
                            footerView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    // Perform action on click
                                    try {
                                        Intent mainIntent = new Intent(aActivity.getBaseContext(), MainActivity.class);
                                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mainIntent.putExtra("url", MooGlobals.getInstance().getConfig().urlHost + MooGlobals.getInstance().getConfig().jListUrls.getString("all_notification"));
                                        aActivity.startActivity(mainIntent);
                                        aActivity.finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            lListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position,
                                                        long id) {
                                    ItemNotification item = (ItemNotification) parent.getAdapter().getItem(position);
                                    Intent mainIntent = new Intent(aActivity.getBaseContext(), MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mainIntent.putExtra("url", item.getLink());
                                    aActivity.startActivity(mainIntent);
                                    aActivity.finish();
                                }
                            });

                            lListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(final AdapterView<?> arg0, final View arg1,
                                                               final int arg2, long arg3) {
                                    final ItemNotification item = (ItemNotification) arg0.getAdapter().getItem(arg2);
                                    List<String> listItems = new ArrayList<String>();
                                    if (item.getUnread()) {
                                        listItems.add(aActivity.getResources().getString(R.string.action_mark_as_read));
                                    }
                                    listItems.add(aActivity.getResources().getString(R.string.action_delete));

                                    final CharSequence[] charSequenceItems = listItems.toArray(new CharSequence[listItems.size()]);

                                    new MaterialDialog.Builder(new ContextThemeWrapper(aActivity, R.style.AppThemeMaterialDialog))
                                            .items(charSequenceItems)
                                            .itemsCallback(new MaterialDialog.ListCallback() {
                                                @Override
                                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                    if (item.getUnread() && which == 0 ) {
                                                        item.setUnread(false);
                                                        LinearLayout lNotification = (LinearLayout)arg0.findViewById(R.id.item);
                                                        lNotification.setBackgroundColor(Color.TRANSPARENT);
                                                        markReadNotify(item.getId());
                                                    }
                                                    else
                                                    {
                                                        deleteNotify(item.getId());
                                                        listAdapter.remove(item);
                                                        lListView.setAdapter(listAdapter);
                                                        if (listAdapter.getCount() == 0)
                                                        {
                                                            tTextNoResult.setVisibility(View.VISIBLE);
                                                            rLoading.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                }
                                            })
                                            .show();

                                    return true;

                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:

                            Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
                            //json = trimMessage(json, "message");
                            //if(json != null) displayMessage(json);
                            Toast.makeText(app.getApplicationContext(), err.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                        case 500:
                            onRefesh();
                            break;
                        case 404:
                            pProgressBar.setVisibility(View.GONE);
                            final RelativeLayout rLoading = (RelativeLayout)aActivity.findViewById(R.id.content_loading);
                            rLoading.setVisibility(View.VISIBLE);
                            tTextNoResult.setVisibility(View.VISIBLE);
                            break;
                    }
                }else{
                    Log.e("moodebug", "Something went wrong!", error);
                }
            }
        });
        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }
    public NotificationResult(MooApplication app, Activity aActivity){
        super(aActivity);
        this.app = app;
    }

    public void setListView(ListView lListView)
    {
        this.lListView = lListView;
    }

    public void setProgressBar(ProgressBar pProgressBar)
    {
        this.pProgressBar = pProgressBar;
    }

    public void setTextNoResult(TextView tTextNoResult)
    {
        this.tTextNoResult = tTextNoResult;
    }

    public void markReadNotify(String id)
    {
        String uri = String.format(api.URL_NOTIFICATION_MARK_READ + id +"?access_token=%s&language=%s",((MooActivity)aActivity).getToken().getAccess_token(),((MooActivity)aActivity).getLanguageCode());
        GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.POST,uri,Object.class,null,
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:

                            Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
                            //json = trimMessage(json, "message");
                            //if(json != null) displayMessage(json);
                            Toast.makeText(app.getApplicationContext(), err.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }else{
                    Log.e("moodebug", "Something went wrong!", error);
                }
            }
        },new GsonBuilder().create()){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("unread","1");
                return params;
            }
        };
        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }

    public void deleteNotify(final String id)
    {
        String uri = String.format(api.URL_NOTIFICATION_DELETE +"?access_token=%s&language=%s",((MooActivity)aActivity).getToken().getAccess_token(),((MooActivity)aActivity).getLanguageCode());
        GsonRequest<Object> gsObjRequest = new GsonRequest<Object>(Request.Method.POST,uri,Object.class,null,
                new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:

                            Error err = MooGlobals.getInstance().getGson().fromJson(new String(response.data), Error.class);
                            //json = trimMessage(json, "message");
                            //if(json != null) displayMessage(json);
                            Toast.makeText(app.getApplicationContext(), err.getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                }else{
                    Log.e("moodebug", "Something went wrong!", error);
                }
            }
        },new GsonBuilder().create()){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("id",id);
                return params;
            }
        };
        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }
}
