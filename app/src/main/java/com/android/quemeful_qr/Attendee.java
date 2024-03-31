package com.android.quemeful_qr;

/**
 * This class is used to add setter and getter methods for the attendees attributes.
 */
public class Attendee {
    private String id;
    private String firstName;
    private String lastName;
    private Integer checkedIn;



    /**
     * This is a constructor (with parameters ie, not a default constructor)
     * @param id the unique user id
     * @param firstName the randomly selected firstname
     * @param lastName the randomly selected lastname
     * @param checkedIn the integer instance checkedIn to know the user check in status.
     */
    public Attendee(String id, String firstName, String lastName, Integer checkedIn) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.checkedIn = checkedIn;
    }

    // getters and setters for all fields for attendees

    /**
     * This method is used to get check in status of a user.
     * @return Integer
     */
    public int getCheckedIn() {
        return checkedIn;
    }

    /**
     * This method is used to set check in status of a user.
     * @param checkedIn the integer instance checkedIn to know the user check in status.
     */
    public void setCheckedIn(Integer checkedIn) {
        this.checkedIn = checkedIn;
    }

    /**
     * This method is used to fetch event id (ie, the unique QR code id).
     * @return String event/QR code id.
     */
    public String getId() {
        return id;
    }

    /**
     * This method is used to set the fetched event id.
     * @param id the fetched string event/QR code id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This is a getter method for first name of attendee.
     * @return String first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This is a setter method for first name of attendee.
     * @param firstName the fetched first name of attendee.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * This is a getter method for last name of attendee.
     * @return string last name of attendee.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This is a setter method for last name of attendee.
     * @param lastName the fetched last name of attendee.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

} // class Attendee closing