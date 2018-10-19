package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {
    final static String DEFAULT = "not available";

    TextView user;
    RecyclerView lists;
    List<String> shopping_lists = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = findViewById(R.id.users_name);

        // Setting up recyclerview for shopping lists
        lists = findViewById(R.id.shopping_lists);
        lists.setLayoutManager(new LinearLayoutManager(this));
        lists.setAdapter(new ListsAdapter(this, shopping_lists));

        try {
            SharedPreferences user_prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            String username = user_prefs.getString("USERNAME", DEFAULT);
            user.setText("Welcome, " + username);
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred while getting user info", Toast.LENGTH_SHORT).show();
        }
    }

    public void apicall(View v) throws Exception{
        String uri = "http://api.walmartlabs.com/v1/search?apiKey=82fg7wp8wb54kxfxdhkaezrx&lsPublisherId={Your%20LinkShare%20Publisher%20Id}&query=ipod";
    }

    public void dialog(View v) {
        // Initializing a name input edittext
        final EditText input = new EditText(this);

        // Create dialog box
        final AlertDialog.Builder get_list_name = new AlertDialog.Builder(this);
        get_list_name.setTitle("Enter the name of new shopping list");
        get_list_name.setView(input);
        get_list_name.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("DEBUG555", "cancel");
            }
        });
        get_list_name.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String list_name = input.getText().toString();
                if(list_name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "You did not enter a list name", Toast.LENGTH_SHORT).show();
                } else {
                    shopping_lists.add(0, list_name);
                    lists.setAdapter(new ListsAdapter(getApplicationContext(), shopping_lists));
                    // TODO: go to adding product page

                }
            }
        });
        get_list_name.show();
        get_list_name.setCancelable(true);
    }
}
