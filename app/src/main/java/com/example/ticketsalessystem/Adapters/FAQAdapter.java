package com.example.ticketsalessystem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketsalessystem.R;
import java.util.List;
import Model.FAQ;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {
    private List<FAQ> mData;
    private Context mContext;

    public FAQAdapter(Context context, List<FAQ> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用 item_faq 佈局
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_faq, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FAQ item = mData.get(position);

        // 設定資料
        holder.tvType.setText("# " + (item.faqTypeName != null ? item.faqTypeName : "一般"));
        holder.tvQuestion.setText("Q: " + item.faqTitle);
        holder.tvAnswer.setText("A: " + item.faqDescription);

        // 點擊展開邏輯
        holder.tvAnswer.setVisibility(item.isExpanded ? View.VISIBLE : View.GONE);
        holder.tvQuestion.setTextColor(item.isExpanded ?
                mContext.getColor(R.color.pixel_yellow) : mContext.getColor(R.color.white));

        holder.itemView.setOnClickListener(v -> {
            item.isExpanded = !item.isExpanded;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() { return mData != null ? mData.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer, tvType;
        public ViewHolder(View v) {
            super(v);
            tvType = v.findViewById(R.id.tv_faq_type);
            tvQuestion = v.findViewById(R.id.tv_faq_question);
            tvAnswer = v.findViewById(R.id.tv_faq_answer);
        }
    }
}