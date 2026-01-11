package com.tcs.JwtLogin.dto;

import com.tcs.JwtLogin.models.SeatType;

public class OrganizerSeatResponse {

    private Integer row;
    private Integer column;
    private String seatName;
    private boolean active;
    private SeatType seatType;

    // getters & setters
    public Integer getRow() { return row; }
    public void setRow(Integer row) { this.row = row; }

    public Integer getColumn() { return column; }
    public void setColumn(Integer column) { this.column = column; }

    public String getSeatName() { return seatName; }
    public void setSeatName(String seatName) { this.seatName = seatName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public SeatType getSeatType() { return seatType; }
    public void setSeatType(SeatType seatType) { this.seatType = seatType; }
}
