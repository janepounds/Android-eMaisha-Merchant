package com.cabral.emaishamerchant.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishamerchant.HomeActivity;
import com.cabral.emaishamerchant.R;
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
    private TextView txtLogin;
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
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            try {
                                String s = response.body().string();
                                JSONObject jsonObject = new JSONObject(s);
                                SharedPrefManager.getInstance(Login.this).saveShopId(jsonObject.getInt("shop_id"));
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            progressDialog.dismiss();
                            try {
                                String s = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(s);
                                Toasty.error(Login.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Toasty.error(Login.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();

                    }
                });
                Intent intent = new Intent(Login.this, HomeActivity.class);
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