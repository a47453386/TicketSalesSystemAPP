package Model;

import com.google.gson.annotations.SerializedName;

public class Area {
    @SerializedName("ticketsAreaID")
    public String ticketsAreaID ;
    @SerializedName("ticketsAreaName")
    public String ticketsAreaName ;
    @SerializedName("price")
    public int price;
    public int capacity ;
    public int sold ;
    @SerializedName("remaining")
    public int remaining ;
    public int rowCount ;
    public int seatCount ;
}
