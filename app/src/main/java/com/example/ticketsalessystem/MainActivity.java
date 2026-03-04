package com.example.ticketsalessystem;


import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import API.ApiService;
import Model.ProgrammeModel;
import Programme.ProgrammeAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgrammeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchData();
    }
    private void fetchData() {
        // 🚩 記得用 10.0.2.2 (模擬器) 或你的電腦 IP (實機)
        String baseUrl = "http://10.10.51.9:5098/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 發出非同步請求
        apiService.getProgrammes().enqueue(new Callback<List<ProgrammeModel>>() {
            @Override
            public void onResponse(Call<List<ProgrammeModel>> call, Response<List<ProgrammeModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 🚩 拿到資料後，丟給 Adapter 顯示
                    adapter = new ProgrammeAdapter(MainActivity.this, response.body());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProgrammeModel>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });

        apiService.getProgrammes().enqueue(new Callback<List<ProgrammeModel>>() {
            @Override
            public void onResponse(Call<List<ProgrammeModel>> call, Response<List<ProgrammeModel>> response) {
                Log.d("API_STATUS", "Code: " + response.code()); // 🚩 觀察這裡是否還是 404

                if (response.isSuccessful() && response.body() != null) {
                    List<ProgrammeModel> data = response.body();
                    Log.d("API_DATA", "抓到資料筆數: " + data.size()); // 🚩 確認有沒有抓到東西

                    if (data.size() > 0) {
                        adapter = new ProgrammeAdapter(MainActivity.this, data);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.w("API_DATA", "後端回傳的是空列表 []");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProgrammeModel>> call, Throwable t) {
                Log.e("API_ERROR", "連線徹底失敗: " + t.getMessage()); // 🚩 檢查是否為網路或 IP 問題
            }
        });
    }
}