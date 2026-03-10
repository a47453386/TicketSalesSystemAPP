package Model;

import com.google.gson.annotations.SerializedName;

public class ReplyItem {
    @SerializedName("replyDescription")
    public String replyDescription;

    @SerializedName("replyStatusName")
    public String replyStatusName;

    @SerializedName("employeeName")
    public String employeeName;

    @SerializedName("createdTime")
    public String createdTime;
}
