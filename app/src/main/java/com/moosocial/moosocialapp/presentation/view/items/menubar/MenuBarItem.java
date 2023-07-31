package com.moosocial.moosocialapp.presentation.view.items.menubar;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class MenuBarItem {
    public String title;
    public String url;
    public String parentTitle;

    public MenuBarItem(String title, String url, String parentTitle)
    {
        this.title = title;
        this.url = url;
        this.parentTitle = parentTitle;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getParentTitle()
    {
        return this.parentTitle;
    }

    public String getUrl()
    {
        return this.url;
    }
}
