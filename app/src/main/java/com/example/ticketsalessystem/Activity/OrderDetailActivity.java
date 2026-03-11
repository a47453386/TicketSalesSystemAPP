package com.example.ticketsalessystem.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.Adapters.TicketAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import Model.UserOrderDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvProgName, tvInfo, tvMarquee;
    private RecyclerView rvTickets;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // 1. 接收從訂單列表傳過來的 ID
        orderId = getIntent().getStringExtra("ORDER_ID");

        initViews();

        if (orderId != null) {
            fetchData(orderId);
        } else {
            Toast.makeText(this, "訂單編號錯誤", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        tvProgName = findViewById(R.id.tv_detail_programme_name);
        tvInfo = findViewById(R.id.tv_detail_info);
        // 🚩 跑馬燈 ID 請確保與 XML 一致
        tvMarquee = findViewById(android.R.id.text1); // 若 XML 沒設 ID 可暫用此或自訂
        rvTickets = findViewById(R.id.rv_ticket_list);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));

        // 🚩 啟動跑馬燈滾動效果
        if (tvMarquee != null) {
            tvMarquee.setSelected(true);
        }
    }

    private void fetchData(String id) {
        // 調用 API
        RetrofitClient.getApiService(this).getOrderDetail(id).enqueue(new Callback<UserOrderDetail>() {
            @Override
            public void onResponse(Call<UserOrderDetail> call, Response<UserOrderDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserOrderDetail data = response.body();

                    // 填充活動主資訊
                    tvProgName.setText("活動名稱: " + data.programmeName);
                    tvInfo.setText("📅 " + data.startTime + " | 📍 " + data.placeName);

                    // 設定票券列表轉接器
                    TicketAdapter adapter = new TicketAdapter(data.tickets, data.isPrintable);
                    rvTickets.setAdapter(adapter);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "找不到訂單詳情", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserOrderDetail> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(OrderDetailActivity.this, "網路連線失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }
}