package com.cabral.emaishamerchantsapp.network;

import retrofit2.Retrofit;

public class RetrofitClientMerchant {
    private static final String BASE_URL = "http://emaishaadmin.myfarmnow.com/api/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;
}
