package com.moosocial.moosocialapp.presentation.model;


import com.moosocial.moosocialapp.MooApplication;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class AppModel implements MooModel {
    protected MooApplication app;
    public AppModel(MooApplication app) {
        this.app = app;
    }
}
