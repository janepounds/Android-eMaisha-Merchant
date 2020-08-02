package com.cabral.emaishamerchantApp.settings.payment_method;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishamerchantApp.R;
import com.cabral.emaishamerchantApp.database.DatabaseAccess;
import com.cabral.emaishamerchantApp.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class AddPaymentMethodActivity extends BaseActivity {


    EditText etxtPaymentMethod;
    TextView txtAddPaymentMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_payment_method);

        etxtPaymentMethod = findViewById(R.id.etxt_payment_method_name);
        txtAddPaymentMethod = findViewById(R.id.txt_add_payment_method);


        txtAddPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String payment_method_name = etxtPaymentMethod.getText().toString().trim();

                if (payment_method_name.isEmpty()) {
                    etxtPaymentMethod.setError(getString(R.string.enter_payment_method_name));
                    etxtPaymentMethod.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddPaymentMethodActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addPaymentMethod(payment_method_name);

                    if (check) {
                        Toasty.success(AddPaymentMethodActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPaymentMethodActivity.this, PaymentMethodActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(AddPaymentMethodActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });


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
