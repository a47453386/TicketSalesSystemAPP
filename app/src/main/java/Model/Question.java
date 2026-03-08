package Model;

import com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("QuestionID")
    public String questionID;

    @SerializedName("questionTitle")
    public String questionTitle;

    @SerializedName("questionDescription")
    public String questionDescription;

    @SerializedName("createdTime")
    public String createdTime;

    @SerializedName("questionTypeName") // 🚩 新增
    public String questionTypeName;

    @SerializedName("replyStatusID")    // 🚩 新增
    public String replyStatusID;

    @SerializedName("hasUpload")        // 🚩 新增
    public boolean hasUpload;

}