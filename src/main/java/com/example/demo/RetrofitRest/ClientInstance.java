package com.example.demo.RetrofitRest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ClientInstance {

    private static final String BASE_URL = "https://api.professionaltutor.com.pk/";
    private static Retrofit retrofit;

    public static Retrofit getIntance() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(interceptor);
        okHttpClientBuilder.connectTimeout(3, TimeUnit.MINUTES);
        okHttpClientBuilder.readTimeout(3, TimeUnit.MINUTES);
        builder.client(okHttpClientBuilder.build());
        retrofit = builder.build();
        return retrofit;

    }


}
