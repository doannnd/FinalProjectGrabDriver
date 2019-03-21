package com.nguyendinhdoan.finalprojectgrabdriver.ui.login;

import android.content.Context;

public class LoginPresenter implements LoginContract.LoginToPresenter, LoginContract.OnLoginListener{

    private LoginContract.LoginToView view;
    private LoginContract.LoginToInteractor model;

    public LoginPresenter(LoginContract.LoginToView view) {
        this.view = view;
        model = new LoginInteractor(this);
    }

    @Override
    public void validateLoginFields(Context context, String email, String phone, String fullName) {
        model.validateLoginFields(context, email, phone, fullName);
    }

    @Override
    public void isLoggedIn() {
        model.isLoggedIn();
    }

    @Override
    public void onValidateError(String message) {
        view.onValidateError(message);
    }

    @Override
    public void onValidateResponse(boolean isValidateSuccess) {
        view.onValidateResponse(isValidateSuccess);
    }

    @Override
    public void isLoggedIn(boolean isLoggedIn) {
        view.isLoggedIn(isLoggedIn);
    }
}
