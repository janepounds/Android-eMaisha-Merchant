package com.cabral.emaishamerchantsapp.customers;

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

import com.cabral.emaishamerchantsapp.HomeActivity;
import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.utils.BaseActivity;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

public class EditCustomersActivity extends BaseActivity {


    EditText etxtCustomerName, etxtAddress, etxtAdressTwo, etxtCustomerCell, etxtCustomerEmail;
    TextView txtEditCustomer, txtUpdateInformation;
    ImageView imgCustomer;
    String mediaPath, encodedImage = "N/A";
    String get_customer_id, get_customer_name, get_customer_cell, get_customer_email, get_customer_address, get_customer_address_two, get_customer_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.edit_customer);


        etxtCustomerName = findViewById(R.id.etxt_customer_name);
        etxtCustomerCell = findViewById(R.id.etxt_customer_cell);
        etxtCustomerEmail = findViewById(R.id.etxt_email);
        etxtAddress = findViewById(R.id.etxt_address);
        etxtAdressTwo = findViewById(R.id.etxt_address_two);
        imgCustomer = findViewById(R.id.customer_image);
        txtEditCustomer = findViewById(R.id.txt_edit_customer);
        txtUpdateInformation = findViewById(R.id.txt_update_customer);

        get_customer_id = getIntent().getExtras().getString("customer_id");
        get_customer_name = getIntent().getExtras().getString("customer_name");
        get_customer_cell = getIntent().getExtras().getString("customer_cell");
        get_customer_email = getIntent().getExtras().getString("customer_email");
        get_customer_address = getIntent().getExtras().getString("customer_address");
        get_customer_address_two = getIntent().getExtras().getString("customer_address_two");
        get_customer_image = getIntent().getExtras().getString("customer_image");


        etxtCustomerName.setText(get_customer_name);
        etxtCustomerCell.setText(get_customer_cell);
        etxtCustomerEmail.setText(get_customer_email);
        etxtAddress.setText(get_customer_address);
        etxtAdressTwo.setText(get_customer_address_two);


        etxtCustomerName.setEnabled(false);
        etxtCustomerCell.setEnabled(false);
        etxtCustomerEmail.setEnabled(false);
        etxtAddress.setEnabled(false);
        etxtAdressTwo.setEnabled(false);

        txtUpdateInformation.setVisibility(View.INVISIBLE);

        imgCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditCustomersActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });


        txtEditCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etxtCustomerName.setEnabled(true);
                etxtCustomerCell.setEnabled(true);
                etxtCustomerEmail.setEnabled(true);
                etxtAddress.setEnabled(true);
                etxtAdressTwo.setEnabled(true);

                etxtCustomerName.setTextColor(Color.RED);
                etxtCustomerCell.setTextColor(Color.RED);
                etxtCustomerEmail.setTextColor(Color.RED);
                etxtAddress.setTextColor(Color.RED);
                etxtAdressTwo.setTextColor(Color.RED);
                txtUpdateInformation.setVisibility(View.VISIBLE);

            }
        });

        if (get_customer_image != null) {
            if (get_customer_image.length() < 6) {

                imgCustomer.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(get_customer_image, Base64.DEFAULT);
                imgCustomer.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        txtUpdateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String customer_name = etxtCustomerName.getText().toString().trim();
                String customer_cell = etxtCustomerCell.getText().toString().trim();
                String customer_email = etxtCustomerEmail.getText().toString().trim();
                String customer_address = etxtAddress.getText().toString().trim();
                String customer_address_two = etxtAdressTwo.getText().toString().trim();

                if (customer_name.isEmpty()) {
                    etxtCustomerName.setError(getString(R.string.enter_customer_name));
                    etxtCustomerName.requestFocus();
                } else if (customer_cell.isEmpty()) {
                    etxtCustomerCell.setError(getString(R.string.enter_customer_cell));
                    etxtCustomerCell.requestFocus();
                } else if (customer_email.isEmpty() || !customer_email.contains("@") || !customer_email.contains(".")) {
                    etxtCustomerEmail.setError(getString(R.string.enter_valid_email));
                    etxtCustomerEmail.requestFocus();
                } else if (customer_address.isEmpty()) {
                    etxtAddress.setError(getString(R.string.enter_customer_address));
                    etxtAddress.requestFocus();
                } else if (customer_address_two.isEmpty()) {
                    etxtAdressTwo.setError(getString(R.string.enter_customer_address));
                    etxtAdressTwo.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(EditCustomersActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.updateCustomer(get_customer_id, customer_name, customer_cell, customer_email, customer_address, customer_address_two, encodedImage);

                    if (check) {
                        Toasty.success(EditCustomersActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditCustomersActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(EditCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

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
