package com.example.ticketsalessystem.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketsalessystem.R;

import java.util.List;

import Model.ProgrammeModel;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private List<ProgrammeModel> mData;
    private Context mContext;

    public BannerAdapter(Context context, List<ProgrammeModel> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🚩 載入對應的 ViewPager2 子項目佈局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProgrammeModel item = mData.get(position);
        if (item == null) return;

        // 1. 填入精選標題
        if (holder.tvTitle != null) {
            holder.tvTitle.setText(item.programmeName);
        }

        // 🚩 2. 圖片網址處理：解決模擬器無法讀取實體 IP 的問題
        String imageUrl = item.coverImage;
        if (imageUrl != null && imageUrl.contains("192.168.0.107")) {
            // 將電腦實體 IP 替換為模擬器專用的 10.0.2.2
            imageUrl = imageUrl.replace("192.168.0.107", "10.0.2.2");
            Log.d("GLIDE_DEBUG", "轉換後的網址: " + imageUrl);
        }

        // 3. 使用 Glide 載入圖片
        Glide.with(mContext)
                .load(imageUrl)
                .centerCrop() // 確保圖片填滿 ViewPager2 區域
                .placeholder(android.R.drawable.ic_menu_gallery) // 載入中的暫位圖
                .error(android.R.drawable.stat_notify_error) // 載入失敗的圖示
                .into(holder.imgBanner);
    }

    @Override
    public int getItemCount() {
        // 🚩 限制輪播數量，避免一次載入過多
        return mData != null ? Math.min(mData.size(), 5) : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
            tvTitle = itemView.findViewById(R.id.tv_banner_title); // 與 XML ID 對齊
        }
    }
}