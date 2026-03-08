package Model;
import com.google.gson.annotations.SerializedName;

public class PublicNotice {
    // 🚩 直接對應 JSON 中的小寫開頭名稱
    @SerializedName("publicNoticeID")
    public String publicNoticeID;

    @SerializedName("publicNoticeTitle")
    public String publicNoticeTitle;

    @SerializedName("publicNoticeDescription")
    public String publicNoticeDescription;

    // 🚩 這裡要用 String 接收 JSON 的 createdTime (2026-02-08...)
    @SerializedName("createdTime")
    public String createdTime;

    @SerializedName("publicNoticeStatus")
    public boolean publicNoticeStatus;
}