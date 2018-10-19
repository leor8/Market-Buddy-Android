package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> lists;
    List_db db;

    public ListsAdapter(Context context, List<String> lists) {
        this.context = context;
        this.lists = lists;
        db = new List_db(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.lists_single, parent, false);
        Item item = new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ((Item)holder).list_name.setText(lists.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView list_name;
        Context context;
        Button delete, edit;

        public Item(View view) {
            super(view);
            list_name = view.findViewById(R.id.list_name_text);
            delete = view.findViewById(R.id.delete_list);
            delete.setOnClickListener(this);
            edit = view.findViewById(R.id.edit_list);
            edit.setOnClickListener(this);
            context = view.getContext();
        }

        @Override
        public void onClick(View v) {
            String name = list_name.getText().toString();

            if(v.getId() == R.id.delete_list) {

                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).equals(name)) {
                        lists.remove(getLayoutPosition());
                        notifyItemRemoved(getLayoutPosition());
                        notifyItemRangeChanged(getLayoutPosition(), lists.size());
                        db.deleteData(name);
                    }
                }
            }

            if(v.getId() == R.id.edit_list) {
                // TODO: pass intent with the name of the list and start activity edit list
            }

        } // On click closing bracket
    } // Item class closing bracket

}
