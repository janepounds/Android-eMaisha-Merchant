package com.cabral.emaishamerchantsapp.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.R;

public class WithdrawActivity extends AppCompatActivity {
    Button txtSubmit;
    EditText etxtAccountNumber, etxtAmount, etxtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Cash Withdraw");

        txtSubmit = findViewById(R.id.txt_withdraw_submit);
        etxtAccountNumber = findViewById(R.id.etxt_customer_withdraw_account);
        etxtAmount = findViewById(R.id.etxt_customer_withdraw_amount);
        etxtPhoneNumber = findViewById(R.id.etxt_customer_withdraw_phone_number);


        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account_number = etxtAccountNumber.getText().toString().trim();
                String amount = etxtAmount.getText().toString().trim();
                String phone_number = etxtPhoneNumber.getText().toString().trim();

                if (account_number.equals("")) {
                    etxtAccountNumber.setError("Account Number is required");
                    etxtAccountNumber.requestFocus();
                    return;
                }
                if (amount.equals("")) {
                    etxtAmount.setError("Amount is required");
                    etxtAmount.requestFocus();
                    return;
                }
                if (phone_number.equals("")) {
                    etxtPhoneNumber.setError("Phone Number is required");
                    etxtPhoneNumber.requestFocus();
                    return;
                }

                Intent intent = new Intent(WithdrawActivity.this, WithdrawDetailsActivity.class);
                startActivity(intent);
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