package com.android.quemeful_qr;

public class Attendee {
    private String id;
    private String firstName;
    private String lastName;

    private Integer checkedIn;

    /**
     * constructor
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param checkedIn
     */
    public Attendee(String id, String firstName, String lastName, Integer checkedIn) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.checkedIn = checkedIn;
    }

    /**
     * get check in status
     * @return int
     */
    public int getCheckedIn() {
        return checkedIn;
    }

    /**
     * set check in status
     * @param checkedIn
     */
    public void setCheckedIn(Integer checkedIn) {
        this.checkedIn = checkedIn;
    }

    /**
     * gets event id (QR code)
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * sets event id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * gets first name of attendee
     * @return String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * sets first name of attendee
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * gets last name of attendee
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * sets last name of attendee
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // getters and setters for all fields
}