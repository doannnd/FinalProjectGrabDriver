package com.nguyendinhdoan.finalprojectgrabdriver.ui.driver;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class DriverPresenter implements DriverContract.DriverToPresenter, DriverContract.OnDriverListener {

    private DriverContract.DriverToView view;
    private DriverContract.DriverToInteractor model;

    public DriverPresenter(DriverContract.DriverToView view) {
        this.view = view;
        model = new DriverInteractor(this);
    }

    @Override
    public void getDeviceLocation(Context context) {
        model.getDeviceLocation(context);
    }


    @Override
    public void getLocationPermission(Context context) {
        model.getLocationPermission(context);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, int[] grantResults) {
        model.onRequestPermissionResult(requestCode, grantResults);
    }

    @Override
    public void getDeviceLocationSuccess(Location lastKnownLocation) {
        view.getDeviceLocationSuccess(lastKnownLocation);
    }

    @Override
    public void getDeviceLocationFailed(LatLng defaultLocation, String message) {
        view.getDeviceLocationFailed(defaultLocation, message);
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
