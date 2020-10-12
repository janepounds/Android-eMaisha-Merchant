package com.cabral.emaishamerchantsapp.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.R;

public class SettlementActivity extends AppCompatActivity {
    TextView txtSubmit;
    String[] types = {"Bank", "Mobile Money"};
    LinearLayout linearBank, linearAccount, linearHolder, linearMobile;
    AutoCompleteTextView act_settle_to;
    EditText etxt_settle_bank, etxt_seetle_mobile_money, etxt_settle_account, etxt_settle_holder_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Settlement");

        txtSubmit = findViewById(R.id.txt_settle_submit);
        act_settle_to = findViewById(R.id.act_settlement_type);

        etxt_settle_account = findViewById(R.id.etxt_settle_account_no);
        etxt_settle_bank = findViewById(R.id.etxt_settle_bank);
        etxt_seetle_mobile_money = findViewById(R.id.etxt_settle_mobile_number);
        etxt_settle_holder_name = findViewById(R.id.etxt_settle_holder_name);


        ArrayAdapter adapter = new ArrayAdapter<String>(SettlementActivity.this, R.layout.list_row, types);
        act_settle_to.setAdapter(adapter);

        act_settle_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act_settle_to.showDropDown();

            }
        });
        act_settle_to.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String settle_to = act_settle_to.getText().toString().trim();
                if (settle_to.equals("Bank")) {
                    linearBank.setVisibility(View.VISIBLE);
                    linearAccount.setVisibility(View.VISIBLE);
                    linearHolder.setVisibility(View.VISIBLE);
                    linearMobile.setVisibility(View.GONE);
                } else {
                    linearBank.setVisibility(View.GONE);
                    linearAccount.setVisibility(View.GONE);
                    linearHolder.setVisibility(View.GONE);
                    linearMobile.setVisibility(View.VISIBLE);
                }
            }
        });

        linearBank = findViewById(R.id.linear_bank);
        linearAccount = findViewById(R.id.linearAccountNumber);
        linearHolder = findViewById(R.id.linearHolder);
        linearMobile = findViewById(R.id.linearMobileNumber);

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettlementActivity.this, SettlementDetailsActivity.class);
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