package com.moosocial.moosocialapp.domain;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SignupConfig {
    protected Boolean require_gender;
    protected Boolean show_gender_signup;
    protected Boolean enable_unspecified_gender;
    protected Boolean birthday_require;
    protected Boolean show_birthday_signup;
    protected Boolean disable_registration;
    protected String key;

    public Boolean getRequireGender() {
        return require_gender;
    }

    public Boolean getShowGenderSignup() {
        return show_gender_signup;
    }

    public Boolean getEnableUnspecifiedGender() {
        return enable_unspecified_gender;
    }

    public Boolean getBirthdayRequire() {
        return birthday_require;
    }

    public Boolean getShowBirthdaySignup() {
        return show_birthday_signup;
    }

    public Boolean getDisableRegistration()
    {
        return disable_registration;
    }

    public String getKey()
    {
        return key;
    }
}
