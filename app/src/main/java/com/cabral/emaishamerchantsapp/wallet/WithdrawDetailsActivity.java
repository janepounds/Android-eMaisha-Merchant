package com.cabral.emaishamerchantsapp.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.R;

public class WithdrawDetailsActivity extends AppCompatActivity {

    TextView txtConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_details);

        txtConfirm = findViewById(R.id.txt_withdraw_confirm);

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawDetailsActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(WithdrawDetailsActivity.this).inflate(R.layout.custom_dialog_enter_customer_pin, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                EditText input_withdraw_pin = dialogView.findViewById(R.id.etxt_create_withdraw_pin);
                TextView card_sumit = dialogView.findViewById(R.id.txt_custom_withdraw_submit_pin);

                card_sumit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(WithdrawDetailsActivity.this, WalletActivity.class);
                        startActivity(intent);

                        AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawDetailsActivity.this);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(WithdrawDetailsActivity.this).inflate(R.layout.custom_dialog_withdraw_success, viewGroup, false);
                        builder.setView(dialogView);
                        AlertDialog alertSuccessDialog = builder.create();
                        alertSuccessDialog.show();
                    }
                });
            }
        });
    }
}