package com.nguyendinhdoan.finalprojectgrabdriver.ui.phone;

import android.content.Context;

public interface PhoneContract {

    interface PhoneToView {
        void onSendVerificationCodeSuccess(boolean isSendVerificationCodeSuccess);

        void onSendVerificationCodeFailed(String message);

        void onLoginWithCredentialSuccess(boolean isLoginWithCredentialSuccess);

        void onLoginWithCredentialFailed(String message);

        void showLoading();

        void hideLoading();
    }

    interface PhoneToPresenter {
        void verifyCodeAndLoginWithCredential(String code, String fullName, String email, String phone);

        void sendVerificationCode(Context context, String phoneNumber);
    }

    interface PhoneToInteractor {
        void verifyCodeAndLoginWithCredential(String code, String fullName, String email, String phone);

        void sendVerificationCode(Context context, String phoneNumber);
    }

    interface OnPhoneListener {
        void onSendVerificationCodeSuccess(boolean isSendVerificationCodeSuccess);

        void onSendVerificationCodeFailed(String message);

        void onLoginWithCredentialSuccess(boolean isLoginWithCredentialSuccess);

        void onLoginWithCredentialFailed(String message);

        void showLoading();

        void hideLoading();
    }

}
