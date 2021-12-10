package com.abhinav.tecstudyapquiz;

public class UsersNames {
    private String yourNaam,yourNo;


    public UsersNames(String yourNaam, String yourNo) {
        this.yourNaam = yourNaam;
        this.yourNo = yourNo;
    }

    public UsersNames() {

    }

    public String getYourNaam() {
        return yourNaam;
    }

    public void setYourNaam(String yourNaam) {
        this.yourNaam = yourNaam;
    }

    public String getYourNo() {
        return yourNo;
    }

    public void setYourNo(String yourNo) {
        this.yourNo = yourNo;
    }
}
