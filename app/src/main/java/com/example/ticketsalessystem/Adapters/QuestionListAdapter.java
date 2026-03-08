package com.example.ticketsalessystem.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketsalessystem.R;
import java.util.List;

import Model.Question;


public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.QViewHolder> {
    private List<Question> list;
    private Context context;

    public QuestionListAdapter(List<Question> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public QViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_log, parent, false);
        return new QViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QViewHolder holder, int position) {
        // 🚩 修正：型別必須是 QuestionCreate
        Question q = list.get(position);

        holder.tvType.setText("# " + (q.questionTypeName != null ? q.questionTypeName : "一般"));
        holder.tvTitle.setText(q.questionTitle);

        // 🚩 同步你的 C# 投影邏輯與 MVC 狀態顏色
        String statusText;
        int badgeColor;
        if ("Y".equals(q.replyStatusID)) {
            statusText = "已結案";
            badgeColor = Color.parseColor("#FF00CC"); // pixel-pink
        } else if ("R".equals(q.replyStatusID)) {
            statusText = "已回覆";
            badgeColor = Color.parseColor("#00CCFF"); // pixel-blue
        } else {
            statusText = "處理中";
            badgeColor = Color.parseColor("#FFCC00"); // pixel-yellow
        }

        holder.tvStatus.setText(statusText);
        holder.tvStatus.setBackgroundColor(badgeColor);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class QViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvTitle, tvStatus;
        public QViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_q_type);
            tvTitle = itemView.findViewById(R.id.tv_q_title);
            tvStatus = itemView.findViewById(R.id.tv_q_status);
        }
    }
}