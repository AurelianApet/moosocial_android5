package com.moosocial.moosocialapp.presentation.view.items.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;

import java.util.List;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 *
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class EmojiGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<EmojiItem> items;

    public EmojiGridViewAdapter(Context mContext, List<EmojiItem> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.emoji_item, null);

            EmojiItem item = (EmojiItem)this.getItem(position);
            TextView tText = (TextView)convertView.findViewById(R.id.text);
            tText.setText(new String(Character.toChars(Integer.parseInt(item.code,16))));
        }

        return convertView;
    }
}
