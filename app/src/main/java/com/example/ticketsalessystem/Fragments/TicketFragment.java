package com.example.ticketsalessystem.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ticketsalessystem.Activity.MainActivity;
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

public class TicketFragment extends Fragment {

    private ImageView imgCover;
    private TextView tvName, tvPlace, tvDesc, tvAreaInfo;
    private Button btnBook;
    private ProgressBar progressBar;
    private Spinner spinnerSessions, spinnerAreas;

    // 資料管理
    private String programmeId;
    private List<Session> sessionList;
    private String selectedSessionId, selectedAreaId, programmeName, selectedSessionTime, selectedAreaName;
    private double selectedPrice;

    /**
     * 🚩 靜態方法：建立 Fragment 時傳入節目 ID
     */
    public static TicketFragment newInstance(String id) {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        args.putString("PROGRAMME_ID", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 🚩 使用原本 activity_ticket 的 XML (因為它會被塞入 MainActivity 的插槽)
        return inflater.inflate(R.layout.activity_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 讀取傳入的 ID
        if (getArguments() != null) {
            programmeId = getArguments().getString("PROGRAMME_ID");
        }

        // 2. 初始化元件
        initViews(view);

        // 3. 抓取資料
        if (programmeId != null) {
            fetchData(programmeId);
        }
    }

    private void initViews(View v) {
        imgCover = v.findViewById(R.id.img_detail_cover);
        tvName = v.findViewById(R.id.tv_detail_name);
        tvPlace = v.findViewById(R.id.tv_detail_place);
        tvDesc = v.findViewById(R.id.tv_detail_description);
        tvAreaInfo = v.findViewById(R.id.tv_area_info);
        spinnerSessions = v.findViewById(R.id.spinner_sessions);
        spinnerAreas = v.findViewById(R.id.spinner_areas);
        btnBook = v.findViewById(R.id.btn_buy);
        progressBar = v.findViewById(R.id.progressBar);

        // 統一標題字體
        tvName.setTypeface(Typeface.MONOSPACE);
    }

    private void fetchData(String id) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService(getContext()).getProgrammeDetail(id).enqueue(new Callback<ProgrammeDetail>() {
            @Override
            public void onResponse(Call<ProgrammeDetail> call, Response<ProgrammeDetail> response) {
                if (!isAdded()) return; // 🚩 防止切換太快導致閃退
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(getContext(), "找不到該節目資訊", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProgrammeDetail> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(ProgrammeDetail data) {
        this.sessionList = data.sessions;
        this.programmeName = data.programmeName;

        Glide.with(this).load(data.coverImage).into(imgCover);
        tvName.setText("[ " + data.programmeName + " ]");
        tvPlace.setText(">>> " + data.placeName);
        tvDesc.setText(data.programmeDescription);

        // 處理場次選單 (帶入像素格式化)
        if (sessionList != null && !sessionList.isEmpty()) {
            List<String> sessionNames = new ArrayList<>();
            for (Session s : sessionList) {
                // 移除 ISO 時間中的 T，讓格式更好看
                String time = s.startTime != null ? s.startTime.replace("T", " ").substring(0, 16) : "";
                sessionNames.add(time);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sessionNames) {
                @NonNull
                @Override
                public View getView(int pos, @Nullable View conv, @NonNull ViewGroup parent) {
                    TextView tv = (TextView) super.getView(pos, conv, parent);
                    tv.setTextColor(Color.parseColor("#FFCC00"));
                    tv.setTypeface(Typeface.MONOSPACE);
                    return tv;
                }

                @Override
                public View getDropDownView(int pos, @Nullable View conv, @NonNull ViewGroup parent) {
                    TextView tv = (TextView) super.getDropDownView(pos, conv, parent);
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.parseColor("#1A1A1A"));
                    tv.setTypeface(Typeface.MONOSPACE);
                    return tv;
                }
            };

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSessions.setAdapter(adapter);

            spinnerSessions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                    Session s = sessionList.get(pos);
                    selectedSessionId = s.sessionID;
                    selectedSessionTime = s.startTime.replace("T", " ").substring(0, 16);
                    if (s.ticketsAreas != null) updateAreaSpinner(s.ticketsAreas);
                }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            });
        }

        btnBook.setOnClickListener(v -> jumpToConfirm());
    }

    private void updateAreaSpinner(final List<Area> areasList) {
        if (areasList == null || areasList.isEmpty()) {
            tvAreaInfo.setText(">>> 目前無可用區域 <<<");
            spinnerAreas.setAdapter(null);
            return;
        }

        List<String> areaNames = new ArrayList<>();
        for (Area a : areasList) {
            areaNames.add(a.ticketsAreaName + " - $" + (int)a.price);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, areaNames) {
            @NonNull
            @Override
            public View getView(int p, @Nullable View c, @NonNull ViewGroup pr) {
                TextView tv = (TextView) super.getView(p, c, pr);
                tv.setTextColor(Color.parseColor("#FFCC00"));
                tv.setTypeface(Typeface.MONOSPACE);
                return tv;
            }

            @Override
            public View getDropDownView(int p, @Nullable View c, @NonNull ViewGroup pr) {
                TextView tv = (TextView) super.getDropDownView(p, c, pr);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#1A1A1A"));
                return tv;
            }
        };

        spinnerAreas.setAdapter(adapter);
        spinnerAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                Area a = areasList.get(pos);
                selectedAreaId = a.ticketsAreaID;
                selectedAreaName = a.ticketsAreaName;
                selectedPrice = a.price;
                tvAreaInfo.setText("目前票價：$" + (int)a.price + "\n剩餘位置：" + a.remaining);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    /**
     * 🚩 核心跳轉：現在是從一個 Fragment 切換到另一個 Fragment (OrderConfirmFragment)
     */
    private void jumpToConfirm() {
        if (selectedSessionId == null || selectedAreaId == null) {
            Toast.makeText(getContext(), "請先選擇場次與區域", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle args = new Bundle();
        args.putString("PROGRAMME_ID", programmeId);
        args.putString("SESSION_ID", selectedSessionId);
        args.putString("AREA_ID", selectedAreaId);
        args.putString("PROGRAMME_NAME", programmeName);
        args.putString("SESSION_TIME", selectedSessionTime);
        args.putString("AREA_NAME", selectedAreaName);
        args.putDouble("TOTAL_AMOUNT", selectedPrice);
        args.putInt("TICKET_COUNT", 1);

        // 🚩 透過 MainActivity 的方法進行切換，確保 Navbar 不會消失
        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            // 直接呼叫 switchFragment，這會保留 Navbar，只抽換內容區
            main.switchFragment(OrderConfirmFragment.newInstance(args));
        }
    }
}