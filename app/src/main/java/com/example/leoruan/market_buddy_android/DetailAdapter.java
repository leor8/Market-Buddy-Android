package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<com.example.leoruan.market_buddy_android.Item> items;
    String[] prices;

    DetailAdapter(Context context, List<com.example.leoruan.market_buddy_android.Item> items, String[] prices) {
        this.context = context;
        this.items = items;
        this.prices = prices;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.detail_single, parent, false);
        Item item = new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ((Item)holder).item_name.setText(items.get(position).get_item_name());
        ((Item)holder).item_count.setText(String.valueOf(items.get(position).get_item_quantity()));
        ((Item)holder).item_price.setText(prices[position]);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        TextView item_name, item_count, item_price;
        Context context;
        int curr_pos;

        public Item(View view) {
            super(view);
            item_name = view.findViewById(R.id.detail_name);
            item_count = view.findViewById(R.id.detail_quantity);
            item_price = view.findViewById(R.id.detail_price);
            context = view.getContext();
        }
    }
}
