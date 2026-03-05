package com.example.ticketsalessystem.Programme;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketsalessystem.Orders.MyOrdersActivity;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.ArrayList;

import Model.BookingRequest;
import Model.BookingResponse;
import Model.PaymentRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmActivity extends AppCompatActivity {

    private TextView tvProgramme, tvSession, tvCount, tvAmount, tvSeats, tvTimer;
    private Button btnConfirm, btnPayNow;
    private ProgressBar progressBar;
    private LinearLayout layoutResult;
    private RadioGroup rgPaymentMethods;

    // 儲存從 Intent 接收到的資料
    private String programmeId, sessionId, areaId;
    private double totalAmount;
    private int ticketCount;

    private String currentOrderId;
    private CountDownTimer countDownTimer; // 全域變數，方便在付款成功時取消

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        initViews();
        receiveIntentData();

        // 點擊「確認購票」按鈕，觸發後端配票
        btnConfirm.setOnClickListener(v -> sendBookingRequest());
    }

    private void initViews() {
        tvProgramme = findViewById(R.id.tv_confirm_programme);
        tvSession = findViewById(R.id.tv_confirm_session);
        tvCount = findViewById(R.id.tv_confirm_count);
        tvAmount = findViewById(R.id.tv_confirm_amount);
        tvSeats = findViewById(R.id.tv_allocated_seats);
        tvTimer = findViewById(R.id.tv_expire_timer);
        btnConfirm = findViewById(R.id.btn_final_confirm);
        progressBar = findViewById(R.id.pb_confirm);
        layoutResult = findViewById(R.id.layout_result);
        rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        btnPayNow = findViewById(R.id.btn_pay_now);

        // 預設先停用付款按鈕，直到訂單建立成功拿到 OrderID
        btnPayNow.setEnabled(false);
    }

    private void receiveIntentData() {
        programmeId = getIntent().getStringExtra("PROGRAMME_ID");
        sessionId = getIntent().getStringExtra("SESSION_ID");
        areaId = getIntent().getStringExtra("AREA_ID");
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0);
        ticketCount = getIntent().getIntExtra("TICKET_COUNT", 1);

        tvProgramme.setText("活動編號: " + programmeId);
        tvSession.setText("場次編號: " + sessionId);
        tvCount.setText("購票張數: " + ticketCount + " 張");
        tvAmount.setText("總計金額: $" + (int)totalAmount);
    }

    private void sendBookingRequest() {
        progressBar.setVisibility(View.VISIBLE);
        btnConfirm.setEnabled(false);

        BookingRequest request = new BookingRequest();
        request.venueID = "";
        request.sessionID = sessionId;
        request.ticketsAreaID = areaId;
        request.memberID = "a8e36451-c3fb-44ba-a05e-602ca0760166"; // 測試用 ID
        request.paymentMethodID = "A"; // 預設初始方式
        request.count = ticketCount;
        request.totalAmount = totalAmount;
        request.seats = new ArrayList<>(); // 自動配票傳空清單

        RetrofitClient.getApiService().confirmBooking(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BookingResponse result = response.body();
                    if (result.success) {
                        showSuccessResult(result);
                    } else {
                        Toast.makeText(OrderConfirmActivity.this, "配票失敗: " + result.message, Toast.LENGTH_LONG).show();
                        btnConfirm.setEnabled(true);
                    }
                } else {
                    btnConfirm.setEnabled(true);
                    Log.e("API_ERROR", "HTTP Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                Toast.makeText(OrderConfirmActivity.this, "網路連線失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessResult(BookingResponse result) {
        btnConfirm.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);

        this.currentOrderId = result.orderID;

        if (result.seats != null) {
            tvSeats.setText("已分配座位: " + String.join(", ", result.seats));
        }

        // 啟動倒數計時
        startCountDown(result.remainingSeconds);

        if (currentOrderId != null) {
            btnPayNow.setEnabled(true);
            btnPayNow.setOnClickListener(v -> {
                // 根據選取的 RadioButton 決定傳送給後端的 PaymentMethodID
                String selectedId = "A"; // 預設 A
                int checkedId = rgPaymentMethods.getCheckedRadioButtonId();

                if (checkedId == R.id.rb_atm_pay) {
                    selectedId = "A";
                } else if (checkedId == R.id.rb_credit_card) {
                    selectedId = "c";
                }

                performPayment(selectedId);
            });
        }
    }

    private void startCountDown(int seconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                long min = (millisUntilFinished / 1000) / 60;
                long sec = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("請於 %02d:%02d 內完成付款", min, sec));
            }

            public void onFinish() {
                tvTimer.setText("訂單已逾期，請返回重新操作");
                tvTimer.setTextColor(0xFFFF0000);
                layoutResult.setBackgroundColor(0xFFFFEBEE);
                btnPayNow.setEnabled(false);
            }
        }.start();
    }

    private void performPayment(String methodId) {
        btnPayNow.setEnabled(false); // 防止重複點擊

        PaymentRequest request = new PaymentRequest(currentOrderId, methodId);

        RetrofitClient.getApiService().processPayment(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    // 付款成功，取消計時器並提示
                    if (countDownTimer != null) countDownTimer.cancel();
                    Toast.makeText(OrderConfirmActivity.this, "付款成功！票券已發送至您的帳戶", Toast.LENGTH_LONG).show();
                    // 🚩 2. 修改這裡：跳轉到訂單清單頁面 (MyOrdersActivity)
                    Intent intent = new Intent(OrderConfirmActivity.this, MyOrdersActivity.class);

                    // 如果你希望跳轉後按「返回鍵」不要回到這個付款確認頁，可以加上這個 Flag
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);
                    finish(); // 結束此頁面
                } else {
                    btnPayNow.setEnabled(true);
                    String msg = response.body() != null ? response.body().message : "付款失敗";
                    Toast.makeText(OrderConfirmActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                btnPayNow.setEnabled(true);
                Toast.makeText(OrderConfirmActivity.this, "連線異常，請檢查網路", Toast.LENGTH_SHORT).show();
            }
        });
    }
}