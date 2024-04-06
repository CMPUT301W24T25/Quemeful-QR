package com.android.quemeful_qr;

public class AttendeeMapPin {
//    private String firstName;
//    private String lastName;
    private String attendeeId;
//    private String location;
    private Double latitude;
    private Double longitude;

    public String getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public AttendeeMapPin(String attendeeId, Double latitude, Double longitude) {
        this.attendeeId = attendeeId;
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
