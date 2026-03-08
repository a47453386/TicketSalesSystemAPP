package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import com.example.ticketsalessystem.Adapters.NewsAdapter; // 確保路徑正確
import java.util.List;
import API.ApiService;
import Model.PublicNotice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 🚩 引用 fragment_news.xml
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = view.findViewById(R.id.recycler_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchNewsData();
        return view;
    }

    private void fetchNewsData() {
        // 使用你的 RetrofitClient 進行連線
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.GetFiveNews().enqueue(new Callback<List<PublicNotice>>() {
            @Override
            public void onResponse(Call<List<PublicNotice>> call, Response<List<PublicNotice>> response) {
                // 🚩 1. 加入這行 Log 來確認是否有進入成功回呼
                Log.d("API_NEWS_DEBUG", "收到回應！狀態碼: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<PublicNotice> data = response.body();
                    // 🚩 2. 加入這行確認資料筆數
                    Log.d("API_NEWS_DEBUG", "公告筆數: " + data.size());

                    recyclerView.setAdapter(new NewsAdapter(getContext(), data));
                } else {
                    Log.e("API_NEWS_DEBUG", "回應成功但內容為空或格式錯誤");
                }
            }

            @Override
            public void onFailure(Call<List<PublicNotice>> call, Throwable t) {
                // 🚩 3. 加入連線失敗的原因
                Log.e("API_NEWS_DEBUG", "連線失敗: " + t.getMessage());
            }
        });
    }
}