package com.cabral.emaishamerchant.product;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.cabral.emaishamerchant.R;
import com.cabral.emaishamerchant.adapter.ProductAdapter;
import com.cabral.emaishamerchant.database.DatabaseAccess;
import com.cabral.emaishamerchant.database.DatabaseOpenHelper;
import com.cabral.emaishamerchant.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ProductActivity extends BaseActivity {

    private RecyclerView recyclerView;
    ProductAdapter productAdapter;

    ImageView imgNoProduct;
    EditText etxtSearch;

    FloatingActionButton fabAdd;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        fabAdd = findViewById(R.id.fab_add);
        etxtSearch = findViewById(R.id.etxt_search);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_product);

        recyclerView = findViewById(R.id.product_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);

        //for interstitial ads show
//        Utils utils=new Utils();
//        utils.interstitialAdsShow(ProductActivity.this);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> productData;
        productData = databaseAccess.getProducts();

        Log.d("data", "" + productData.size());

        if (productData.size() <= 0) {
            Toasty.info(this, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.no_data);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            productAdapter = new ProductAdapter(ProductActivity.this, productData);

            recyclerView.setAdapter(productAdapter);


        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //  searchData(s.toString());

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchProductList;

                searchProductList = databaseAccess.getSearchProducts(s.toString());


                if (searchProductList.size() <= 0) {
                    //  Toasty.info(ProductActivity.this, "No Product Found!", Toast.LENGTH_SHORT).show();

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);
                    //  txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    productAdapter = new ProductAdapter(ProductActivity.this, searchProductList);

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
        getMenuInflater().inflate(R.menu.all_product_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            case R.id.menu_export:


                folderChooser();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void folderChooser() {
        new ChooserDialog(ProductActivity.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        onExport(path);
                        Log.d("path", path);

                    }
                })
                .build()
                .show();
    }


    public void onExport(String path) {

        String directory_path = path;
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directory_path);
        sqliteToExcel.exportSingleTable("products", "products.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(ProductActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        loading.dismiss();
                        Toasty.success(ProductActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                    }
                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(ProductActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
