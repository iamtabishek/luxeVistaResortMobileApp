package com.example.luxevistaapp;

public class AppUser {
    private String name;
    private String email;

    private String phoneNumber; // Optional, initially null
    private String nic; // Optional, initially null
    private boolean isVerified; // To indicate OTP verification status

    // Default constructor required for calls to DataSnapshot.getValue(AppUser.class)
    public AppUser() {}

    // Constructor for initial sign-up without NIC and phone number
    public AppUser(String name, String email) {
        this.name = name;
        this.email = email;
        this.phoneNumber = null;
        this.nic = null;
    }

    // Getters and Setters for all fields
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

}
