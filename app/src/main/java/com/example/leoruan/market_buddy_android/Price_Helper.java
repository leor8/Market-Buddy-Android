package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class Price_Helper extends SQLiteOpenHelper {
    private Context context;

    private static final String CREATE_TABLE =
            "CREATE TABLE " +
                    Price_Constant.TABLE_NAME + " (" +
                    Price_Constant.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Price_Constant.NAME + " TEXT, " +
                    Price_Constant.ITEMID + " TEXT, " +
                    Price_Constant.ITEMPRICE + " TEXT);" ;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Price_Constant.TABLE_NAME;

    public Price_Helper(Context context){
        super(context, Price_Constant.DATABASE_NAME, null, Price_Constant.DATABASE_VERSION);
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
