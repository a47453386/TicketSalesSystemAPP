package com.example.ticketsalessystem;

import API.ApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    // 🚩 指向你本機 API 的網址
    public static final String IP = "10.0.2.2";

    public static final String BASE_URL = "http://" + IP + ":5098/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}