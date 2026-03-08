package com.example.ticketsalessystem.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.ticketsalessystem.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutIndicator;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);

        // 載入子組件
        loadChildComponents();

        return view;
    }

    private void loadChildComponents() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.news_component_container, new NewsFragment()) // 🚩 載入這個組件
                .replace(R.id.programme_component_container, new FeatureProgrammesFragment())
                .commit();
    }

    // 提供給子組件呼叫，用來同步連動指示器
    public void setupBannerIndicator() {
        if (tabLayoutIndicator != null && viewPagerBanner != null) {
            new TabLayoutMediator(tabLayoutIndicator, viewPagerBanner, (tab, position) -> {}).attach();
            startAutoSlider();
        }
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerBanner != null && viewPagerBanner.getAdapter() != null) {
                int nextItem = (viewPagerBanner.getCurrentItem() + 1) % viewPagerBanner.getAdapter().getItemCount();
                viewPagerBanner.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000);
            }
        }
    };

    private void startAutoSlider() {
        sliderHandler.removeCallbacks(sliderRunnable);
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}