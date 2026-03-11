package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketsalessystem.Activity.MainActivity;
import com.example.ticketsalessystem.Adapters.QuestionListAdapter;
import com.example.ticketsalessystem.Fragments.QuestionDetailFragment;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;

import java.util.List;

import Model.QuestionDetail; // 🚩 確保 import 正確
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuestionsFragment extends Fragment {

    private RecyclerView rvOrders;

    private ProgressBar pbLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_orders, container, false);

        rvOrders = v.findViewById(R.id.rv_orders);
        pbLoading = v.findViewById(R.id.pb_loading);
        TextView tvMarquee = v.findViewById(R.id.tv_order_marquee);

        TextView tvTitle = v.findViewById(R.id.tv_order_title);
        if (tvTitle != null) {
            tvTitle.setText("[ 問題進度追蹤 ]"); // 你想換成的標題文字
        }

        if (tvMarquee != null) {
            tvMarquee.setText(">>> 點擊項目查看詳細回覆內容 <<<");
            tvMarquee.setSelected(true);
        }

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchMyQuestions();

        return v;
    }

    private void fetchMyQuestions() {
        if (pbLoading != null) pbLoading.setVisibility(View.VISIBLE);

        // 🚩 Callback 型別必須與介面定義完全一致
        RetrofitClient.getApiService(getContext()).GetMyQuestions().enqueue(new Callback<List<QuestionDetail>>() {
            @Override
            public void onResponse(Call<List<QuestionDetail>> call, Response<List<QuestionDetail>> response) {
                if (isAdded() && pbLoading != null) pbLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    QuestionListAdapter adapter = new QuestionListAdapter(
                            response.body(),
                            getContext(),
                            new QuestionListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(QuestionDetail question) {
                                    openQuestionDetail(question.questionID);
                                }
                            }
                    );
                    rvOrders.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<QuestionDetail>> call, Throwable t) {
                // 🚩 這裡原本紅字，因為參數型別要跟上面 Callback 的泛型一致
                if (isAdded() && pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                }
                Log.e("API_FAILURE", "連線失敗: " + t.getMessage());
                Toast.makeText(getContext(), "伺服器連線失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openQuestionDetail(String questionId) {
        QuestionDetailFragment detailFragment = QuestionDetailFragment.newInstance(questionId);
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.switchFragment(detailFragment);
        }
    }
}