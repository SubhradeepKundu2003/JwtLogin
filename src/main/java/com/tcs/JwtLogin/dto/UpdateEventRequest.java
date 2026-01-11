package com.tcs.JwtLogin.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class UpdateEventRequest {

    private String eventName;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;

    // Only seatType updates
    private List<OrganizerSeatRequest> seatOverrides;

    // getters & setters

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List<OrganizerSeatRequest> getSeatOverrides() {
        return seatOverrides;
    }

    public void setSeatOverrides(List<OrganizerSeatRequest> seatOverrides) {
        this.seatOverrides = seatOverrides;
    }
}
