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
import com.example.ticketsalessystem.Adapters.QuestionListAdapter;
import com.example.ticketsalessystem.Fragments.QuestionDetailFragment;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import com.example.ticketsalessystem.SessionManager;

import java.util.List;

import Model.QuestionDetail; // 🚩 確保 import 正確
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuestionsFragment extends Fragment {

    private RecyclerView rvOrders;
    private ProgressBar pbLoading;
    private TextView tvTitle, tvEmptyView;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 共用 fragment_my_orders 的像素風佈局
        return inflater.inflate(R.layout.fragment_my_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化 SessionManager 與元件
        sessionManager = new SessionManager(requireContext());
        initViews(view);

        // 2. 登入檢查 (沒登入就踢走)
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(getContext(), "請先登入以追蹤問題進度", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchFragment(new LoginFragment());
            }
            return;
        }

        // 3. UI 初始設定
        if (tvTitle != null) tvTitle.setText("[ 問題進度追蹤 ]");

        TextView tvMarquee = view.findViewById(R.id.tv_order_marquee);
        if (tvMarquee != null) {
            tvMarquee.setText(">>> 點擊項目查看詳細回覆內容 <<<");
            tvMarquee.setSelected(true);
        }

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4. 開始抓資料
        fetchMyQuestions();
    }

    private void initViews(View v) {
        rvOrders = v.findViewById(R.id.rv_orders);
        pbLoading = v.findViewById(R.id.pb_loading);
        tvTitle = v.findViewById(R.id.tv_order_title);
        tvEmptyView = v.findViewById(R.id.tv_empty_view); // 🚩 也要綁定這個
    }

    private void fetchMyQuestions() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);

        // 🚩 Callback 型別必須與介面定義完全一致
        RetrofitClient.getApiService(requireContext()).GetMyQuestions().enqueue(new Callback<List<QuestionDetail>>() {
            @Override
            public void onResponse(Call<List<QuestionDetail>> call, Response<List<QuestionDetail>> response) {
                if (!isAdded()) return;
                if (pbLoading != null) pbLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ 成功拿到問題清單
                    tvEmptyView.setVisibility(View.GONE);
                    rvOrders.setVisibility(View.VISIBLE);

                    QuestionListAdapter adapter = new QuestionListAdapter(
                            response.body(),
                            getContext(),
                            question -> openQuestionDetail(question.questionID)
                    );
                    rvOrders.setAdapter(adapter);
                } else {
                    // ❌ 處理 404 (沒發問過) 或其他錯誤
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<QuestionDetail>> call, Throwable t) {
                if (!isAdded()) return;
                if (pbLoading != null) pbLoading.setVisibility(View.GONE);
                Log.e("API_FAILURE", "連線失敗: " + t.getMessage());
                tvEmptyView.setVisibility(View.VISIBLE);
                tvEmptyView.setText("--- [ 系統 ]：網路連線異常 ---");
            }
        });
    }
    private void handleErrorResponse(Response<?> response) {
        String serverMsg = "目前尚無諮詢紀錄";
        try {
            if (response.errorBody() != null) {
                org.json.JSONObject jObj = new org.json.JSONObject(response.errorBody().string());
                serverMsg = jObj.getString("message");
            }
        } catch (Exception e) {
            Log.e("API_ERROR", "解析失敗");
        }

        if (response.code() == 404) {
            tvEmptyView.setVisibility(View.VISIBLE);
            tvEmptyView.setText("---" + serverMsg + "---");
            rvOrders.setVisibility(View.GONE);
//            tvTitle.setText("[ 尚無問題 ]");
        } else if (response.code() == 401) {
            sessionManager.logout();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchFragment(new LoginFragment());
            }
        }
    }
    private void openQuestionDetail(String questionId) {
        QuestionDetailFragment detailFragment = QuestionDetailFragment.newInstance(questionId);
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.switchFragment(detailFragment);
        }
    }
}