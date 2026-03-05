package Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Session {
    @SerializedName("sessionID")
    public String sessionID ;
    @SerializedName("startTime")
    public String startTime;

    public String saleStartTime ;
    public String saleEndTime ;
    @SerializedName("ticketsAreas")
    public List<Area> ticketsAreas;


}
