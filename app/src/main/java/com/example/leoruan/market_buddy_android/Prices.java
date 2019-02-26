package com.example.leoruan.market_buddy_android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Prices extends AppCompatActivity {
    RecyclerView stores;
    List<String> store_names = new ArrayList<String>();
    List<String> store_address = new ArrayList<String>();
    Intent received;
    LocationManager lm;
    private boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String listid;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(49.187532, -122.849362);
    double longitude = -122.849362;
    double latitude = 49.187532;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prices);

        received = getIntent();
        listid = received.getStringExtra("LISTID");
        // Common stores
        store_names.add("Save On Foods");
        store_names.add("Safeway");
        store_names.add("Walmart");
        store_names.add("Nesters Market");
        store_names.add("Superstore");
        store_names.add("TNT Supermarket");

        // Map setup
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        // Getting current location
        lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            mLocationPermissionGranted = true;
        }

        if(mLocationPermissionGranted) {
            getDeviceLocation();
        }

        stores = findViewById(R.id.StoreLists);
        stores.setLayoutManager(new LinearLayoutManager(this));
        stores.setAdapter(new StoreAdapter(this, store_names, listid, store_address));
    }

    // API CALLS
    private String readJSONData(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 25;

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
                conn.disconnect();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        stream.close();
        return result;
    }

    private class ReadJSONDataTask extends AsyncTask<String, Void, String> {
        Exception exception = null;

        protected String doInBackground(String... url) {
            try {
                return readJSONData(url[0]);
            } catch (IOException e) {
                exception = e;
            }

            return null;
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("candidates");
                store_address.add(results.getJSONObject(0).getString("formatted_address"));
                stores.setAdapter(new StoreAdapter(Prices.this, store_names, listid, store_address));

            } catch (Exception e) {
                exception = e;
            }

        }

    }


    // OnClick to start map activity
    public void startMap(View v) {
        if(store_address.size() < 6) {
            Toast.makeText(this, "All addresses have not yet finished loading. Please wait.", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(this, Map.class);

            // passing addresses for marker making
            i.putExtra("SAVEON", store_address.get(0));
            i.putExtra("SAFEWAY", store_address.get(1));
            i.putExtra("WALMART", store_address.get(2));
            i.putExtra("NESTERS", store_address.get(3));
            i.putExtra("SUPERSTORE", store_address.get(4));
            i.putExtra("TNT", store_address.get(5));
            startActivity(i);
        }
    }


    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();
                            getStoreAddress(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        } else {
                            getStoreAddress(49.187532, -122.849362);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getStoreAddress(double latitude, double longitude) {
        // Saveonfoods
        String saveonUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "input=saveonfoods&" +
                "inputtype=textquery&" +
                "fields=formatted_address,name&" +
                "locationbias=circle:1500@" +
                String.valueOf(latitude) + "," + String.valueOf(longitude) +
                "&key=" + getString(R.string.google_places_api);
        new ReadJSONDataTask().execute(saveonUrl);

        // Safeway
        String safewayUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "input=safeway&" +
                "inputtype=textquery&" +
                "fields=formatted_address,name&" +
                "locationbias=circle:1500@" +
                String.valueOf(latitude) + "," + String.valueOf(longitude) +
                "&key=" + getString(R.string.google_places_api);
        new ReadJSONDataTask().execute(safewayUrl);

        // Walmart
        String walUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "input=walmart&" +
                "inputtype=textquery&" +
                "fields=formatted_address,name&" +
                "locationbias=circle:1500@" +
                String.valueOf(latitude) + "," + String.valueOf(longitude) +
                "&key=" + getString(R.string.google_places_api);
        new ReadJSONDataTask().execute(walUrl);

        // nesters
        String nestUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "input=nestersmarket&" +
                "inputtype=textquery&" +
                "fields=formatted_address,name&" +
                "locationbias=circle:1500@" +
                String.valueOf(latitude) + "," + String.valueOf(longitude) +
                "&key=" + getString(R.string.google_places_api);
        new ReadJSONDataTask().execute(nestUrl);


        // SuperStore
        String superStoreUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "input=superstore&" +
                "inputtype=textquery&" +
                "fields=formatted_address,name&" +
                "locationbias=circle:1500@" +
                String.valueOf(latitude) + "," + String.valueOf(longitude) +
                "&key=" + getString(R.string.google_places_api);
        new ReadJSONDataTask().execute(superStoreUrl);

        // TNT
        String tntUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?" +
                "input=TNT&" +
                "inputtype=textquery&" +
                "fields=formatted_address,name&" +
                "locationbias=circle:1500@" +
                String.valueOf(latitude) + "," + String.valueOf(longitude) +
                "&key=" + getString(R.string.google_places_api);
        new ReadJSONDataTask().execute(tntUrl);
    }

}

