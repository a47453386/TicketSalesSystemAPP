package com.example.ticketsalessystem.Orders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import Model.BookingDetailsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private ProgressBar progressBar;
    private List<BookingDetailsResponse> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        initViews();
        fetchOrders(); // 🚩 進入頁面直接抓資料
    }

    private void initViews() {
        // 1. 綁定 ID (請確保 XML 中有 android:id="@+id/rv_orders")
        rvOrders = findViewById(R.id.rv_orders);
        progressBar = findViewById(R.id.pb_loading); // 建議在 XML 加入一個進度條

        // 2. 設定 LayoutManager (線性列表)
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // 3. 初始化 Adapter 並綁定
        adapter = new OrderAdapter(orderList, this);
        rvOrders.setAdapter(adapter);
    }

    private void fetchOrders() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // 🚩 呼叫剛才定義好的 ApiService
        RetrofitClient.getApiService().GetOrdersIndex().enqueue(new Callback<List<BookingDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<BookingDetailsResponse>> call, Response<List<BookingDetailsResponse>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // 清空舊資料並加入新資料
                    orderList.clear();
                    orderList.addAll(response.body());

                    // 通知 Adapter 畫面需要更新
                    adapter.notifyDataSetChanged();

                    if (orderList.isEmpty()) {
                        Toast.makeText(MyOrdersActivity.this, "目前沒有訂單紀錄", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyOrdersActivity.this, "資料讀取失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookingDetailsResponse>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "連線失敗: " + t.getMessage());
                Toast.makeText(MyOrdersActivity.this, "網路連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}