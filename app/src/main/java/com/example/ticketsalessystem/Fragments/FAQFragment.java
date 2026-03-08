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
import com.example.ticketsalessystem.Adapters.FAQAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import java.util.List;
import Model.FAQ;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 載入你剛才提供的 XML
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        recyclerView = view.findViewById(R.id.recycler_faq);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchFAQs();
        return view;
    }

    private void fetchFAQs() {
        RetrofitClient.getApiService().GetFAQs().enqueue(new Callback<List<FAQ>>() {
            @Override
            public void onResponse(Call<List<FAQ>> call, Response<List<FAQ>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    // 將資料餵給 Adapter
                    recyclerView.setAdapter(new FAQAdapter(getContext(), response.body()));
                } else {
                    Log.e("API_FAQ", "Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<FAQ>> call, Throwable t) {
                Log.e("API_FAQ", "連線失敗: " + t.getMessage());
            }
        });
    }
}