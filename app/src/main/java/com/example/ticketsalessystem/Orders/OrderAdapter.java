package com.example.ticketsalessystem.Orders;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ticketsalessystem.R;
import java.util.List;

import Model.BookingDetailsResponse;

import Model.TicketDetail;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<BookingDetailsResponse> orderList;
    private Context context;

    public OrderAdapter(List<BookingDetailsResponse> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        BookingDetailsResponse order = orderList.get(position);

        // 1. 基礎資訊綁定
        holder.tvOrderId.setText("訂單編號: " + order.orderID);
        holder.tvProgrammeName.setText(order.programmeName);
        holder.tvStartTime.setText(order.startTime);
        holder.tvAmount.setText("總計額：$" + (int)order.finalAmount);

        // 2. 🚩 狀態顏色與按鈕邏輯 (對應 MVC switch)
        if ("Y".equals(order.orderStatusID)) {
            holder.tvStatus.setText("● 付款完成");
            holder.tvStatus.setTextColor(Color.parseColor("#00FF66")); // 像素亮綠
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("入場證明");
            holder.btnAction.setOnClickListener(v -> {
                // 這裡可以導向顯示 QR Code 的 Activity
                // Intent intent = new Intent(context, TicketDetailActivity.class);
                // intent.putExtra("ORDER_ID", order.orderID);
                // context.startActivity(intent);
            });
        } else if ("P".equals(order.orderStatusID)) {
            holder.tvStatus.setText("● 待付款");
            holder.tvStatus.setTextColor(Color.parseColor("#FFCC00")); // 像素黃
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("前往付款");
            holder.btnAction.setOnClickListener(v -> {
                // 回到付款頁面
            });
        } else if ("C".equals(order.orderStatusID)) {
            holder.tvStatus.setText("● 已取消");
            holder.tvStatus.setTextColor(Color.parseColor("#FF00CC")); // 霓虹粉
            holder.btnAction.setVisibility(View.GONE);
        }

        // 3. 🚩 摺疊明細邏輯
        holder.btnToggleDetail.setOnClickListener(v -> {
            if (holder.layoutDetail.getVisibility() == View.GONE) {
                holder.layoutDetail.setVisibility(View.VISIBLE);
                holder.btnToggleDetail.setText("[ 收合明細 ]");

                // 組合票券細項 (Row, Seat, Price)
                StringBuilder sb = new StringBuilder();
                if (order.ticketDetails != null) {
                    for (TicketDetail detail : order.ticketDetails) {
                        sb.append("🎟️ ").append(detail.seatInfo)
                                .append(" | $").append((int)detail.price).append("\n");
                    }
                }
                holder.tvDetailContent.setText(sb.toString());
            } else {
                holder.layoutDetail.setVisibility(View.GONE);
                holder.btnToggleDetail.setText("[ 檢視明細 ]");
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    // --- ViewHolder 內部類別 ---
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvProgrammeName, tvStartTime, tvAmount, tvStatus, tvDetailContent;
        Button btnToggleDetail, btnAction;
        LinearLayout layoutDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvProgrammeName = itemView.findViewById(R.id.tv_programme_name);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvAmount = itemView.findViewById(R.id.tv_total_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDetailContent = itemView.findViewById(R.id.tv_detail_content);
            btnToggleDetail = itemView.findViewById(R.id.btn_toggle_detail);
            btnAction = itemView.findViewById(R.id.btn_action);
            layoutDetail = itemView.findViewById(R.id.layout_ticket_details);
        }
    }
}