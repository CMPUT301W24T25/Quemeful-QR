package com.android.quemeful_qr;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Event implements Serializable {
    //Event event = new Event(eventUUID, eventName, checkInTimeDate, checkInLocation, attendees);
    private String eventUUID;
    private String eventName;
    private String eventPoster;
    private LocalDateTime checkInTimeDate;
    private Double checkInLocation;

//    private ArrayList<Attendees> attendeeList;

    Event(String eventUUID, String eventName, String eventPoster){
        this.eventUUID = eventUUID;
        this.eventName = eventName;
        this.eventPoster = eventPoster;
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

    public String getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
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
