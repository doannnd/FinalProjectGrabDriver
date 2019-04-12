package com.nguyendinhdoan.finalprojectgrabdriver.ui.driver;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.nguyendinhdoan.finalprojectgrabdriver.R;
import com.nguyendinhdoan.finalprojectgrabdriver.ui.login.LoginActivity;
import com.nguyendinhdoan.finalprojectgrabdriver.ui.setting.SettingsActivity;
import com.nguyendinhdoan.finalprojectgrabdriver.util.CommonUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback,
        DriverContract.DriverToView, CompoundButton.OnCheckedChangeListener,
        PlaceSelectionListener, View.OnTouchListener {

    public static final String TAG = "DRIVER_ACTIVITY";
    public static final int DEFAULT_ZOOM_GOOGLE_MAP = 14;
    public static final float POLYLINE_WIDTH = 4F;
    public static final double CIRCLE_RADIUS = 100.0;
    public static final float CIRCLE_STROKE_WIDTH = 1f;

    @BindView(R.id.layout_driver)
    ConstraintLayout layoutDriver;
    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_passenger_avl_loading)
    AVLoadingIndicatorView avLoadingIndicatorView;
    @BindView(R.id.activity_driver_switch_state_driver)
    Switch switchStateDriver;
    @BindView(R.id.activity_driver_et_search)
    EditText etSearch;

    private GoogleMap mGoogleMap;
    private DriverContract.DriverToPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        ButterKnife.bind(this);

        setupUi();
        addEvents();
        presenter = new DriverPresenter(this);
        presenter.getDeviceLocation(this);
    }

    private void setupUi() {
        setupToolbar();
        setupGoogleMap();
        setupPlaces();
    }

    private void addEvents() {
        switchStateDriver.setOnCheckedChangeListener(this);
        etSearch.setOnTouchListener(this);
    }

    private void setupGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_driver_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupPlaces() {
        Places.initialize(this, getString(R.string.google_api_key));
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.action_profile: {
                break;
            }
            case R.id.action_logout: {
                FirebaseAuth.getInstance().signOut();
                launchLogin();
                break;
            }
            case R.id.action_settings: {
                Intent intentSettingActivity = new Intent(this, SettingsActivity.class);
                intentSettingActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSettingActivity);
                finish();
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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //
        settingDisplayMapType();
        // get location permission of the device
        presenter.getLocationPermission(this);
        // update ui
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        presenter.getDeviceLocation(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        presenter.onRequestPermissionResult(requestCode, grantResults);
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (DriverInteractor.mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                presenter.getLocationPermission(this);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void showCurrentLocation() {
        try {
            if (DriverInteractor.mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void hideCurrentLocation() {
        try {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void getDeviceLocationSuccess(Location mLastKnownLocation) {
        if (mLastKnownLocation != null) {
            LatLng coordinateCurrentLocation = new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinateCurrentLocation, DEFAULT_ZOOM_GOOGLE_MAP));
            // add circle for current location
            /*mGoogleMap.addCircle(new CircleOptions().center(coordinateCurrentLocation)
                    .radius(CIRCLE_RADIUS).strokeWidth(CIRCLE_STROKE_WIDTH)
                    .strokeColor(Color.BLUE).fillColor(Color.argb(70,9, 131, 247)));*/
            // add marker and title
           /* mMarker = mGoogleMap.addMarker(new MarkerOptions().position(coordinateCurrentLocation)
                    .title("You"));*/
        }
        //
        changeStateWorkingOfUser();
    }

    @Override
    public void getDeviceLocationFailed(LatLng mDefaultLocation, String message) {
        if (mDefaultLocation != null && message != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM_GOOGLE_MAP));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showDirectionRoute(List<LatLng> polyLineList) {
        PolylineOptions polylineOptions = new PolylineOptions();
        PolylineOptions overLayPolyLineOptions = new PolylineOptions();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
            polylineOptions.add(latLng);
            overLayPolyLineOptions.add(latLng);
        }

        polylineOptions.color(ContextCompat.getColor(this, R.color.colorBlue));
        polylineOptions.width(POLYLINE_WIDTH);
        final Polyline bluePolyline = mGoogleMap.addPolyline(polylineOptions);

        overLayPolyLineOptions.color(ContextCompat.getColor(this, R.color.colorBlueLight));
        overLayPolyLineOptions.width(POLYLINE_WIDTH);
        final Polyline blueLightPolyline = mGoogleMap.addPolyline(overLayPolyLineOptions);

        LatLngBounds bounds = builder.build();
        int padding = 150; // offset from edges of the map in pixels

        // add marker end point direction
        LatLng endPointCoordinator = polyLineList.get(polyLineList.size() - 1);
        Marker markerEndpoint = mGoogleMap.addMarker(new MarkerOptions()
                .position(endPointCoordinator)
                .title("pickup"));
        markerEndpoint.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.moveCamera(cameraUpdate);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> points = bluePolyline.getPoints();
                int percentValue = (int) animation.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                blueLightPolyline.setPoints(p);
            }
        });
        valueAnimator.start();

    }

    @Override
    public void showPickupLocationName(String placeName) {
        etSearch.setText(placeName);
    }

    @Override
    public void showLoading() {
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        avLoadingIndicatorView.setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isOnline) {
        CommonUtils.saveStateWorkingOfDriver(this, isOnline);

        if (isOnline) {
            showSnackBar(getString(R.string.snack_online));
            showCurrentLocation();
        } else {
            showSnackBar(getString(R.string.snack_offline));
            hideCurrentLocation();
        }
    }

    private void changeStateWorkingOfUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(CommonUtils.PREF_KEY_SATE_OF_DRIVER, Context.MODE_PRIVATE);
        boolean isOnline = sharedPreferences.getBoolean(CommonUtils.KEY_IS_ONLINE, false);
        if (isOnline) {
            switchStateDriver.setChecked(true);
            showSnackBar(getString(R.string.snack_online));
            showCurrentLocation();
        } else {
            switchStateDriver.setChecked(false);
            showSnackBar(getString(R.string.snack_offline));
            hideCurrentLocation();
        }
    }

    private void settingDisplayMapType() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String typeMap = sharedPreferences.getString(getString(R.string.setting_dialog_key_map_type), "");
        switch (typeMap) {
            case "0":
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "1":
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "2":
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "3":
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        // set background color for snack bar
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        snackbar.show();
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        Toast.makeText(this, "Place: " + place.getName() + ", " + place.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(@NonNull Status status) {
        Toast.makeText(this, "An error occurred: " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                presenter.searchLocationWithAutoComplete(this);
                break;
            }
            case MotionEvent.ACTION_UP: {
                v.performClick();
                break;
            }
            default:
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(this, requestCode, resultCode, data);
    }
}
