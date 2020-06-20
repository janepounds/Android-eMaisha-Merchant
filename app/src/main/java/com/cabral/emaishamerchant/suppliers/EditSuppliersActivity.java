package com.cabral.emaishamerchant.suppliers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishamerchant.HomeActivity;
import com.cabral.emaishamerchant.R;
import com.cabral.emaishamerchant.customers.EditCustomersActivity;
import com.cabral.emaishamerchant.database.DatabaseAccess;
import com.cabral.emaishamerchant.utils.BaseActivity;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class EditSuppliersActivity extends BaseActivity {

    EditText etxtSuppliersName, etxtSuppliersContactPerson, etxtSuppliersAddress, etxtSuppliersAddressTwo, etxtSuppliersCell, etxtSuppliersEmail;
    ImageView imgSupplier;
    String mediaPath, encodedImage = "N/A";
    TextView txtEditSuppliers, txtUpdateSuppliers,txtChooseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_suppliers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_suppliers);


        etxtSuppliersName = findViewById(R.id.etxt_supplier_name);
        etxtSuppliersContactPerson = findViewById(R.id.etxt_supplier_contact_name);
        etxtSuppliersCell = findViewById(R.id.etxt_supplier_cell);
        etxtSuppliersEmail = findViewById(R.id.etxt_supplier_email);
        etxtSuppliersAddress = findViewById(R.id.etxt_supplier_address);
        etxtSuppliersAddressTwo = findViewById(R.id.etxt_supplier_address_two);
        imgSupplier = findViewById(R.id.supplier_image);
        txtChooseImage = findViewById(R.id.txt_choose_supplier_image);

        txtUpdateSuppliers = findViewById(R.id.txt_update_suppliers);
        txtEditSuppliers = findViewById(R.id.txt_edit_suppliers);

        imgSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditSuppliersActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });

        txtChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditSuppliersActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });



        String get_suppliers_id = getIntent().getExtras().getString("suppliers_id");
        String get_suppliers_name = getIntent().getExtras().getString("suppliers_name");
        String get_suppliers_contact_person = getIntent().getExtras().getString("suppliers_contact_person");
        String get_suppliers_cell = getIntent().getExtras().getString("suppliers_cell");
        String get_suppliers_email = getIntent().getExtras().getString("suppliers_email");
        String get_suppliers_address = getIntent().getExtras().getString("suppliers_address");
        String get_suppliers_address_two = getIntent().getExtras().getString("suppliers_address_two");
       String get_supplier_image = getIntent().getExtras().getString("suppliers_image");


        etxtSuppliersName.setText(get_suppliers_name);
        etxtSuppliersContactPerson.setText(get_suppliers_contact_person);
        etxtSuppliersCell.setText(get_suppliers_cell);
        etxtSuppliersEmail.setText(get_suppliers_email);
        etxtSuppliersAddress.setText(get_suppliers_address);
        etxtSuppliersAddressTwo.setText(get_suppliers_address_two);

        if (get_supplier_image != null) {
            if (get_supplier_image.length() < 6) {

                imgSupplier.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(get_supplier_image, Base64.DEFAULT);
                imgSupplier.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        etxtSuppliersName.setEnabled(false);
        etxtSuppliersContactPerson.setEnabled(false);
        etxtSuppliersCell.setEnabled(false);
        etxtSuppliersEmail.setEnabled(false);
        etxtSuppliersAddress.setEnabled(false);
        etxtSuppliersAddressTwo.setEnabled(false);


        txtUpdateSuppliers.setVisibility(View.INVISIBLE);

        txtEditSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                etxtSuppliersName.setEnabled(true);
                etxtSuppliersContactPerson.setEnabled(true);
                etxtSuppliersCell.setEnabled(true);
                etxtSuppliersEmail.setEnabled(true);
                etxtSuppliersAddress.setEnabled(true);
                etxtSuppliersAddressTwo.setEnabled(true);


                etxtSuppliersName.setTextColor(Color.RED);
                etxtSuppliersContactPerson.setTextColor(Color.RED);
                etxtSuppliersCell.setTextColor(Color.RED);
                etxtSuppliersEmail.setTextColor(Color.RED);
                etxtSuppliersAddress.setTextColor(Color.RED);
                etxtSuppliersAddressTwo.setTextColor(Color.RED);


                txtUpdateSuppliers.setVisibility(View.VISIBLE);

            }
        });


        txtUpdateSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String suppliers_name = etxtSuppliersName.getText().toString().trim();
                String suppliers_contact_person = etxtSuppliersContactPerson.getText().toString().trim();
                String suppliers_cell = etxtSuppliersCell.getText().toString().trim();
                String suppliers_email = etxtSuppliersEmail.getText().toString().trim();
                String suppliers_address = etxtSuppliersAddress.getText().toString().trim();
                String suppliers_address_two = etxtSuppliersAddressTwo.getText().toString().trim();


                if (suppliers_name.isEmpty()) {
                    etxtSuppliersName.setError(getString(R.string.enter_suppliers_name));
                    etxtSuppliersName.requestFocus();
                } else if (suppliers_contact_person.isEmpty()) {
                    etxtSuppliersContactPerson.setError(getString(R.string.enter_suppliers_contact_person_name));
                    etxtSuppliersContactPerson.requestFocus();
                } else if (suppliers_cell.isEmpty()) {
                    etxtSuppliersCell.setError(getString(R.string.enter_suppliers_cell));
                    etxtSuppliersCell.requestFocus();
                } else if (suppliers_email.isEmpty() || !suppliers_email.contains("@") || !suppliers_email.contains(".")) {
                    etxtSuppliersEmail.setError(getString(R.string.enter_valid_email));
                    etxtSuppliersEmail.requestFocus();
                } else if (suppliers_address.isEmpty()) {
                    etxtSuppliersAddress.setError(getString(R.string.enter_suppliers_address));
                    etxtSuppliersAddress.requestFocus();
                }else if (suppliers_address_two.isEmpty()) {
                    etxtSuppliersAddressTwo.setError(getString(R.string.enter_suppliers_address));
                    etxtSuppliersAddress.requestFocus();
                }
                else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditSuppliersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateSuppliers(get_suppliers_id, suppliers_name, suppliers_contact_person, suppliers_cell, suppliers_email, suppliers_address, suppliers_address_two, encodedImage);

                    if (check) {
                        Toasty.success(EditSuppliersActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditSuppliersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(EditSuppliersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }


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

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
