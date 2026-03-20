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

        // a. 先判斷當前是否為模擬器 (Android SDK 的標準判斷方式)
                boolean isEmulator = android.os.Build.FINGERPRINT.startsWith("generic")
                        || android.os.Build.FINGERPRINT.startsWith("unknown")
                        || android.os.Build.MODEL.contains("google_sdk")
                        || android.os.Build.MODEL.contains("Emulator")
                        || android.os.Build.MODEL.contains("Android SDK built for x86")
                        || android.os.Build.MANUFACTURER.contains("Genymotion");

                Log.d("GLIDE_DEBUG", "當前環境是否為模擬器: " + isEmulator);

        // b. 根據環境決定是否替換 IP
                if (imageUrl != null) {
                    if (isEmulator && imageUrl.contains("192.168.0.107")) {
                        // [環境：模擬器] -> 將實體 IP 替換為 10.0.2.2
                        imageUrl = imageUrl.replace("192.168.0.107", "10.0.2.2");
                        Log.d("GLIDE_DEBUG", "[模擬器] 轉換網址為 loopback: " + imageUrl);
                    } else {
                        // [環境：實體手機 或 IP不符] -> 保持原樣 (192.168.0.107)
                        Log.d("GLIDE_DEBUG", "[實體手機] 使用原始網址: " + imageUrl);
                    }
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