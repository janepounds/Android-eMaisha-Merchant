package com.cabral.emaishamerchantsapp.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchantsapp.HomeActivity;
import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.network.NetworkStateChecker;
import com.cabral.emaishamerchantsapp.network.RetrofitClient;
import com.cabral.emaishamerchantsapp.storage.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private TextView txtLogin, signupText;
    private EditText etxtContact, etxtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        etxtContact = findViewById(R.id.etxt_merchant_contact);
        etxtPassword = findViewById(R.id.etxt_merchant_password);

        txtLogin = findViewById(R.id.txt_shop_login);
        signupText = findViewById(R.id.login_signupText);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shop_contact = etxtContact.getText().toString().trim();
                String password = etxtPassword.getText().toString().trim();

                if (shop_contact.isEmpty()) {
                    etxtContact.setError("Phone is customer");
                    etxtContact.requestFocus();
                    return;
                }
                if (shop_contact.charAt(0) == '0') {
                    shop_contact = shop_contact.substring(1, shop_contact.length());
                    shop_contact = getString(R.string.ugandan_code) + shop_contact;
                    Log.w("contact", shop_contact);
                }

                if (password.isEmpty()) {
                    etxtPassword.setError("Password is required");
                    etxtPassword.requestFocus();
                    return;
                }
                Log.d("Phone", shop_contact);
                Log.d("Password", password);
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .loginShop(
                                shop_contact,
                                password
                        );

                ProgressDialog progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setTitle("Please Wait");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();


                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String s = null;
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                s = response.body().string();
                                if (s != null) {
                                    JSONObject jsonObject = new JSONObject(s);
                                    SharedPrefManager.getInstance(Login.this).saveShopId(jsonObject.getInt("shop_id"));
                                    SharedPrefManager.getInstance(Login.this).saveToken(jsonObject.getString("token"));
                                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(Login.this);
                                    databaseAccess.open();
                                    boolean check = databaseAccess.addShopInformation(jsonObject.getJSONObject("data").getString("shop_name"), jsonObject.getJSONObject("data").getString("shop_contact"), jsonObject.getJSONObject("data").getString("shop_email"), jsonObject.getJSONObject("data").getString("shop_address"), jsonObject.getJSONObject("data").getString("shop_currency"), jsonObject.getJSONObject("data").getString("latitude"), jsonObject.getJSONObject("data").getString("longitude"));

                                    if (check) {
                                        Toasty.success(Login.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        NetworkStateChecker.RegisterDeviceForFCM(getApplicationContext());
                                        Intent intent = new Intent(Login.this, HomeActivity.class);
                                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toasty.error(Login.this, R.string.failed, Toast.LENGTH_SHORT).show();

                                    }

                                } else {
                                    Log.d("Error", String.valueOf(response));
                                    Toasty.error(Login.this, R.string.failed, Toast.LENGTH_SHORT).show();

                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            progressDialog.dismiss();
                            Log.d("Error", String.valueOf(response));
                            Toasty.error(Login.this, R.string.failed, Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                    }
                });


            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (SharedPrefManager.getInstance(this).isShopSynced()) {
            Intent intent = new Intent(Login.this, HomeActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

}