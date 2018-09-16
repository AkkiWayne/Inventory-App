package com.example.android.inventoryapptry1;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.android.inventoryapptry1.data.ItemContract.ItemEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri mCurrentUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneNumberEditText;
    private boolean mItemHasChanged = false;
    private int givenQuantity;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener () {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_editor );
        Intent intent = getIntent ();
        mCurrentUri = intent.getData ();
        if (mCurrentUri == null) {
            setTitle ( getString ( R.string.edit_title_new_product ) );
            invalidateOptionsMenu ();
        } else {
            setTitle ( getString ( R.string.edit_title_edit_existing_product ) );
            getLoaderManager ().initLoader ( EXISTING_ITEM_LOADER, null, this );
        }
        mNameEditText =  findViewById ( R.id.edit_product_name );
        mPriceEditText =  findViewById ( R.id.edit_price_field );
        mQuantityEditText =  findViewById ( R.id.edit_quantity );
        mSupplierNameEditText =  findViewById ( R.id.edit_supplier_name_text_field );
        mSupplierPhoneNumberEditText =  findViewById ( R.id.edit_phone_text_field );
        Button mIncrease = findViewById ( R.id.edit_quantity_increase );
        Button mDecrease =  findViewById ( R.id.edit_quantity_decrease );
        mNameEditText.setOnTouchListener ( mTouchListener );
        mPriceEditText.setOnTouchListener ( mTouchListener );
        mQuantityEditText.setOnTouchListener ( mTouchListener );
        mSupplierNameEditText.setOnTouchListener ( mTouchListener );
        mSupplierPhoneNumberEditText.setOnTouchListener ( mTouchListener );
        mIncrease.setOnTouchListener ( mTouchListener );
        mDecrease.setOnTouchListener ( mTouchListener );
        mIncrease.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText ().toString ();
                if (TextUtils.isEmpty ( quantity )) {
                    Toast.makeText ( EditorActivity.this, R.string.editor_quantity_field_cant_be_empty, Toast.LENGTH_SHORT ).show ();
                    return;
                } else {
                    givenQuantity = Integer.parseInt ( quantity );
                    mQuantityEditText.setText ( String.valueOf ( givenQuantity + 1 ) );
                }
            }
        } );
        mDecrease.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText ().toString ();
                if (TextUtils.isEmpty ( quantity )) {
                    Toast.makeText ( EditorActivity.this, R.string.editor_quantity_field_cant_be_empty, Toast.LENGTH_SHORT ).show ();
                    return;
                } else {
                    givenQuantity = Integer.parseInt ( quantity );
                    if ((givenQuantity - 1) >= 0) {
                        mQuantityEditText.setText ( String.valueOf ( givenQuantity - 1 ) );
                    } else {
                        Toast.makeText ( EditorActivity.this, R.string.editor_quantity_cant_be_less_then_0, Toast.LENGTH_SHORT ).show ();
                    }
                }
            }
        } );
        final Button mPhoneCall = findViewById ( R.id.call_supplier_button );
        mPhoneCall.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String phoneNumber = mSupplierPhoneNumberEditText.getText ().toString ().trim ();
                Intent intent = new Intent ( Intent.ACTION_DIAL );
                intent.setData ( Uri.parse ( "tel:" + phoneNumber ) );
                if (intent.resolveActivity ( getPackageManager () ) != null) {
                    startActivity ( intent );
                }
            }
        } );
    }
    private void saveProductItem() {
        String productNameString = mNameEditText.getText().toString().trim ();
        String priceString = mPriceEditText.getText ().toString ().trim ();
        String quantityString = mQuantityEditText.getText ().toString ().trim ();
        String supplierNameString = mSupplierNameEditText.getText ().toString ().trim ();
        String supplierPhoneNumberString = mSupplierPhoneNumberEditText.getText ().toString ().trim ();


        if (mCurrentUri == null &&
                TextUtils.isEmpty ( productNameString ) && TextUtils.isEmpty ( priceString ) &&
                TextUtils.isEmpty ( quantityString ) && TextUtils.isEmpty ( supplierNameString ) &&
                TextUtils.isEmpty ( supplierPhoneNumberString ))
            {
                    Toast.makeText ( this, getString ( R.string.editor_fill_in ), Toast.LENGTH_LONG ).show ();

                    return ;
            }
        if (TextUtils.isEmpty ( productNameString )) {
            mNameEditText.setError ( getString ( R.string.error_empty_field_name ) );

             return ;
        }
        if (TextUtils.isEmpty ( priceString )) {
            mPriceEditText.setError ( getString ( R.string.error_empty_field_price ) );

             return ;
        }
        if (TextUtils.isEmpty ( quantityString )) {
            mQuantityEditText.setError ( getString ( R.string.editor_quantity_field_cant_be_empty ) );

             return ;
        }
        if (TextUtils.isEmpty ( supplierNameString )) {
            mSupplierNameEditText.setError ( getString ( R.string.error_empty_field_supplier_name ) );

             return ;
        }
        if (TextUtils.isEmpty ( supplierPhoneNumberString )) {
            mSupplierPhoneNumberEditText.setError ( getString ( R.string.error_empty_field_supplier_phone_number ) );

             return ;
        }


        ContentValues values = new ContentValues ();
        values.put ( ItemEntry.COLUMN_NAME, productNameString );
        values.put ( ItemEntry.COLUMN_PRICE, priceString );
        values.put ( ItemEntry.COLUMN_QUANTITY, quantityString );
        values.put ( ItemEntry.COLUMN_SUPPLIER_NAME, supplierNameString );
        values.put ( ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString );

        if (mCurrentUri == null) {
            Uri newUri = getContentResolver ().insert ( ItemEntry.CONTENT_URI, values );
            if (newUri == null) {
                Toast.makeText ( this, getString ( R.string.insert_item_failed ), Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( this, getString ( R.string.insert_item_successful ), Toast.LENGTH_SHORT ).show ();
            }
            finish();
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_item_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ().inflate ( R.menu.menu_editor, menu );
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu ( menu );
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem ( R.id.action_delete );
            menuItem.setVisible ( false );
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId ()) {

            case R.id.action_save:
                saveProductItem ();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog ();
                return true;

            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                            }
                        };

                showUnsavedChangedDialog ( discardButtonClickListener );
                return true;
        }
        return super.onOptionsItemSelected ( item );
    }
    @Override
    public void onBackPressed() {

        if (!mItemHasChanged) {
            super.onBackPressed ();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish ();
                    }
                };
        showUnsavedChangedDialog ( discardButtonClickListener );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
                ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader ( this,
                mCurrentUri,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount () < 1) {
            return;
        }

        if (cursor.moveToFirst ()) {
            int itemNameColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_NAME );
            int priceColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_QUANTITY );
            int supplierNameColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_SUPPLIER_NAME );
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex ( ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER );

            String product = cursor.getString ( itemNameColumnIndex );
            int price = cursor.getInt ( priceColumnIndex );
            int quantity = cursor.getInt ( quantityColumnIndex );
            String supplierName = cursor.getString ( supplierNameColumnIndex );
            String supplierPhoneNumber = cursor.getString ( supplierPhoneNumberColumnIndex );

            mNameEditText.setText (product);
            mPriceEditText.setText (Integer.toString (price) );
            mQuantityEditText.setText (Integer.toString (quantity) );
            mSupplierNameEditText.setText ( supplierName );
            mSupplierPhoneNumberEditText.setText  ( supplierPhoneNumber );
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText ( "" );
        mPriceEditText.setText ( "" );
        mQuantityEditText.setText ( "" );
        mSupplierNameEditText.setText ( "" );
        mSupplierPhoneNumberEditText.setText ( "" );
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.delete_dialog_msg );
        builder.setPositiveButton ( R.string.delete, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteItem ();
            }
        } );

        builder.setNegativeButton ( R.string.cancel, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );
        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
    }

    private void showUnsavedChangedDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.unsaved_changes_dialog_msg );
        builder.setPositiveButton ( R.string.discard, discardButtonClickListener );
        builder.setNegativeButton ( R.string.keep_editing, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );
        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
    }
    private void deleteItem() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver ().delete ( mCurrentUri, null, null );
            if (rowsDeleted == 0) {
                Toast.makeText ( this, getString ( R.string.delete_product_failed ), Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( this, getString ( R.string.delete_product_successful ), Toast.LENGTH_SHORT ).show ();
            }
        }
        finish ();
    }
}
