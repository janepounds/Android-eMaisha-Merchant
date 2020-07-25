package com.cabral.emaishamerchant.network;

import com.cabral.emaishamerchant.models.CategoriesResponse;
import com.cabral.emaishamerchant.models.ProductResponse;
import com.cabral.emaishamerchant.models.ShopResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {
    @FormUrlEncoded
    @POST("postMerchantShops")
    Call<ResponseBody> postShop(
            @Field("shop_name") String shop_name,
            @Field("shop_contact") String shop_contact,
            @Field("shop_email") String shop_email,
            @Field("shop_address") String shop_address,
            @Field("shop_currency") String shop_currency,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST("registerMerchant")
    Call<ResponseBody> registerShop(
            @Field("shop_name") String shop_name,
            @Field("shop_contact") String shop_contact,
            @Field("shop_email") String shop_email,
            @Field("shop_address") String shop_address,
            @Field("shop_currency") String shop_currency,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("loginMerchant")
    Call<ResponseBody> loginShop(
            @Field("shop_contact") String shop_contact,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("postCustomer")
    Call<ResponseBody> postCustomer(
            @Field("shop_id") Integer shop_id,
            @Field("customer_id") String customer_id,
            @Field("customer_name") String customer_name,
            @Field("customer_cell") String customer_cell,
            @Field("customer_email") String customer_email,
            @Field("customer_address") String customer_address,
            @Field("customer_address_two") String customer_address_two,
            @Field("customer_image") String customer_image
    );

    @FormUrlEncoded
    @POST("postSuppliers")
    Call<ResponseBody> postSupplier(
            @Field("shop_id") Integer shop_id,
            @Field("suppliers_id") String suppliers_id,
            @Field("suppliers_name") String suppliers_name,
            @Field("suppliers_contact_person") String suppliers_contact_person,
            @Field("suppliers_cell") String suppliers_cell,
            @Field("suppliers_email") String suppliers_email,
            @Field("suppliers_address") String suppliers_address,
            @Field("suppliers_address_two") String suppliers_address_two,
            @Field("suppliers_image") String suppliers_image
    );

    @FormUrlEncoded
    @POST("postExpenses")
    Call<ResponseBody> postExpense(
            @Field("shop_id") Integer shop_id,
            @Field("expense_id") String expense_id,
            @Field("expense_name") String expense_name,
            @Field("expense_note") String expense_note,
            @Field("expense_amount") String expense_amount,
            @Field("expense_date") String expense_date,
            @Field("expense_time") String expense_time
    );

    @FormUrlEncoded
    @POST("postProductCategories")
    Call<ResponseBody> postCategory(
            @Field("shop_id") Integer shop_id,
            @Field("category_id") String category_id,
            @Field("category_name") String category_name
    );

    @FormUrlEncoded
    @POST("stockMerchantProduct")
    Call<ResponseBody> postProduct(
            @Field("id") String id,
            @Field("shop_id") Integer shop_id,
            @Field("product_id") Integer product_id,
            // @Field("product_name") String product_name,
            // @Field("product_code") String product_code,
            //  @Field("product_category") String product_category,
            // @Field("product_description") String product_description,
            @Field("product_buy_price") String product_buy_price,
            @Field("product_sell_price") String product_sell_price,
            @Field("product_supplier") String product_supplier,
            @Field("product_image") String product_image,
            @Field("product_stock") int product_stock,
            @Field("product_weight_unit_id") String product_weight_unit_id,
            @Field("product_weight") String weight
    );

    @FormUrlEncoded
    @POST("postPaymentMethod")
    Call<ResponseBody> postPaymentMethod(
            @Field("shop_id") Integer shop_id,
            @Field("payment_method_id") String payment_method_id,
            @Field("payment_method_name") String payment_method_name
    );

    @FormUrlEncoded
    @POST("postCart")
    Call<ResponseBody> postCart(
            @Field("shop_id") Integer shop_id,
            @Field("cart_id") String cart_id,
            @Field("product_id") String product_id,
            @Field("product_weight") String product_weight,
            @Field("product_weight_unit") String product_weight_unit,
            @Field("product_price") String product_price,
            @Field("product_qty") String product_qty
    );

    @FormUrlEncoded
    @POST("postProductWeight")
    Call<ResponseBody> postWeight(
            @Field("shop_id") Integer shop_id,
            @Field("weight_id") String weight_id,
            @Field("weight_unit") String weight_unit
    );


    @FormUrlEncoded
    @POST("postOrderList")
    Call<ResponseBody> postOrderList(
            @Field("shop_id") Integer shop_id,
            @Field("order_id") String order_id,
            @Field("invoice_id") String invoice_id,
            @Field("order_date") String order_date,
            @Field("order_time") String order_time,
            @Field("order_type") String order_type,
            @Field("order_payment_method") String order_payment_method,
            @Field("customer_name") String customer_name
    );

    @FormUrlEncoded
    @POST("postOrderType")
    Call<ResponseBody> postOrderType(
            @Field("shop_id") Integer shop_id,
            @Field("order_type_id") String order_type_id,
            @Field("order_type_name") String order_type_name
    );

    @GET("getCategories")
    Call<CategoriesResponse> getCategories();

    @GET("getProductsByCategory/{id}")
    Call<ProductResponse> getProducts(
            @Path("id") int id
    );



}
