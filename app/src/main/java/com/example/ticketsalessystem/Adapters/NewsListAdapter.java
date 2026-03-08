package com.example.ticketsalessystem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketsalessystem.R;
import java.util.List;
import Model.PublicNotice;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
    private List<PublicNotice> mData;
    private Context mContext;

    public NewsListAdapter(Context context, List<PublicNotice> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🚩 複用你之前做好的 item_news 佈局
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PublicNotice item = mData.get(position);
        holder.tvTitle.setText(item.publicNoticeTitle);

        // 🚩 處理日期：YYYY-MM-DD
        if (item.createdTime != null && item.createdTime.contains("T")) {
            holder.tvDate.setText("[" + item.createdTime.split("T")[0] + "]");
        }

        // 🚩 點擊顯示詳細內容
        holder.itemView.setOnClickListener(v -> {
            new AlertDialog.Builder(mContext)
                    .setTitle(item.publicNoticeTitle)
                    .setMessage(item.publicNoticeDescription) // 顯示完整描述
                    .setPositiveButton("我知道了", null)
                    .show();
        });
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