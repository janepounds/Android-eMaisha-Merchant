package com.cabral.emaishamerchantApp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.cabral.emaishamerchantApp.customers.CustomersActivity;
import com.cabral.emaishamerchantApp.database.DatabaseAccess;
import com.cabral.emaishamerchantApp.expense.ExpenseActivity;
import com.cabral.emaishamerchantApp.network.RetrofitClient;
import com.cabral.emaishamerchantApp.orders.OrdersActivity;
import com.cabral.emaishamerchantApp.pos.PosActivity;
import com.cabral.emaishamerchantApp.product.ProductActivity;
import com.cabral.emaishamerchantApp.report.ReportActivity;
import com.cabral.emaishamerchantApp.settings.SettingsActivity;
import com.cabral.emaishamerchantApp.storage.SharedPrefManager;
import com.cabral.emaishamerchantApp.suppliers.SuppliersActivity;
import com.cabral.emaishamerchantApp.utils.BaseActivity;
import com.cabral.emaishamerchantApp.utils.LocaleManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {


    CardView cardCustomers, cardProducts, cardSupplier, cardPos, cardOrderList, cardReport, cardSettings, cardExpense;
    //for double back press to exit
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    private List<HashMap<String, String>> customers, products, categories, weights, suppliers, expenses, carts, payment_methods, orderList, orderTypes;
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar_home);
        context = this.getApplicationContext();
        cardCustomers = findViewById(R.id.card_customers);
        cardSupplier = findViewById(R.id.card_suppliers);
        cardProducts = findViewById(R.id.card_products);
        cardPos = findViewById(R.id.card_pos);
        cardOrderList = findViewById(R.id.card_order_list);
        cardReport = findViewById(R.id.card_report);
        cardSettings = findViewById(R.id.card_settings);
        cardExpense = findViewById(R.id.card_expense);


        if (Build.VERSION.SDK_INT >= 23) //Android MarshMellow Version or above
        {
            requestPermission();

        }
        //NetworkStateChecker.RegisterDeviceForFCM(HomeActivity.this);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(HomeActivity.this);
        databaseAccess.open();
        customers = databaseAccess.getCustomers();
        products = databaseAccess.getProducts();
        categories = databaseAccess.getProductCategory();
        weights = databaseAccess.getWeightUnit();
        suppliers = databaseAccess.getSuppliers();
        expenses = databaseAccess.getAllExpense();
        carts = databaseAccess.getCartProduct();
        payment_methods = databaseAccess.getPaymentMethod();
        orderList = databaseAccess.getOrderList();
        orderTypes = databaseAccess.getOrderType();

        Integer shop_id = SharedPrefManager.getInstance(HomeActivity.this).getShopId();


        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getBackup(
                        shop_id
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String s = null;
                    try {
                        s = response.body().string();
                        if (s != null) {
                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray my_customers = null;
                            JSONArray my_products = null;
                            JSONArray my_cart = null;
                            JSONArray my_suppliers = null;
                            JSONArray my_expenses = null;

                            if (customers.size() <= 0) {
                                my_customers = jsonObject.getJSONArray("customers");
                                if (my_customers.length() > 0) {
                                    for (int i = 0; i < my_customers.length(); i++) {

                                        boolean check = databaseAccess.addCustomer(my_customers.getJSONObject(i).getString("customer_name"), my_customers.getJSONObject(i).getString("customer_cell"), my_customers.getJSONObject(i).getString("customer_email"), my_customers.getJSONObject(i).getString("customer_address"), my_customers.getJSONObject(i).getString("customer_address_two"), my_customers.getJSONObject(i).getString("customer_image"));

                                        if (check) {
                                            Log.w("Customer Insert", "Customer Inserted Successfully");
                                        } else {

                                            Log.e("Customer Failure", "Customer Insertion Failed");

                                        }
                                    }
                                } else {
                                    Log.d("Customer Length ", "No Customers Backed up");
                                }
                            }

                            if (suppliers.size() <= 0) {
                                my_suppliers = jsonObject.getJSONArray("suppliers");
                                if (my_suppliers.length() > 0) {
                                    for (int i = 0; i < my_suppliers.length(); i++) {
                                        boolean check = databaseAccess.addSuppliers(my_suppliers.getJSONObject(i).getString("suppliers_name"), my_suppliers.getJSONObject(i).getString("suppliers_contact_person"), my_suppliers.getJSONObject(i).getString("suppliers_cell"), my_suppliers.getJSONObject(i).getString("suppliers_email"), my_suppliers.getJSONObject(i).getString("suppliers_address"), my_suppliers.getJSONObject(i).getString("suppliers_address_two"), my_suppliers.getJSONObject(i).getString("suppliers_image"));

                                        if (check) {
                                            Log.w("Suppliers Insert", "Customer Inserted Successfully");
                                        } else {

                                            Log.e("Suppliers Failure", "Customer Insertion Failed");

                                        }

                                    }
                                } else {
                                    Log.d("No Suppliers", "Shop Has No Suppliers Backed Up");
                                }

                            }

                            if (products.size() <= 0) {
                                my_products = jsonObject.getJSONArray("products");
                                if(my_products.length()>0){
                                    Log.w("ProductSize",my_products.length()+"---------------------");
                                    for(int i = 0; i< my_products.length(); i++){
                                        boolean check= databaseAccess.addProduct(my_products.getJSONObject(i).getString("product_id"), my_products.getJSONObject(i).getString("product_name"), my_products.getJSONObject(i).getString("product_code"), my_products.getJSONObject(i).getString("product_category"),my_products.getJSONObject(i).getString("product_description"),my_products.getJSONObject(i).getString("product_buy_price"), my_products.getJSONObject(i).getString("product_sell_price"), my_products.getJSONObject(i).getString("product_stock"),my_products.getJSONObject(i).getString("product_supplier"), my_products.getJSONObject(i).getString("product_image"), my_products.getJSONObject(i).getString("product_weight_unit"),my_products.getJSONObject(i).getString("product_weight"));

                                        if (check) {
                                            Log.w("Products Insert", "Product Inserted Successfully");
                                        } else {
                                            Log.e("Product Insert", "product Insertion failed");

                                        }

                                    }
                                } else {
                                    Log.d("No Products", "Shop Has No Products Backed Up");

                                }
                            }


                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("Error Occurred", "Error Occurred");
                    Log.d("Error response", String.valueOf(response));
                    Log.d("Error Code", String.valueOf(response.code()));

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.d("Error Occurred", "Error Occurred");

            }
        });

        Call<ResponseBody> call1 = RetrofitClient
                .getInstance()
                .getApi()
                .getOrders(
                        shop_id
                );

        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String s = null;
                    try {
                        s = response.body().string();
                        if (s != null) {
                            JSONObject jsonObject = new JSONObject(s);
                            Log.d("Order Fetch", String.valueOf(jsonObject.getJSONArray("orders")));
                            JSONArray order_array = jsonObject.getJSONArray("orders");
                            for(int i = 0; i<order_array.length(); i++){
                                Log.d("Order", String.valueOf(order_array.getJSONObject(i)));
                                boolean check = databaseAccess.addOrder(order_array.getJSONObject(i));
                                if(check){
                                    Log.d("Update Status", "New Order inserted or updated Successfully");
                                }else{
                                    Log.d("Update Failure", "New Order insertion failed");
                                }

                            }
                        } else {

                            Log.d("Order Fetch", "Response is Empty");
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Log.d("Order Fetch", "Response is an Error");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
            }
        });

        cardCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CustomersActivity.class);
                startActivity(intent);


            }
        });

        cardSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SuppliersActivity.class);
                startActivity(intent);


            }
        });


        cardProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                startActivity(intent);


            }
        });


        cardPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PosActivity.class);
                startActivity(intent);


            }
        });

        cardOrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                startActivity(intent);


            }
        });


        cardReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });


        cardExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ExpenseActivity.class);
                startActivity(intent);
            }
        });


        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {


            case R.id.local_french:
                setNewLocale(this, LocaleManager.FRENCH);
                return true;


            case R.id.local_english:
                setNewLocale(this, LocaleManager.ENGLISH);
                return true;


            case R.id.local_bangla:
                setNewLocale(this, LocaleManager.BANGLA);
                return true;

            case R.id.local_spanish:
                setNewLocale(this, LocaleManager.SPANISH);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    //double backpress to exit
    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {

            finishAffinity();

        } else {
            Toasty.info(this, R.string.press_once_again_to_exit,
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //  Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}
