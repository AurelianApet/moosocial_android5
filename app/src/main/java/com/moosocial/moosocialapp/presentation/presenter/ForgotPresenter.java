package com.moosocial.moosocialapp.presentation.presenter;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.SignupConfig;
import com.moosocial.moosocialapp.domain.interactor.ForgotResult;
import com.moosocial.moosocialapp.domain.interactor.GetSignupConfig;
import com.moosocial.moosocialapp.domain.interactor.SignupResult;
import com.moosocial.moosocialapp.presentation.view.activities.SignupActivity;
import com.moosocial.moosocialapp.presentation.view.items.util.SpinnerItem;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class ForgotPresenter extends AppPresenter
{
    private EditText eEmail;
    private TextView tEmailError;
    public ForgotPresenter(Activity activity) {
        super(activity);

        eEmail = (EditText)activity.findViewById(R.id.input_email);

        tEmailError = (TextView)activity.findViewById(R.id.email_error_message);
    }

    public void onForgot()
    {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        boolean valid = true;

        String email = eEmail.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tEmailError.setText(activity.getResources().getString(R.string.login_error_email));
            tEmailError.setVisibility(View.VISIBLE);
            valid = false;
        } else {
            tEmailError.setText(null);
            tEmailError.setVisibility(View.GONE);
        }

        if (valid) {
            ForgotResult sForgot = new ForgotResult(activity);
            sForgot.setData(email);
            sForgot.execute();
        }
    }
}
