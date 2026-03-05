package Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProgrammeDetail {
    @SerializedName("programmeID")
    public String programmeID ;
    @SerializedName("programmeName")
    public String programmeName;
    @SerializedName("statusName")
    public String statusName;
    @SerializedName("placeName")
    public String placeName;
    @SerializedName("programmeDescription")
    public String programmeDescription ;
    public String notice ;
    public String purchaseReminder ;
    public String collectionReminder ;

    public String refundPolicy;

    @SerializedName("coverImage")
    public String coverImage ;
    public String seatImage ;

    public String onShelfTime ;
    public String updatedAt ;
    public String employeeName;

    @SerializedName("sessions")
    public List<Session> sessions ;
    @SerializedName("descriptionImages")
    public List<DescriptionImage> descriptionImages;
}
