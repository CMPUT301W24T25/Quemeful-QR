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

    private String poster;
    EventHelper(){

    }
    EventHelper(String id, String title, String location, String time, String date, String description, String poster){
        this.id = id;
        this.title = title;
        this.location = location;
        this.time = time;
        this.date = date;
        this.description = description;
        this.poster = poster;
//        this.attendeeList = ArrayList< Attendees >
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

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

