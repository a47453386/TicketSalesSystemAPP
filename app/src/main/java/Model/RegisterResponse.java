package Model;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    // 🚩 使用 SerializedName 強制對齊 API 的小寫回傳
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("errorField")
    private String errorField;

    @SerializedName("memberID")
    private String memberID;

    // --- Getters ---
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getErrorField() { return errorField; }
    public String getMemberID() { return memberID; }
}