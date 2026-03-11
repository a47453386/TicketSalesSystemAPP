package Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BookingRequest {
    @SerializedName("venueID")
    public String venueID = ""; // 初始化為空字串，防止 null 崩潰

    @SerializedName("sessionID")
    public String sessionID = "";

    @SerializedName("ticketsAreaID")
    public String ticketsAreaID = "";

    @SerializedName("memberID")
    public String memberID = "";

    @SerializedName("paymentMethodID")
    public String paymentMethodID = "A";

    @SerializedName("count")
    public int count = 1;

    @SerializedName("totalAmount")
    public double totalAmount = 0.0;

    @SerializedName("seats")
    public List<String> seats = new ArrayList<>();
}
