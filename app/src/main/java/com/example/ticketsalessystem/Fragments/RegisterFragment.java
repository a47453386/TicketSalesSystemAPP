package com.example.ticketsalessystem.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import java.util.Calendar;
import java.util.Locale;
import API.ApiService;
import Model.MemberCreate;
import Model.RegisterResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private EditText etAccount, etPassword, etConfirmPassword, etName, etEmail, etPhone, etAddress, etBirthday, etNationalID;
    private RadioGroup rgGender;
    private Button btnRegister;
    private TextView tvBackToLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(view);

        etBirthday.setOnClickListener(v -> showDatePicker());
        btnRegister.setOnClickListener(v -> performRegister());
        tvBackToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchFragment(new LoginFragment());
            }
        });

        return view;
    }

    private void initViews(View v) {
        etAccount = v.findViewById(R.id.et_reg_account);
        etPassword = v.findViewById(R.id.et_reg_password);
        etConfirmPassword = v.findViewById(R.id.et_reg_confirm_password);
        etName = v.findViewById(R.id.et_reg_name);
        etEmail = v.findViewById(R.id.et_reg_email);
        etPhone = v.findViewById(R.id.et_reg_phone);
        etAddress = v.findViewById(R.id.et_reg_address);
        etBirthday = v.findViewById(R.id.et_reg_birthday);
        etNationalID = v.findViewById(R.id.et_reg_national_id);
        rgGender = v.findViewById(R.id.rg_reg_gender);
        btnRegister = v.findViewById(R.id.btn_execute_register);
        tvBackToLogin = v.findViewById(R.id.tv_back_to_login);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            etBirthday.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void performRegister() {
        String pwd = etPassword.getText().toString().trim();
        String account = etAccount.getText().toString().trim();

        // 基本空值檢查
        if (account.isEmpty() || pwd.isEmpty()) {
            Toast.makeText(getContext(), "系統: 帳號密碼不可為空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pwd.equals(etConfirmPassword.getText().toString().trim())) {
            Toast.makeText(getContext(), "系統: 密碼不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isMale = rgGender.getCheckedRadioButtonId() == R.id.rb_reg_male;

        // 🚩 重點修正：對齊 MemberCreate 的 11 個參數建構子 (小寫版)
        MemberCreate request = new MemberCreate(
                etName.getText().toString().trim(),
                etAddress.getText().toString().trim(),
                etBirthday.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                isMale,
                etNationalID.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                account,
                pwd,
                false, // isPhoneVerified: 註冊預設為 false
                "A"    // accountStatusID: 註冊預設為 "A"
        );

        ApiService apiService = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                // 🚩 處理 200 OK 的情況
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse res = response.body();
                    Log.d("API_DEBUG", "Success: " + res.isSuccess() + " | Msg: " + res.getMessage());
                    if (res.isSuccess()) {
                        Toast.makeText(getContext(), "註冊成功！", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).switchFragment(new LoginFragment());
                        }
                    } else {
                        // 處理後端業務邏輯錯誤 (如手機重複)
                        Toast.makeText(getContext(), "註冊失敗: " + res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                // 🚩 處理 400 Bad Request 或其他錯誤
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            Log.e("API_400_DETAIL", "錯誤詳情: " + errorJson);
                            Toast.makeText(getContext(), "註冊失敗: 資料格式不符", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("API_ERROR", "解析錯誤訊息失敗", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                Log.e("API_NETWORK_FAILURE", "連線失敗: ", t);
                Toast.makeText(getContext(), "連線失敗: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}