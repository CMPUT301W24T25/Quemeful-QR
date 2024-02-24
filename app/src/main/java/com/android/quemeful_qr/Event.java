package com.android.quemeful_qr;

import android.provider.CalendarContract;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Event {
    //Event event = new Event(eventUUID, eventName, checkInTimeDate, checkInLocation, attendees);
    private String eventUUID;
    private String eventName;
    private LocalDateTime checkInTimeDate;
    private Double checkInLocation;

//    private ArrayList<Attendees> attendeeList;

    Event(String eventUUID, String eventName){
        this.eventUUID = eventUUID;
        this.eventName = eventName;
//        this.checkInLocation = checkInLocation;
//        this.checkInTimeDate = checkInTimeDate;
//        this.attendeeList = ArrayList< Attendees >
    }

    public String getEventUUID() {
        return eventUUID;
    }

    public void setEventUUID(String eventUUID) {
        this.eventUUID = eventUUID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getCheckInTimeDate() {
        return checkInTimeDate;
    }

    public void setCheckInTimeDate(LocalDateTime checkInTimeDate) {
        this.checkInTimeDate = checkInTimeDate;
    }

    public Double getCheckInLocation() {
        return checkInLocation;
    }

    public void setCheckInLocation(Double checkInLocation) {
        this.checkInLocation = checkInLocation;
    }
}
