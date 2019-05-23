package omdvet.com.WebServices.Models;

import com.google.gson.annotations.SerializedName;

public class Emp {
    @SerializedName("id")
    public String id;

    public String name;
    public String apiToken;
    public String email;
    public String phone;
    public String is_admin;
    public String Address;
    public String User_id;
    public String gain;
    public String clients;
}
