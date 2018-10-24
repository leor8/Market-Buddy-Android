package com.example.leoruan.market_buddy_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class Item_db {

    private SQLiteDatabase db;
    private Context context;
    private final Item_Helper helper;

    public Item_db (Context c){
        context = c;
        helper = new Item_Helper(context);
    }

    public long insertData (String name, String quantity, String listId)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Item_Constant.NAME, name);
        contentValues.put(Item_Constant.QUANTITY, quantity);
        contentValues.put(Item_Constant.LISTID, listId);
        long id = db.insert(Item_Constant.TABLE_NAME, null, contentValues);
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

    public Cursor getData(String listid)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        // TODO: Check if list id is the same as requested
        String[] columns = {Item_Constant.UID, Item_Constant.NAME, Item_Constant.QUANTITY, Item_Constant.LISTID};

        // TODO: there is a problem with query method getting items, fix it before anything else
        Cursor cursor = db.query(Item_Constant.TABLE_NAME, columns, null, null, null, null, null);

        // Checking if the item belongs to the db
        int index1 = cursor.getColumnIndex(Item_Constant.LISTID);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d("DEBUG555", cursor.getString((index1)));
            Log.d("DEBUG555",  "listid is: " + listid);
            if(cursor.getString(index1).equals(listid)) {
                // TODO: Return cursor is not the right way to go. Should return arralist.
                return cursor;
            }
            cursor.moveToNext();
        }

        Toast.makeText(context, "Fail to retrieve data", Toast.LENGTH_SHORT).show();
        return null;
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
