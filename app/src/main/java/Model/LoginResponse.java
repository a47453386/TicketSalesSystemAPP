package Model;

public class LoginResponse {
    public boolean success;
    public String message;
    public String memberID ;
    public String name ;
    public boolean isSuccess() { return success; }
    public String getMemberID() { return memberID; }
    public String getName() { return name; }
}
