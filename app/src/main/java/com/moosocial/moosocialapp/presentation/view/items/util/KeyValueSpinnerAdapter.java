package com.moosocial.moosocialapp.presentation.view.items.util;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;

import java.util.ArrayList;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class KeyValueSpinnerAdapter implements SpinnerAdapter {
    Context context;
    ArrayList<SpinnerItem> aListItem;

    public KeyValueSpinnerAdapter(Context context ,ArrayList<SpinnerItem> aListItem){
        this.context =context;
        this.aListItem = aListItem;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return aListItem.size();
    }

    @Override
    public SpinnerItem getItem(int position) {
        // TODO Auto-generated method stub
        return aListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
        if (position == 0) {//Special style for dropdown header
            textview.setTextColor(context.getResources().getColor(R.color.spin_text_no_select_color));
        }
        textview.setText(aListItem.get(position).getText());

        return textview;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textview = (TextView) inflater.inflate(android.R.layout.simple_spinner_item, null);
        textview.setText(aListItem.get(position).getText());

        if (position == 0) {//Special style for dropdown header
            textview.setTextColor(context.getResources().getColor(R.color.spin_text_no_select_color));
        }

        textview.setPadding(10,0,0,0);

        return textview;
    }
}
