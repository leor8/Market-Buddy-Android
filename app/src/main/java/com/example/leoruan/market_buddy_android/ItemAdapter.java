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

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<com.example.leoruan.market_buddy_android.Item> items;

    public ItemAdapter(Context context, List<com.example.leoruan.market_buddy_android.Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.items_single, parent, false);
        ItemAdapter.Item item = new ItemAdapter.Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ((ItemAdapter.Item)holder).set_position(position);
        ((ItemAdapter.Item)holder).item_name.setText(items.get(position).get_item_name());
        ((ItemAdapter.Item)holder).item_count.setText(String.valueOf(items.get(position).get_item_quantity()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView item_name, item_count;
        Context context;
        Button add, minus, remove;
        int curr_pos;

        public Item(View view) {
            super(view);
            item_name = view.findViewById(R.id.item_name);
            item_count = view.findViewById(R.id.item_count);
            context = view.getContext();

            add = view.findViewById(R.id.add);
            add.setOnClickListener(this);

            minus = view.findViewById(R.id.minus);
            minus.setOnClickListener(this);

            remove = view.findViewById(R.id.remove);
            remove.setOnClickListener(this);

            Log.d("DEBUG555", "" + curr_pos);
        }

        @Override
        public void onClick(View v) {
//            String name = item_name.getText().toString();
            if(v.getId() == R.id.add) {
                items.get(curr_pos).update_quantity(1);
                item_count.setText(String.valueOf(items.get(curr_pos).get_item_quantity()));
            }

            if(v.getId() == R.id.minus) {
                if(items.get(curr_pos).get_item_quantity() > 0) {
                    items.get(curr_pos).update_quantity(-1);
                    item_count.setText(String.valueOf(items.get(curr_pos).get_item_quantity()));
                }
            }

            if(v.getId() == R.id.remove) {
                items.remove(getLayoutPosition());
                notifyItemRemoved(getLayoutPosition());
                notifyItemRangeChanged(getLayoutPosition(), items.size());
            }
        } // On click closing bracket

        public void set_position(int position) {
            curr_pos = position;
        }
    } // Item class closing bracket

}
