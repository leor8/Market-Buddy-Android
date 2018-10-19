package com.example.leoruan.market_buddy_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class List_db {
    private SQLiteDatabase list_db;
    private Context context;
    private final List_Helper helper;

    public List_db(Context c) {
        context = c;
        helper = new List_Helper(context);
    }

    public long InsertData (String name) {
        list_db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.NAME, name);
        long id = list_db.insert(Constant.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor GetData() {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constant.UID, Constant.NAME};
        Cursor cursor = db.query(Constant.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public String getSelectedData(String name)
    {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constant.NAME};

        String selection = Constant.NAME + "='" + name + "'";
        Cursor cursor = db.query(Constant.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Constant.NAME);
            String listName = cursor.getString(index1);
            buffer.append(listName);
        }
        return buffer.toString();
    }

    public int deleteData(String list_name){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {list_name};
        int count = db.delete(Constant.TABLE_NAME, Constant.NAME + "=?", whereArgs);
        return count;
    }

}
