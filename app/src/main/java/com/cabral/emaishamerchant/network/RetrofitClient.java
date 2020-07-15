package com.cabral.emaishamerchant.network;

import retrofit2.Retrofit;

public class RetrofitClient {
    private  static final String BASE_URL = "http://emaishaadmin.myfarmnow.com/api/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit =new  Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance==null){
            mInstance = new RetrofitClient();
        }

        return  mInstance;
    }

    public  Api getApi(){
        return  retrofit.create(Api.class);
    }
}
