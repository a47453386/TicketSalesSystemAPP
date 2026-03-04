package Model;

import java.util.List;

public class BookingResponse {
    public boolean success;
    public String message;
    public String orderID;
    public List<String> seats;
    public double finalAmount;
    public int remainingSeconds;
    public String expireTimeText;
}
