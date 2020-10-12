package com.cabral.emaishamerchantsapp.wallet;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.auth.Login;
import com.cabral.emaishamerchantsapp.network.RetrofitClientMerchant;
import com.cabral.emaishamerchantsapp.storage.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardDetail extends AppCompatActivity {
    TextView txtOpenCardPInDialog;
    String firstname, lastname, middlename, gender, date_of_birth, district, village, sub_county, landmark, phone_number, email, next_of_kin_name, next_of_kin_second_name, next_of_kin_relationship, next_of_kin_contact, nin, national_id_valid_upto, national_id_photo, customer_photo, customer_photo_with_id;
    EditText etxtCardNumber, etxtCardAccount, etxtExpiryDate, etxtCvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Card Detail");

        etxtCardNumber = findViewById(R.id.etxt_card_number);
        etxtCardAccount = findViewById(R.id.etxt_card_account_number);
        etxtExpiryDate = findViewById(R.id.etxt_card_expiry_date);
        etxtCvv = findViewById(R.id.etxt_card_cvv);

        etxtExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatePicker(etxtExpiryDate, CardDetail.this);
            }
        });

        firstname = getIntent().getExtras().getString("firstname");
        lastname = getIntent().getExtras().getString("lastname");
        middlename = getIntent().getExtras().getString("middlename");
        gender = getIntent().getExtras().getString("gender");
        date_of_birth = getIntent().getExtras().getString("date_of_birth");
        district = getIntent().getExtras().getString("district");
        sub_county = getIntent().getExtras().getString("sub_county");
        village = getIntent().getExtras().getString("village");
        landmark = getIntent().getExtras().getString("landmark");
        phone_number = getIntent().getExtras().getString("phone_number");
        email = getIntent().getExtras().getString("email");
        next_of_kin_name = getIntent().getExtras().getString("next_of_kin_name");
        next_of_kin_second_name = getIntent().getExtras().getString("next_of_kin_second_name");
        next_of_kin_relationship = getIntent().getExtras().getString("next_of_kin_relationship");
        next_of_kin_contact = getIntent().getExtras().getString("next_of_kin_contact");
        nin = getIntent().getExtras().getString("nin");
        national_id_valid_upto = getIntent().getExtras().getString("national_id_valid_upto");
        national_id_photo = getIntent().getExtras().getString("national_id_photo");
        customer_photo = getIntent().getExtras().getString("customer_photo");
        customer_photo_with_id = getIntent().getExtras().getString("customer_photo_with_id");


        txtOpenCardPInDialog = findViewById(R.id.txt_card_submit);

        txtOpenCardPInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account_number = etxtCardAccount.getText().toString().trim();
                String card_number = etxtCardNumber.getText().toString().trim();
                String expiry_date = etxtExpiryDate.getText().toString().trim();
                String cvv = etxtCvv.getText().toString().trim();

                if (account_number.equals("")) {
                    etxtCardAccount.setError("Account Number is required");
                    etxtCardAccount.requestFocus();
                    return;
                }
                if (card_number.equals("")) {
                    etxtCardNumber.setError("Card Number is required");
                    etxtCardNumber.requestFocus();
                    return;
                }
                if (expiry_date.equals("")) {
                    etxtExpiryDate.setError("Expiry Date is required");
                    etxtExpiryDate.requestFocus();
                    return;
                }
                if (cvv.equals("")) {
                    etxtCvv.setError("Cvv is required");
                    etxtCvv.requestFocus();
                    return;
                }

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
                        String pin = input_create_pin.getText().toString().trim();
                        String confirm_pin = input_confirm_pin.getText().toString().trim();
                        String token = "Bearer " + SharedPrefManager.getInstance(CardDetail.this).getToken();

                        if (pin.equals("")) {
                            input_create_pin.setError("Pin is required");
                            input_create_pin.requestFocus();
                            return;
                        }
                        if (confirm_pin.equals("")) {
                            input_confirm_pin.setError("Pin is required");
                            input_confirm_pin.requestFocus();
                            return;

                        }
                        if (!pin.equals(confirm_pin)) {
                            input_confirm_pin.setError("The pins are not matching");
                            input_confirm_pin.requestFocus();
                            return;
                        }
                        Log.d("Kin Name", next_of_kin_name);
                        Log.d("Kin Second Name", next_of_kin_second_name);
                        Log.d("Kin Relationship", next_of_kin_name);
                        Log.d("Kin Contact", next_of_kin_contact);

                        Call<ResponseBody> call = RetrofitClientMerchant
                                .getInstance()
                                .getApi()
                                .createAccount(
                                        token,
                                        firstname,
                                        lastname,
                                        middlename,
                                        gender,
                                        date_of_birth,
                                        district,
                                        sub_county,
                                        village,
                                        landmark,
                                        phone_number,
                                        email,
                                        next_of_kin_name,
                                        next_of_kin_second_name,
                                        next_of_kin_relationship,
                                        next_of_kin_contact,
                                        nin,
                                        national_id_valid_upto,
                                        national_id_photo,
                                        customer_photo,
                                        customer_photo_with_id,
                                        account_number,
                                        card_number,
                                        expiry_date,
                                        cvv,
                                        pin

                                );

                        ProgressDialog progressDialog = new ProgressDialog(CardDetail.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.setTitle("Please Wait");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                if (response.isSuccessful()) {
                                    progressDialog.dismiss();
                                    alertDialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CardDetail.this);
                                    ViewGroup viewGroup = findViewById(android.R.id.content);
                                    View dialogView = LayoutInflater.from(CardDetail.this).inflate(R.layout.custom_dialog_pin_sucess, viewGroup, false);
                                    builder.setView(dialogView);
                                    AlertDialog alertPinDialog = builder.create();
                                    alertPinDialog.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            alertDialog.dismiss();
                                        }
                                    }, 12000);  // 1500 milliseconds

                                    Intent intent = new Intent(CardDetail.this, WalletActivity.class);
                                    startActivity(intent);
                                } else if (response.code() == 401) {
                                    progressDialog.dismiss();
                                    Toast.makeText(CardDetail.this, "Error while creating account", Toast.LENGTH_SHORT).show();
                                    String s = null;
                                    try {
                                        s = response.errorBody().string();
                                        if (s != null) {
                                            JSONObject jsonObject = new JSONObject(s);
                                            if (jsonObject.getString("message").equals("token_expired")) {
                                                Intent intent = new Intent(CardDetail.this, Login.class);
                                                startActivity(intent);
                                            }

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(CardDetail.this, "Error while creating account", Toast.LENGTH_SHORT).show();

                                        }


                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(CardDetail.this, "Error while creating account", Toast.LENGTH_SHORT).show();
                                t.printStackTrace();
                            }
                        });


                    }
                });
            }
        });
    }

    public static void addDatePicker(final EditText ed_, final Context context) {
        ed_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                final DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        int month = selectedmonth + 1;
                        NumberFormat formatter = new DecimalFormat("00");
                        ed_.setText(selectedyear + "-" + formatter.format(month) + "-" + formatter.format(selectedday));
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.show();

            }
        });
        ed_.setInputType(InputType.TYPE_NULL);
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