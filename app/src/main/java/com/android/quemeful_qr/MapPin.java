package com.android.quemeful_qr;


public class MapPin {
    private String title;
    private String location;
    private double latitude;
    private double longitude;

    public MapPin(String title, String location, double latitude, double longitude) {
        this.title = title;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MapPin() {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
