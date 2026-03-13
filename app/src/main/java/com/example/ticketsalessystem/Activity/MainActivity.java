package com.example.ticketsalessystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

// 🚩 匯入所有的 Fragment (請確認你的路徑是否正確)
import com.example.ticketsalessystem.Fragments.HomeFragment;
import com.example.ticketsalessystem.Fragments.FAQFragment;
import com.example.ticketsalessystem.Fragments.LoginFragment;
import com.example.ticketsalessystem.Fragments.MemberFragment;
import com.example.ticketsalessystem.Fragments.MyOrdersFragment;
import com.example.ticketsalessystem.Fragments.MyQuestionsFragment;
import com.example.ticketsalessystem.Fragments.NewsListFragment;
import com.example.ticketsalessystem.Fragments.OrderConfirmFragment;
import com.example.ticketsalessystem.Fragments.QuestionFragment;
import com.example.ticketsalessystem.R;
import com.example.ticketsalessystem.RetrofitClient;
import com.example.ticketsalessystem.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 載入佈局
        setContentView(R.layout.activity_main);

        setupNavigation();   // 初始化固定按鈕
        updateNavUI();       // 🚩 初始化動態按鈕 (登入/會員)
        startStatusBlink();  // 啟動底部閃爍特效

        // 檢查是否有從其他 Activity (如 TicketActivity) 傳來的跳轉指令
        if (!checkIntentExtras()) {
            // 如果沒有特殊指令，且是第一次啟動，載入預設首頁
            if (savedInstanceState == null) {
                switchFragment(new HomeFragment());
            }
        }
    }

    /**
     * 🚩 處理「從外部跳回來」的邏輯 (例如：購票完跳回確認頁)
     */
    private boolean checkIntentExtras() {
        if (getIntent().hasExtra("OPEN_CONFIRM")) {
            Bundle args = getIntent().getBundleExtra("OPEN_CONFIRM");
            OrderConfirmFragment fragment = OrderConfirmFragment.newInstance(args);
            switchFragment(fragment);
            return true;
        }
        return false;
    }

    /**
     * 當 App 已經在背景，接收到新的 Intent 時觸發
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkIntentExtras();
    }

    /**
     * 綁定固定不變的導航按鈕
     */
    private void setupNavigation() {
        findViewById(R.id.btn_nav_programmes).setOnClickListener(v -> switchFragment(new HomeFragment()));
        findViewById(R.id.btn_nav_news).setOnClickListener(v -> switchFragment(new NewsListFragment()));
        findViewById(R.id.btn_nav_faq).setOnClickListener(v -> switchFragment(new FAQFragment()));
        findViewById(R.id.btn_nav_member).setOnClickListener(v -> switchFragment(new MemberFragment()));
        findViewById(R.id.btn_nav_question).setOnClickListener(v -> switchFragment(new QuestionFragment()));

        // 注意：btn_nav_login 的邏輯統一在 updateNavUI() 處理，避免衝突
    }

    /**
     * 🚩 核心：根據登入狀態動態更新「登入/會員」按鈕
     */
    public void updateNavUI() {
        // 1. 取得按鈕與 Session 狀態
        Button btnNavLogin = findViewById(R.id.btn_nav_login);
        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            // --- 🚩 登入狀態：切換為會員選單鍵 ---

            // 設定圖示 (若為 MaterialButton 建議使用 setIconResource)
            if (btnNavLogin instanceof com.google.android.material.button.MaterialButton) {
                ((com.google.android.material.button.MaterialButton) btnNavLogin).setIconResource(R.drawable.ic_member);
            } else {
                btnNavLogin.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_member, 0, 0);
            }

            btnNavLogin.setOnClickListener(v -> {
                // 🚩 A. 建立像素風 Context (套用 R.style.PixelPopupMenu)
                androidx.appcompat.view.ContextThemeWrapper wrapper =
                        new androidx.appcompat.view.ContextThemeWrapper(this, R.style.PixelPopupMenu);

                PopupMenu popup = new PopupMenu(wrapper, v);
                popup.getMenuInflater().inflate(R.menu.menu_member_options, popup.getMenu());

                // 🚩 B. 暴力破解：強制讓 PopupMenu 顯示圖示
                try {
                    java.lang.reflect.Field[] fields = popup.getClass().getDeclaredFields();
                    for (java.lang.reflect.Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            java.lang.reflect.Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 🚩 C. 統一染色為「像素亮藍色」
                // 在 show() 之前遍歷選單，把所有 Icon 漆成藍色
                android.view.Menu menu = popup.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    android.view.MenuItem item = menu.getItem(i);
                    if (item.getIcon() != null) {
                        // 使用亮藍色 #00CCFF
                        androidx.core.graphics.drawable.DrawableCompat.setTint(
                                item.getIcon(),
                                android.graphics.Color.parseColor("#00F3FF")
                        );
                    }
                }

                // 🚩 C. 設定選單點擊邏輯
                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.menu_view_profile) {
                        switchFragment(new MemberFragment());
                    } else if (id == R.id.menu_my_orders) {
                        switchFragment(new MyOrdersFragment());
                    } else if (id == R.id.menu_my_questions) {
                        switchFragment(new MyQuestionsFragment());
                    }else if (id == R.id.menu_logout) {
                        performLogout(); // 執行登出組合拳
                    }
                    return true;
                });
                popup.show();
            });

        } else {
            // --- 🚩 未登入狀態：保持為一般登入鍵 ---
            if (btnNavLogin instanceof com.google.android.material.button.MaterialButton) {
                ((com.google.android.material.button.MaterialButton) btnNavLogin).setIconResource(R.drawable.ic_login);
            } else {
                btnNavLogin.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_login, 0, 0);
            }

            btnNavLogin.setOnClickListener(v -> {
                Toast.makeText(this, "SYSTEM: 進入登入程序...", Toast.LENGTH_SHORT).show();
                switchFragment(new LoginFragment());
            });
        }
    }

    /**
     * 🚩 執行登出組合拳
     */
    private void performLogout() {
        new SessionManager(this).logout();    // 清除手機 Session
        RetrofitClient.clearCookies();        // 清除持久化 Cookie
        updateNavUI();                        // 立即更新 Navbar UI
        switchFragment(new HomeFragment());   // 跳轉回首頁
        Toast.makeText(this, "系統: 已安全登出", Toast.LENGTH_SHORT).show();
    }

    /**
     * 通用的 Fragment 切換方法
     */
    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 底部狀態欄小圓點閃爍效果
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

    public void switchToOrdersFragment() {
        // 切換到訂單 Fragment
        switchFragment(new MyOrdersFragment());

        // 顯示一條溫馨提示
        Toast.makeText(this, "系統: 支付成功！已為您跳轉至訂單清單...", Toast.LENGTH_LONG).show();
    }
}