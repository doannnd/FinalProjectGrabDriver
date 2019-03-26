package com.nguyendinhdoan.finalprojectgrabdriver.ui.phone;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nguyendinhdoan.finalprojectgrabdriver.data.model.Driver;

import java.util.concurrent.TimeUnit;

public class PhoneInteractor implements PhoneContract.PhoneToInteractor{

    private static final String TAG = "PhoneInteractor";
    public static final Long VERIFICATION_CODE_TIMEOUT_DURATION = 30L;

    private String verificationId;
    private PhoneContract.OnPhoneListener listener;

    public PhoneInteractor(PhoneContract.OnPhoneListener listener) {
        this.listener = listener;
    }

    @Override
    public void sendVerificationCode(Context context, String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                VERIFICATION_CODE_TIMEOUT_DURATION,
                TimeUnit.SECONDS,
                (Activity) context,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = s;
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        listener.onSendVerificationCodeSuccess(true);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        listener.onSendVerificationCodeFailed(e.getMessage());
                        Log.e(TAG, e.getMessage());
                    }
                }
        );

    }

    @Override
    public void verifyCodeAndLoginWithCredential(String code, String fullName, String email, String phone) {
        if (TextUtils.isEmpty(code) || code.length() < 6) {
            listener.onLoginWithCredentialFailed("Enter a valid verification code");
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        loginWithCredential(credential, fullName, email, phone);
    }

    private void loginWithCredential(PhoneAuthCredential credential, final String fullName,
                                     final String email, final String phone) {
        listener.showLoading();
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createUserInDatabase(fullName, email, phone);
                        }   else {
                            listener.onLoginWithCredentialFailed(task.getException().getMessage());
                            Log.e(TAG, task.getException().getMessage());
                        }
                        listener.hideLoading();
                    }
                });
    }

    private void createUserInDatabase(String fullName, String email, String phone) {
        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference().child("drivers");
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // set data for user
        Driver driver = new Driver();
        driver.setDriverId(driverId);
        driver.setFullName(fullName);
        driver.setEmail(email);
        driver.setPhone(phone);

        drivers.child(driverId).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onLoginWithCredentialSuccess(true);
                } else {
                    listener.onLoginWithCredentialFailed(task.getException().getMessage());
                    Log.e(TAG, task.getException().getMessage());
                }
            }
        });

    }
}
