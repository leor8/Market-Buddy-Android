package com.example.leoruan.market_buddy_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Price_db {
    private SQLiteDatabase db;
    private Context context;
    private final Price_Helper helper;
    List<String> store_names = new ArrayList<String>();

    public Price_db (Context c){
        context = c;
        helper = new Price_Helper(context);
        store_names.add("Save On Foods");
        store_names.add("Safeway");
        store_names.add("Walmart");
        store_names.add("Nesters Market");
        store_names.add("Superstore");
        store_names.add("TNT Supermarket");
    }

    public long insertData (String price, String itemId)
    {
        db = helper.getWritableDatabase();
        float prices = Float.valueOf(price);
        long id = 0;
        for(int i = 0; i < store_names.size(); i++) {
            if (id >= 0) {
                String priceFinal = priceGenerate(prices);
                ContentValues contentValues = new ContentValues();
                contentValues.put(Price_Constant.NAME, store_names.get(i));
                contentValues.put(Price_Constant.ITEMPRICE, priceFinal);
                contentValues.put(Price_Constant.ITEMID, itemId);
                id = db.insert(Price_Constant.TABLE_NAME, null, contentValues);
            } else {
                return -1;
            }
        }
        return id;
    }

    public String getPrice(String storeName, String itemId){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Price_Constant.ITEMPRICE};
        String selection = Price_Constant.NAME + "='" + storeName + "' AND " + Price_Constant.ITEMID + "='" + itemId + "'";
//        String selection = Price_Constant.NAME + "='" + storeName + "'";
        Cursor cursor = db.query(Price_Constant.TABLE_NAME, columns, selection, null, null, null, null);
        String price = "0";
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            int index1 = cursor.getColumnIndex(Price_Constant.ITEMPRICE);
            price = cursor.getString(index1);
        }
        return price;
    }

    private String priceGenerate(float price) {
        int max = 100;
        int min = -100;
        int range = max - min + 1;
        float rand = (float)(Math.random() * range) + min;
        float inpecent = rand/1000;
        price = price * (1 + inpecent);
        return String.valueOf(price);
    }
}
