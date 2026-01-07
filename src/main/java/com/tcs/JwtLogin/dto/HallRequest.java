package com.tcs.JwtLogin.dto;


import com.tcs.JwtLogin.models.Amenities;
import java.time.LocalDate;
import java.util.List;

public class HallRequest {

    private String name;
    private String place;

    private LocalDate openFrom;
    private LocalDate openTo;

    private int rows;
    private int columns;

    private Amenities amenities;

    private List<SeatRequest> seats;

    // getters & setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDate getOpenFrom() {
        return openFrom;
    }

    public void setOpenFrom(LocalDate openFrom) {
        this.openFrom = openFrom;
    }

    public LocalDate getOpenTo() {
        return openTo;
    }

    public void setOpenTo(LocalDate openTo) {
        this.openTo = openTo;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Amenities getAmenities() {
        return amenities;
    }

    public void setAmenities(Amenities amenities) {
        this.amenities = amenities;
    }

    public List<SeatRequest> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatRequest> seats) {
        this.seats = seats;
    }
}

