package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import Model.MemberUserEdit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberEditFragment extends Fragment {

    // 🚩 UI 元件宣告
    private EditText etTel, etEmail, etAddress;
    private TextView tvName, tvBirthday, tvNationalId, tvGender;
    private ProgressBar pbLoading;
    private Button btnSave;

    // 🚩 測試用 MemberID
    private String currentMemberId = "a8e36451-c3fb-44ba-a05e-602ca0760166";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 載入 ScrollView 佈局
        View v = inflater.inflate(R.layout.fragment_member_edit, container, false);

        initViews(v);
        fetchProfile(); // 🚩 啟動時即向資料庫抓取資料

        btnSave.setOnClickListener(view -> saveProfile());
        return v;
    }

    private void initViews(View v) {
        // 🚩 綁定編輯元件
        etTel = v.findViewById(R.id.et_edit_tel);
        etEmail = v.findViewById(R.id.et_edit_email);
        etAddress = v.findViewById(R.id.et_edit_address);

        // 🚩 綁定唯讀顯示元件 (補齊身分證與性別)
        tvName = v.findViewById(R.id.tv_display_name);
        tvBirthday = v.findViewById(R.id.tv_display_birthday);
        tvNationalId = v.findViewById(R.id.tv_display_id);
        tvGender = v.findViewById(R.id.tv_display_gender);

        btnSave = v.findViewById(R.id.btn_save_profile);
        pbLoading = v.findViewById(R.id.pb_loading);
    }

    private void fetchProfile() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);

        // 呼叫 ApiService 抓取資料
        RetrofitClient.getApiService().GetProfile(currentMemberId).enqueue(new Callback<MemberUserEdit>() {
            @Override
            public void onResponse(Call<MemberUserEdit> call, Response<MemberUserEdit> response) {
                if (isAdded() && pbLoading != null) pbLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    MemberUserEdit vm = response.body();

                    // 1. 填入唯讀區塊
                    tvName.setText("姓名：" + vm.name);
                    tvNationalId.setText("身分證字號：" + vm.nationalID);

                    // 處理性別轉換 (boolean -> String)
                    String genderText = vm.gender ? "男" : "女";
                    tvGender.setText("性別：" + genderText);

                    // 2. 處理生日格式 (去除 T00:00:00)
                    String bday = vm.birthday;
                    if (bday != null && bday.contains("T")) bday = bday.split("T")[0];
                    tvBirthday.setText("生日：" + bday);

                    // 3. 填入可編輯區塊
                    etTel.setText(vm.tel);
                    etEmail.setText(vm.email);
                    etAddress.setText(vm.address);
                }
            }
            @Override
            public void onFailure(Call<MemberUserEdit> call, Throwable t) {
                if (isAdded() && pbLoading != null) pbLoading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "載入失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String newTel = etTel.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newAddress = etAddress.getText().toString().trim();

        // 🚩 前端驗證：對接 C# 手機 10 碼與格式邏輯
        if (newTel.length() != 10 || !newTel.startsWith("09")) {
            etTel.setError("手機格式錯誤，請輸入 09 開頭的 10 位數字");
            return;
        }

        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        // 封裝更新資料
        MemberUserEdit updateVm = new MemberUserEdit();
        updateVm.memberID = currentMemberId;
        updateVm.tel = newTel;
        updateVm.email = newEmail;
        updateVm.address = newAddress;

        // 送出 POST 請求
        RetrofitClient.getApiService().UpdateProfile(updateVm).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isAdded() && pbLoading != null) pbLoading.setVisibility(View.GONE);
                btnSave.setEnabled(true);

                if (response.isSuccessful()) {
                    // 同步 MVC 成功訊息
                    Toast.makeText(getContext(), "🎉 [系統指令]：個人資料已更新", Toast.LENGTH_SHORT).show();

                    // 回到會員中心
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).switchFragment(new MemberFragment());
                    }
                } else {
                    // 處理重複手機號碼報錯
                    Toast.makeText(getContext(), "❌ 更新失敗：手機號碼可能已被使用", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isAdded() && pbLoading != null) pbLoading.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(getContext(), "網路連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}