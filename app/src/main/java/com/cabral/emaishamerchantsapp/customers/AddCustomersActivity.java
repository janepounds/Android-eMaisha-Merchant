package com.cabral.emaishamerchantsapp.customers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.cabral.emaishamerchantsapp.HomeActivity;
import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.database.DatabaseOpenHelper;
import com.cabral.emaishamerchantsapp.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class AddCustomersActivity extends BaseActivity {


    ProgressDialog loading;
    EditText etxtCustomerName, etxtAddress, etxtAddressTwo, etxtCustomerCell, etxtCustomerEmail;
    ImageView imgCustomer;
    String mediaPath, encodedImage = "N/A";
    TextView txtAddCustomer, txtChooseImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_customer);


        etxtCustomerName = findViewById(R.id.etxt_customer_name);
        etxtCustomerCell = findViewById(R.id.etxt_customer_cell);
        etxtCustomerEmail = findViewById(R.id.etxt_email);
        etxtAddress = findViewById(R.id.etxt_address);
        etxtAddressTwo = findViewById(R.id.etxt_address_two);
        txtAddCustomer = findViewById(R.id.txt_add_customer);
        imgCustomer = findViewById(R.id.customer_image);
        txtChooseImage = findViewById(R.id.txt_choose_customer_image);

        txtChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddCustomersActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });

        imgCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddCustomersActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });



        txtAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String customer_name = etxtCustomerName.getText().toString().trim();
                String customer_cell = etxtCustomerCell.getText().toString().trim();
                String customer_email = etxtCustomerEmail.getText().toString().trim();
                String customer_address = etxtAddress.getText().toString().trim();
                String customer_address_two = etxtAddressTwo.getText().toString().trim();


                if (customer_name.isEmpty()) {
                    etxtCustomerName.setError(getString(R.string.enter_customer_name));
                    etxtCustomerName.requestFocus();
                } else if (customer_cell.isEmpty()) {
                    etxtCustomerCell.setError(getString(R.string.enter_customer_cell));
                    etxtCustomerCell.requestFocus();
                }

                else if (customer_email.isEmpty() || !customer_email.contains("@") || !customer_email.contains(".")) {
                    etxtCustomerEmail.setError(getString(R.string.enter_valid_email));
                    etxtCustomerEmail.requestFocus();
                }

                else if (customer_address.isEmpty()) {
                    etxtAddress.setError(getString(R.string.enter_customer_address));
                    etxtAddress.requestFocus();
                }
                else if (customer_address_two.isEmpty()) {
                    etxtAddress.setError(getString(R.string.enter_customer_address));
                    etxtAddress.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCustomersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addCustomer(customer_name, customer_cell, customer_email, customer_address, customer_address_two,encodedImage);

                    if (check) {
                        Toasty.success(AddCustomersActivity.this, R.string.customer_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCustomersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(AddCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_product_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_import:


                fileChooser();
                return true;




            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCustomersActivity.this);
        databaseAccess.open();


        File file = new File(directory_path);
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show();
            return;
        }

        // Is used to import data from excel without dropping table
//         ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(),DatabaseOpenHelper.DATABASE_NAME);

        // if you want to add column in excel and import into DB, you must drop the table
        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, false);
        // Import EXCEL FILE to SQLite
        excelToSQLite.importFromFile(directory_path, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(AddCustomersActivity.this);
                loading.setMessage(getString(R.string.data_importing_please_wait));
                loading.setCancelable(false);
                loading.show();

            }

            @Override
            public void onCompleted(String dbName) {


                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        loading.dismiss();
                        Toasty.success(AddCustomersActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCustomersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }, 5000);


            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Log.d("Error : ", "" + e.getMessage());
                Toasty.error(AddCustomersActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (requestCode == 1213 && resultCode == RESULT_OK && null != data) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);
                imgCustomer.setImageBitmap(selectedImage);

                encodedImage = encodeImage(selectedImage);


            }


        } catch (Exception e) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }



    public void fileChooser() {
        new ChooserDialog(AddCustomersActivity.this)


                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
//                        Toast.makeText(AddCustomersActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                        onImport(path);
                    }
                })
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Log.d("CANCEL", "CANCEL");
                        dialog.cancel(); // MUST have
                    }
                })
                .build()
                .show();
    }


}
