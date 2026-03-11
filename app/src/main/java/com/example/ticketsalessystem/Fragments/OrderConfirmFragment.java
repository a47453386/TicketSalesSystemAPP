package com.example.ticketsalessystem.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import com.example.ticketsalessystem.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

import Model.BookingRequest;
import Model.BookingResponse;
import Model.PaymentRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmFragment extends Fragment {

    private TextView tvTitle, tvProgramme, tvSession, tvArea, tvCount, tvAmount, tvSeats, tvTimer;
    private Button btnConfirm, btnPayNow;
    private ProgressBar progressBar;
    private LinearLayout layoutResult;
    private RadioGroup rgPaymentMethods;

    private String programmeId, sessionId, areaId, programmeName, sessionTime, areaName;
    private double totalAmount;
    private int ticketCount;
    private String currentOrderId;
    private CountDownTimer countDownTimer;

    // 🚩 使用 newInstance 傳遞資料 (取代 Intent)
    public static OrderConfirmFragment newInstance(Bundle args) {
        OrderConfirmFragment fragment = new OrderConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 載入剛才設計的像素風 Layout
        return inflater.inflate(R.layout.fragment_order_confirm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        receiveArguments();

        btnConfirm.setOnClickListener(v -> sendBookingRequest());
    }

    private void initViews(View v) {
        tvTitle = v.findViewById(R.id.tv_title);
        tvProgramme = v.findViewById(R.id.tv_confirm_programme);
        tvSession = v.findViewById(R.id.tv_confirm_session);
        tvArea = v.findViewById(R.id.tv_confirm_area);
        tvCount = v.findViewById(R.id.tv_confirm_count);
        tvAmount = v.findViewById(R.id.tv_confirm_amount);
        tvSeats = v.findViewById(R.id.tv_allocated_seats);
        tvTimer = v.findViewById(R.id.tv_expire_timer);
        btnConfirm = v.findViewById(R.id.btn_final_confirm);
        btnPayNow = v.findViewById(R.id.btn_pay_now);
        progressBar = v.findViewById(R.id.pb_confirm);
        layoutResult = v.findViewById(R.id.layout_result);
        rgPaymentMethods = v.findViewById(R.id.rg_payment_methods);

        applyPixelTypeface();
    }

    private void applyPixelTypeface() {
        Typeface pixelFont = Typeface.MONOSPACE;
        tvTitle.setTypeface(pixelFont);
        tvProgramme.setTypeface(pixelFont);
        tvSession.setTypeface(pixelFont);
        tvArea.setTypeface(pixelFont);
        tvCount.setTypeface(pixelFont);
        tvAmount.setTypeface(pixelFont);
        tvTimer.setTypeface(pixelFont);
        btnConfirm.setTypeface(pixelFont);
        btnPayNow.setTypeface(pixelFont);
    }

    private void receiveArguments() {
        if (getArguments() != null) {
            Bundle args = getArguments();
            programmeId = args.getString("PROGRAMME_ID");
            sessionId = args.getString("SESSION_ID");
            areaId = args.getString("AREA_ID");
            programmeName = args.getString("PROGRAMME_NAME");
            sessionTime = args.getString("SESSION_TIME");
            areaName = args.getString("AREA_NAME");
            totalAmount = args.getDouble("TOTAL_AMOUNT", 0.0);
            ticketCount = args.getInt("TICKET_COUNT", 1);

            tvProgramme.setText(programmeName != null ? programmeName : "[ 活動載入中 ]");
            tvSession.setText(">>> 時間: " + sessionTime);
            tvArea.setText(">>> 區域: " + areaName);
            tvCount.setText(">>> 張數: " + ticketCount + " 張");
            tvAmount.setText("TOTAL: $" + (int)totalAmount);
        }
    }


    private void sendBookingRequest() {
        // 1. 初始化讀取狀態
        progressBar.setVisibility(View.VISIBLE);
        btnConfirm.setEnabled(false);
        btnConfirm.setText("[ 正在傳輸數據... ]");

        //初始化 SessionManager
        SessionManager sessionManager = new SessionManager(getContext());
        String realMemberId = sessionManager.getMemberID();

        if (realMemberId == null) {
            Toast.makeText(getContext(), "系統錯誤：請先重新登入", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).switchFragment(new LoginFragment());
            return;
        }

        // 2. 封裝請求資料
        BookingRequest request = new BookingRequest();
        request.venueID = "";
        request.sessionID = sessionId;
        request.ticketsAreaID = areaId;
        // 🚩 測試用 ID，建議確認資料庫中確實有此會員
        request.memberID = realMemberId;
        request.paymentMethodID = "A"; // 預設支付方式
        request.count = ticketCount;
        request.totalAmount = totalAmount;
        request.seats = new ArrayList<>(); // 自動配票傳送空清單

        // 3. 發送 API 請求
        RetrofitClient.getApiService(getContext()).confirmBooking(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                // 🚩 安全檢查：若使用者已離開此 Fragment 則不執行後續動作
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    BookingResponse result = response.body();

                    if (result.success) {
                        // 🚩 成功：進入配票成功流程（顯示座位與計時器）
                        showSuccessResult(result);
                    } else {
                        // 🚩 邏輯失敗：例如「票券已售罄」或「餘額不足」
                        resetConfirmButton();
                        Toast.makeText(getContext(), "配票失敗: " + result.message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // 🚩 伺服器代碼錯誤：如 404, 500
                    resetConfirmButton();
                    String errorInfo = "系統異常代碼: " + response.code();
                    Log.e("API_ERROR", errorInfo);
                    Toast.makeText(getContext(), "伺服器反應異常 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                resetConfirmButton();

                // 🚩 網路連線失敗：如 伺服器沒開、手機沒網路
                Log.e("API_ERROR", "網路連線異常: " + t.getMessage());
                Toast.makeText(getContext(), "連線失敗，請檢查網路狀態", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 🚩 輔助方法：將確認按鈕恢復至初始像素風狀態
     */
    private void resetConfirmButton() {
        if (btnConfirm != null) {
            btnConfirm.setEnabled(true);
            btnConfirm.setText("[ 確認執行配票 ]");
        }
    }

    private void showSuccessResult(BookingResponse result) {
        btnConfirm.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
        this.currentOrderId = result.orderID;

        if (result.seats != null && !result.seats.isEmpty()) {
            tvSeats.setText("已分配座位: " + String.join(", ", result.seats));
        }

        startCountDown(result.remainingSeconds > 0 ? result.remainingSeconds : 600);

        btnPayNow.setEnabled(true);
        btnPayNow.setOnClickListener(v -> {
            String selectedId = (rgPaymentMethods.getCheckedRadioButtonId() == R.id.rb_credit_card) ? "C" : "A";
            performPayment(selectedId);
        });
    }

    private void startCountDown(int seconds) {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                long min = (millisUntilFinished / 1000) / 60;
                long sec = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "請於 %02d:%02d 內完成付款", min, sec));
            }

            public void onFinish() {
                tvTimer.setText("! 訂單已逾期 !");
                tvTimer.setTextColor(Color.RED);
                btnPayNow.setEnabled(false);
            }
        }.start();
    }

    private void performPayment(String methodId) {
        btnPayNow.setEnabled(false);
        btnPayNow.setText("[ 處理金鑰中... ]");

        PaymentRequest request = new PaymentRequest(currentOrderId, methodId);
        RetrofitClient.getApiService(getContext()).processPayment(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    if (countDownTimer != null) countDownTimer.cancel();
                    Toast.makeText(getContext(), "交易成功！正在同步票券...", Toast.LENGTH_SHORT).show();

                    // 🚩 付款成功後，通知 MainActivity 切換到「我的訂單」Fragment
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).switchToOrdersFragment();
                    }
                } else {
                    btnPayNow.setEnabled(true);
                    btnPayNow.setText("[ 重新付款 ]");
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                if (!isAdded()) return;
                btnPayNow.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}