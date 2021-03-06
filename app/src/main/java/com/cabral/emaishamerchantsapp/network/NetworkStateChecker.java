package com.cabral.emaishamerchantsapp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.models.DeviceInfo;
import com.cabral.emaishamerchantsapp.models.UserData;
import com.cabral.emaishamerchantsapp.storage.SharedPrefManager;
import com.cabral.emaishamerchantsapp.utils.Utilities;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
    private List<HashMap<String, String>> shop_information, customers, products, categories, weights, suppliers, expenses, carts, payment_methods, orderList, orderTypes;

    public static void RegisterDeviceForFCM(final Context context) {
        DeviceInfo device = Utilities.getDeviceInfo(context);

        String shop_id = SharedPrefManager.getInstance(context).getShopId() + "";


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceID = instanceIdResult.getToken();
                Log.w("NEWTOKEN", deviceID);

                Call<UserData> call = RetrofitClient.getInstance().getApi()
                        .registerDeviceToFCM
                                (
                                        deviceID,
                                        device.getDeviceType(),
                                        device.getDeviceRAM(),
                                        device.getDeviceProcessors(),
                                        device.getDeviceAndroidOS(),
                                        device.getDeviceLocation(),
                                        device.getDeviceModel(),
                                        device.getDeviceManufacturer(),
                                        shop_id
                                );

                call.enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call, retrofit2.Response<UserData> response) {

                        if (response.isSuccessful()) {
                            if (response.body().getSuccess().equalsIgnoreCase("1")) {

                                Log.i("notification", response.body().getMessage());
//                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            } else {

                                Log.i("notification", response.body().getMessage());
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.i("notification", response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {
//                Toast.makeText(context, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });


    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        shop_information = databaseAccess.getShopInformation();
        Log.d("Shop information", String.valueOf(shop_information));
        shop_name = shop_information.get(0).get("shop_name");
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


        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.d("Check Connection", "Network is connected");

                if (SharedPrefManager.getInstance(context).isShopSynced()) {
                    Log.d("Sync Status", "Shop Already Synced");
                    Integer shop_id = SharedPrefManager.getInstance(context).getShopId();


                    for (int i = 0; i < weights.size(); i++) {
                        saveWeights(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "_" + shop_id + weights.get(i).get("weight_id"),
                                weights.get(i).get("weight_unit")
                        );
                    }

                    for (int i = 0; i < customers.size(); i++) {
                        saveCustomer(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "CT" + shop_id + customers.get(i).get("customer_id"),
                                customers.get(i).get("customer_name"),
                                customers.get(i).get("customer_cell"),
                                customers.get(i).get("customer_email"),
                                customers.get(i).get("customer_address"),
                                customers.get(i).get("customer_address_two"),
                                customers.get(i).get("customer_image")
                        );
                    }

                    for (int i = 0; i < suppliers.size(); i++) {
                        saveSupplier(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "SUP" + shop_id + suppliers.get(i).get("suppliers_id"),
                                suppliers.get(i).get("suppliers_name"),
                                suppliers.get(i).get("suppliers_contact_person"),
                                suppliers.get(i).get("suppliers_cell"),
                                suppliers.get(i).get("suppliers_email"),
                                suppliers.get(i).get("suppliers_address"),
                                suppliers.get(i).get("suppliers_address_two"),
                                suppliers.get(i).get("suppliers_image")


                        );
                    }

                    for (int i = 0; i < expenses.size(); i++) {
                        saveExpense(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "EX" + shop_id + expenses.get(i).get("expense_id"),
                                expenses.get(i).get("expense_name"),
                                expenses.get(i).get("expense_note"),
                                expenses.get(i).get("expense_amount"),
                                expenses.get(i).get("expense_date"),
                                expenses.get(i).get("expense_time")


                        );
                    }

                    for (int i = 0; i < carts.size(); i++) {
                        saveCart(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "CT" + shop_id + carts.get(i).get("cart_id"),
                                carts.get(i).get("product_id"),
                                carts.get(i).get("product_weight"),
                                carts.get(i).get("product_weight_unit"),
                                carts.get(i).get("product_price"),
                                carts.get(i).get("product_qty")
                        );
                    }

                    for (int i = 0; i < payment_methods.size(); i++) {
                        savePayment(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "PT" + shop_id + payment_methods.get(i).get("payment_method_id"),
                                payment_methods.get(i).get("payment_method_name")

                        );
                    }

                    for (int i = 0; i < orderList.size(); i++) {
                        saveOrderList(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "ORDER" + shop_id + orderList.get(i).get("order_id"),
                                orderList.get(i).get("invoice_id"),
                                orderList.get(i).get("order_date"),
                                orderList.get(i).get("order_time"),
                                orderList.get(i).get("order_type"),
                                orderList.get(i).get("order_payment_method"),
                                orderList.get(i).get("customer_name")

                        );
                    }
                    for (int i = 0; i < orderTypes.size(); i++) {
                        saveOrderTypes(
                                shop_id,
                                shop_name.replaceAll(" ", "") + "WHT" + shop_id + orderTypes.get(i).get("order_type_id"),
                                orderTypes.get(i).get("order_type_name")
                        );
                    }

                } else {
                    saveShop(
                            shop_information.get(0).get("shop_name"),
                            shop_information.get(0).get("shop_contact"),
                            shop_information.get(0).get("shop_email"),
                            shop_information.get(0).get("shop_address"),
                            shop_information.get(0).get("shop_currency"),
                            shop_information.get(0).get("latitude"),
                            shop_information.get(0).get("longitude")
                    );

                }


            }
        }

    }

    private void saveShop(String name, String contact, String email, String address, String currency, String latitude, String longitude) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postShop(
                        name,
                        contact,
                        email,
                        address,
                        currency,
                        latitude,
                        longitude
                );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Shop Sync", "Shop Synced");
                    try {
                        String s = response.body().string();
                        Log.d("Response", s);
                        JSONObject jsonObject = new JSONObject(s);
                        SharedPrefManager.getInstance(context).saveShopId(jsonObject.getInt("shop_id"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
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

    private void saveCategories(Integer shop_id, String category_id, String category_name) {
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
                if (response.isSuccessful()) {
                    Log.d("Category Sync", "Category Synced");
                } else {
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

    private void saveWeights(Integer shop_id, String weight_id, String weight_unit) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postWeight(
                        shop_id,
                        weight_id,
                        weight_unit
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Weight Sync", "Weight Synced");
                } else {
                    Log.d("Weight Sync Failure", "Weight Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Weight Sync Failure", "Weight Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void saveCustomer(Integer shop_id, String customer_id, String customer_name, String customer_cell, String customer_email, String customer_address, String customer_address_two, String customer_image) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postCustomer(
                        shop_id,
                        customer_id,
                        customer_name,
                        customer_cell,
                        customer_email,
                        customer_address,
                        customer_address_two,
                        customer_image
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Customer Sync Status", "Customer Synced");

                } else {
                    Log.d("Customer Sync Failure", "Customer Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Customer Sync Failure", "Customer Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void saveSupplier(Integer shop_id, String suppliers_id, String suppliers_name, String suppliers_contact_person, String suppliers_cell, String suppliers_email, String suppliers_address, String suppliers_address_two, String suppliers_image) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postSupplier(
                        shop_id,
                        suppliers_id,
                        suppliers_name,
                        suppliers_contact_person,
                        suppliers_cell,
                        suppliers_email,
                        suppliers_address,
                        suppliers_address_two,
                        suppliers_image
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Supplier Sync", "Supplier Synced");

                } else {
                    Log.d("Supplier Sync Failure", "Supplier Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Supplier Sync Failure", "Supplier Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void saveExpense(Integer shop_id, String expense_id, String expense_name, String expense_note, String expense_amount, String expense_date, String expense_time) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postExpense(
                        shop_id,
                        expense_id,
                        expense_name,
                        expense_note,
                        expense_amount,
                        expense_date,
                        expense_time
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Expense Sync", "Expense Synced");

                } else {
                    Log.d("Expense Sync Failure", "Expense Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Expense Sync Failure", "Expense Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void saveCart(Integer shop_id, String cart_id, String product_id, String product_weight, String product_weight_unit, String product_price, String product_qty) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postCart(
                        shop_id,
                        cart_id,
                        product_id,
                        product_weight,
                        product_weight_unit,
                        product_price,
                        product_qty
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Cart Sync", "Cart Synced");

                } else {
                    Log.d("Cart Sync Failure", "Cart Synced Failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Cart Sync Failure", "Cart Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void savePayment(Integer shop_id, String payment_method_id, String payment_method_name) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postPaymentMethod(
                        shop_id,
                        payment_method_id,
                        payment_method_name
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Payment Method Sync", "Payment Method Synced");
                } else {
                    Log.d("Payment  Sync Failure", "Payment Method Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Payment Sync Failure", "Order Type Synced Failed");
                t.printStackTrace();
            }
        });
    }

    private void saveOrderList(Integer shop_id, String order_id, String invoice_id, String order_date, String order_time, String order_type, String order_payment_method, String customer_name) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postOrderList(
                        shop_id,
                        order_id,
                        invoice_id,
                        order_date,
                        order_time,
                        order_type,
                        order_payment_method,
                        customer_name
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Order List Sync", "Order List Synced");
                } else {
                    Log.d("OrderList  Sync Failure", "Order List Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("OrderList Sync Failure", "Order List Synced Failed");
                t.printStackTrace();
            }
        });
    }

    //*********** Register Device to Admin Panel with the Device's Info ********//

    private void saveOrderTypes(Integer shop_id, String order_type_id, String order_type_name) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .postOrderType(
                        shop_id,
                        order_type_id,
                        order_type_name
                );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Order Type Sync", "Order Type Synced");
                } else {
                    Log.d("Order Type Sync Failure", "Order Type Synced Failed");
                    Log.d("Error", String.valueOf(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Order Type Sync Failure", "Order Type Synced Failed");
                t.printStackTrace();
            }
        });
    }

}


