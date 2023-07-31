package com.moosocial.moosocialapp.presentation.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.presentation.view.items.user.UserListSelectAdapter;
import com.moosocial.moosocialapp.util.Mention.mentions.UserMention;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SelectFriendActivity extends MooActivity{
    public ActionBar ab;
    ArrayList<UserMention> friends = null;
    ListView lFriends;
    LinearLayout lListImage;
    UserListSelectAdapter listAdapter;
    ArrayList<UserMention> aUserSelect = new ArrayList<UserMention>();
    public static final String EXTRA_RESULT = "select_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        final Intent intent = getIntent();
        Gson gson = new Gson();
        friends = gson.fromJson(intent.getStringExtra("friends"),new TypeToken<ArrayList<UserMention>>(){}.getType());
        String sTitle = intent.getStringExtra("title");

        lListImage = (LinearLayout)findViewById(R.id.list_image);

        TextView tTitle = (TextView)findViewById(R.id.text_bar);
        tTitle.setText(sTitle);

        lFriends = (ListView)findViewById(R.id.list_user);

        listAdapter = new UserListSelectAdapter(this,friends);
        lFriends.setAdapter(listAdapter);
        lFriends.setClickable(true);
        lFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                UserMention item = (UserMention) parent.getAdapter().getItem(position);
                if (item.getChecked())
                {
                    item.setChecked(false);
                }
                else
                {
                    item.setChecked(true);
                }
                listAdapter.setItem(item,position);
                listAdapter.notifyDataSetChanged();

                updateListImage();
            }
        });

        EditText eSearch = (EditText)findViewById(R.id.suggest_search);
        eSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                String text = cs.toString().toLowerCase(Locale.getDefault());
                listAdapter.filter(text);

                lFriends.setSelectionAfterHeaderView();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        ImageView iBack = (ImageView)findViewById(R.id.btn_back);
        iBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button bDone = (Button)findViewById(R.id.done);
        bDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent data = new Intent();
                Gson gson = new Gson();
                data.putExtra(EXTRA_RESULT,gson.toJson(aUserSelect));
                setResult(RESULT_OK, data);
                finish();
            }
        });

        updateListImage();
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

    public void updateListImage()
    {
        lListImage.removeAllViews();
        ArrayList<UserMention> list = listAdapter.getReals();
        aUserSelect.clear();
        for(UserMention user: list){
            if (!user.getChecked())
                continue;

            aUserSelect.add(user);
            CircleImageView imageView = new CircleImageView(this);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(height, height);
            lp.setMargins(5, 5, 5, 5);
            imageView.setLayoutParams(lp);
            Glide.with(this).load(user.getAvatar())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            lListImage.addView(imageView);
        }
    }

}
