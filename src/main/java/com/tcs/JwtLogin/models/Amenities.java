package com.tcs.JwtLogin.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class Amenities {

    private boolean ac;
    private boolean wheelchairAccess;
    private boolean parking;
    private boolean washroom;

    // getters & setters

    public boolean isAc() {
        return ac;
    }

    public void setAc(boolean ac) {
        this.ac = ac;
    }

    public boolean isWheelchairAccess() {
        return wheelchairAccess;
    }

    public void setWheelchairAccess(boolean wheelchairAccess) {
        this.wheelchairAccess = wheelchairAccess;
    }

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public boolean isWashroom() {
        return washroom;
    }

    public void setWashroom(boolean washroom) {
        this.washroom = washroom;
    }
}

