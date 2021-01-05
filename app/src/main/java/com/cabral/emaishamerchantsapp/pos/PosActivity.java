package com.cabral.emaishamerchantsapp.pos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.adapter.PosProductAdapter;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;

public class PosActivity extends BaseActivity {


    public static EditText etxtSearch,etxtCharge;
    PosProductAdapter productAdapter;
    TextView txtNoProducts,txtEnter,txtItems;
    View enterView, itemsView;
    ConstraintLayout layoutCart;
    ImageView imgNoProduct, imgScanner;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_product);

        etxtSearch = findViewById(R.id.etxt_search);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtNoProducts = findViewById(R.id.txt_no_products);
        imgScanner = findViewById(R.id.img_scanner);

        etxtCharge = findViewById(R.id.pos_charge);
        txtEnter = findViewById(R.id.txt_enter);
        txtItems = findViewById(R.id.txt_items);
        enterView = findViewById(R.id.enter_selected);
        itemsView = findViewById(R.id.items_selected);
        layoutCart = findViewById(R.id.layout_cart);

        //for interstitial ads show
//        Utils utils=new Utils();
//        utils.interstitialAdsShow(PosActivity.this);
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(PosActivity.this);
        databaseAccess.open();

        txtEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterView.setVisibility(View.VISIBLE);
                itemsView.setVisibility(View.INVISIBLE);
                etxtSearch.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                layoutCart.setVisibility(View.GONE);
                etxtCharge.setVisibility(View.VISIBLE);
                etxtCharge.requestFocus();

            }
        });
        txtItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterView.setVisibility(View.INVISIBLE);
                itemsView.setVisibility(View.VISIBLE);
                etxtSearch.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                layoutCart.setVisibility(View.VISIBLE);
                etxtCharge.setVisibility(View.GONE);
                etxtCharge.clearFocus();




                //get data from local database
                List<HashMap<String, String>> productList;
                productList = databaseAccess.getProducts();

                if (productList.size() <= 0 ) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);
                    txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    txtNoProducts.setVisibility(View.GONE);

                    productAdapter = new PosProductAdapter(PosActivity.this, productList);

                    recyclerView.setAdapter(productAdapter);


                }

            }
        });


        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivity(intent);
            }
        });


        imgScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);




        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchProductList;

                searchProductList = databaseAccess.getSearchProducts(s.toString());


                if (searchProductList.size() <= 0) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);
                    txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    txtNoProducts.setVisibility(View.GONE);

                    productAdapter = new PosProductAdapter(PosActivity.this, searchProductList);

                    recyclerView.setAdapter(productAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    protected void onResume() {
        //disable the power key
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onResume();
        //isOpen = false;
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //
        //            case R.id.menu_cart_button:
        //                Intent intent = new Intent(PosActivity.this, ProductCart.class);
        //                startActivity(intent);
        //                return true;
        //
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; goto parent activity.
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
