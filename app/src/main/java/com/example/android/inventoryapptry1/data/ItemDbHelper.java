package com.example.android.inventoryapptry1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapptry1.data.ItemContract.ItemEntry;


public class ItemDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "product.db";
    public static final int DATABASE_VERSION = 4;
    public ItemDbHelper(Context context) {
        super ( context, DATABASE_NAME, null, DATABASE_VERSION );
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL,"
                + ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        db.execSQL ( SQL_CREATE_ITEM_TABLE );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}


