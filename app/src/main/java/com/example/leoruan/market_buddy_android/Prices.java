package com.example.leoruan.market_buddy_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Prices extends AppCompatActivity {
    RecyclerView stores;
    List<String> store_names = new ArrayList<String>();
    Intent received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prices);

        // Common stores
        store_names.add("Save On Foods");
        store_names.add("Safeway");
        store_names.add("Walmart");
        store_names.add("Nesters Market");
        store_names.add("Superstore");
        store_names.add("TNT Supermarket");

        received = getIntent();
        String listid = received.getStringExtra("LISTID");

        stores = findViewById(R.id.StoreLists);
        stores.setLayoutManager(new LinearLayoutManager(this));
        stores.setAdapter(new StoreAdapter(this, store_names, listid));
    }

    public void startMap(View v) {
        Intent i = new Intent(this, Map.class);
        startActivity(i);
    }

}
