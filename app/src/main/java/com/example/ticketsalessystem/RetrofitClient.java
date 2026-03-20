package com.example.ticketsalessystem;

import android.content.Context;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.concurrent.TimeUnit;

import API.ApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    public static final String IP = "192.168.0.107"; // 模擬器用 10.0.2.2
    public static final String BASE_URL = "http://" + IP + ":5098/";

    // 🚩 使用 ClearableCookieJar 介面，支援持久化儲存
    private static ClearableCookieJar cookieJar = null;

    // 🚩 修正：getInstance 需要傳入 Context 以初始化 Cookie 儲存
    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {

            Context appContext = context.getApplicationContext();

            // 1. 初始化持久化 CookieJar (這會把 Cookie 存進手機硬碟)
            if (cookieJar == null) {
                cookieJar = new PersistentCookieJar(
                        new SetCookieCache(),
                        new SharedPrefsCookiePersistor(context.getApplicationContext())
                );
            }

            // 2. 加入 Logging 攔截器，方便在 Logcat 看 Cookie 有沒有傳成功
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 3. 建立 OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS) // 🚩 增加連線逾時設定
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            // 4. 建立 Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // 🚩 修正：ApiService 也需要 Context
    public static ApiService getApiService(Context context) {
        return getInstance(context).create(ApiService.class);
    }
    public static void clearCookies() {
        if (cookieJar != null) {
            cookieJar.clear(); // 🚩 徹底清除所有快取與持久化的 Cookie
        }
    }
}