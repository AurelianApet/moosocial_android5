package com.moosocial.moosocialapp.presentation.presenter;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.SignupConfig;
import com.moosocial.moosocialapp.domain.interactor.GetSignupConfig;
import com.moosocial.moosocialapp.domain.interactor.SignupResult;
import com.moosocial.moosocialapp.presentation.view.activities.SignupActivity;
import com.moosocial.moosocialapp.presentation.view.items.util.SpinnerItem;

import org.w3c.dom.Text;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SignupPresenter extends AppPresenter
{
    private EditText eEmail;
    private EditText eName;
    private EditText ePassword;
    private CheckBox cTerm;
    private Spinner sGender;
    private EditText eBirthday;

    private TextView tEmailError;
    private TextView tNameError;
    private TextView tPasswordError;
    private TextView tTermError;
    private TextView tGenderError;
    private TextView tBirthdayError;

    private SignupConfig sSignupConfig;

    public SignupPresenter(Activity activity) {
        super(activity);

        eEmail = (EditText)activity.findViewById(R.id.input_email);
        eName = (EditText)activity.findViewById(R.id.input_name);
        ePassword = (EditText)activity.findViewById(R.id.input_password);
        cTerm = (CheckBox)activity.findViewById(R.id.term);
        sGender = (Spinner)activity.findViewById(R.id.gender);
        eBirthday = (EditText)activity.findViewById(R.id.input_birthday);

        tEmailError = (TextView)activity.findViewById(R.id.email_error_message);
        tNameError = (TextView)activity.findViewById(R.id.name_error_message);
        tPasswordError = (TextView)activity.findViewById(R.id.password_error_message);
        tTermError = (TextView)activity.findViewById(R.id.term_error_message);
        tGenderError = (TextView)activity.findViewById(R.id.gender_error_message);
        tBirthdayError = (TextView)activity.findViewById(R.id.password_error_birthday);

    }

    public void setSignupConfig(SignupConfig sSignupConfig)
    {
        this.sSignupConfig = sSignupConfig;
    }

    public void getConfig()
    {
        GetSignupConfig gGetSignupConfig = new GetSignupConfig(activity,this);
        gGetSignupConfig.execute();
    }

    public void onSignup()
    {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        boolean valid = true;

        String email = eEmail.getText().toString().trim();
        String name = eName.getText().toString().trim();
        String password = ePassword.getText().toString().trim();
        Boolean term = cTerm.isChecked();
        String birthday = eBirthday.getText().toString().trim();
        SpinnerItem item = (SpinnerItem)sGender.getSelectedItem();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tEmailError.setText(activity.getResources().getString(R.string.login_error_email));
            tEmailError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            tEmailError.setText(null);
            tEmailError.setVisibility(View.GONE);
        }

        if (name.isEmpty()) {
            tNameError.setText(activity.getResources().getString(R.string.signup_error_name));
            tNameError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            tNameError.setText(null);
            tNameError.setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            tPasswordError.setText(activity.getResources().getString(R.string.login_error_password));
            tPasswordError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            tPasswordError.setText(null);
            tPasswordError.setVisibility(View.GONE);
        }

        if (sSignupConfig.getShowBirthdaySignup()) {
            if (sSignupConfig.getBirthdayRequire()) {
                if (birthday.isEmpty()) {
                    tBirthdayError.setText(activity.getResources().getString(R.string.signup_error_birthday));
                    tBirthdayError.setVisibility(View.VISIBLE);
                    valid = false;
                } else {
                    tBirthdayError.setText(null);
                    tBirthdayError.setVisibility(View.GONE);
                }
            }
        }

        if (sSignupConfig.getShowGenderSignup()) {
            if (sSignupConfig.getRequireGender()) {
                if (item.getValue().isEmpty())
                {
                    tGenderError.setText(activity.getResources().getString(R.string.signup_error_gender));
                    tGenderError.setVisibility(View.VISIBLE);
                    valid = false;
                }
                else
                {
                    tGenderError.setText(null);
                    tGenderError.setVisibility(View.GONE);
                }
            }
        }

        if (!term)
        {
            tTermError.setText(activity.getResources().getString(R.string.signup_error_term));
            tTermError.setVisibility(View.VISIBLE);
            valid = false;
        }
        else
        {
            tTermError.setText(null);
            tTermError.setVisibility(View.GONE);
        }
        if (valid) {
            SignupResult sSignup = new SignupResult(activity);
            sSignup.setData(email, name, password, item.getValue(),birthday);
            sSignup.execute();
        }
    }
}
