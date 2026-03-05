package com.example.ticketsalessystem;

import API.ApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.10.51.9:5098/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // 自動將 JSON 轉成你的 Model
                    .build();
        }
        // 回傳 ApiService 實作，讓你能呼叫 getProgrammeDetail(id)
        return retrofit.create(ApiService.class);
    }
}
