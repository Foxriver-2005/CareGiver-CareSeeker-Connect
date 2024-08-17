package com.care.careme.Models;

public class CareSeeker_Details {

    private String phone,name;

    public CareSeeker_Details() {
    }

    public CareSeeker_Details(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
