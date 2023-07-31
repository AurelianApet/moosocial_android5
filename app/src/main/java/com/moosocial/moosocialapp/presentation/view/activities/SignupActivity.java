package com.moosocial.moosocialapp.presentation.view.activities;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.moosocial.moosocialapp.MooApplication;
import com.moosocial.moosocialapp.R;
import com.moosocial.moosocialapp.domain.SignupConfig;
import com.moosocial.moosocialapp.presentation.presenter.SignupPresenter;
import com.moosocial.moosocialapp.presentation.view.items.util.KeyValueSpinnerAdapter;
import com.moosocial.moosocialapp.presentation.view.items.util.SpinnerItem;
import com.moosocial.moosocialapp.util.MooGlobals;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Copyright (c) SocialLOFT LLC
 * mooSocial - The Web 2.0 Social Network Software
 * @website: http://www.moosocial.com
 * @author: mooSocial
 * @license: https://moosocial.com/license/
 */
public class SignupActivity extends MooActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{
    public ActionBar ab;
    public SignupPresenter sSignupPresenter;
    public ProgressBar pProgressBar;
    public View vContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initBackground();

        pProgressBar = (ProgressBar)findViewById(R.id.signup_progress);
        vContent = (View) findViewById(R.id.signup_content);

        CheckBox checkbox = (CheckBox)findViewById(R.id.term);
        TextView textView = (TextView)findViewById(R.id.term_text);

        String url = "";
        try {
            url = MooGlobals.getInstance().getConfig().urlHost + MooGlobals.getInstance().getConfig().jListUrls.getString("term_service");
        } catch (JSONException e) {
        }

        checkbox.setText("");
        textView.setText(Html.fromHtml("<a href='" + url + "'>" + getResources().getString(R.string.term_service_text) + "</a>"));
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        sSignupPresenter = new SignupPresenter(this);
        sSignupPresenter.getConfig();

        ImageView iBack = (ImageView)findViewById(R.id.img_back);
        iBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            };
        });

        Button bSignUp = (Button)findViewById(R.id.signup);
        bSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sSignupPresenter.onSignup();
            };
        });
    }

    public void initLayout(SignupConfig sSignupConfig)
    {
        //Init gender
        Spinner sGender = (Spinner)findViewById(R.id.gender);
        ArrayList<SpinnerItem> aGender = new ArrayList<SpinnerItem>();
        aGender.add(new SpinnerItem("", getResources().getString(R.string.signup_gender)));
        aGender.add(new SpinnerItem("Male", getResources().getString(R.string.signup_gender_male)));
        aGender.add(new SpinnerItem("Female", getResources().getString(R.string.signup_gender_female)));
        if (sSignupConfig.getEnableUnspecifiedGender())
            aGender.add(new SpinnerItem("Unknown", getResources().getString(R.string.signup_gender_unknown)));

        KeyValueSpinnerAdapter adapter = new KeyValueSpinnerAdapter(this, aGender);
        sGender.setAdapter(adapter);

        //Init birthday
        TextView tBirthday = (TextView) findViewById(R.id.input_birthday);
        tBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            SignupActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setMaxDate(Calendar.getInstance());
                    dpd.setAccentColor(R.color.mdtp_accent_color);
                    dpd.setThemeDark(false);
                    dpd.vibrate(true);
                    dpd.dismissOnPause(false);
                    dpd.showYearPickerFirst(false);
                    dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            TextView tBirthday = (TextView) findViewById(R.id.input_birthday);
                            tBirthday.clearFocus();
                        }
                    });

                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            }
        });

        if (!sSignupConfig.getShowBirthdaySignup())
        {
            View vBirthdayContent = (View)findViewById(R.id.content_birthday);
            vBirthdayContent.setVisibility(View.GONE);
        }

        if (!sSignupConfig.getShowGenderSignup())
        {
            View vGenderContent = (View)findViewById(R.id.content_gender);
            vGenderContent.setVisibility(View.GONE);
        }

        hideLoading();

        if (sSignupConfig.getDisableRegistration())
        {
            ((LinearLayout)findViewById(R.id.signup_message_error)).setVisibility(View.VISIBLE);
            ((LinearLayout)findViewById(R.id.signup_content)).setVisibility(View.GONE);
        }
        else
        {
            Button bSignUp = (Button)findViewById(R.id.signup);
            bSignUp.setVisibility(View.VISIBLE);
        }
    }

    public void showLoading()
    {
        pProgressBar.setVisibility(View.VISIBLE);
        vContent.setVisibility(View.GONE);
    }

    public void hideLoading()
    {
        pProgressBar.setVisibility(View.GONE);
        vContent.setVisibility(View.VISIBLE);
    }

    public void showErrorLoadConfig()
    {
        pProgressBar.setVisibility(View.GONE);
        vContent.setVisibility(View.GONE);

        LinearLayout lMessage = (LinearLayout)findViewById(R.id.signup_message_error_getconfig);
        lMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");

        if(tpd != null) tpd.setOnTimeSetListener(this);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        TextView tBirthday = (TextView) findViewById(R.id.input_birthday);
        String sDate = (++monthOfYear)+"/"+dayOfMonth+"/"+year;
        tBirthday.setText(sDate);
        tBirthday.clearFocus();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }
}
