package com.cabral.emaishamerchant.network;

import com.google.gson.internal.$Gson$Preconditions;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
