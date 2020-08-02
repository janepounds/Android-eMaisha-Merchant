package com.cabral.emaishamerchantApp.suppliers;

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
import com.cabral.emaishamerchantApp.HomeActivity;
import com.cabral.emaishamerchantApp.R;
import com.cabral.emaishamerchantApp.database.DatabaseAccess;
import com.cabral.emaishamerchantApp.database.DatabaseOpenHelper;
import com.cabral.emaishamerchantApp.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class AddSuppliersActivity extends BaseActivity {


    ProgressDialog loading;
    EditText etxtSuppliersName,etxtSuppliersContactPerson,etxtSuppliersAddress,etxtSuppliersAddressTwo,etxtSuppliersCell,etxtSuppliersEmail;
    ImageView imgSupplier;
    String mediaPath, encodedImage = "N/A";
    TextView txtAddSuppliers,txtChooseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_suppliers);

        etxtSuppliersName=findViewById(R.id.etxt_supplier_name);
        etxtSuppliersContactPerson=findViewById(R.id.etxt_supplier_contact_name);
        etxtSuppliersCell=findViewById(R.id.etxt_supplier_cell);
        etxtSuppliersEmail=findViewById(R.id.etxt_supplier_email);
        etxtSuppliersAddress=findViewById(R.id.etxt_supplier_address);
        etxtSuppliersAddressTwo = findViewById(R.id.etxt_supplier_address_two);
        imgSupplier = findViewById(R.id.supplier_image);
        txtChooseImage = findViewById(R.id.txt_choose_supplier_image);
        txtAddSuppliers=findViewById(R.id.txt_add_supplier);

        txtChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddSuppliersActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });

        imgSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddSuppliersActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });


        txtAddSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String suppliers_name=etxtSuppliersName.getText().toString().trim();
                String suppliers_contact_person=etxtSuppliersContactPerson.getText().toString().trim();
                String suppliers_cell=etxtSuppliersCell.getText().toString().trim();
                String suppliers_email=etxtSuppliersEmail.getText().toString().trim();
                String suppliers_address=etxtSuppliersAddress.getText().toString().trim();
                String suppliers_address_two = etxtSuppliersAddressTwo.getText().toString().trim();


                if (suppliers_name.isEmpty())
                {
                    etxtSuppliersName.setError(getString(R.string.enter_suppliers_name));
                    etxtSuppliersName.requestFocus();
                }

               else if (suppliers_contact_person.isEmpty())
                {
                    etxtSuppliersContactPerson.setError(getString(R.string.enter_suppliers_contact_person_name));
                    etxtSuppliersContactPerson.requestFocus();
                }

                else if (suppliers_cell.isEmpty())
                {
                    etxtSuppliersCell.setError(getString(R.string.enter_suppliers_cell));
                    etxtSuppliersCell.requestFocus();
                }


                else if (suppliers_email.isEmpty() || !suppliers_email.contains("@") || !suppliers_email.contains("."))
                {
                    etxtSuppliersEmail.setError(getString(R.string.enter_valid_email));
                    etxtSuppliersEmail.requestFocus();
                }

                else if (suppliers_address.isEmpty())
                {
                    etxtSuppliersAddress.setError(getString(R.string.enter_suppliers_address));
                    etxtSuppliersAddress.requestFocus();
                }
                else if (suppliers_address_two.isEmpty())
                {
                    etxtSuppliersAddressTwo.setError(getString(R.string.enter_suppliers_address));
                    etxtSuppliersAddressTwo.requestFocus();
                }

                else
                {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddSuppliersActivity.this);
                    databaseAccess.open();

                    boolean check=databaseAccess.addSuppliers(suppliers_name,suppliers_contact_person,suppliers_cell,suppliers_email,suppliers_address,suppliers_address_two, encodedImage);

                    if (check)
                    {
                        Toasty.success(AddSuppliersActivity.this, R.string.suppliers_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AddSuppliersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    else {

                        Toasty.error(AddSuppliersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }




            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_supplier_menu, menu);
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
            case R.id.menu_import_supplier:

                fileChooser();

            default:
                return super.onOptionsItemSelected(item);
        }
    }








    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddSuppliersActivity.this);
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

                loading = new ProgressDialog(AddSuppliersActivity.this);
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
                        Toasty.success(AddSuppliersActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddSuppliersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }, 5000);


            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Log.d("Error : ", "" + e.getMessage());
                Toasty.error(AddSuppliersActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
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
                imgSupplier.setImageBitmap(selectedImage);

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
        new ChooserDialog(AddSuppliersActivity.this)


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
