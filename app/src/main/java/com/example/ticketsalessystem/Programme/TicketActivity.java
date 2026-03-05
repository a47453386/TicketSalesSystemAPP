package com.example.ticketsalessystem.Programme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import Model.Area;
import Model.ProgrammeDetail;
import Model.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketActivity extends AppCompatActivity {

    private ImageView imgCover;
    private TextView tvName, tvPlace, tvDesc, tvAreaInfo;
    private Button btnBook;
    private ProgressBar progressBar;
    private Spinner spinnerSessions, spinnerAreas;

    // 資料傳遞與管理
    private String programmeId;
    private List<Session> sessionList;
    private String selectedSessionId;
    private String selectedAreaId;
    private double selectedPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        initViews();

        programmeId = getIntent().getStringExtra("PROGRAMME_ID");
        if (programmeId != null) {
            fetchData(programmeId);
        } else {
            Toast.makeText(this, "無效的節目 ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        imgCover = findViewById(R.id.img_detail_cover);
        tvName = findViewById(R.id.tv_detail_name);
        tvPlace = findViewById(R.id.tv_detail_place);
        tvDesc = findViewById(R.id.tv_detail_description);
        tvAreaInfo = findViewById(R.id.tv_area_info);
        spinnerSessions = findViewById(R.id.spinner_sessions);
        spinnerAreas = findViewById(R.id.spinner_areas);
        btnBook = findViewById(R.id.btn_buy);
        progressBar = findViewById(R.id.progressBar);
    }

    private void fetchData(String id) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().getProgrammeDetail(id).enqueue(new Callback<ProgrammeDetail>() {
            @Override
            public void onResponse(Call<ProgrammeDetail> call, Response<ProgrammeDetail> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(TicketActivity.this, "找不到該節目資訊", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProgrammeDetail> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "詳情抓取失敗: " + t.getMessage());
                Toast.makeText(TicketActivity.this, "網路連線錯誤", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(ProgrammeDetail data) {
        this.sessionList = data.sessions;

        Glide.with(this)
                .load(data.coverImage)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imgCover);

        tvName.setText(data.programmeName);
        tvPlace.setText(data.placeName);
        tvDesc.setText(data.programmeDescription);

        // 1. 處理「場次選單」
        if (sessionList != null && !sessionList.isEmpty()) {
            List<String> sessionNames = new ArrayList<>();
            for (Session s : sessionList) {
                sessionNames.add(s.startTime);
            }

            ArrayAdapter<String> sessionAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, sessionNames);
            sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSessions.setAdapter(sessionAdapter);

            // 🚩 監聽場次切換 -> 更新區域選單
            spinnerSessions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Session selectedSession = sessionList.get(position);
                    selectedSessionId = selectedSession.sessionID;
                    if (selectedSession.ticketsAreas != null) {
                        Log.d("DEBUG_DATA", "場次選中: " + selectedSessionId + "，找到票區數量: " + selectedSession.ticketsAreas.size());
                        updateAreaSpinner(selectedSession.ticketsAreas);
                    } else {
                        // 🚩 修正：這裡應該印出 selectedSession.ticketsAreas，因為 areasList 在這不可見
                        Log.e("API_ERROR", "場次 " + selectedSessionId + " 的 ticketsAreas 為 null (解析失敗)");
                        updateAreaSpinner(new ArrayList<Area>());
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        btnBook.setOnClickListener(v -> jumpToConfirm());
    }

    // 🚩 將參數名稱改為 areasList 並加上 final
    private void updateAreaSpinner(final List<Area> areasList) {
        // 安全檢查：若列表為空則顯示提示
        if (areasList == null || areasList.isEmpty()) {
            tvAreaInfo.setText("此場次目前無可用區域");
            spinnerAreas.setAdapter(null);
            selectedAreaId = null;
            return;
        }

        // 1. 建立選單顯示文字
        List<String> areaNames = new ArrayList<>();
        for (Area a : areasList) {
            areaNames.add(a.ticketsAreaName + " - $" + a.price);
        }

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, areaNames);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAreas.setAdapter(areaAdapter);

        // 2. 監聽區域選擇
        spinnerAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 🚩 這裡使用剛才傳入的 areasList
                Area selected = areasList.get(position);
                selectedAreaId = selected.ticketsAreaID;
                selectedPrice = (double) selected.price;

                tvAreaInfo.setText("目前票價：$" + selected.price + "\n剩餘位置：" + selected.remaining);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAreaId = null;
            }
        });
    }

    private void jumpToConfirm() {
        if (selectedSessionId == null || selectedAreaId == null) {
            Toast.makeText(this, "請先選擇場次與區域", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, OrderConfirmActivity.class);
        intent.putExtra("PROGRAMME_ID", programmeId);
        intent.putExtra("SESSION_ID", selectedSessionId);
        intent.putExtra("AREA_ID", selectedAreaId);
        intent.putExtra("TOTAL_AMOUNT", selectedPrice);
        intent.putExtra("TICKET_COUNT", 1);
        startActivity(intent);
    }
}