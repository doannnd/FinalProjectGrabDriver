package com.nguyendinhdoan.finalprojectgrabdriver.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class CommonUtils {

    public static final String PREF_KEY_SATE_OF_DRIVER = "PREF_KEY_STATE_OF_USER";
    public static final String KEY_IS_ONLINE = "KEY_IS_ONLINE";

    public static boolean validateFullName(String fullName) {
        return !TextUtils.isEmpty(fullName);
    }

    public static boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validatePhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void saveStateWorkingOfDriver(Activity activity, boolean isOnline) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREF_KEY_SATE_OF_DRIVER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_ONLINE, isOnline);
        editor.apply();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                isConnected = true;
            } else {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
        }
        }
        return isConnected;
    }
}
