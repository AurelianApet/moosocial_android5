package com.moosocial.moosocialapp.util.Mention.mentions;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */

public class UserMention implements Mentionable {

    protected int id;
    protected String name;
    protected String avatar;
    protected Boolean checked;

    public UserMention(int id , String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.checked = false;
    }

    public int getId()
    {
        return this.id;
    }

    public String getAvatar()
    {
        return this.avatar;
    }

    public String getName() {
        return name;
    }

    public Boolean getChecked()
    {
        return checked;
    }

    public void setChecked(Boolean checked)
    {
        this.checked = checked;
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return this.name;
            case PARTIAL:
            case NONE:
            default:
                return "";
        }
    }

    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return this.id;
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
    }
}

