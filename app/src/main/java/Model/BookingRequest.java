package Model;

import java.util.List;

public class BookingRequest {
    public String venueID;
    public String sessionID;
    public String ticketsAreaID;
    public String memberID;
    public String paymentMethodID;
    public int count;
    public double totalAmount;
    public List<String> seats;
}
