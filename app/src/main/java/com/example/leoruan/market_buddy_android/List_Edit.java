package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        list = findViewById(R.id.list_info_name);
        search = findViewById(R.id.search_input);
        received = getIntent();

        list.setText(received.getStringExtra("LISTNAME"));
        temp_item.add(new Item("a", 1));
        temp_item.add(new Item("b", 1));
        temp_item.add(new Item("a", 1));

        items = findViewById(R.id.user_picked_items);
        items.setLayoutManager(new LinearLayoutManager(this));
        items.setAdapter(new ItemAdapter(this, user_selected));
    }

    public void start_searching(View v) {
        String search_query = search.getText().toString();
        final AlertDialog.Builder search_results = new AlertDialog.Builder(this);
        search_results.setTitle("Search Result");

        // Getting filtered result
        List<Item> filtered_result = new ArrayList<Item>();
        for(int i = 0; i < temp_item.size(); i++) {
            if(temp_item.get(i).get_item_name().equals(search_query)) {
                filtered_result.add(temp_item.get(i));
            }
        }

        // Dialog only accepts array
        final String[] filtered_array = new String[filtered_result.size()];
        for(int i = 0; i < filtered_result.size(); i++) {
            filtered_array[i] = filtered_result.get(i).get_item_name();
        }

        // Setting dialog
        search_results.setItems(filtered_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // This will be used to display recyclerview outside
                user_selected.add(new Item(filtered_array[which], 1));
                items.setAdapter(new ItemAdapter(getApplicationContext(), user_selected));
            }
        });

        search_results.setCancelable(true);
        search_results.show();
    }
}
