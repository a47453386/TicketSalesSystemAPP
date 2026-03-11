package com.example.ticketsalessystem.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionFragment extends Fragment {
    private Spinner spinnerType;
    private EditText etTitle, etDesc;
    private TextView tvPdfName;
    private Uri pdfUri;

    // 問題分類清單 (需對應資料庫 FAQType 表的 ID)
    private String[] typeNames = {"一般詢問", "退票申請", "活動相關", "系統問題"};
    private String[] typeIds = {"T01", "T02", "T03", "T04"};

    private final ActivityResultLauncher<Intent> selectPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    pdfUri = result.getData().getData();
                    // 顯示選取的檔案資訊
                    tvPdfName.setText("已載入檔案：attachment.pdf");
                    tvPdfName.setTextColor(Color.parseColor("#00FF66")); // 像素霓虹綠
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question_create, container, false);

        // 初始化 UI
        spinnerType = v.findViewById(R.id.spinner_question_type);
        etTitle = v.findViewById(R.id.et_question_title);
        etDesc = v.findViewById(R.id.et_question_desc);
        tvPdfName = v.findViewById(R.id.tv_pdf_name);

        // 設定 Spinner 樣式與資料
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, typeNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.WHITE); // 設定下拉選單未展開時的文字顏色
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // 選擇 PDF 檔案
        v.findViewById(R.id.btn_select_pdf).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            selectPdfLauncher.launch(intent);
        });

        // 發送訊號
        v.findViewById(R.id.btn_submit_question).setOnClickListener(view -> submitQuestion());

        return v;
    }

    private void submitQuestion() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String typeId = typeIds[spinnerType.getSelectedItemPosition()];

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(getContext(), "請輸入主旨與詳細內容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 轉換為 Multipart RequestBody
        RequestBody rbTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody rbDesc = RequestBody.create(MediaType.parse("text/plain"), desc);
        RequestBody rbType = RequestBody.create(MediaType.parse("text/plain"), typeId);

        MultipartBody.Part filePart = null;
        if (pdfUri != null) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(pdfUri);
                byte[] bytes = getBytes(inputStream);
                RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), bytes);
                // "upload" 必須對應後端 C# 參數 [FromForm] IFormFile? upload
                filePart = MultipartBody.Part.createFormData("upload", "attachment.pdf", requestFile);
            } catch (Exception e) {
                Log.e("API", "PDF 處理失敗: " + e.getMessage());
            }
        }

        // 執行 API 傳輸
        RetrofitClient.getApiService(getContext()).QuestionsCreate(rbTitle, rbDesc, rbType, filePart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "🎉 [發送訊號成功]", Toast.LENGTH_SHORT).show();

                            // 🚩 修正：呼叫 MainActivity 已經寫好的 switchFragment 方法
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).switchFragment(new MemberFragment());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("API", "連線失敗: " + t.getMessage());
                        Toast.makeText(getContext(), "伺服器無回應", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 將檔案流轉為位元組陣列
    private byte[] getBytes(InputStream is) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}