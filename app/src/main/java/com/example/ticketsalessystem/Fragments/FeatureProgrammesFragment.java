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
import com.example.ticketsalessystem.Adapters.BannerAdapter;
import com.example.ticketsalessystem.Adapters.ProgrammeAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import java.util.List;
import API.ApiService;
import Model.ProgrammeModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeatureProgrammesFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 🚩 關鍵：防止 NestedScrollView 滑動衝突
        recyclerView.setNestedScrollingEnabled(false);

        fetchFeatureData();
        return recyclerView;
    }

    private void fetchFeatureData() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getProgrammes().enqueue(new Callback<List<ProgrammeModel>>() {
            @Override
            public void onResponse(Call<List<ProgrammeModel>> call, Response<List<ProgrammeModel>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<ProgrammeModel> data = response.body();

                    // 1. 設定下方列表
                    recyclerView.setAdapter(new ProgrammeAdapter(getContext(), data));

                    // 2. 取得父容器的 ViewPager 並設定 Banner
                    if (getParentFragment() instanceof HomeFragment) {
                        HomeFragment home = (HomeFragment) getParentFragment();
                        View homeView = home.getView();
                        if (homeView != null) {
                            androidx.viewpager2.widget.ViewPager2 pager = homeView.findViewById(R.id.viewPagerBanner);
                            pager.setAdapter(new BannerAdapter(getContext(), data));
                            home.setupBannerIndicator();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProgrammeModel>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}