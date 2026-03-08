package Model;

import java.util.List;

public class UserOrderDetail {

    public String orderID;
    public String programmeName;
    public int finalAmount;
    public String startTime;

    public String placeName;
    public String orderStatusName;
    public boolean isPrintable;
    public List<UserTicketItem>tickets;

    public List<String> seats;
}
