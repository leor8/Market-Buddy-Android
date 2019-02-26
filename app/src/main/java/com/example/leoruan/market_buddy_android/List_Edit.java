package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class List_Edit extends AppCompatActivity implements SensorEventListener{
    final static String DEFAULT = "not available";
    TextView list;
    EditText search;
    Intent received;

    List<Item> temp_item = new ArrayList<Item>();
    List<Item> user_selected = new ArrayList<Item>();

    RecyclerView items;

    List_db listdb;
    List_Helper listhelper;
    Item_db itemdb;
    Item_Helper itemhelper;
    String listid;

    Boolean result_received = true;

    SensorManager sensorManager;
    Sensor acc;
    private boolean shakable = true;
    long lastUpdate;
    double last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;
    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        list = findViewById(R.id.list_info_name);
        search = findViewById(R.id.search_input);
        received = getIntent();

        list.setText(received.getStringExtra("LISTNAME"));

        // Getting all the existing items from db
        listdb = new List_db(this);
        listhelper = new List_Helper(this);

        listid = (listdb.getSelectedData(list.getText().toString())).substring(listdb.getSelectedData(list.getText().toString()).lastIndexOf(" ") + 1);

        itemdb = new Item_db(this);
        itemhelper = new Item_Helper(this);

        user_selected = itemdb.getData(listid);

        checkConnection();

        // Setting up Recyclerview
        items = findViewById(R.id.user_picked_items);
        items.setLayoutManager(new LinearLayoutManager(this));
        items.setAdapter(new ItemAdapter(this, user_selected));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();

    }

    public void start_searching(View v) {
//        result_received = true; // Set this to false when using real data from api
        String search_query = search.getText().toString();
        shakable = false;

        // Getting filtered result
        String myUrl = "http://api.walmartlabs.com/v1/search?apiKey=" + getString(R.string.walmart_api) + "&query=" + search_query;
        new ReadJSONDataTask().execute(myUrl);
    }

    private void display_result() {
        final String search_query = search.getText().toString();
        final AlertDialog.Builder search_results = new AlertDialog.Builder(this);
        search_results.setTitle("Search Result");

        final List<Item> filtered_result = temp_item;

        // Dialog only accepts array
        if(filtered_result.size() > 0) {
            final String[] filtered_array = new String[filtered_result.size()];
            final String[] id_array = new String[filtered_result.size()];
            final String[] price_array = new String[filtered_result.size()];
            for (int i = 0; i < filtered_result.size(); i++) {
                filtered_array[i] = filtered_result.get(i).get_item_name();
                id_array[i] = filtered_result.get(i).get_item_itemid();
                price_array[i] = filtered_result.get(i).get_item_price();
            }
            // Setting dialog
            search_results.setItems(filtered_array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // This will be used to display recyclerview outside
                    if (user_selected.size() == 0) {
                        user_selected.add(new Item(filtered_array[which], 1, listid, id_array[which], price_array[which]));
                    } else {
                        for (int i = 0; i < user_selected.size(); i++) {
                            if (id_array[which].equals(user_selected.get(i).get_item_itemid())) {
                                user_selected.get(i).update_quantity(1);
                                break;
                            } else if (i == user_selected.size() - 1) {
                                user_selected.add(new Item(filtered_array[which], 1, listid, id_array[which], price_array[which]));
                                break;
                            }
                        }
                    }
                    items.setAdapter(new ItemAdapter(getApplicationContext(), user_selected));
                    temp_item.clear();
                    filtered_result.clear();
                    search.setText("");
                    shakable = true;
                    dialog.dismiss();
                }
            });
        } else {
            search_results.setMessage("The item you are looking for is not found.");
        }

        search_results.setCancelable(true);
        search_results.show();

    }
    public void checkConnection(){
        ConnectivityManager connectMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            //fetch data

            String networkType = networkInfo.getTypeName().toString();
            Toast.makeText(this, "connected to " + networkType, Toast.LENGTH_LONG).show();
        }
        else {
            //display error
            Toast.makeText(this, "no network connection", Toast.LENGTH_LONG).show();
        }

    }

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
            temp_item.clear();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray results = jsonObject.getJSONArray("items");
                for(int i = 0; i < 8; i++) {
                    JSONObject curr_obj = results.getJSONObject(i);
                    temp_item.add(new Item(curr_obj.getString("name"), 1, listid, curr_obj.getString("itemId"), curr_obj.getString("salePrice")));
                }
                display_result();
            } catch (Exception e) {
                exception = e;
                display_result();
            }

        }

    }

    public void back(View v) {
        itemdb.insertWholeList(user_selected, listid);
        Intent i = new Intent(getApplicationContext(), UserProfile.class);
        startActivity(i);
    }

    public void pricePage(View v) {
        itemdb.insertWholeList(user_selected, listid);
        Intent i = new Intent(getApplicationContext(), Prices.class);
        i.putExtra("LISTID", listid);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        itemdb.insertWholeList(user_selected, listid);
    }


    // Sensor required events
    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if(type == Sensor.TYPE_ACCELEROMETER && shakable && !first) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                double x = event.values.clone()[0];
                double y = event.values.clone()[1];
                double z = event.values.clone()[2];

                double speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    shakable = false;
                    final AlertDialog.Builder clearItem = new AlertDialog.Builder(this);
                    clearItem.setTitle("Do you want to clear all the items in the list?");
                    clearItem.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shakable = true;
                        }
                    });
                    clearItem.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user_selected.clear();
                            items.setAdapter(new ItemAdapter(getApplicationContext(), user_selected));
                            shakable = true;
                        }
                    });
                    clearItem.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            shakable = true;
                        }
                    });
                    clearItem.show();
                    clearItem.setCancelable(true);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        } else if (first) {
            last_x = event.values.clone()[0];
            last_y = event.values.clone()[1];
            last_z = event.values.clone()[2];
            first = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i) {
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
