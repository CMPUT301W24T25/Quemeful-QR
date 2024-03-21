package com.android.quemeful_qr;

public class Notification {
    private String title;
    private String body;
    private String from;

    private String date_time;

    private String image;

    public Notification(String title, String body, String from, String date_times, String image) {
        this.title = title;
        this.body = body;
        this.from = from;
        this.date_time = date_times;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
    }

    public String getDate_time() {
        return date_time;
    }
    public String getImage() {
        return image;
    }
}
