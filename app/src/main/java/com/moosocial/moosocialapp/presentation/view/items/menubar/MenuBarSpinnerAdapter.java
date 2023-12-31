package com.moosocial.moosocialapp.presentation.view.items.menubar;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;

import java.util.List;

/**
 * Created by wins7 on 09/06/2015.
 */
public class MenuBarSpinnerAdapter extends BaseAdapter
{
    private Context mContext;
    private List<MenuBarItem> mValuesList;
    private Boolean bFirst = true;

    public MenuBarSpinnerAdapter(Context mContext, List<MenuBarItem> mValuesList)
    {
        this.mContext = mContext;
        this.mValuesList = mValuesList;
    }



    @Override
    public int getCount()
    {
        return mValuesList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mValuesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent)
    {
        if (view == null || !view.getTag().toString().equals("DROPDOWN"))
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.custom_spinner_dropdown_item, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(R.id.spinner_item_text);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN"))
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.custom_spinner_toolbar, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(R.id.spinner_item_text);
        if (bFirst)
        {
            textView.setText(getParentTitle(position));
            bFirst = false;
        }
        else
        {
            textView.setText(getTitle(position));
        }
        return view;
    }

    private String getTitle(int position)
    {
        return position >= 0 && position < mValuesList.size() ?   mValuesList.get(position).getTitle() : "";
    }

    private String getParentTitle(int position)
    {
        return position >= 0 && position < mValuesList.size() ?   mValuesList.get(position).getParentTitle() : "";
    }
}
