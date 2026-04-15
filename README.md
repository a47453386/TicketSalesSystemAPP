# 📱 票務管理系統 - 使用者端 Android App

供使用者瀏覽活動、購票、管理訂單的 Android 行動應用程式，
與後端 Web API 即時串接，已實際部署於實體 Android 裝置。

## 🎬 功能展示影片
👉 [https://canva.link/1odpz69pbqo58w6]

## 🛠 使用技術
- 平台：Android（Java）
- 後端串接：ASP.NET Core 8 Web API（Retrofit / OkHttp）
- 身份驗證：JWT Token 管理
- QR Code 產生與顯示
- Git / GitHub

## ✨ 主要功能
- 會員註冊／登入／個人資料編輯
- 活動與場次瀏覽查詢
- 手動選位與自動配位購票
- 訂單查詢與管理
- QR Code 票券產生（供驗證端掃描入場）
- 公告瀏覽
- FAQ 提問與查詢

## 📲 部署狀態
本專案已成功部署並安裝於實體 Android 裝置，可完整操作所有功能。

## 🔗 相關專案
- 後端 MVC：[https://github.com/a47453386/TicketSalesSystem.git]
- 票券驗證 App：[https://github.com/a47453386/TicketVerifyApp.git]

## 💻 本機執行方式
1. Clone 此專案至 Android Studio
2. 修改 API 連線位址（Base URL 設定檔）
3. 確認後端 TicketSalesSystem Web API 已啟動
4. 安裝至實體 Android 裝置或模擬器執行
