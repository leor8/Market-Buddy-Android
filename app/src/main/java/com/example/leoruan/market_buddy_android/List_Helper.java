package com.example.leoruan.market_buddy_android;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class List_Helper extends SQLiteOpenHelper {
    private Context context;


    private static final String CREATE_TABLE =
            "CREATE TABLE "+
                    Constant.TABLE_NAME + " (" +
                    Constant.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Constant.NAME + " TEXT);" ;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + Constant.TABLE_NAME;

    public List_Helper(Context context){
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Toast.makeText(context, "Error creating/updating database", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (SQLException e) {
            Toast.makeText(context, "Error updating database", Toast.LENGTH_LONG).show();
        }
    }
}
