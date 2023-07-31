package com.moosocial.moosocialapp.presentation.view.items.notification;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.ItemNotification;
import com.moosocial.moosocialapp.util.MooGlobals;

import java.util.List;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class NotificationListViewAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ItemNotification> items;
    public NotificationListViewAdapter(Activity activity, List<ItemNotification> items) {
        this.activity = activity;
        this.items = items;
    }

    public void remove(ItemNotification item)
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
            convertView = inflater.inflate(R.layout.notification_item, null);

            ItemNotification item = (ItemNotification)this.getItem(position);
            //linearLayout
            LinearLayout lNotification = (LinearLayout)convertView.findViewById(R.id.item);
            if (item.getUnread()) {
                lNotification.setBackgroundColor(Color.parseColor("#f1f1f1"));
            }

            //title
            /*TextView tTitle = (TextView) convertView
                    .findViewById(R.id.item_title);
            tTitle.setText(item.getFromName());*/

            //time
            TextView tTime = (TextView) convertView
                    .findViewById(R.id.item_time);
            tTime.setText(item.getCreatedTime());

            //text
            TextView tText = (TextView) convertView
                    .findViewById(R.id.item_text);
            String sTitle = "<b>" + item.getFromName() + "</b>" + " " + item.getTitle();
            tText.setText(Html.fromHtml(sTitle));

            //avatar
            ImageView mImageView = (ImageView) convertView
                    .findViewById(R.id.imageItem);

            String url = item.getFromAvatar();

            Glide.with(activity).load(url)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
        }

        return convertView;
    }
}
