package com.moosocial.moosocialapp.presentation.model;

import com.moosocial.moosocialapp.MooApplication;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class NotificationModel extends AppModel  {
    private String id;
    private String url;
    public NotificationModel(MooApplication app, String id, String url)
    {
        super(app);
        this.id = id;
        this.url = url;
    }

    public String getId()
    {
        return this.id;
    }

    public String getUrl()
    {
        return this.url;
    }
}
