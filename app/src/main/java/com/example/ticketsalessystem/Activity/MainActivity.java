package com.example.ticketsalessystem.Activity;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

// 匯入你各個功能頁面的 Fragment
import com.example.ticketsalessystem.Fragments.HomeFragment;
import com.example.ticketsalessystem.Fragments.FAQFragment;
import com.example.ticketsalessystem.Fragments.MemberFragment;
import com.example.ticketsalessystem.Fragments.NewsFragment;
import com.example.ticketsalessystem.Fragments.NewsListFragment;
import com.example.ticketsalessystem.Fragments.QuestionFragment;
//import com.example.ticketsalessystem.Fragments.MyOrdersFragment;
import com.example.ticketsalessystem.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 🚩 載入你剛修正好的那個「平均分配」佈局
        setContentView(R.layout.activity_main);

        // 1. 讓底部的狀態小圓點閃爍 (還原 Web 版 server-monitor.js 效果)
        startStatusBlink();

        // 2. 預設載入「節目資訊」(對應 Home/Index)
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment());
        }

        // 3. 綁定所有導航點擊事件
        setupNavigation();
    }

    /**
     * 綁定頂部導航按鈕的點擊監聽
     */
    private void setupNavigation() {
        // 節目資訊
        findViewById(R.id.btn_nav_programmes).setOnClickListener(v -> switchFragment(new HomeFragment()));

        // 最新消息
        findViewById(R.id.btn_nav_news).setOnClickListener(v -> switchFragment(new NewsListFragment()));

        // 常見問題
        findViewById(R.id.btn_nav_faq).setOnClickListener(v -> switchFragment(new FAQFragment()));

        // 訂單查詢 (對應你之前寫的訂單列表)直接納入會員中心
//        findViewById(R.id.btn_nav_orders).setOnClickListener(v -> switchFragment(new MyOrdersFragment()));

        // 會員中心
        findViewById(R.id.btn_nav_member).setOnClickListener(v -> switchFragment(new MemberFragment()));

        // 我要發問
        findViewById(R.id.btn_nav_question).setOnClickListener(v -> switchFragment(new QuestionFragment()));

        // 會員登入 (黃色圖示按鈕)
        findViewById(R.id.btn_nav_login).setOnClickListener(v -> {
            // 🚩 這裡可以實作彈出 Dialog 或是跳轉登入 Activity
            Toast.makeText(this, "INITIALIZING AUTH SYSTEM...", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 🚩 核心：切換 Fragment 方法 (對應 MVC 的 RenderBody 功能)
     * @param fragment 要換上去的新頁面
     */
    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                // 加入淡入淡出動畫，讓切換更流暢
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                // 🚩 替換到 activity_main.xml 中那個帶有像素框的容器
                .replace(R.id.content_container, fragment)
                .commit();
    }

    /**
     * 實作像素風狀態小燈的呼吸閃爍效果
     */
    private void startStatusBlink() {
        TextView statusDot = findViewById(R.id.tv_status_dot);
        if (statusDot != null) {
            Animation blink = new AlphaAnimation(0.0f, 1.0f);
            blink.setDuration(1000); // 1秒閃爍一次
            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            statusDot.startAnimation(blink);
        }
    }
}