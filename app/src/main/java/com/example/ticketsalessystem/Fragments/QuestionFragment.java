package com.example.ticketsalessystem.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.example.ticketsalessystem.Activity.MainActivity;
import Model.QuestionType;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import com.example.ticketsalessystem.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionFragment extends Fragment {
    private Spinner spinnerType;
    private EditText etTitle, etDesc;
    private TextView tvPdfName;
    private Uri pdfUri;
    private SessionManager sessionManager;
    private List<QuestionType> dynamicTypeList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> selectPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    pdfUri = result.getData().getData();
                    tvPdfName.setText("已載入 PDF");
                    tvPdfName.setTextColor(Color.GREEN);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        if (!sessionManager.isLoggedIn()) {
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).switchFragment(new LoginFragment());
            return;
        }

        spinnerType = view.findViewById(R.id.spinner_question_type);
        etTitle = view.findViewById(R.id.et_question_title);
        etDesc = view.findViewById(R.id.et_question_desc);
        tvPdfName = view.findViewById(R.id.tv_pdf_name);

        view.findViewById(R.id.btn_select_pdf).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("application/pdf");
            selectPdfLauncher.launch(intent);
        });

        view.findViewById(R.id.btn_submit_question).setOnClickListener(v -> submitQuestion());

        // 🚩 啟動時立刻載入分類
        loadQuestionTypes();
    }

    private void loadQuestionTypes() {
        RetrofitClient.getApiService(getContext()).getQuestionTypes().enqueue(new Callback<List<QuestionType>>() {
            @Override
            public void onResponse(Call<List<QuestionType>> call, Response<List<QuestionType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dynamicTypeList = response.body();

                    // 🚩 修正白色方塊：自定義文字顏色
                    ArrayAdapter<QuestionType> adapter = new ArrayAdapter<QuestionType>(requireContext(), android.R.layout.simple_spinner_item, dynamicTypeList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView tv = (TextView) super.getView(position, convertView, parent);
                            tv.setTextColor(Color.WHITE); // 閉合時顯示白色 (適配黑底)
                            return tv;
                        }
                        @Override
                        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                            tv.setTextColor(Color.BLACK); // 展開時顯示黑色 (適配系統白底)
                            tv.setBackgroundColor(Color.WHITE);
                            return tv;
                        }
                    };
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerType.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<QuestionType>> call, Throwable t) {
                Toast.makeText(getContext(), "分類載入失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitQuestion() {
        if (spinnerType.getSelectedItem() == null) return;

        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String typeId = ((QuestionType) spinnerType.getSelectedItem()).id;
        String memberId = sessionManager.getMemberID();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(getContext(), "請輸入內容", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody rbTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody rbDesc = RequestBody.create(MediaType.parse("text/plain"), desc);
        RequestBody rbType = RequestBody.create(MediaType.parse("text/plain"), typeId);
        RequestBody rbMemberId = RequestBody.create(MediaType.parse("text/plain"), memberId);

        MultipartBody.Part filePart = null;
        if (pdfUri != null) {
            try {
                InputStream is = requireContext().getContentResolver().openInputStream(pdfUri);
                byte[] bytes = getBytes(is);
                RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), bytes);
                filePart = MultipartBody.Part.createFormData("upload", "attachment.pdf", requestFile);
            } catch (Exception e) { e.printStackTrace(); }
        }

        RetrofitClient.getApiService(getContext()).QuestionsCreate(rbTitle, rbDesc, rbType, rbMemberId, filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "🎉 發送成功", Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).switchFragment(new MemberFragment());
                } else {
                    Toast.makeText(getContext(), "發送失敗：" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { Toast.makeText(getContext(), "連線失敗", Toast.LENGTH_SHORT).show(); }
        });
    }

    private byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead; byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) buffer.write(data, 0, nRead);
        return buffer.toByteArray();
    }
}