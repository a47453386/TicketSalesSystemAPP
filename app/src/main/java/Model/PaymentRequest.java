package Model;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {
    @SerializedName("orderID")
    public String orderID;

    @SerializedName("paymentMethodID")
    public String paymentMethodID;

    public PaymentRequest(String orderID, String paymentMethodID) {
        this.orderID = orderID;
        this.paymentMethodID = paymentMethodID;
    }
}
