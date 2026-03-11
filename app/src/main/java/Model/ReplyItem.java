package Model;

import com.google.gson.annotations.SerializedName;

public class ReplyItem {

    @SerializedName("replyStatusID")
    public String replyStatusID;
    @SerializedName("replyDescription")
    public String replyDescription;

    @SerializedName("replyStatusName")
    public String replyStatusName;

    @SerializedName("employeeName")
    public String employeeName;

    @SerializedName("createdTime")
    public String createdTime;


}
