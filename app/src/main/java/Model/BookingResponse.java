package Model;

import java.util.List;

public class BookingResponse {
    public String programmeName;
    public String startTime;
    public String placeName;
    public boolean success;
    public String message ;
    public String orderID;
    public List<String> seats ;
    public int finalAmount;
    public int remainingSeconds;

    public String expireTimeText;

    public String orderStatusID;
    public String orderStatusName ;
}
