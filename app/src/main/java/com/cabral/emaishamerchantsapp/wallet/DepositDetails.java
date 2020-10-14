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

public class DepositDetails extends AppCompatActivity {
    TextView txtType,txtTypeNumber,txtTotal,txtAmount;
    Button txtSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_details);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Deposit Details");

        txtType = findViewById(R.id.txtType);
        txtTypeNumber = findViewById(R.id.txtType_number);
        txtTotal = findViewById(R.id.txtTotalAmount);
        txtAmount = findViewById(R.id.txtAmount);

        if(getIntent().getExtras().getString("type").equals("account")){
            txtType.setText("Account Number");
            txtTypeNumber.setText(getIntent().getExtras().getString("account"));
        }
        if(getIntent().getExtras().getString("type").equals("phone")){
            txtType.setText("Phone Number");
            txtTypeNumber.setText(getIntent().getExtras().getString("phone"));
        }

        txtTotal.setText(getIntent().getExtras().getString("amount"));
        txtAmount.setText(getIntent().getExtras().getString("amount"));


        txtSubmit = findViewById(R.id.txt_card_confirm);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DepositDetails.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(DepositDetails.this).inflate(R.layout.custom_dialog_enter_agent_pin, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                EditText input_create_pin = dialogView.findViewById(R.id.etxt_create_agent_pin);
                TextView card_sumit = dialogView.findViewById(R.id.txt_custom_add_agent_submit_pin);
                card_sumit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(DepositDetails.this, WalletActivity.class);
                        startActivity(intent);
                        AlertDialog.Builder builder = new AlertDialog.Builder(DepositDetails.this);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(DepositDetails.this).inflate(R.layout.custom_dialog_agent_deposit_success, viewGroup, false);
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