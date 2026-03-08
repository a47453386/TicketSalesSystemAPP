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

import com.example.ticketsalessystem.Adapters.QuestionListAdapter;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.List;

import Model.Question; // 🚩 使用你確認後的 Model 名稱
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuestionsFragment extends Fragment {

    private RecyclerView rvOrders; // 對應 activity_my_orders.xml 中的 ID
    private ProgressBar pbLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1. 載入佈局 (複用黑底黃字的訂單清單佈局)
        View v = inflater.inflate(R.layout.activity_my_orders, container, false);

        // 2. 初始化元件，解決 Cannot resolve symbol 報錯
        rvOrders = v.findViewById(R.id.rv_orders);
        pbLoading = v.findViewById(R.id.pb_loading);
        TextView tvMarquee = v.findViewById(R.id.tv_order_marquee);

        // 3. 設定跑馬燈文字與效果
        if (tvMarquee != null) {
            tvMarquee.setText(">>> [ 諮詢進度追蹤系統 ] 如要退票請上傳 PDF 申請書 <<<");
            tvMarquee.setSelected(true);
        }

        // 4. 設定 RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // 5. 抓取資料
        fetchMyQuestions();

        return v;
    }

    private void fetchMyQuestions() {
        // 🚩 顯示進度條
        if (pbLoading != null) {
            pbLoading.setVisibility(View.VISIBLE);
        }

        // 🚩 呼叫 API：不帶參數 (對應後端寫死的測試邏輯)
        RetrofitClient.getApiService().GetMyQuestions().enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                // 🚩 回應後隱藏進度條
                if (isAdded() && pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    // 🚩 修正：傳入資料列表與 Context，解決 Expected 2 arguments 報錯
                    QuestionListAdapter adapter = new QuestionListAdapter(response.body(), getContext());
                    rvOrders.setAdapter(adapter);
                } else {
                    Log.e("API_ERROR", "回傳代碼: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                // 🚩 失敗也要隱藏進度條，避免一直轉圈圈
                if (isAdded() && pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                    Log.e("API_FAILURE", "連線失敗: " + t.getMessage());
                }
            }
        });
    }
}