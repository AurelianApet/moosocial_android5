package com.moosocial.moosocialapp.presentation.view.items.menubar;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;

public class MenuBarOnItemSelectedListener implements OnItemSelectedListener {
    private Activity aActivity;
    private Boolean bFirst = true;
    public MenuBarOnItemSelectedListener(Activity aActivity)
    {
        super();
        this.aActivity = aActivity;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        MenuBarItem item = (MenuBarItem) parent.getItemAtPosition(pos);
        TextView textView = (TextView) view.findViewById(R.id.spinner_item_text);
        if (!bFirst) {
            Log.wtf("aaaaa", "1");
            textView.setText(item.getTitle());
        }
        else
        {
            bFirst = false;
            textView.setText(item.getParentTitle());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}