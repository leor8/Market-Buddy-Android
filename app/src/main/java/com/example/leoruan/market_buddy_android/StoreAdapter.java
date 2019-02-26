package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> stores;
    private List<String> address;
    private String listid;

    public StoreAdapter(Context context, List<String> stores, String listid, List<String> address) {
        this.context = context;
        this.stores = stores;
        this.listid = listid;
        this.address = address;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.stores_single, parent, false);
        Item item = new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Item) holder).store_name.setText(stores.get(position).toString());
        ((Item) holder).price.setText("$" + ((Item) holder).getTotal(((Item) holder).items));
        if (position < address.size()) {
            ((Item)holder).store_address.setText(address.get(position).toString());
        } else {
            ((Item)holder).store_address.setText("Loading...");
        }
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView store_name, price, store_address;
        Context context;
        Button delete, edit;
        Price_db pricedb;
        Item_db itemdb;

        List<com.example.leoruan.market_buddy_android.Item> items;

        public Item(View view) {
            super(view);
            store_name = view.findViewById(R.id.store_name_text);
            context = view.getContext();
            view.setOnClickListener(this);

            itemdb = new Item_db(context);
            pricedb = new Price_db(context);

            items = itemdb.getData(listid);
            store_address = view.findViewById(R.id.address);

            price = view.findViewById(R.id.total);
        }

        @Override
        public void onClick(View v) {
            String name = store_name.getText().toString();
            Intent i = new Intent(context, StoreDetail.class);
            i.putExtra("LISTID", listid);
            i.putExtra("STORENAME", name);
            context.startActivity(i);
        } // On click closing bracket

        private String getTotal(List<com.example.leoruan.market_buddy_android.Item> curr_items) {
            float total = 0;

            for(int i = 0; i < curr_items.size(); i++) {
                float each = Float.valueOf(pricedb.getPrice(store_name.getText().toString(), items.get(i).get_item_itemid()));
                float with_quantity = each * items.get(i).get_item_quantity();
                total += with_quantity;
            }
            return String.format("%.02f", total);
        }

    } // Item class closing bracket


}
