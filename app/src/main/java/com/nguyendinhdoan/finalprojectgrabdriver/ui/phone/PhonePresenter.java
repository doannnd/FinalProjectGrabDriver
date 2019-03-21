package com.nguyendinhdoan.finalprojectgrabdriver.ui.phone;

import android.content.Context;

public class PhonePresenter implements PhoneContract.PhoneToPresenter, PhoneContract.OnPhoneListener{

    private PhoneContract.PhoneToView view;
    private PhoneContract.PhoneToInteractor model;

    public PhonePresenter(PhoneContract.PhoneToView view) {
        this.view = view;
        model = new PhoneInteractor(this);
    }


    @Override
    public void verifyCodeAndLoginWithCredential(String code, String fullName, String email, String password) {
        model.verifyCodeAndLoginWithCredential(code, fullName, email, password);
    }

    @Override
    public void sendVerificationCode(Context context, String phoneNumber) {
        model.sendVerificationCode(context, phoneNumber);
    }

    @Override
    public void onSendVerificationCodeSuccess(boolean isSendVerificationCodeSuccess) {
        view.onSendVerificationCodeSuccess(isSendVerificationCodeSuccess);
    }

    @Override
    public void onSendVerificationCodeFailed(String message) {
        view.onSendVerificationCodeFailed(message);
    }

    @Override
    public void onLoginWithCredentialSuccess(boolean isLoginWithCredentialSuccess) {
        view.onLoginWithCredentialSuccess(isLoginWithCredentialSuccess);
    }

    @Override
    public void onLoginWithCredentialFailed(String message) {
        view.onLoginWithCredentialFailed(message);
    }

    @Override
    public void showLoading() {
        view.showLoading();
    }

    @Override
    public void hideLoading() {
        view.hideLoading();
    }
}
