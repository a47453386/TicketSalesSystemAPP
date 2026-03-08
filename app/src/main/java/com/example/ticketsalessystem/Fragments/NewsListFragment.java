package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.Adapters.NewsListAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.List;
import Model.PublicNotice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsListFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_all_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchAllNews();
        return view;
    }

    private void fetchAllNews() {
        // 🚩 呼叫獲取所有公告的接口
        RetrofitClient.getApiService().GetAllNews().enqueue(new Callback<List<PublicNotice>>() {
            @Override
            public void onResponse(Call<List<PublicNotice>> call, Response<List<PublicNotice>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    recyclerView.setAdapter(new NewsListAdapter(getContext(), response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<PublicNotice>> call, Throwable t) {
                Log.e("API_ALL_NEWS", t.getMessage());
            }
        });
    }
}