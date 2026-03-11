package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.databinding.FragmentQuestionDetailBinding;
import com.example.ticketsalessystem.databinding.ItemReplyBinding;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import Model.QuestionDetail;
import Model.ReplyItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionDetailFragment extends Fragment {

    private FragmentQuestionDetailBinding binding;
    private ReplyAdapter adapter;
    private String questionId;

    // 建立實例時傳入 QuestionID
    public static QuestionDetailFragment newInstance(String id) {
        QuestionDetailFragment fragment = new QuestionDetailFragment();
        Bundle args = new Bundle();
        args.putString("question_id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionId = getArguments().getString("question_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用 ViewBinding 載入你提供的 XML
        binding = FragmentQuestionDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 設定 RecyclerView
        setupRecyclerView();

        // 2. 執行 API 請求
        if (questionId != null) {
            fetchData();
        } else {
            Toast.makeText(getContext(), "無效的問題 ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        adapter = new ReplyAdapter(new ArrayList<>());
        binding.rvReplies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReplies.setAdapter(adapter);
    }

    private void fetchData() {
        // 🚩 這裡假設你已經有 RetrofitClient 類別
        // 請確保你的 ApiService 裡有 @GET("api/HomeApi/GetQuestionsDetail/{id}")
        RetrofitClient.getApiService(getContext()).getQuestionDetail(questionId).enqueue(new Callback<QuestionDetail>() {
            @Override
            public void onResponse(Call<QuestionDetail> call, Response<QuestionDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayData(response.body());
                } else {
                    // 🚩 這裡修改：印出詳細的錯誤代碼與訊息
                    int code = response.code();
                    Log.e("API_DEBUG", "錯誤代碼: " + code);
                    if (code == 401) {
                        Toast.makeText(getContext(), "身分驗證失敗 (401)，請重新登入", Toast.LENGTH_SHORT).show();
                    } else if (code == 404) {
                        Toast.makeText(getContext(), "找不到該問題 (404)，請確認 ID", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "伺服器錯誤 (" + code + ")", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<QuestionDetail> call, Throwable t) {
                Log.e("API_ERROR", "連線失敗: " + t.getMessage());
                Toast.makeText(getContext(), "伺服器連線超時，請檢查網路", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(QuestionDetail data) {
        // --- A. 處理頂部導覽資訊 ---
        String orderIdText = (data.questionID != null && data.questionID.length() > 8)
                ? data.questionID.substring(0, 8) : data.questionID;
        binding.tvOrderInfo.setText("訂單編號: #" + orderIdText + " | 種類: " + data.questionTypeName);

        // --- B. 處理狀態 Badge (對應 Web 版邏輯) ---
        String status = "待處理";
        if (data.reply != null && !data.reply.isEmpty()) {
            status = "已回覆";
            // 檢查最後一筆回覆是否為結案狀態
            ReplyItem lastReply = data.reply.get(data.reply.size() - 1);
            if ("Y".equals(lastReply.replyStatusName) || "結案".equals(lastReply.replyStatusName)) {
                status = "結案";
            }
        }
        binding.tvStatusBadge.setText("狀態: " + status);

        // --- C. 處理問題主體 ---
        binding.tvSubject.setText(data.questionTitle);
        binding.tvDescription.setText(data.questionDescription);

        // 處理時間格式 (移除 ISO 格式中的 'T')
        if (data.createdTime != null) {
            binding.tvCreatedTime.setText("上傳時間: " + data.createdTime.replace("T", " ").substring(0, 19));
        }

        // --- D. 處理附件 (如果有檔案則顯示按鈕) ---
        if (data.uploadFile != null && !data.uploadFile.isEmpty()) {
            binding.layoutAttachment.setVisibility(View.VISIBLE);
            binding.layoutAttachment.setOnClickListener(v -> {
                // 這裡可以實作開啟瀏覽器觀看圖片
                Toast.makeText(getContext(), "開啟附件: " + data.uploadFile, Toast.LENGTH_SHORT).show();
            });
        }

        // --- E. 更新回覆清單 ---
        if (data.reply != null) {
            adapter.updateData(data.reply);
        }
    }

    // 🚩 內部類別：處理管理員回覆的 Adapter
    private class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
        private List<ReplyItem> replyList;

        public ReplyAdapter(List<ReplyItem> list) {
            this.replyList = list;
        }

        public void updateData(List<ReplyItem> newList) {
            this.replyList = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 使用之前為你設計的 item_reply 佈局
            ItemReplyBinding itemBinding = ItemReplyBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ReplyViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
            ReplyItem item = replyList.get(position);
            holder.binding.tvReplyDescription.setText(item.replyDescription);

            // 組合回覆者資訊與時間
            String dateStr = item.createdTime != null ? item.createdTime.replace("T", " ").substring(0, 16) : "";
            String meta = "回覆者: " + item.employeeName + " | " + dateStr;
            holder.binding.tvReplyDate.setText(meta);
        }

        @Override
        public int getItemCount() {
            return replyList.size();
        }

        class ReplyViewHolder extends RecyclerView.ViewHolder {
            ItemReplyBinding binding;
            public ReplyViewHolder(ItemReplyBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 防止記憶體洩漏
    }
}