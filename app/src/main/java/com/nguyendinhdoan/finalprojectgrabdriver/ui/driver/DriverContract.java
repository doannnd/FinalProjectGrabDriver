package com.nguyendinhdoan.finalprojectgrabdriver.ui.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface DriverContract {
    interface DriverToView {
        void getDeviceLocationSuccess(Location lastKnownLocation);

        void getDeviceLocationFailed(LatLng defaultLocation, String message);

        void showDirectionRoute(List<LatLng> polyLineList);

        void showLoading();

        void hideLoading();

        void showPickupLocationName(String placeName);
    }

    interface DriverToPresenter {
        void getDeviceLocation(Context context);

        void getLocationPermission(Context context);

        void onRequestPermissionResult(int requestCode, int[] grantResults);

        void searchLocationWithAutoComplete(Activity activity);

        void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
    }

    interface DriverToInteractor {
        void getDeviceLocation(Context context);

        void getLocationPermission(Context context);

        void onRequestPermissionResult(int requestCode, int[] grantResults);

        void searchLocationWithAutoComplete(Activity activity);

        void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
    }

    interface OnDriverListener {
        void getDeviceLocationSuccess(Location lastKnowLocation);

        void getDeviceLocationFailed(LatLng defaultLocation, String message);

        void showLoading();

        void hideLoading();

        void showDirectionRoute(List<LatLng> polyLineList);

        void showPickupLocationName(String placeName);
    }

}
