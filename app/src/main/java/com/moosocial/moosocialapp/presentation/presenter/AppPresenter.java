package com.moosocial.moosocialapp.presentation.presenter;

import android.app.Activity;

import java.util.Objects;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public abstract class AppPresenter implements Presenter {
    protected Activity activity;
    protected Object model;
    public AppPresenter(Activity activity) {
        this.activity = activity;
    }



    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
