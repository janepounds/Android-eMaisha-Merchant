package com.cabral.emaishamerchantsapp.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.R;

public class DepositsActivity extends AppCompatActivity {
    Button txtSubmit;
    EditText etxtPhone, etxtAccount, etxtAmount;
    LinearLayout linearAccount, linearPhone;
    CheckBox chkUseAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposits);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Cash Deposit");

        etxtPhone = findViewById(R.id.etxt_phone_number);
        etxtAccount = findViewById(R.id.etxt_customer_account);
        etxtAmount = findViewById(R.id.etxt_customer_amount);

        linearAccount = findViewById(R.id.linearAccount);
        linearPhone = findViewById(R.id.linearPhoneNumber);
        chkUseAccount = findViewById(R.id.checkBoxAccount);


        txtSubmit = findViewById(R.id.txt_deposits_submit);

        chkUseAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkUseAccount.isChecked()) {
                    linearPhone.setVisibility(View.GONE);
                    linearAccount.setVisibility(View.VISIBLE);
                    chkUseAccount.setVisibility(View.GONE);
                }
            }
        });


        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etxtPhone.getText().toString().trim();
                String account = etxtAccount.getText().toString().trim();
                String amount = etxtAmount.getText().toString().trim();


                if (chkUseAccount.isChecked()) {
                    if (account.equals("")) {
                        etxtAccount.setError("Account is required");
                        etxtAccount.requestFocus();
                        return;
                    }
                    if (amount.equals("")) {
                        etxtAmount.setError("Account is required");
                        etxtAmount.requestFocus();
                        return;
                    }

                    Intent intent = new Intent(DepositsActivity.this, DepositDetails.class);
                    intent.putExtra("account", account);
                    intent.putExtra("amount", amount);
                    intent.putExtra("type", "account");
                    startActivity(intent);

                } else {

                    if (phone.equals("")) {
                        etxtPhone.setError("Phone is required");
                        etxtPhone.requestFocus();
                        return;
                    }
                    if (amount.equals("")) {
                        etxtAmount.setError("Account is required");
                        etxtAmount.requestFocus();
                        return;
                    }

                    Intent intent = new Intent(DepositsActivity.this, DepositDetails.class);
                    intent.putExtra("phone", phone);
                    intent.putExtra("amount", amount);
                    intent.putExtra("type", "phone");
                    startActivity(intent);

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