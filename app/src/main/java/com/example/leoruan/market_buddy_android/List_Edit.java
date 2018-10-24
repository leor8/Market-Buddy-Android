package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class List_Edit extends AppCompatActivity {
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

    Boolean result_received = false;

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

        Cursor cursor = itemdb.getData(listid);

        checkConnection();
        // Initializing user selected arraylist
//        int index1 = cursor.getColumnIndex(Item_Constant.NAME);
//        int index2 = cursor.getColumnIndex(Item_Constant.QUANTITY);
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()) {
//            String itemname = cursor.getString(index1);
//            int itemcount = Integer.parseInt(cursor.getString(index2));
//            user_selected.add(new Item(itemname, itemcount, listid));
//            cursor.moveToNext();
//        }

        // Dummy Data to search list, this part will be replaced with an api call to walmart api
        temp_item.add(new Item("a", 1, listid, "123"));
        temp_item.add(new Item("b", 1, listid, "234"));
        temp_item.add(new Item("a", 1, listid, "345"));

        // Setting up Recyclerview
        items = findViewById(R.id.user_picked_items);
        items.setLayoutManager(new LinearLayoutManager(this));
        items.setAdapter(new ItemAdapter(this, user_selected));

    }

    public void start_searching(View v) {
        result_received = true; // Set this to false when using real data from api
        String search_query = search.getText().toString();
        final AlertDialog.Builder search_results = new AlertDialog.Builder(this);
        search_results.setTitle("Search Result");

        // Getting filtered result
//        String myUrl = "http://api.walmartlabs.com/v1/search?apiKey=82fg7wp8wb54kxfxdhkaezrx&query=ipod";
//
//        new ReadJSONDataTask().execute(myUrl);

        if(result_received) {
            List<Item> filtered_result = new ArrayList<Item>();
            for (int i = 0; i < temp_item.size(); i++) {
                if (temp_item.get(i).get_item_name().equals(search_query)) {
                    filtered_result.add(temp_item.get(i));
                }
            }

            // Dialog only accepts array
            final String[] filtered_array = new String[filtered_result.size()];
            final String[] id_array = new String[filtered_result.size()];
            for (int i = 0; i < filtered_result.size(); i++) {
                filtered_array[i] = filtered_result.get(i).get_item_name();
                id_array[i] = filtered_result.get(i).get_item_itemid();
            }

            // Setting dialog
            search_results.setItems(filtered_array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // This will be used to display recyclerview outside
                    user_selected.add(new Item(filtered_array[which], 1, listid, id_array[which]));
                    items.setAdapter(new ItemAdapter(getApplicationContext(), user_selected));

                    long id = itemdb.insertData(filtered_array[which], "1", listid);
                    Log.d("DEBUG555", "" + id);
                }
            });
        } else {
            search_results.setMessage("Loading...");
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
        int len = 2500;

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
            Log.d("DEBUG555", ""+ response);

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
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
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
                JSONArray results = jsonObject.getJSONArray("items");
                for(int i = 0; i < results.length(); i++) {
                    JSONObject curr_obj = results.getJSONObject(i);
                    temp_item.add(new Item(curr_obj.getString("name"), 1, listid, curr_obj.getString("itemId")));
                    result_received = true;
                }
            } catch (Exception e) {
                exception = e;
            }

        }

    }
}
