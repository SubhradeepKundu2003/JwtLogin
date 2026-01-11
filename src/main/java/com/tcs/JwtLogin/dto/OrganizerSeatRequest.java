package com.tcs.JwtLogin.dto;

import com.tcs.JwtLogin.models.SeatType;

public class OrganizerSeatRequest {

    private Integer row;
    private Integer column;
    private Boolean active;
    private SeatType seatType;

    // getters & setters

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }
}
