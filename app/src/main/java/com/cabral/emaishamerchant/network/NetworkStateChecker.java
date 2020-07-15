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
    private List<HashMap<String, String>> shop_information, customers;
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


        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d("Check Connection", "Network is connected");

                if (SharedPrefManager.getInstance(context).isShopSynced()) {
                    Log.d("Sync Status", "Shop Already Synced");
                    Log.d("Shop id", String.valueOf(SharedPrefManager.getInstance(context).getShopId()));

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
}


