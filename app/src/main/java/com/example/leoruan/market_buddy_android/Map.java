package com.example.leoruan.market_buddy_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 14;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Geocoder coder;
    List<Address> address;
    Intent received;
    LatLng saveon, safeway, wal, nest, superstore, tnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        coder = new Geocoder(this);
        received = getIntent();

        // Save on foods marker latlong
        try {
            address = coder.getFromLocationName(received.getStringExtra("SAVEON"), 5);

            if(address != null) {
                Address location = address.get(0);
                saveon = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e){
            Log.d("DEBUG555", "ERROR");
        }

        // Safeway marker latlong
        try {
            address = coder.getFromLocationName(received.getStringExtra("SAFEWAY"), 5);

            if(address != null) {
                Address location = address.get(0);
                safeway = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e){
            Log.d("DEBUG555", "ERROR");
        }

        // walmart marker latlong
        try {
            address = coder.getFromLocationName(received.getStringExtra("WALMART"), 5);

            if(address != null) {
                Address location = address.get(0);
                wal = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e){
            Log.d("DEBUG555", "ERROR");
        }

        // NESTER marker latlong
        try {
            address = coder.getFromLocationName(received.getStringExtra("NESTERS"), 5);

            if(address != null) {
                Address location = address.get(0);
                nest = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e){
            Log.d("DEBUG555", "ERROR");
        }

        // superstore marker latlong
        try {
            address = coder.getFromLocationName(received.getStringExtra("SUPERSTORE"), 5);

            if(address != null) {
                Address location = address.get(0);
                superstore = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e){
            Log.d("DEBUG555", "ERROR");
        }

        // tnt marker latlong
        try {
            address = coder.getFromLocationName(received.getStringExtra("TNT"), 5);

            if(address != null) {
                Address location = address.get(0);
                tnt = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e){
            Log.d("DEBUG555", "ERROR");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        mMap.addMarker(new MarkerOptions().position(saveon).title("Save On Foods").snippet(received.getStringExtra("SAVEON")));
        mMap.addMarker(new MarkerOptions().position(safeway).title("Safeway").snippet(received.getStringExtra("SAFEWAY")));
        mMap.addMarker(new MarkerOptions().position(wal).title("Walmart").snippet(received.getStringExtra("WALMART")));
        mMap.addMarker(new MarkerOptions().position(nest).title("Nesters Market").snippet(received.getStringExtra("NESTERS")));
        mMap.addMarker(new MarkerOptions().position(superstore).title("Superstore").snippet(received.getStringExtra("SUPERSTORE")));
        mMap.addMarker(new MarkerOptions().position(tnt).title("TNT Supermarket").snippet(received.getStringExtra("TNT")));

    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        } else {
                            Log.d("Debug555", "Current location is null. Using defaults.");
                            Log.e("Debug555", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
