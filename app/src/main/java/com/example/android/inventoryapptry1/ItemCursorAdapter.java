package com.example.android.inventoryapptry1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.inventoryapptry1.data.ItemContract.ItemEntry;

public class ItemCursorAdapter extends CursorAdapter {
    public ItemCursorAdapter(Context context, Cursor cursor) {
        super ( context, cursor, 0 );
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from ( context ).inflate ( R.layout.list_item, parent, false );
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView NameTextView =  view.findViewById ( R.id.product_name );
        TextView priceTextView =  view.findViewById ( R.id.price );
        TextView quantityTextView =  view.findViewById ( R.id.quantity );
        Button sellButton =  view.findViewById ( R.id.sell_button );
        int nameColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_NAME );
        int priceColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_PRICE );
        int quantityColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_QUANTITY );
        String itemName = "Name of Item: " + cursor.getString ( nameColumnIndex );
        String price = "Price: " + cursor.getString ( priceColumnIndex ) + "Rs";
        String quantity = "Quantity available: " + cursor.getString ( quantityColumnIndex );
        NameTextView.setText ( itemName );
        priceTextView.setText ( price );
        quantityTextView.setText ( quantity );
        String currentQuantityString = cursor.getString ( quantityColumnIndex );
        final int currentQuantity = Integer.valueOf ( currentQuantityString );
        final int productId = cursor.getInt ( cursor.getColumnIndex ( ItemEntry._ID ) );
        sellButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (currentQuantity > 0) {
                    int newQuantity = currentQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId ( ItemEntry.CONTENT_URI, productId );
                    ContentValues values = new ContentValues ();
                    values.put ( ItemEntry.COLUMN_QUANTITY, newQuantity );
                    context.getContentResolver ().update ( quantityUri, values, null, null );
                }
                else {
                    Toast.makeText ( context, "This product currently not available", Toast.LENGTH_SHORT ).show ();
                }
            }
        } );
    }
}

