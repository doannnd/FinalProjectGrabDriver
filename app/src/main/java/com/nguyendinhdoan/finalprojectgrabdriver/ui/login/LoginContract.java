package com.nguyendinhdoan.finalprojectgrabdriver.ui.login;

import android.content.Context;

public interface LoginContract {

    interface LoginToPresenter {
        void validateLoginFields(Context context, String email, String phone, String fullName);

        void isLoggedIn();
    }

    interface LoginToView {

        void onValidateError(String message);

        void onValidateResponse(boolean isValidateSuccess);

        void isLoggedIn(boolean isLoggedIn);
    }

    interface LoginToInteractor {
        void validateLoginFields(Context context, String email, String phone, String fullName);

        void isLoggedIn();
    }

    interface OnLoginListener {

        void onValidateError(String message);

        void onValidateResponse(boolean isValidateSuccess);

        void isLoggedIn(boolean isLoggedIn);
    }
}
