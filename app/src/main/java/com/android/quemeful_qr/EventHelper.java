//https://stackoverflow.com/a/60389994
package com.android.quemeful_qr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;

public class EventHelper implements Serializable {
    private String id;
    private String title;
    private String location;
    private String time;
    private String date;
    private String description;
    private String organizer;
    private String poster;

    /**
     * get organizer of event
     * @return String
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * sets the organizer for an event
     * @param organizer
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * EventHelper is sometimes called without parameters so this constructor is added
     */
    EventHelper(){

    }

    /**
     * event attributes
     * @param id
     * @param title
     * @param location
     * @param time
     * @param date
     * @param description
     * @param poster
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

    /**
     * gets description of event
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets description of event
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * gets id of event(QR code)
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * sets id of event
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * gets title of event
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets title of event
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * get location of event
     * @return String
     */
    public String getLocation() {
        return location;
    }

    /**
     * sets the location of the event
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}

