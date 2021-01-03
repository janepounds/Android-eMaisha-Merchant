package com.cabral.emaishamerchantsapp.orders;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.adapter.OnlineOrdersAdapter;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OnlineOrders extends AppCompatActivity {
    ImageView imgNoProduct;
    TextView txtNoProducts;
    EditText etxtSearch;
    private RecyclerView recyclerView;
    private OnlineOrdersAdapter onlineOrdersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_orders);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("Orders");

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);

        txtNoProducts = findViewById(R.id.txt_no_products);
        etxtSearch = findViewById(R.id.etxt_search_order);

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OnlineOrders.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OnlineOrders.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderList;
        orderList = databaseAccess.getOnlineOrderList();

        if (orderList.size() <= 0) {
            //if no data in local db, then load data from server
            Toasty.info(OnlineOrders.this, R.string.no_order_found, Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.ic_delivery_cuate);
            txtNoProducts.setVisibility(View.VISIBLE);
        } else {
            onlineOrdersAdapter = new OnlineOrdersAdapter(OnlineOrders.this, orderList);

            recyclerView.setAdapter(onlineOrdersAdapter);
        }


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