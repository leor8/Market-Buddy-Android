package com.example.leoruan.market_buddy_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Item_db {

    private SQLiteDatabase db;
    private Context context;
    private final Item_Helper helper;

    public Item_db (Context c){
        context = c;
        helper = new Item_Helper(context);
    }

    public long insertData (String name, String quantity, String listId, String price)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Item_Constant.NAME, name);
        contentValues.put(Item_Constant.QUANTITY, quantity);
        contentValues.put(Item_Constant.LISTID, listId);
        contentValues.put(Item_Constant.PRICE, price);
        long id = db.insert(Item_Constant.TABLE_NAME, null, contentValues);
        return id;
    }

    public long insertWholeList(List<Item> items, String listid) {
        db = helper.getWritableDatabase();
        List<Item> existed_items = getData(listid);
        if(existed_items.size() > 0) {
            String[] whereArgs = {listid};
            db.delete(Item_Constant.TABLE_NAME, Item_Constant.LISTID + "=?", whereArgs);
        }
        long id = 0;
        for(int i = 0; i < items.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Item_Constant.NAME, items.get(i).get_item_name());
            contentValues.put(Item_Constant.QUANTITY, items.get(i).get_item_quantity());
            contentValues.put(Item_Constant.LISTID, items.get(i).get_item_listid());
            contentValues.put(Item_Constant.PRICE, items.get(i).get_item_price());
            id = db.insert(Item_Constant.TABLE_NAME, null, contentValues);
            if(id < 0) {
                return id;
            }
        }

        return id;
    }

    public long updateData (String name, String quantity, String listId) {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Item_Constant.NAME, name);
        contentValues.put(Item_Constant.QUANTITY, quantity);
        contentValues.put(Item_Constant.LISTID, listId);
        long id = db.update(Item_Constant.TABLE_NAME, contentValues, Item_Constant.NAME + "='" + name + "' AND " + Item_Constant.LISTID + "='"+ listId + "'", null);
        return id;
    }

    public List<Item> getData(String listid)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Item_Constant.UID, Item_Constant.NAME, Item_Constant.QUANTITY, Item_Constant.LISTID, Item_Constant.PRICE};

        Cursor cursor = db.query(Item_Constant.TABLE_NAME, columns, null, null, null, null, null);
        List<Item> returning_items = new ArrayList<Item>();

        // Checking if the item belongs to the db
        int index1 = cursor.getColumnIndex(Item_Constant.LISTID);
        int index2 = cursor.getColumnIndex(Item_Constant.QUANTITY);
        int index3 = cursor.getColumnIndex(Item_Constant.NAME);
        int index4 = cursor.getColumnIndex(Item_Constant.UID);
        int index5 = cursor.getColumnIndex(Item_Constant.PRICE);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(index1).equals(listid)) {
                returning_items.add(new Item(cursor.getString(index3), Integer.parseInt(cursor.getString(index2)), listid, cursor.getString(index4), cursor.getString(index5)));
            }
            cursor.moveToNext();
        }

        return returning_items;
    }

    public String getSelectedData(String name, String listId)
    {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Item_Constant.NAME, Item_Constant.QUANTITY};

        // TODO: Include the list id in select clause
        String selection = Item_Constant.NAME + "='" + name + "' AND " + Item_Constant.LISTID + "='" + listId + "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Item_Constant.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Item_Constant.NAME);
            int index2 = cursor.getColumnIndex(Item_Constant.QUANTITY);
            String itemName = cursor.getString(index1);
            String itemCount = cursor.getString(index2);
            buffer.append(itemName + " " + itemCount + "\n");
        }
        return buffer.toString();
    }

    public int deleteRow(String itemName, String listid){
        SQLiteDatabase db = helper.getWritableDatabase();
        // TODO: check if the list id is the same
        String[] whereArgs = {itemName, listid};
        int count = db.delete(Item_Constant.TABLE_NAME, Item_Constant.NAME + "=? AND " + Item_Constant.LISTID + "=?", whereArgs);
        return count;
    }
}
