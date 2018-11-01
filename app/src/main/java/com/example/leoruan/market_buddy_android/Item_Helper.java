package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class Item_Helper extends SQLiteOpenHelper {
    private Context context;

    private static final String CREATE_TABLE =
            "CREATE TABLE " +
                    Item_Constant.TABLE_NAME + " (" +
                    Item_Constant.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Item_Constant.NAME + " TEXT, " +
                    Item_Constant.QUANTITY + " TEXT, " +
                    Item_Constant.PRICE + " TEXT, " +
                    Item_Constant.LISTID + " TEXT);" ;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Item_Constant.TABLE_NAME;

    public Item_Helper(Context context){
        super (context, Item_Constant.DATABASE_NAME, null, Item_Constant.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d("DEBUG555", CREATE_TABLE);
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Toast.makeText(context, "Error Creating item db", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e) {
            Toast.makeText(context, "Error updating item db", Toast.LENGTH_LONG).show();
        }
    }


}
