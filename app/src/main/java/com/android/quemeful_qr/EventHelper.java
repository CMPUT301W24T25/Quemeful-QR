package com.android.quemeful_qr;

import java.io.Serializable;

/**
 * This class is used to add setter and getter methods for the events and its attributes,
 * (for functionality of an organizer)
 * Reference URL- https://stackoverflow.com/questions/60389906/could-not-deserialize-object-does-not-define-a-no-argument-constructor-if-you/60389994#60389994
 * Author- Royal Tiger, License- CC BY-SA 4.0, Published Date- 25 Feb, 2020.
 */
public class EventHelper implements Serializable {
    private String id;
    private String title;
    private String location;
    private String time;
    private String date;
    private String description;
    private String organizer;
    private String poster;

    // event organizer getter
    public String getOrganizer() {
        return organizer;
    }

    // event organizer setter
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * EventHelper default constructor (no parameters)
     */
    EventHelper(){}

    /**
     * EventHelper constructor with its attributes as parameters,
     * which is used to initialize all the event attributes.
     * @param id the event with the specific id.
     * @param title the name of the event.
     * @param location the location where the event will take place.
     * @param time the time at which the event will start and end.
     * @param date the date when the event will start and end.
     * @param description the purpose or abouts of the event.
     * @param poster the event poster.
     */
    EventHelper(String id, String title, String location, String time, String date, String description, String poster){
        this.id = id;
        this.title = title;
        this.location = location;
        this.time = time;
        this.date = date;
        this.description = description;
//        this.organizer = organizer;
        this.poster = poster;
//        this.attendeeList = ArrayList< Attendees >
    }

    // event description getter
    public String getDescription() {
        return description;
    }

    // event description setter
    public void setDescription(String description) {
        this.description = description;
    }

    // event id getter
    public String getId() {
        return id;
    }

    // event id setter
    public void setId(String id) {
        this.id = id;
    }

    // event title getter
    public String getTitle() {
        return title;
    }

    // event title setter
    public void setTitle(String title) {
        this.title = title;
    }

    // event location getter
    public String getLocation() {
        return location;
    }

    // event location setter
    public void setLocation(String location) {
        this.location = location;
    }

    // event time getter
    public String getTime() {
        return time;
    }

    // event time setter
    public void setTime(String time) {
        this.time = time;
    }

    // event date getter
    public String getDate() {
        return date;
    }

    // event date setter
    public void setDate(String date) {
        this.date = date;
    }

    // event poster getter
    public String getPoster() {
        return poster;
    }

    // event poster setter
    public void setPoster(String poster) {
        this.poster = poster;
    }
} // class closing