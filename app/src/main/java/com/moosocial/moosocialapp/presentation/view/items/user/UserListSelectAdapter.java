package com.moosocial.moosocialapp.presentation.view.items.user;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.util.Mention.mentions.UserMention;
import com.moosocial.moosocialapp.util.MooGlobals;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 *
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class UserListSelectAdapter extends BaseAdapter  {
    private LayoutInflater inflater;
    private ArrayList<UserMention> items;
    private ArrayList<UserMention> reals = new ArrayList<>();
    private Activity activity;
    public UserListSelectAdapter(Activity activity,ArrayList<UserMention> items) {
        this.items = items;
        this.reals.addAll(items);
        this.activity = activity;
    }

    public ArrayList<UserMention> getReals()
    {
        return this.reals;
    }

    public void remove(UserMention item)
    {
        items.remove(item);
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_user_select, null);
        }

        UserMention item = (UserMention)this.getItem(position);
        //time
        ImageView iAvatar = (ImageView) convertView
                .findViewById(R.id.imageItem);

        Glide.with(activity).load(item.getAvatar())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(iAvatar);

        TextView tTtile = (TextView) convertView
                .findViewById(R.id.item_title);

        tTtile.setText(item.getName());

        ImageView iChecked = (ImageView) convertView
                .findViewById(R.id.checked);
        iChecked.setColorFilter(ContextCompat.getColor(activity, R.color.bar_menu_top_color));
        if (item.getChecked())
        {
            tTtile.setTextColor(ContextCompat.getColor(activity, R.color.bar_menu_top_color));
            iChecked.setVisibility(View.VISIBLE);
        }
        else
        {
            tTtile.setTextColor(ContextCompat.getColor(activity, R.color.jumbo));
            iChecked.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setItem(UserMention item,int postion)
    {
        this.items.set(postion,item);
        int i;
        for (i = 0;i<reals.size();i++)
        {
            if (reals.get(i).getId() == item.getId()) {
                reals.set(i,item);
                break;
            }
        }
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(reals);
        }
        else
        {
            for (UserMention uMention : reals)
            {
                if (uMention.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    items.add(uMention);
                }
            }
        }
        notifyDataSetChanged();
    }
}