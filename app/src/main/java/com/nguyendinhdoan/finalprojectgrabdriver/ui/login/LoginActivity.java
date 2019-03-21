package com.nguyendinhdoan.finalprojectgrabdriver.ui.login;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.nguyendinhdoan.finalprojectgrabdriver.R;
import com.nguyendinhdoan.finalprojectgrabdriver.ui.driver.DriverActivity;
import com.nguyendinhdoan.finalprojectgrabdriver.ui.phone.PhoneActivity;
import com.nguyendinhdoan.finalprojectgrabdriver.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginToView,
        TextWatcher, TextView.OnEditorActionListener{

    public static final String LOGIN_USER_FULL_NAME = "LOGIN_USER_FULL_NAME";
    public static final String LOGIN_USER_EMAIL = "LOGIN_USER_EMAIL";
    public static final String LOGIN_USER_PHONE = "LOGIN_USER_PHONE";

    @BindView(R.id.layout_login)
    ConstraintLayout layoutLogin;
    @BindView(R.id.activity_login_layout_email)
    TextInputLayout layoutEmail;
    @BindView(R.id.activity_login_layout_phone)
    TextInputLayout layoutPhone;
    @BindView(R.id.activity_login_layout_full_name)
    TextInputLayout layoutFullName;
    @BindView(R.id.activity_login_et_email)
    TextInputEditText etEmail;
    @BindView(R.id.activity_login_et_phone)
    TextInputEditText etPhone;
    @BindView(R.id.activity_login_et_full_name)
    TextInputEditText etFullName;

    LoginContract.LoginToPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        addEvents();
        presenter = new LoginPresenter(this);
    }

    private void addEvents() {
        etEmail.addTextChangedListener(this);
        etPhone.addTextChangedListener(this);
        etFullName.addTextChangedListener(this);
        // imeOption: actionSend --> handleLogin
        etPhone.setOnEditorActionListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.isLoggedIn();
    }

    @OnClick(R.id.activity_login_btn_login)
    void handleLogin() {
        String email = etEmail.getText().toString();
        String password = etPhone.getText().toString();
        String fullName = etFullName.getText().toString();
        presenter.validateLoginFields(this, email, password, fullName);
    }

    @Override
    public void onValidateError(String message) {
        Snackbar.make(layoutLogin, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onValidateResponse(boolean isValidateSuccess) {
        if (isValidateSuccess) {
            launchVerifyPhone();
        }
    }

    @Override
    public void isLoggedIn(boolean isLoggedIn) {
        if(isLoggedIn) {
            launchDriver();
        }
    }

    private void launchVerifyPhone() {
        Intent intentPhone = new Intent(this, PhoneActivity.class);
        // put login user data in bundle
        Bundle bundlePhone = new Bundle();
        bundlePhone.putString(LOGIN_USER_FULL_NAME, etFullName.getText().toString());
        bundlePhone.putString(LOGIN_USER_EMAIL, etEmail.getText().toString());
        bundlePhone.putString(LOGIN_USER_PHONE, etPhone.getText().toString());
        intentPhone.putExtras(bundlePhone);

        intentPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentPhone);
        finish();
    }

    private void launchDriver() {
        Intent intentPassenger = new Intent(this, DriverActivity.class);
        intentPassenger.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentPassenger);
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

        if (!CommonUtils.validateFullName(etFullName.getText().toString())) {
            layoutFullName.setError(getString(R.string.error_full_name));
        } else {
            layoutFullName.setErrorEnabled(false);
        }

        if (!CommonUtils.validateEmail(etEmail.getText().toString())) {
            layoutEmail.setError(getString(R.string.error_email));
            return;
        } else {
            layoutEmail.setErrorEnabled(false);
        }

        if (!CommonUtils.validatePhone(etPhone.getText().toString())) {
            layoutPhone.setError(getString(R.string.error_phone));
            return;
        } else {
            layoutPhone.setErrorEnabled(false);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            String email = etEmail.getText().toString();
            String password = etPhone.getText().toString();
            String fullName = etFullName.getText().toString();
            presenter.validateLoginFields(this, email, password, fullName);
            return true;
        }
        return false;
    }
}
