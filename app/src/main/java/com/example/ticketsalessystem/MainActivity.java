package com.example.ticketsalessystem;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import API.ApiService;
import Model.ProgrammeModel;
import com.example.ticketsalessystem.Programme.ProgrammeAdapter;
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
        // 🚩 1. 直接呼叫封裝好的 RetrofitClient，省略重複的 Builder 邏輯
        RetrofitClient.getApiService().getProgrammes().enqueue(new Callback<List<ProgrammeModel>>() {
            @Override
            public void onResponse(Call<List<ProgrammeModel>> call, Response<List<ProgrammeModel>> response) {
                // 🚩 2. 保留你的偵錯 Log
                Log.d("API_STATUS", "HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<ProgrammeModel> data = response.body();
                    Log.d("API_DATA", "抓到資料筆數: " + data.size());

                    if (data.size() > 0) {
                        // 🚩 3. 成功拿到資料，設定 Adapter
                        adapter = new ProgrammeAdapter(MainActivity.this, data);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.w("API_DATA", "後端回傳的是空列表 []，請檢查資料庫");
                        Toast.makeText(MainActivity.this, "目前沒有活動資訊", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_ERROR", "回傳失敗，請檢查 API 路徑是否正確");
                }
            }

            @Override
            public void onFailure(Call<List<ProgrammeModel>> call, Throwable t) {
                // 🚩 4. 處理徹底失敗（如 IP 錯誤或沒開網路）
                Log.e("API_ERROR", "連線徹底失敗: " + t.getMessage());
                Toast.makeText(MainActivity.this, "網路連線失敗，請檢查設定", Toast.LENGTH_LONG).show();
            }
        });
    }
}