package com.example.ticketsalessystem.Activity;

import android.content.Intent; // 🚩 補上漏掉的匯入
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

// 匯入所有的 Fragment
import com.example.ticketsalessystem.Fragments.HomeFragment;
import com.example.ticketsalessystem.Fragments.FAQFragment;
import com.example.ticketsalessystem.Fragments.LoginFragment;
import com.example.ticketsalessystem.Fragments.MemberFragment;
import com.example.ticketsalessystem.Fragments.NewsListFragment;
import com.example.ticketsalessystem.Fragments.OrderConfirmFragment;
import com.example.ticketsalessystem.Fragments.OrdersFragment;
import com.example.ticketsalessystem.Fragments.QuestionFragment;
import com.example.ticketsalessystem.Fragments.MyQuestionsFragment; // 假設這是你的訂單/諮詢列表頁面
import com.example.ticketsalessystem.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 🚩 載入你那個有 Navbar + ContentFrame + StatusBar 的佈局
        setContentView(R.layout.activity_main);

        initViews();
        setupNavigation();
        startStatusBlink();

        // 1. 檢查是否有從 TicketActivity 傳過來的跳轉指令
        if (!checkIntentExtras()) {
            // 2. 如果沒有指令，且是第一次啟動，則載入首頁
            if (savedInstanceState == null) {
                switchFragment(new HomeFragment());
            }
        }
    }

    private void initViews() {
        // 這裡可以初始化你的 Navbar 背景色或其他全域 UI
    }

    /**
     * 🚩 處理「從外面跳回來」的邏輯 (例如：從購票頁跳回確認頁)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // 🚩 必須更新 Intent，否則 getIntent() 拿到的還是舊的
        checkIntentExtras();
    }

    private boolean checkIntentExtras() {
        if (getIntent().hasExtra("OPEN_CONFIRM")) {
            Bundle args = getIntent().getBundleExtra("OPEN_CONFIRM");
            // 🚩 使用剛才設計的 OrderConfirmFragment
            OrderConfirmFragment fragment = OrderConfirmFragment.newInstance(args);
            switchFragment(fragment);
            return true;
        }
        return false;
    }

    /**
     * 綁定頂部導航按鈕 (Navbar)
     */
    private void setupNavigation() {
        // 節目資訊 (首頁)
        findViewById(R.id.btn_nav_programmes).setOnClickListener(v -> switchFragment(new HomeFragment()));

        // 最新消息
        findViewById(R.id.btn_nav_news).setOnClickListener(v -> switchFragment(new NewsListFragment()));

        // 常見問題 (FAQ)
        findViewById(R.id.btn_nav_faq).setOnClickListener(v -> switchFragment(new FAQFragment()));

        // 會員中心
        findViewById(R.id.btn_nav_member).setOnClickListener(v -> switchFragment(new MemberFragment()));

        // 我要發問 (諮詢)
        findViewById(R.id.btn_nav_question).setOnClickListener(v -> switchFragment(new QuestionFragment()));

        // 登入系統
        findViewById(R.id.btn_nav_login).setOnClickListener(v -> {
            // 1. 顯示像素風系統提示
            Toast.makeText(this, "系統:會員登入...", Toast.LENGTH_SHORT).show();

            // 2. 🚩 正式切換到 LoginFragment
            switchFragment(new LoginFragment());
        });
    }

    /**
     * 🚩 核心：切換 Fragment 方法 (對應 MVC 的 RenderBody)
     * 將內容塞進 XML 中的 content_container 插槽
     */
    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                // 🚩 確保 ID 對應 XML 裡的 FrameLayout
                .replace(R.id.content_container, fragment)
                .addToBackStack(null) // 允許使用者按返回鍵回到上一個 Fragment
                .commit();
    }

    /**
     * 🚩 功能：跳轉到「我的諮詢/訂單」列表
     * 用於付款成功後的自動跳轉
     */
    public void switchToOrdersFragment() {
        // 這裡換成你實際的訂單列表 Fragment 類別
        switchFragment(new OrdersFragment());

        // 可以在這裡視覺化提醒使用者目前在「諮詢」分頁
        Toast.makeText(this, "支付成功！正在讀取訂單...", Toast.LENGTH_LONG).show();
    }

    /**
     * 底部狀態小圓點閃爍效果
     */
    private void startStatusBlink() {
        TextView statusDot = findViewById(R.id.tv_status_dot);
        if (statusDot != null) {
            Animation blink = new AlphaAnimation(0.2f, 1.0f);
            blink.setDuration(800);
            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            statusDot.startAnimation(blink);
        }
    }
}