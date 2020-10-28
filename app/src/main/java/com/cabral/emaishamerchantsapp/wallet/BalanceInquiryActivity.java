package com.cabral.emaishamerchantsapp.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.R;

public class BalanceInquiryActivity extends AppCompatActivity {
    Button txtSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Balance Inquiry");

        txtSubmit = findViewById(R.id.txt_balance_inquiry_submit);

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BalanceInquiryActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(BalanceInquiryActivity.this).inflate(R.layout.custom_dialog_enter_balance_inquiry_pin, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                EditText input_withdraw_pin = dialogView.findViewById(R.id.etxt_enter_balance_pin);
                TextView card_sumit = dialogView.findViewById(R.id.txt_custom_balance_submit_pin);

                card_sumit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BalanceInquiryActivity.this, WalletActivity.class);
                        startActivity(intent);

                        AlertDialog.Builder builder = new AlertDialog.Builder(BalanceInquiryActivity.this);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(BalanceInquiryActivity.this).inflate(R.layout.custom_dialog_balance_success, viewGroup, false);
                        builder.setView(dialogView);
                        AlertDialog alertSuccessDialog = builder.create();
                        alertSuccessDialog.show();
                    }
                });
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