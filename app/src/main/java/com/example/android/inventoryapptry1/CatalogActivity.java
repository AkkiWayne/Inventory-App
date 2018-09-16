package com.example.android.inventoryapptry1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.android.inventoryapptry1.data.ItemContract.ItemEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ITEM_LOADER = 0;
    ItemCursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_catalog );
        Button fab = (Button) findViewById ( R.id.fab );
        fab.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent ( CatalogActivity.this, EditorActivity.class );
                startActivity ( intent );
            }
        } );
        ListView ItemListView = (ListView) findViewById (R.id.list);
        View emptyView = findViewById ( R.id.empty_view );
        ItemListView.setEmptyView ( emptyView );
        mCursorAdapter = new ItemCursorAdapter ( this, null );
        ItemListView.setAdapter ( mCursorAdapter );
        ItemListView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent ( CatalogActivity.this, EditorActivity.class );
                Uri currentUri = ContentUris.withAppendedId ( ItemEntry.CONTENT_URI, id );
                intent.setData ( currentUri );
                startActivity ( intent );
            }
        } );
        getLoaderManager ().initLoader ( ITEM_LOADER, null, this );
    }
    private void insertItem() {
        ContentValues values = new ContentValues ();
        values.put ( ItemEntry.COLUMN_NAME, "Rice Packets (1kg)" );
        values.put ( ItemEntry.COLUMN_PRICE, 25 );
        values.put ( ItemEntry.COLUMN_QUANTITY, 10 );
        values.put ( ItemEntry.COLUMN_SUPPLIER_NAME, "SANJAY" );
        values.put ( ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 123456 );
        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }
    private void deleteAllItem() {
        int rowsDeleted = getContentResolver ().delete ( ItemEntry.CONTENT_URI, null, null );
        Log.v ( "CatalogActivity", rowsDeleted + " rows deleted from crochet database" );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.insert_dummy_data:
                insertItem();
                return true;
            case R.id.delete_all_entries:
                deleteAllItem();
                return true;
        }
        return super.onOptionsItemSelected ( item );
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
        };
        return new CursorLoader(this,
                ItemEntry.CONTENT_URI,
                projection,
                null,
                null,
                null );
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor ( data );
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor ( null );
    }
}