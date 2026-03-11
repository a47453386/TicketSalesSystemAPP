package com.example.ticketsalessystem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.Fragments.TicketFragment;
import com.example.ticketsalessystem.R;

import java.util.List;

import Model.ProgrammeModel;

public class ProgrammeAdapter extends RecyclerView.Adapter<ProgrammeAdapter.ViewHolder> {

    private List<ProgrammeModel> mData;
    private Context mContext;

    public ProgrammeAdapter(Context context, List<ProgrammeModel> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🚩 載入對應的 item_programme.xml 佈局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_programme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProgrammeModel item = mData.get(position);
        if (item == null) return;

        // 1. 填入基本文字資料
        holder.tvName.setText(item.programmeName);
        holder.tvPlace.setText("@ " + item.placeName);
        holder.tvStartTime.setText("時間：" + item.startTime);

        // 2. 處理剩餘票數與視覺顏色
        if (item.remaining <= 0) {
            holder.tvRemaining.setText("[ SOLD OUT ]");
            holder.tvRemaining.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvRemaining.setText("剩餘票數：" + item.remaining);
            // 🚩 使用像素藍色以維持整體風格
            holder.tvRemaining.setTextColor(mContext.getResources().getColor(R.color.pixel_blue));
        }

        // 3. 🚩 處理圖片網址 IP 轉換 (解決模擬器讀取問題)
        String imageUrl = item.coverImage;
        if (imageUrl != null && imageUrl.contains("192.168.0.107")) {
            // 將電腦實體 IP 替換為模擬器專用的 10.0.2.2
            imageUrl = imageUrl.replace("192.168.0.107", "10.0.2.2");
        }

        // 4. 使用 Glide 載入圖片並進行像素優化
        Glide.with(mContext)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.pixel_placeholder)
                .into(holder.imgCover);

        // 5. 設定點擊項目跳轉至購票頁面
        holder.itemView.setOnClickListener(v -> {
            android.content.Context currentContext = v.getContext();

            if (currentContext instanceof MainActivity) {
                // 1. 建立 Fragment 實例
                String pId = item.programmeID;
                TicketFragment detailFragment = TicketFragment.newInstance(pId);

                // 2. 強制轉型並呼叫 switchFragment
                ((MainActivity) currentContext).switchFragment(detailFragment);
            } else {
                Log.e("AdapterError", "Context is not MainActivity!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvName, tvPlace, tvRemaining, tvStartTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_cover);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPlace = itemView.findViewById(R.id.tv_place);
            tvRemaining = itemView.findViewById(R.id.tv_remaining);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
        }
    }
}