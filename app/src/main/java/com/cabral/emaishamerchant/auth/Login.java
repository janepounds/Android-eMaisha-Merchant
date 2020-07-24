package com.cabral.emaishamerchant.auth;

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

import com.cabral.emaishamerchant.HomeActivity;
import com.cabral.emaishamerchant.R;
import com.cabral.emaishamerchant.database.DatabaseAccess;
import com.cabral.emaishamerchant.network.RetrofitClient;
import com.cabral.emaishamerchant.storage.SharedPrefManager;

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
                String contact = etxtContact.toString().trim();
                String password = etxtPassword.toString().toString().trim();

                if (contact.isEmpty()) {
                    etxtContact.setError("Phone is customer");
                    etxtContact.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    etxtPassword.setError("Password is required");
                    etxtPassword.requestFocus();
                    return;
                }
                Log.d("Phone", contact);
                Log.d("Password", password);
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .loginShop(
                                contact,
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
                            if (s != null) {
                                try {
                                    s = response.body().string();
                                    JSONObject jsonObject = new JSONObject(s);
                                    SharedPrefManager.getInstance(Login.this).saveShopId(jsonObject.getInt("shop_id"));
                                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(Login.this);
                                    databaseAccess.open();
                                    boolean check = databaseAccess.addShopInformation(jsonObject.getJSONObject("data").getString("shop_name"), jsonObject.getJSONObject("data").getString("shop_contact"), jsonObject.getJSONObject("data").getString("shop_email"), jsonObject.getJSONObject("data").getString("shop_address"), jsonObject.getJSONObject("data").getString("shop_currency"), jsonObject.getJSONObject("data").getString("latitude"), jsonObject.getJSONObject("data").getString("longitude"));

                                    if (check) {
                                        Toasty.success(Login.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, HomeActivity.class);
                                        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toasty.error(Login.this, R.string.failed, Toast.LENGTH_SHORT).show();

                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toasty.error(Login.this, R.string.failed, Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            progressDialog.dismiss();
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

}