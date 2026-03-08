package Model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * 對應 C# VMMemberUserEdit 的 Java Model
 */
public class MemberUserEdit implements Serializable {

    @SerializedName("memberID") // 必須與 C# 屬性名稱完全一致
    public String memberID;

    @SerializedName("name")
    public String name;

    @SerializedName("address")
    public String address;

    @SerializedName("birthday")
    public String birthday; // 建議傳輸時使用 String，格式由後端 C# ToString 處理

    @SerializedName("tel")
    public String tel;

    @SerializedName("gender")
    public boolean gender;

    @SerializedName("nationalID")
    public String nationalID;

    @SerializedName("email")
    public String email;

    @SerializedName("account")
    public String account;

    @SerializedName("password")
    public String password;

    // 空建構子是 Gson 必要條件
    public MemberUserEdit() {}
}