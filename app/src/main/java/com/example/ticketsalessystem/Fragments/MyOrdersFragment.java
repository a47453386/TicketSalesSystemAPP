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

import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.Adapters.OrderAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import com.example.ticketsalessystem.SessionManager;

import java.util.ArrayList;
import java.util.List;

import Model.BookingDetailsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersFragment extends Fragment {

    private TextView tvTitle, tvEmptyView;
    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private ProgressBar progressBar;
    private List<BookingDetailsResponse> orderList = new ArrayList<>();
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(requireContext(), "偵錯：Fragment 已啟動", Toast.LENGTH_LONG).show();
        sessionManager = new SessionManager(requireContext());

        // 1. 登入檢查
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(getContext(), "請先登入以查看訂單", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchFragment(new LoginFragment());
            }
            return;
        }

        initViews(view);
        setupRecyclerView();

        // 2. 跑馬燈啟動
        TextView tvMarquee = view.findViewById(R.id.tv_order_marquee);
        if (tvMarquee != null) tvMarquee.setSelected(true);

        // 3. 初始偵錯狀態
//        if (tvEmptyView != null) {
//            tvEmptyView.setVisibility(View.VISIBLE);
//            tvEmptyView.setText(">>> 正在搜尋訂單資料...");
//        }


        fetchOrders();
    }

    private void initViews(View v) {
        tvTitle = v.findViewById(R.id.tv_order_title);
        tvEmptyView = v.findViewById(R.id.tv_empty_view);
        rvOrders = v.findViewById(R.id.rv_orders);
        progressBar = v.findViewById(R.id.pb_loading);
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(orderList, getContext());
        rvOrders.setAdapter(adapter);
    }

    private void fetchOrders() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService(requireContext()).GetOrdersIndex().enqueue(new Callback<List<BookingDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<BookingDetailsResponse>> call, Response<List<BookingDetailsResponse>> response) {
                Log.d("API_RESULT", "HTTP Code: " + response.code());

                if (getActivity() == null) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    // ✅ 200 OK 的邏輯
                    orderList.clear();
                    if (response.body() != null) orderList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    tvEmptyView.setVisibility(View.GONE);
                    rvOrders.setVisibility(View.VISIBLE);
                    tvTitle.setText("[ 訂單查詢系統 ]");
                }
                else {
                    // ❌ 處理 404 (沒訂單)
                    if (response.code() == 404) {
                        String serverMessage = "查無資料"; // 預設值

                        try {
                            // 🚩 關鍵：從 errorBody 中抓取 {"message":"目前沒有訂單"}
                            if (response.errorBody() != null) {
                                String errorStr = response.errorBody().string();
                                org.json.JSONObject jsonObject = new org.json.JSONObject(errorStr);
                                serverMessage = jsonObject.getString("message");
                            }
                        } catch (Exception e) {
                            Log.e("API_ERROR", "解析訊息失敗", e);
                        }

//                         🚩 顯示到你的黃色 TextView 畫面上
                        if (tvEmptyView != null) {
                            tvEmptyView.setVisibility(View.VISIBLE);
                            tvEmptyView.setText("--- 目前尚無訂單 ---");
                            tvEmptyView.bringToFront(); // 確保文字在最前面
                        }

                        // 隱藏列表並修改標題顏色作為「肉眼偵錯」
//                        rvOrders.setVisibility(View.GONE);
//                        tvTitle.setText("[ 尚無訂單 ]");
//                        tvTitle.setTextColor(android.graphics.Color.RED);
                    }
                    else if (response.code() == 401) {
                        handleUnauthorized();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BookingDetailsResponse>> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "連線失敗: " + t.getMessage());
                if (tvEmptyView != null) {
                    tvEmptyView.setVisibility(View.VISIBLE);
                    tvEmptyView.setText(">>> 連線失敗，請檢查網路狀態");
                }
            }
        });
    }

    private void handleUnauthorized() {
        sessionManager.logout();
        Toast.makeText(getContext(), "登入逾時，請重新登入", Toast.LENGTH_SHORT).show();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateNavUI();
            ((MainActivity) getActivity()).switchFragment(new LoginFragment());
        }
    }
}