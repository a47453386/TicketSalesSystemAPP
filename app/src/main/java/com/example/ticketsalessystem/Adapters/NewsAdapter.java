package com.example.ticketsalessystem.Adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.R;

import java.util.List;

import Model.PublicNotice;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<PublicNotice> mData;
    private Context mContext;

    public NewsAdapter(Context context, List<PublicNotice> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PublicNotice item = mData.get(position);
        holder.tvTitle.setText(item.publicNoticeTitle);
        // 🚩 這裡可以格式化你的日期
        String rawDate = item.createdTime; // 確保 Model 裡對應的是 createdTime
        if (rawDate != null && rawDate.contains("T")) {
            // 方法 A：用 "T" 分割字串，取第 0 個部分
            String formattedDate = rawDate.split("T")[0];
            holder.tvDate.setText("[" + formattedDate + "]");
        } else if (rawDate != null && rawDate.length() >= 10) {
            // 方法 B：直接截取前 10 個字元 (YYYY-MM-DD)
            holder.tvDate.setText("[" + rawDate.substring(0, 10) + "]");
        } else {
            holder.tvDate.setText("[無日期]");
        }

    }

    @Override
    public int getItemCount() { return mData != null ? mData.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTitle;
        public ViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tv_news_date);
            tvTitle = v.findViewById(R.id.tv_news_title);
        }
    }
}