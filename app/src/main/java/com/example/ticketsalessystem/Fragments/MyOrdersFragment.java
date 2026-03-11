package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.Adapters.OrderAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import Model.BookingDetailsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersFragment extends Fragment {

    private TextView tvProgName, tvInfo;
    private RecyclerView rvTickets;
    private OrderAdapter adapter;
    private ProgressBar progressBar;
    private List<BookingDetailsResponse> orderList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 🚩 載入你的像素風 XML
        return inflater.inflate(R.layout.fragment_my_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();

        // 啟動警告跑馬燈
        TextView tvWarning = view.findViewById(R.id.tv_warning_marquee);
        if (tvWarning != null) tvWarning.setSelected(true);

        // 🚩 進入頁面直接抓資料
        fetchOrders();
    }

    private void initViews(View v) {
        tvProgName = v.findViewById(R.id.tv_detail_programme_name);
        tvInfo = v.findViewById(R.id.tv_detail_info);
        rvTickets = v.findViewById(R.id.rv_ticket_list); // 🚩 對應 XML 中的 ID
        // 如果 XML 裡有進度條，請確保 ID 正確
        progressBar = v.findViewById(R.id.pb_loading);
    }

    private void setupRecyclerView() {
        rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        // 使用原本的 OrderAdapter
        adapter = new OrderAdapter(orderList, getContext());
        rvTickets.setAdapter(adapter);
    }

    private void fetchOrders() {

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService(getContext()).GetOrdersIndex().enqueue(new Callback<List<BookingDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<BookingDetailsResponse>> call, Response<List<BookingDetailsResponse>> response) {
                // 🚩 安全檢查：若 Fragment 已銷毀則不執行
                if (!isAdded()) return;

                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (orderList.isEmpty()) {
                        Toast.makeText(getContext(), "目前沒有訂單紀錄", Toast.LENGTH_SHORT).show();
                    }

                    // 🚩 可選：將第一筆訂單資訊更新到上方的 tvProgName (若有資料)
                    if (!orderList.isEmpty()) {
                        tvProgName.setText("最新訂單: " + orderList.get(0).programmeName);
                    }
                } else {
                    Toast.makeText(getContext(), "資料讀取失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookingDetailsResponse>> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "連線失敗: " + t.getMessage());
                Toast.makeText(getContext(), "網路連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}