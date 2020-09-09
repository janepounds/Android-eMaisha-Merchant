package com.cabral.emaishamerchantsapp.wallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.orders.OnlineOrderDetailsActivity;

public class CardDetail extends AppCompatActivity {
    TextView txtOpenCardPInDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Card Detail");

        txtOpenCardPInDialog = findViewById(R.id.txt_card_submit);

        txtOpenCardPInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CardDetail.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(CardDetail.this).inflate(R.layout.enter_card_pin, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                EditText input_create_pin = dialogView.findViewById(R.id.etxt_create_pin);
                EditText input_confirm_pin = dialogView.findViewById(R.id.etxt_confirm_pin);
                TextView card_sumit = dialogView.findViewById(R.id.txt_custom_add_submit_pin);
                card_sumit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Closing","Close dialog");
                        alertDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(CardDetail.this);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(CardDetail.this).inflate(R.layout.custom_dialog_pin_sucess, viewGroup, false);
                        builder.setView(dialogView);
                        AlertDialog alertPinDialog = builder.create();
                        alertPinDialog.show();

                        Intent intent = new Intent(CardDetail.this,WalletActivity.class );
                        startActivity(intent);


                    }
                });
            }
        });
    }

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