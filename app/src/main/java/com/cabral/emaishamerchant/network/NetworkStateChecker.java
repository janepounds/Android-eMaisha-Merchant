package com.cabral.emaishamerchant.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.cabral.emaishamerchant.database.DatabaseAccess;
import com.cabral.emaishamerchant.storage.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkStateChecker extends BroadcastReceiver {
    private Context context;
    private String shop_name;
    private List<HashMap<String, String>> shop_information, customers,products,categories;
    private Integer shop_id;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        shop_information = databaseAccess.getShopInformation();
        shop_name = shop_information.get(0).get("shop_name");
        customers = databaseAccess.getCustomers();
        products = databaseAccess.getProducts();
        categories = databaseAccess.getProductCategory();


        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d("Check Connection", "Network is connected");

                if (SharedPrefManager.getInstance(context).isShopSynced()) {
                    Log.d("Sync Status", "Shop Already Synced");
                    Integer shop_id = SharedPrefManager.getInstance(context).getShopId();
                    for (int i =0; i<products.size(); i++){
                        saveProducts(
                                shop_id,
                                products.get(i).get("product_id"),
                                products.get(i).get("product_name"),
                                products.get(i).get("product_code"),
                                products.get(i).get("product_category"),
                                products.get(i).get("product_description"),
                                products.get(i).get("product_buy_price"),
                                products.get(i).get("product_sell_price"),
                                products.get(i).get("product_supplier"),
                                products.get(i).get("product_image"),
                                products.get(i).get("product_stock"),
                                products.get(i).get("product_weight_unit_id"),
                                products.get(i).get("product_weight")
                        );
                    }

                    for (int i = 0; i<categories.size();i++ ){
                        saveCategories(
                                shop_id,
                                categories.get(i).get("category_id"),
                                categories.get(i).get("category_name")
                        );
                    }


                }else{
                    saveShop(
                            shop_information.get(0).get("shop_name"),
                            shop_information.get(0).get("shop_contact"),
                            shop_information.get(0).get("shop_email"),
                            shop_information.get(0).get("shop_address"),
                            shop_information.get(0).get("shop_currency")
                    );

                }



            }
        }

    }
    private void saveShop(String name, String contact, String email,String address, String currency){
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postShop(
                        name,
                        contact,
                        email,
                        address,
                        currency
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("Shop Sync", "Shop Synced");
                    try {
                    String s = response.body().string();
                    JSONObject jsonObject = new JSONObject(s);
                    SharedPrefManager.getInstance(context).saveShopId(jsonObject.getInt("shop_id"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d("Shop Sync Failure", "Shop Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Shop Sync Failure", "Shop Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void saveProducts(Integer shop_id, String product_id, String product_name,String product_code, String product_category,String product_description,String product_buy_price,String product_sell_price,String product_supplier,String product_image,String product_stock,String product_weight_unit_id,String weight){
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postProduct(
                       shop_id,
                        product_id,
                        product_name,
                        product_code,
                        product_category,
                        product_description,
                        product_buy_price,
                        product_sell_price,
                        product_supplier,
                        product_image,
                        product_stock,
                        product_weight_unit_id,
                        weight
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("Product Sync", "Product Synced");

                }else{
                    Log.d("Product Sync Failure", "Product Synced Failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Product Sync Failure", "Product Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void saveCategories(Integer shop_id, String category_id, String category_name){
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postCategory(
                      shop_id,
                        category_id,
                        category_name
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("Category Sync", "Category Synced");
                }else{
                    Log.d("Category Sync Failure", "Category Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Category Sync Failure", "Category Synced Failed");
                t.printStackTrace();
            }
        });
    }

}


