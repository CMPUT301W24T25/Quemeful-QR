package com.android.quemeful_qr;

public class UserSettings {


    //location switch
    public static final String LOCATION_PREFERENCES = "customLocationPreferences";

    public static final String CUSTOM_LOCATION = "customLocation";
    public static final String LOCATION_OFF = "locationOff";
    public static final String LOCATION_ON = "locationOn";

    private String CustomLocation;

    public String getCustomLocation() {
        return CustomLocation;
    }

    public void setCustomLocation(String customLocation) {
        CustomLocation = customLocation;
    }
}
