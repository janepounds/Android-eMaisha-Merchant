package com.cabral.emaishamerchantsapp.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cabral.emaishamerchantsapp.R;

public class WithdrawActivity extends AppCompatActivity {
    TextView txtSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Cash Withdraw");

        txtSubmit = findViewById(R.id.txt_withdraw_submit);

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawActivity.this,WithdrawDetailsActivity.class );
                startActivity(intent);
            }
        });
    }
}