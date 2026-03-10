package Model;

import com.google.gson.annotations.SerializedName;

public class QuestionDetail {
    @SerializedName("questionID")
    public String questionID;

    @SerializedName("questionTitle")
    public String questionTitle;

    @SerializedName("questionDescription")
    public String questionDescription;

    @SerializedName("createdTime")
    public String createdTime;

    @SerializedName("uploadFile")
    public String uploadFile;

    @SerializedName("questionTypeName")
    public String questionTypeName;

    // 🚩 注意這裡：JSON 裡面的 Key 是 "reply"
    @SerializedName("reply")
    public List<ReplyItem> replies;
}
