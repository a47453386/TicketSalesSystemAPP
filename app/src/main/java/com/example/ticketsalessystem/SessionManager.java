package com.example.ticketsalessystem;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "TicketAppSession";
    private static final String KEY_MEMBER_ID = "member_id";
    private static final String KEY_USER_NAME = "user_name"; // 🚩 建議多記一個姓名，UI 顯示較親切

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        // 🚩 使用 getApplicationContext() 避免 Activity 銷毀時產生的記憶體洩漏
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // 儲存登入資訊 (建議把姓名也存進去)
    public void saveLoginSession(String memberID, String name) {
        editor.putString(KEY_MEMBER_ID, memberID);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public String getMemberID() {
        return pref.getString(KEY_MEMBER_ID, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "訪客");
    }

    // 🚩 判斷是否已登入：增加空字串檢查
    public boolean isLoggedIn() {
        String id = getMemberID();
        return id != null && !id.trim().isEmpty();
    }

    // 執行登出：徹底清除
    public void logout() {
        editor.clear(); // 清空所有欄位
        editor.commit(); // 登出建議用 commit 確保立即生效
    }
}