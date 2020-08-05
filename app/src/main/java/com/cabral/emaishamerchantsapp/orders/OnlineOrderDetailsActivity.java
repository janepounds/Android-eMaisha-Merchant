package com.cabral.emaishamerchantsapp.orders;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.adapter.OnlineOrderDetailsAdapter;
import com.cabral.emaishamerchantsapp.adapter.OrderDetailsAdapter;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineOrderDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OnlineOrderDetailsAdapter onlineOrderDetailsAdapter;
    String order_id,customer_name,order_status,currency;
    double total_price;
    TextView txtSubTotal,txtCustomerName,txtOrderStatus, txtApprove, txtReject,txtDelivery,txtCustomerPhone,txtCustomerEmail, txtOverallTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_order_details);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Order Details");

        recyclerView = findViewById(R.id.recycler);
        order_id = getIntent().getExtras().getString("order_id");
        customer_name = getIntent().getExtras().getString("customer_name");
        order_status = getIntent().getExtras().getString("order_status");
        txtSubTotal = findViewById(R.id.txt_online_total_price);
        txtOrderStatus = findViewById(R.id.txt_online_order_status);
        txtCustomerName = findViewById(R.id.txt_online_order_customer_name);
        txtApprove = findViewById(R.id.txt_approve_online);
        txtDelivery = findViewById(R.id.txt_online_delivery_price);
        txtCustomerPhone = findViewById(R.id.txt_online_order_customer_phone);
        txtCustomerEmail = findViewById(R.id.txt_online_order_customer_email);
        txtOverallTotal = findViewById(R.id.txt_online_overall_total_price);
        txtReject = findViewById(R.id.txt_reject_online);


        txtOrderStatus.setText(order_status);
        txtCustomerName.setText(customer_name);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OnlineOrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OnlineOrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(order_id);
        Log.d("Order List", String.valueOf(orderDetailsList));

        onlineOrderDetailsAdapter = new OnlineOrderDetailsAdapter(OnlineOrderDetailsActivity.this, orderDetailsList);

        recyclerView.setAdapter(onlineOrderDetailsAdapter);

        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();
        currency = shopData.get(0).get("shop_currency");


        databaseAccess.open();
        total_price = databaseAccess.totalOrderPrice(order_id);
        Double total = total_price + Double.parseDouble(txtDelivery.getText().toString().trim());
        txtSubTotal.setText(currency+ " "+total);
//        txtTotalPrice.setText(currency + total_price);

        txtReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OnlineOrderDetailsActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(OnlineOrderDetailsActivity.this).inflate(R.layout.custom_reject_order_dialog, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                EditText input = dialogView.findViewById(R.id.dialog_reject_comment);
                TextView txt_reject_order = dialogView.findViewById(R.id.custom_reject_order);
                TextView txt_cancel_order = dialogView.findViewById(R.id.custom_reject_cancel);

                txt_cancel_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });



        txtApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .updateOrderStatus(
                                order_id
                        );
                ProgressDialog progressDialog = new ProgressDialog(OnlineOrderDetailsActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setTitle("Please Wait");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OnlineOrderDetailsActivity.this);
                            databaseAccess.open();
                            boolean check = databaseAccess.updateOrder(order_id, "Completed");
                            if (check) {
                                progressDialog.dismiss();
                                txtApprove.setVisibility(View.GONE);
                                Toasty.success(OnlineOrderDetailsActivity.this, "Order Succesfully Approved", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toasty.error(OnlineOrderDetailsActivity.this, "Order Approval failed", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toasty.error(OnlineOrderDetailsActivity.this, "Order Approval failed", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                        Toasty.error(OnlineOrderDetailsActivity.this, "Order Approval failed", Toast.LENGTH_SHORT).show();
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