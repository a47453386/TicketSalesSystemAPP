package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.Adapters.OrderAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.List;

import Model.BookingDetailsResponse;
import Model.UserOrderDetail; // 🚩 確保 Model 名稱與你定義的一致
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {
    private RecyclerView rvOrders;
    private ProgressBar pbLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. 載入你的黑底黃字佈局
        View v = inflater.inflate(R.layout.fragment_my_orders, container, false);

        // 2. 初始化元件
        rvOrders = v.findViewById(R.id.rv_orders);
        pbLoading = v.findViewById(R.id.pb_loading);
        TextView tvMarquee = v.findViewById(R.id.tv_order_marquee);

        // 3. 啟動跑馬燈效果
        if (tvMarquee != null) {
            tvMarquee.setSelected(true);
        }

        // 4. 設定 RecyclerView 佈局管理器
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // 5. 抓取資料
        fetchOrders();

        return v;
    }

    private void fetchOrders() {
        pbLoading.setVisibility(View.VISIBLE); // 顯示轉圈圈

        // 🚩 修正：因為 API 沒定義參數，所以這裡不能傳入 memberId
        RetrofitClient.getApiService(getContext()).GetOrdersIndex().enqueue(new Callback<List<BookingDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<BookingDetailsResponse>> call, Response<List<BookingDetailsResponse>> response) {
                if (isAdded()) pbLoading.setVisibility(View.GONE); // 關閉轉圈圈

                if (response.isSuccessful() && response.body() != null) {
                    // 使用對應的 Model 和 Adapter 顯示資料
                    OrderAdapter adapter = new OrderAdapter(response.body(), getContext());
                    rvOrders.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<BookingDetailsResponse>> call, Throwable t) {
                if (isAdded()) pbLoading.setVisibility(View.GONE); // 失敗也要關閉轉圈圈
            }
        });
    }
}