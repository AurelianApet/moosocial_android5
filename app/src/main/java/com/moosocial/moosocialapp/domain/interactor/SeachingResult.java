package com.moosocial.moosocialapp.domain.interactor;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.moosocial.moosocialapp.domain.ItemSearch;
import com.moosocial.moosocialapp.presentation.view.activities.MainActivity;
import com.moosocial.moosocialapp.presentation.view.activities.MooActivity;
import com.moosocial.moosocialapp.presentation.view.items.search.SearchExpandableListAdapter;
import com.moosocial.moosocialapp.presentation.view.items.search.SearchGroup;
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
public class SeachingResult extends UseCase {
    private MooApplication app;
    private String sKeySearch;
    private ExpandableListView expListView;
    private TextView tTextNoResult;
    private ProgressBar pProgressBar;
    private MooApi api ;
    @Override
    public void execute() {
        if (((MooActivity)aActivity).getToken() == null)
        {
            return;
        }
        String uri = String.format(api.URL_SEARCH  +"?access_token=%s",((MooActivity)aActivity).getToken().getAccess_token());
        GsonRequest<ItemSearch[]> gsObjRequest = new GsonRequest<ItemSearch[]>(Request.Method.POST,uri,ItemSearch[].class,null,
                new Response.Listener<ItemSearch[]>() {
                    @Override
                    public void onResponse(ItemSearch[] response) {
                        pProgressBar.setVisibility(View.GONE);
                        if (response.length == 0)
                        {
                            tTextNoResult.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            tTextNoResult.setVisibility(View.GONE);
                            expListView.setVisibility(View.VISIBLE);
                            expListView.setDivider(null);

                            final ArrayList<SearchGroup> listDataGroup = new ArrayList<SearchGroup>();
                            ArrayList<String> exitsGroup = new ArrayList<String>();
                            HashMap<String,List<ItemSearch>> itemGroup = new HashMap<String,List<ItemSearch>>();
                            int i;
                            for (i = 0; i < response.length; i++)
                            {
                                ItemSearch item = response[i];
                                if (!exitsGroup.contains(item.getType()))
                                {
                                    String url = MooGlobals.getInstance().getConfig().urlHost + "/search/suggestion/"+item.getType().toLowerCase()+"/" + sKeySearch;
                                    listDataGroup.add(new SearchGroup(item.getType(),url));
                                    exitsGroup.add(item.getType());
                                }
                                List<ItemSearch> tmp;
                                if (itemGroup.get(item.getType()) == null)
                                {
                                    tmp = new ArrayList<ItemSearch>();
                                    itemGroup.put(item.getType(),tmp);
                                }
                                else
                                {
                                   tmp = itemGroup.get(item.getType());

                                }
                                tmp.add(item);
                            }
                            HashMap<String, List<ItemSearch>> listDataChild = new HashMap<String, List<ItemSearch>>();
                            for(Map.Entry<String, List<ItemSearch>> entry: itemGroup.entrySet()) {
                                String key = entry.getKey();
                                List<ItemSearch> value = entry.getValue();
                                listDataChild.put(key,value);
                            }
                            final SearchExpandableListAdapter listAdapter = new SearchExpandableListAdapter(aActivity, listDataGroup, listDataChild);

                            expListView.setAdapter(listAdapter);

                            for(i=0; i < listAdapter.getGroupCount(); i++) {
                                expListView.expandGroup(i);
                            }

                            if (expListView.getFooterViewsCount() == 0) {
                                View footerView = aActivity.getLayoutInflater().inflate(R.layout.search_footer, null);
                                expListView.addFooterView(footerView);
                                footerView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        // Perform action on click
                                        Intent mainIntent = new Intent(aActivity.getBaseContext(), MainActivity.class);
                                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mainIntent.putExtra("url", MooGlobals.getInstance().getConfig().urlHost + "/search/index/" + sKeySearch);
                                        aActivity.startActivity(mainIntent);
                                        aActivity.finish();
                                    }
                                });
                            }

                            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                                public boolean onChildClick(ExpandableListView parent, View v,
                                                            int groupPosition, int childPosition, long id) {
                                    ItemSearch selected = (ItemSearch) listAdapter.getChild(groupPosition,childPosition);
                                    Intent mainIntent = new Intent(aActivity.getBaseContext(), MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mainIntent.putExtra("url", selected.getUrl());
                                    aActivity.startActivity(mainIntent);
                                    aActivity.finish();
                                    return true;
                                }
                            });

                            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                                @Override
                                public boolean onGroupClick(ExpandableListView parent, View v,
                                                            int groupPosition, long id) {
                                    SearchGroup group = listDataGroup.get(groupPosition);
                                    Intent mainIntent = new Intent(aActivity.getBaseContext(), MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mainIntent.putExtra("url", group.getUrl());
                                    aActivity.startActivity(mainIntent);
                                    aActivity.finish();
                                    return true; // This way the expander cannot be collapsed
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
                            tTextNoResult.setVisibility(View.VISIBLE);
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
                params.put("keyword",sKeySearch);
                params.put("language",((MooActivity)aActivity).getLanguageCode());
                return params;
            }
        };
        MooGlobals.getInstance().getRequestQueue().add(gsObjRequest);
    }
    public SeachingResult(MooApplication app, Activity aActivity){
        super(aActivity);
        this.app = app;
    }

    public void setKeySearch(String sKeySearch)
    {
        this.sKeySearch = sKeySearch;
    }

    public void setExpandableListView(ExpandableListView expListView)
    {
        this.expListView = expListView;
    }

    public void setProgressBar(ProgressBar pProgressBar)
    {
        this.pProgressBar = pProgressBar;
    }

    public void setTextNoResult(TextView tTextNoResult)
    {
        this.tTextNoResult = tTextNoResult;
    }
}
