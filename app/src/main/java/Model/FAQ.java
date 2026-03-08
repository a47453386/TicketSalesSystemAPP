package Model;
import com.google.gson.annotations.SerializedName;

public class FAQ {
    @SerializedName("faqid")
    public String faqid;

    @SerializedName("faqTitle")
    public String faqTitle;

    @SerializedName("faqDescription")
    public String faqDescription;

    @SerializedName("faqTypeName")
    public String faqTypeName;

    // 🚩 邏輯欄位：記錄該項目是否處於展開狀態 (不需對應 API)
    public boolean isExpanded = false;
}