package com.example.ticketsalessystem.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketsalessystem.R;
import java.util.List;
import Model.QuestionDetail; // 🚩 統一使用 QuestionDetail
import Model.ReplyItem;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {

    private List<QuestionDetail> list;
    private Context context;
    private OnItemClickListener listener;

    // 🚩 介面定義：確保參數型別是 QuestionDetail
    public interface OnItemClickListener {
        void onItemClick(QuestionDetail question);
    }

    public QuestionListAdapter(List<QuestionDetail> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用你提供的像素風佈局
        View view = LayoutInflater.from(context).inflate(R.layout.item_question_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionDetail item = list.get(position);

        holder.tvQTitle.setText(item.questionTitle);
        holder.tvQType.setText("# " + item.questionTypeName);

        // 🚩 直接使用最外層的 replyStatusID (由 GetMyQuestions 提供)
        String statusID = item.replyStatusID;
        String statusText = "待處理";
        int color = Color.parseColor("#00CCFF"); // 藍色

        if ("Y".equals(statusID)) {
            statusText = "結案";
            color = Color.parseColor("#00FF66"); // 綠色
        } else if ("已回覆".equals(item.replyStatusID)) { // 或是根據您的 ID 邏輯
            statusText = "已回覆";
            color = Color.parseColor("#FFCC00"); // 黃色
        }

        if (item.hasUpload) {
            holder.tvimage.setVisibility(View.VISIBLE);
        } else {
            holder.tvimage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.tvQStatus.setText(statusText);
        holder.tvQStatus.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQType, tvQTitle, tvQStatus;
        ImageView tvimage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 🚩 這裡必須對應你的 XML
            tvQType = itemView.findViewById(R.id.tv_q_type);
            tvQTitle = itemView.findViewById(R.id.tv_q_title);
            tvimage=itemView.findViewById(R.id.iv_has_attachment);
            tvQStatus = itemView.findViewById(R.id.tv_q_status);
        }
    }
}