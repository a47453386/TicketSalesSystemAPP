package com.example.ticketsalessystem.Programme;

import android.content.Context;
import android.content.Intent;
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

public class ProgrammeAdapter extends RecyclerView.Adapter<ProgrammeAdapter.ViewHolder> {

    private List<ProgrammeModel> mData;
    private Context mContext;

    // 建構子：讓 MainActivity 把資料傳進來
    public ProgrammeAdapter(Context context, List<ProgrammeModel> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🚩 載入你之前做好的 item_programme.xml 佈局
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_programme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProgrammeModel item = mData.get(position);

        // 填入文字資料
        holder.tvName.setText(item.programmeName);
        holder.tvPlace.setText(item.placeName);
        holder.tvRemaining.setText("剩餘票數：" + item.remaining);
        holder.tvStartTime.setText("時間：" + item.startTime);

        // 處理公告小圖示
        if (item.publicNoticeTilte != null && !item.publicNoticeTilte.isEmpty()) {
            holder.tvName.append(" [📢]");
        }

        // 🚩 使用 Glide 載入後端拼好的完整圖片網址
        Glide.with(mContext)
                .load(item.coverImage)
                .placeholder(android.R.drawable.ic_menu_gallery) // 載入中的暫位圖
                .into(holder.imgCover);

        // 🚩 點擊事件：跳轉到購票頁面
        holder.itemView.setOnClickListener(v -> {
//             使用 v.getContext() 獲取最準確的 Context
            Intent intent = new Intent(v.getContext(), TicketActivity.class);
            intent.putExtra("PROGRAMME_ID", item.programmeID);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    // ViewHolder：定義卡片裡有哪些 UI 元件
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvName, tvPlace, tvRemaining, tvStartTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_cover);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPlace = itemView.findViewById(R.id.tv_place);
            tvRemaining = itemView.findViewById(R.id.tv_remaining);
            tvStartTime = itemView.findViewById(R.id.tv_start_time); // 🚩 記得 XML 也要有這個 ID
        }
    }

}
