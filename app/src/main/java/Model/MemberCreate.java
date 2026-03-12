package Model;

import com.google.gson.annotations.SerializedName;

public class MemberCreate {

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("tel")
    private String tel;

    @SerializedName("gender")
    private boolean gender;

    @SerializedName("nationalID")
    private String nationalID;

    @SerializedName("email")
    private String email;

    @SerializedName("account")
    private String account;

    @SerializedName("password")
    private String password;

    @SerializedName("isPhoneVerified")
    private boolean isPhoneVerified;

    @SerializedName("accountStatusID")
    private String accountStatusID;

    // --- 建構子 (Constructor) ---
    public MemberCreate(String name, String address, String birthday, String tel,
                        boolean gender, String nationalID, String email,
                        String account, String password,
                        boolean isPhoneVerified, String accountStatusID) {
        this.name = name;
        this.address = address;
        this.birthday = birthday;
        this.tel = tel;
        this.gender = gender;
        this.nationalID = nationalID;
        this.email = email;
        this.account = account;
        this.password = password;
        this.isPhoneVerified = isPhoneVerified;
        this.accountStatusID = accountStatusID;
    }

    // --- Getter 與 Setter ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public boolean isGender() { return gender; }
    public void setGender(boolean gender) { this.gender = gender; }

    public String getNationalID() { return nationalID; }
    public void setNationalID(String nationalID) { this.nationalID = nationalID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isPhoneVerified() { return isPhoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.isPhoneVerified = phoneVerified; }

    public String getAccountStatusID() { return accountStatusID; }
    public void setAccountStatusID(String accountStatusID) { this.accountStatusID = accountStatusID; }
}