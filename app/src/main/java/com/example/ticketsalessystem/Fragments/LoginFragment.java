package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import com.example.ticketsalessystem.SessionManager;

import Model.LoginRequest;
import Model.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText etaccount, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister, tvForgotPassword;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_pixel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(getContext());
        initViews(view);

        btnLogin.setOnClickListener(v -> performLogin());

        // 註冊與忘記密碼的跳轉
//        tvGoRegister.setOnClickListener(v -> {
//            if (getActivity() instanceof MainActivity) {
//                ((MainActivity) getActivity()).switchFragment(new RegisterFragment());
//            }
//        });
    }

    private void initViews(View v) {
        etaccount = v.findViewById(R.id.et_login_account);
        etPassword = v.findViewById(R.id.et_login_password);
        btnLogin = v.findViewById(R.id.btn_execute_login);
        tvGoRegister = v.findViewById(R.id.tv_go_register);
        tvForgotPassword = v.findViewById(R.id.tv_forgot_password);
    }

    private void performLogin() {
        String account = etaccount.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "請填入完整憑證", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("[ 驗證金鑰中... ]");

        LoginRequest request = new LoginRequest(account, password);


        RetrofitClient.getApiService(getContext()).login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse result = response.body();
                    if (result.success) {
                        // 🚩 核心動作：將 MemberID 存入手機
                        sessionManager.saveLoginSession(
                                result.getMemberID(),
                                result.getName() // 這是我們之前建議你在 API 補上的欄位
                        );

                        Toast.makeText(getContext(), "存取許可：歡迎回來", Toast.LENGTH_SHORT).show();

                        // 登入成功後跳轉回首頁
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).switchFragment(new HomeFragment());
                        }
                    } else {
                        resetButton();
                        Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    resetButton();
                    Toast.makeText(getContext(), "系統拒絕存取 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (!isAdded()) return;
                resetButton();
                Toast.makeText(getContext(), "連線中斷，請重試", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetButton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("[ 登入 ]");
    }
}