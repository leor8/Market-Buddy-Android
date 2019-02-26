package com.example.leoruan.market_buddy_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class StoreDetail extends AppCompatActivity {
    Intent received;
    TextView storename, priceDisplay;
    Price_db pricedb;
    Item_db itemdb;

    List<Item> items;
    String currStore;
    RecyclerView listDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        itemdb = new Item_db(this);
        pricedb = new Price_db(this);

        received = getIntent();

        storename = findViewById(R.id.storeDetailName);
        storename.setText(received.getStringExtra("STORENAME"));
        currStore = storename.getText().toString();

        priceDisplay = findViewById(R.id.priceDisplay);

        items = itemdb.getData(received.getStringExtra("LISTID"));

        priceDisplay.setText(getTotal(items));

        String[] prices = new String[items.size()];
        for(int i = 0; i < items.size(); i++) {
            Log.d("DEBUG555", pricedb.getPrice(currStore, items.get(i).get_item_itemid()));
            prices[i] = String.format("%.02f", Float.valueOf(pricedb.getPrice(currStore, items.get(i).get_item_itemid())));
        }

        listDetail = findViewById(R.id.itemsWithPrice);
        listDetail.setLayoutManager(new LinearLayoutManager(this));
        listDetail.setAdapter(new DetailAdapter(this, items, prices));
    }

    private String getTotal(List<Item> curr_items) {
        float total = 0;

        for(int i = 0; i < curr_items.size(); i++) {
            float each = Float.valueOf(pricedb.getPrice(currStore, items.get(i).get_item_itemid()));
            float with_quantity = each * items.get(i).get_item_quantity();
            total += with_quantity;
        }
        return String.format("%.02f", total);
    }

    public void storeWebsite(View v) {
        String currStore = storename.getText().toString();
        String URL = "www.google.ca";
        switch (currStore) {
            case "Save On Foods":
                URL = "https://www.saveonfoods.com/";
                break;
            case "Safeway":
                URL = "http://savewaymarket.com/";
                break;
            case "Walmart":
                URL = "https://www.walmart.ca/en";
                break;
            case "Nesters Market":
                URL = "http://www.nestersmarket.com/";
                break;
            case "Superstore":
                URL = "https://www.realcanadiansuperstore.ca/";
                break;
            case "TNT Supermarket":
                URL = "https://www.tnt-supermarket.com/bc/";
                break;
                default:
                    URL = "www.google.ca";
                    break;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(URL));
        startActivity(i);
    }

}
