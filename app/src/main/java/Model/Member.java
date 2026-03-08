package Model;
import com.google.gson.annotations.SerializedName;

public class Member {
    @SerializedName("memberID")
    public String memberID;

    @SerializedName("name")
    public String name;

    @SerializedName("address")
    public String address;

    @SerializedName("birthday")
    public String birthday;

    @SerializedName("tel")
    public String tel;

    @SerializedName("gender")
    public boolean gender;

    @SerializedName("nationalID")
    public String nationalID;

    @SerializedName("email")
    public String email;

    @SerializedName("isPhoneVerified")
    public boolean isPhoneVerified;

    @SerializedName("statusName")
    public String statusName;
}