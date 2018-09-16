package com.example.android.inventoryapptry1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.android.inventoryapptry1.data.ItemContract.ItemEntry;


import java.util.Objects;

public class ItemProvider extends ContentProvider {
    private static final String LOG_TAG = ItemProvider.class.getSimpleName ();
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher ( UriMatcher.NO_MATCH );
    static {
        sUriMatcher.addURI ( ItemContract.CONTENT_AUTHORITY,ItemContract.PATH_ITEMS, ITEMS );
        sUriMatcher.addURI ( ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS+"/#", ITEM_ID);
    }
    private ItemDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper (getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase ();
        Cursor cursor;
        int match = sUriMatcher.match (uri);
        switch (match) {
            case ITEMS:
                cursor = database.query ( ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder );
                break;
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query ( ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder );
                break;
            default:
                throw new IllegalArgumentException ( "Cannot query unknown URI" + uri );
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
    public Uri insert( Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match (uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri,contentValues);
            default:
                throw new IllegalArgumentException ( "Insertion is not supported for " + uri );
        }
    }
    private Uri insertItem(Uri uri, ContentValues values) {
        String productName = values.getAsString ( ItemEntry.COLUMN_NAME );
        if (productName == null) {
            throw new IllegalArgumentException ( "Item needs a name" );
        }
        Integer price = values.getAsInteger ( ItemEntry.COLUMN_PRICE );
        if (price != null && price < 0) {
            throw new IllegalArgumentException ( "Item needs a price" );
        }
        Integer quantity = values.getAsInteger ( ItemEntry.COLUMN_QUANTITY );
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException ( "Quantity is required" );
        }
        String supplierName = values.getAsString ( ItemEntry.COLUMN_SUPPLIER_NAME );
        if (supplierName == null) {
            throw new IllegalArgumentException ( "Name of supplier is required" );
        }
        String supplierPhoneNumber = values.getAsString ( ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER );
        if (supplierPhoneNumber == null)
            throw new IllegalArgumentException ( " A valid phone number is required" );
        SQLiteDatabase database = mDbHelper.getWritableDatabase ();
        long id = database.insert ( ItemEntry.TABLE_NAME, null, values );
        if (id == -1) {
            Log.e ( LOG_TAG, "Failed to insert row for " + uri );
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId ( uri, id );
    }
    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase ();
        int rowsDeleted;
        final int match = sUriMatcher.match ( uri );
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete ( ItemEntry.TABLE_NAME, selection, selectionArgs );
                break;
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf ( ContentUris.parseId ( uri ) )};
                rowsDeleted = database.delete ( ItemEntry.TABLE_NAME, selection, selectionArgs );
                break;
            default:
                throw new IllegalArgumentException ( "Delete is not supported for " + uri );
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public int update( Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match ( uri );
        switch (match) {
            case ITEMS:
                return updateItem ( uri, contentValues, selection, selectionArgs );
            case ITEM_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf ( ContentUris.parseId ( uri ) )};
                return updateItem ( uri, contentValues, selection, selectionArgs );
            default:
                throw new IllegalArgumentException ( "Update is not supported for " + uri );
        }
    }
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ItemEntry.COLUMN_NAME)) {
            String productName = values.getAsString(ItemEntry.COLUMN_NAME);
            if (productName == null) {
                throw new IllegalArgumentException(" Product requires a name");
            }
        }
        if (values.containsKey(ItemEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ItemEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires a valid price either 0 or above");
            }
        }
        if (values.containsKey(ItemEntry.COLUMN_NAME)) {
            String supplierName = values.getAsString(ItemEntry.COLUMN_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Please insert a valid supplier name");
            }
        }
        if (values.containsKey(ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber == null) {
                throw new IllegalArgumentException("Please insert a valid phone number");
            }
            if (values.size() == 0) {
                return 0;
            }
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
        @Override
        public String getType(Uri uri) {
            final int match = sUriMatcher.match ( uri );
            switch (match) {
                case ITEMS:
                    return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
                case ITEM_ID:
                    return ItemEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException ( "Unknown URI" + uri + " with match " + match );
            }
        }
}


