package com.example.ticketsalessystem.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import Model.Member;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberFragment extends Fragment {

    private TextView tvName, tvNationalId, tvEmail, tvAddress, tvPhoneStatus, tvBirthday, tvGender;
    private Button btnOrders, btnQuestions, btnUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);

        // 1. 初始化元件 (對應你的 Layout ID)
        initViews(view);

        // 2. 啟動底部的跑馬燈
        TextView tvMarquee = view.findViewById(R.id.tv_member_marquee);
        tvMarquee.setSelected(true);

        // 3. 執行 API 抓取
        // 🚩 這裡的 ID 目前寫死，之後建議從 SharedPreferences 取得登入者的 ID
        fetchMemberData("a8e36451-c3fb-44ba-a05e-602ca0760166");

        // 4. 設定按鈕點擊監聽 (先預留功能)
        setupListeners(view);

        return view;
    }

    private void initViews(View v) {
        tvName = v.findViewById(R.id.tv_member_name);
        tvNationalId = v.findViewById(R.id.tv_national_id);
        tvEmail = v.findViewById(R.id.tv_email);
        tvAddress = v.findViewById(R.id.tv_address);
        tvPhoneStatus = v.findViewById(R.id.tv_phone_status);

        // 如果你的 Layout 有這兩項也順便綁定
        tvBirthday = v.findViewById(R.id.tv_birthday);
        tvGender = v.findViewById(R.id.tv_gender);

        btnOrders = v.findViewById(R.id.btn_nav_user_orders);
        btnQuestions = v.findViewById(R.id.btn_nav_my_questions);
        btnUpdate = v.findViewById(R.id.btn_nav_update_profile);
    }

    private void fetchMemberData(String id) {
        RetrofitClient.getApiService().GetMemberDetails(id).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                }
            }
            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Log.e("API_MEMBER", "載入失敗: " + t.getMessage());
            }
        });
    }

    private void bindData(Member member) {
        tvName.setText(member.name);
        tvNationalId.setText(member.nationalID);
        tvEmail.setText(member.email);
        tvAddress.setText(member.address);

        if (tvBirthday != null) tvBirthday.setText(member.birthday);
        if (tvGender != null) tvGender.setText(member.gender ? "MALE / 男" : "FEMALE / 女");

        // 🚩 處理驗證狀態與帳號狀態 (StatusName)
        String statusInfo = "● 狀態: " + member.statusName;
        if (member.isPhoneVerified) {
            tvPhoneStatus.setText(statusInfo + " (已認證)");
            tvPhoneStatus.setTextColor(Color.parseColor("#00FF66")); // 霓虹綠
        } else {
            tvPhoneStatus.setText(statusInfo + " (未驗證)");
            tvPhoneStatus.setTextColor(Color.RED);
        }
    }

    // 建議傳入 view 參數，確保 findViewById 找得到元件
    private void setupListeners(View view) {
        // 1. 訂單清單導向
        view.findViewById(R.id.btn_nav_user_orders).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchFragment(new OrdersFragment());
            }
        });

        // 2. 我的問題清單導向
        View btnMyQuestions = view.findViewById(R.id.btn_nav_my_questions);
        if (btnMyQuestions != null) {
            btnMyQuestions.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchFragment(new MyQuestionsFragment());
                }
            });
        }

        // 3. 其他按鈕 (如資料編輯)
        View btnUpdate = view.findViewById(R.id.btn_nav_update_profile);
        if (btnUpdate != null) {
            btnUpdate.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    // 🚩 修正：將 Toast 換成正式的 Fragment 切換
                    ((MainActivity) getActivity()).switchFragment(new MemberEditFragment());
                }
            });
        }
    }
}