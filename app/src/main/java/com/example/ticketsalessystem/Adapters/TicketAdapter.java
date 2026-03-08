package com.example.ticketsalessystem.Adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout; // 🚩 配合 XML 的 FrameLayout
import android.widget.ImageView;
import android.widget.LinearLayout; // 🚩 配合 XML 的 LinearLayout
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;

import Model.UserTicketItem;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private List<UserTicketItem> ticketList;
    private boolean isPrintable;

    public TicketAdapter(List<UserTicketItem> ticketList, boolean isPrintable) {
        this.ticketList = ticketList;
        this.isPrintable = isPrintable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 載入 item_pixel_ticket.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pixel_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserTicketItem item = ticketList.get(position);

        // 1. 綁定基本文字
        holder.tvArea.setText("票區: " + item.ticketsAreaName);
        holder.tvSeat.setText(item.seat);
        if (item.ticketsID != null && item.ticketsID.length() > 8) {
            holder.tvId.setText("ID: " + item.ticketsID.substring(0, 8));
        }

        // 2. 顯示/隱藏邏輯控制
        if (isPrintable && item.checkInCode != null) {
            // 已開放：顯示 QR Code 容器，隱藏鎖定狀態
            holder.layoutQr.setVisibility(View.VISIBLE);
            holder.layoutLocked.setVisibility(View.GONE);

            // 產生並設定 QR Code 圖片
            Bitmap qrBitmap = generateQRCode(item.checkInCode);
            if (qrBitmap != null) {
                holder.imgQr.setImageBitmap(qrBitmap);
            }
        } else {
            // 尚未開放：顯示鎖定狀態，隱藏 QR Code 容器
            holder.layoutQr.setVisibility(View.GONE);
            holder.layoutLocked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return ticketList != null ? ticketList.size() : 0;
    }

    // 🚩 QR Code 產生邏輯
    private Bitmap generateQRCode(String text) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvArea, tvSeat, tvId;
        ImageView imgQr;
        FrameLayout layoutQr;      // 🚩 對應 XML 的 FrameLayout
        LinearLayout layoutLocked; // 🚩 對應 XML 的 LinearLayout

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 🚩 ID 必須與 XML 完全對應
            tvArea = itemView.findViewById(R.id.tv_ticket_area);
            tvSeat = itemView.findViewById(R.id.tv_ticket_seat);
            tvId = itemView.findViewById(R.id.tv_ticket_id_small);
            imgQr = itemView.findViewById(R.id.img_qrcode);
            layoutQr = itemView.findViewById(R.id.layout_qr_container);
            layoutLocked = itemView.findViewById(R.id.layout_locked_status);
        }
    }
}