package com.moosocial.moosocialapp.presentation.view.items.util;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SpinnerItem {
    private String sText;
    private String sValue;

    public SpinnerItem(String sValue,String sText)
    {
        this.sValue = sValue;
        this.sText = sText;
    }

    public String getText()
    {
        return this.sText;
    }

    public String getValue()
    {
        return this.sValue;
    }
}
