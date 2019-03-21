package com.nguyendinhdoan.finalprojectgrabdriver.ui.phone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.nguyendinhdoan.finalprojectgrabdriver.R;
import com.nguyendinhdoan.finalprojectgrabdriver.ui.driver.DriverActivity;
import com.nguyendinhdoan.finalprojectgrabdriver.ui.login.LoginActivity;
import com.nguyendinhdoan.finalprojectgrabdriver.util.CommonUtils;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneActivity extends AppCompatActivity implements TextWatcher, PhoneContract.PhoneToView{

    public static final long VERIFY_PHONE_NUMBER_TIME_OUT = 30000;
    public static final long COUNT_DOWN_INTERVAL = 1000;

    @BindView(R.id.activity_phone_layout_phone)
    ConstraintLayout layoutPhone;
    @BindView(R.id.activity_phone_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_phone_tv_request_new_code)
    TextView tvRequestNewCode;
    @BindView(R.id.activity_phone_tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.activity_phone_et_verification_code)
    EditText etVerificationCode;
    @BindView(R.id.activity_phone_avl_loading)
    AVLoadingIndicatorView avlLoading;

    private PhoneContract.PhoneToPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.bind(this);

        setupUi();
        presenter = new PhonePresenter(this);
        presenter.sendVerificationCode(this, tvPhoneNumber.getText().toString().replace(" ", ""));
    }

    private void setupUi() {
        setupToolbar();
        setupTextView();
        addEvents();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupTextView() {
        Bundle bundleLogin = getIntent().getExtras();
        if (bundleLogin != null) {
            String phoneNumber = bundleLogin.getString(LoginActivity.LOGIN_USER_PHONE);
            if (phoneNumber != null && phoneNumber.charAt(0) == '0') {
                // sub string if first character is 0
                phoneNumber = phoneNumber.substring(1);
            }
            tvPhoneNumber.setText(getString(R.string.label_phone_number, phoneNumber));
        }
        // default
        tvRequestNewCode.setText(getString(R.string.label_request_a_new_code, String.valueOf(30)));

    }

    private void addEvents() {
        etVerificationCode.addTextChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                launchLogin();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchLogin() {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentLogin);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 6) {
            handleTimeout();

            // get login data
            Bundle bundleLogin = getIntent().getExtras();
            if (bundleLogin != null) {
                String fullName = bundleLogin.getString(LoginActivity.LOGIN_USER_FULL_NAME);
                String email = bundleLogin.getString(LoginActivity.LOGIN_USER_EMAIL);
                String phone =  tvPhoneNumber.getText().toString().replace(" ", "");
                String verificationCode = etVerificationCode.getText().toString();

                CommonUtils.closeKeyBoard(this);
                // handle event
                presenter.verifyCodeAndLoginWithCredential(verificationCode, fullName, email, phone);
            }
        }
    }

    private void handleTimeout() {
        new CountDownTimer(VERIFY_PHONE_NUMBER_TIME_OUT, COUNT_DOWN_INTERVAL) {
            int timeout = 30;
            @Override
            public void onTick(long millisUntilFinished) {
                tvRequestNewCode.setText(getString(R.string.label_request_a_new_code, String.valueOf(--timeout)));
            }

            @Override
            public void onFinish() {
                // TODO: Handle error send verification code
                tvRequestNewCode.setText((Html.fromHtml(getString(R.string.label_request_new_code))));
                tvRequestNewCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.sendVerificationCode(PhoneActivity.this, tvPhoneNumber.getText().toString().replace(" ", ""));
                        handleTimeout();
                    }
                });
            }
        }.start();
    }

    @Override
    public void onSendVerificationCodeSuccess(boolean isSendVerificationCodeSuccess) {

    }

    @Override
    public void onSendVerificationCodeFailed(String message) {
        errorMessageVerificationCode(message);
    }

    @Override
    public void onLoginWithCredentialSuccess(boolean isLoginWithCredentialSuccess) {
        if (isLoginWithCredentialSuccess) {
            launchDriver();
        }
    }

    @Override
    public void onLoginWithCredentialFailed(String message) {
        errorMessageVerificationCode(message);
    }

    @Override
    public void showLoading() {
        avlLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        avlLoading.setVisibility(View.GONE);
    }

    private void launchDriver() {
        Intent intentPassenger = new Intent(this, DriverActivity.class);
        intentPassenger.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentPassenger);
        finish();
    }

    private void errorMessageVerificationCode(String message) {
        Snackbar snackbar = Snackbar.make(layoutPhone, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        TextView content = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        content.setTextColor(Color.BLACK);
        snackbar.show();
    }

}
