package com.rice.mandi.Retrofit;

import android.content.Context;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitApiClient {
    public static final String BASE_URL="http://backend.qlxsale.com/public/api/";
    public static final String IMAGE_BASE_URL = "http://backend.qlxsale.com/public/images/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitApiClient(){
        if(retrofit==null){
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(600, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .build();
            retrofit=new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
