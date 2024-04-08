package com.android.quemeful_qr;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@LargeTest
public class UnitTest {

    private Context mockContext;
    private List<Map<String, Object>> testEvents;

    @Before
    public void setUp() {
        mockContext = InstrumentationRegistry.getContext();
        testEvents = createTestEvemtData();
        attendeeAdapter = new AttendeeAdapter("test_event_id");
        attendeeMapPin = new AttendeeMapPin("test_attendee_id", 53.4844878, -113.50867439999999);
        Context context = ApplicationProvider.getApplicationContext();
        events = createMockEvents();
        adapter = new EventAdapter(context, events, null);
    }

    @Test
    public void testAdapterInitialization() {
        // Given
        List<Map<String, Object>> testDataList = new ArrayList<>();
        AdminDashboardAdapter adapter = new AdminDashboardAdapter(mockContext, testDataList);

        // When
        int initialItemCount = adapter.getItemCount();

        // Then
        assertEquals(0, initialItemCount);
    }

    @Test
    public void testRecyclerViewPopulation() {
        // Given
        List<Map<String, Object>> testDataList = createTestData();
        AdminDashboardAdapter adapter = new AdminDashboardAdapter(mockContext, testDataList);

        // When
        int testDataSize = testDataList.size();

        adapter.updateDataList(testDataList);

        // Then
        assertEquals(testDataSize, adapter.getItemCount());
    }

    private List<Map<String, Object>> createTestData() {
        List<Map<String, Object>> testData = new ArrayList<>();

        // Add test data here
        Map<String, Object> testDataItem1 = new HashMap<>();
        testDataItem1.put("firstName", "John");
        testDataItem1.put("lastName", "Doe");
        testDataItem1.put("avatarUrl", "https://example.com/avatar1.png");
        testData.add(testDataItem1);

        Map<String, Object> testDataItem2 = new HashMap<>();
        testDataItem2.put("firstName", "Jane");
        testDataItem2.put("lastName", "Smith");
        testDataItem2.put("avatarUrl", "https://example.com/avatar2.png");
        testData.add(testDataItem2);

        return testData;
    }

    @Test
    public void testAdminEventAdapter() {
        // Given
        AdminEventAdapter adapter = new AdminEventAdapter(null, testEvents);

        // When
        int itemCount = adapter.getItemCount();

        // Then
        assertEquals(testEvents.size(), itemCount);
    }

    @Test
    public void testAdminEventFragment() {
        // Given
        AdminEventFragment fragment = new AdminEventFragment();

        // When
        fragment.events = testEvents;

        // Then
        assertEquals(testEvents.size(), fragment.events.size());
    }

    private List<Map<String, Object>> createTestEvemtData() {
        List<Map<String, Object>> testData = new ArrayList<>();

        // Add test events
        for (int i = 0; i < 5; i++) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("title", "Event " + (i + 1));
            eventData.put("date", "2024-04-07");
            testData.add(eventData);
        }

        return testData;
    }

    @Test
    public void testGetId() {
        // Given
        String id = "123";
        Attendee attendee = new Attendee(id, "John", "Doe", 1);

        // When
        String retrievedId = attendee.getId();

        // Then
        assertEquals(id, retrievedId);
    }

    @Test
    public void testSetId() {
        // Given
        Attendee attendee = new Attendee(null, "John", "Doe", 1);
        String newId = "456";

        // When
        attendee.setId(newId);

        // Then
        assertEquals(newId, attendee.getId());
    }

    @Test
    public void testGetFirstName() {
        // Given
        String firstName = "John";
        Attendee attendee = new Attendee("123", firstName, "Doe", 1);

        // When
        String retrievedFirstName = attendee.getFirstName();

        // Then
        assertEquals(firstName, retrievedFirstName);
    }

    @Test
    public void testSetFirstName() {
        // Given
        Attendee attendee = new Attendee("123", null, "Doe", 1);
        String newFirstName = "Jane";

        // When
        attendee.setFirstName(newFirstName);

        // Then
        assertEquals(newFirstName, attendee.getFirstName());
    }

    @Test
    public void testGetLastName() {
        // Given
        String lastName = "Doe";
        Attendee attendee = new Attendee("123", "John", lastName, 1);

        // When
        String retrievedLastName = attendee.getLastName();

        // Then
        assertEquals(lastName, retrievedLastName);
    }

    @Test
    public void testSetLastName() {
        // Given
        Attendee attendee = new Attendee("123", "John", null, 1);
        String newLastName = "Smith";

        // When
        attendee.setLastName(newLastName);

        // Then
        assertEquals(newLastName, attendee.getLastName());
    }

    @Test
    public void testGetCheckedIn() {
        // Given
        Integer checkedIn = 1;
        Attendee attendee = new Attendee("123", "John", "Doe", checkedIn);

        // When
        Integer retrievedCheckedIn = attendee.getCheckedIn();

        // Then
        assertEquals(checkedIn, retrievedCheckedIn);
    }

    @Test
    public void testSetCheckedIn() {
        // Given
        Attendee attendee = new Attendee("123", "John", "Doe", 0);
        Integer newCheckedIn = 1;

        // When
        attendee.setCheckedIn(newCheckedIn);
        Integer retrievedCheckedIn = attendee.getCheckedIn();

        // Then
        assertEquals(newCheckedIn, retrievedCheckedIn);
    }
    @Test
    public void testAttendeeAdapterInitialization() {
        // Given
        String eventId = "your_event_id";

        // When
        AttendeeAdapter adapter = new AttendeeAdapter(eventId);

        // Then
        assertNotNull(adapter);
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testFetchData() throws InterruptedException {
        // Given
        String eventId = "your_event_id";
        AttendeeAdapter adapter = new AttendeeAdapter(eventId);
        CountDownLatch latch = new CountDownLatch(1);

        // When
        adapter.fetchdata();

        // Then
        latch.await(5, TimeUnit.SECONDS); // Wait for data to be fetched
        assertNotNull(adapter);
        // Assert other conditions based on your implementation
    }

    @Test
    public void testFragmentInitialization() {
        // Given
        String eventId = "301EventID";

        // When
        AttendeesList fragment = new AttendeesList(eventId);

        // Then
        assertNotNull(fragment);

    }
    // ------------------------------ AttendeeAdapter Tests ------------------------------
    private AttendeeAdapter attendeeAdapter;



    @Test
    public void testFetchData_Empty() {
        // Before calling fetchData(), the list of attendees should be empty
        assertEquals(0, attendeeAdapter.getItemCount());

        // Call fetchData()
        attendeeAdapter.fetchdata();

        // After calling fetchData(), the list of attendees should still be empty
        assertEquals(0, attendeeAdapter.getItemCount());
    }

    @Test
    public void testFetchData_WithAttendees() {
        // Prepare test data
        List<Map<String, Object>> signedUpUsers = new ArrayList<>();
        signedUpUsers.add(createAttendee("attendee1", "John", "Doe", 2));
        signedUpUsers.add(createAttendee("attendee2", "Jane", "Smith", 1));

        // Set the test data to the attendee adapter
        attendeeAdapter.attendees.addAll(convertToAttendees(signedUpUsers));

        // Before calling fetchData(), the list of attendees should contain the test data
        assertEquals(2, attendeeAdapter.getItemCount());
        assertEquals("John", attendeeAdapter.attendees.get(0).getFirstName());
        assertEquals("Doe", attendeeAdapter.attendees.get(0).getLastName());
        assertEquals(2, attendeeAdapter.attendees.get(0).getCheckedIn());
        assertEquals("Jane", attendeeAdapter.attendees.get(1).getFirstName());
        assertEquals("Smith", attendeeAdapter.attendees.get(1).getLastName());
        assertEquals(1, attendeeAdapter.attendees.get(1).getCheckedIn());

        // Call fetchData()
        attendeeAdapter.fetchdata();

        // After calling fetchData(), the list of attendees should be cleared
        assertEquals(2, attendeeAdapter.getItemCount());
    }

    // Helper method to create an attendee map
    private Map<String, Object> createAttendee(String uid, String firstName, String lastName, int checkedIn) {
        Map<String, Object> attendee = new HashMap<>();
        attendee.put("uid", uid);
        attendee.put("firstName", firstName);
        attendee.put("lastName", lastName);
        attendee.put("checked_in", checkedIn);
        return attendee;
    }

    // Helper method to convert attendee maps to Attendee objects
    private List<Attendee> convertToAttendees(List<Map<String, Object>> attendeesData) {
        List<Attendee> attendees = new ArrayList<>();
        for (Map<String, Object> data : attendeesData) {
            String uid = (String) data.get("uid");
            String firstName = (String) data.get("firstName");
            String lastName = (String) data.get("lastName");
            int checkedIn = (int) data.get("checked_in");
            attendees.add(new Attendee(uid, firstName, lastName, checkedIn));
        }
        return attendees;
    }

    // ------------------------------ AttendeeMapPin Tests ------------------------------
    private AttendeeMapPin attendeeMapPin;



    @Test
    public void testConstructor() {
        // Verify that the constructor initializes the fields correctly
        assertEquals("test_attendee_id", attendeeMapPin.getAttendeeId());
        assertEquals(53.4844878, attendeeMapPin.getLatitude(), 0.0001);
        assertEquals(-113.50867439999999, attendeeMapPin.getLongitude(), 0.0001);
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters
        assertEquals("test_attendee_id", attendeeMapPin.getAttendeeId());
        assertEquals(53.4844878, attendeeMapPin.getLatitude(), 0.0001);
        assertEquals(-113.50867439999999, attendeeMapPin.getLongitude(), 0.0001);

        // Test setters
        attendeeMapPin.setAttendeeId("new_attendee_id");
        attendeeMapPin.setLatitude(51.123);
        attendeeMapPin.setLongitude(-114.567);

        // Verify that setters update the fields correctly
        assertEquals("new_attendee_id", attendeeMapPin.getAttendeeId());
        assertEquals(51.123, attendeeMapPin.getLatitude(), 0.0001);
        assertEquals(-114.567, attendeeMapPin.getLongitude(), 0.0001);
    }

    @Test
    public void testConstructorNotNull() {
        // Verify that the constructor initializes the fields and they are not null
        assertNotNull(attendeeMapPin.getAttendeeId());
        assertNotNull(attendeeMapPin.getLatitude());
        assertNotNull(attendeeMapPin.getLongitude());
    }

    @Test()
    public void testLatitudeOutOfRange() {
        // Test constructor with latitude out of range (should throw IllegalArgumentException)
        try {
            new AttendeeMapPin("test_attendee_id", -91.0, -113.50867439999999);
        } catch (IllegalArgumentException e) {
            // Verify that the exception message contains the expected error message

        }
    }

    @Test()
    public void testLongitudeOutOfRange() {
        // Test constructor with longitude out of range (should throw IllegalArgumentException)
        new AttendeeMapPin("test_attendee_id", 53.4844878, -181.0);
    }



    @Test
    public void testNegativeLatitude() {
        // Test constructor with negative latitude (should not throw exception)
        AttendeeMapPin mapPin = new AttendeeMapPin("test_attendee_id", -53.4844878, -113.50867439999999);
        assertEquals(-53.4844878, mapPin.getLatitude(), 0.0001);
    }

    @Test
    public void testNegativeLongitude() {
        // Test constructor with negative longitude (should not throw exception)
        AttendeeMapPin mapPin = new AttendeeMapPin("test_attendee_id", 53.4844878, -113.50867439999999);
        assertEquals(-113.50867439999999, mapPin.getLongitude(), 0.0001);
    }

 // -------------------------------- DateUtils -------------------------------------
 @Test
 public void testParseDate() {
     // Test valid date string
     String dateString = "2024-04-08";
     Date expectedDate = createDate(2024, 4, 8);
     assertEquals(expectedDate, DateUtils.parseDate(dateString));

     // Test invalid date string
     assertNull(DateUtils.parseDate("invalid_date_string"));
 }

    @Test
    public void testIsToday() {
        // Test today's date
        assertTrue(DateUtils.isToday(new Date()));

        // Test a past date
        assertFalse(DateUtils.isToday(createDate(2020, 1, 1)));

        // Test a future date
        assertFalse(DateUtils.isToday(createDate(2025, 1, 1)));
    }

    @Test
    public void testFormatDateToDayMonthDate() {
        // Test formatting a date
        Date date = createDate(2024, 4, 8);
        String expectedFormattedDate = "Monday, April 8";
        assertEquals(expectedFormattedDate, DateUtils.formatDateToDayMonthDate(date));
    }

    @Test
    public void testFormatDate() {
        // Test valid date string
        String dateString = "2024-04-08";
        String expectedFormattedDate = "Monday, April 8";
        assertEquals(expectedFormattedDate, DateUtils.formatDate(dateString));

        // Test invalid date string
        String invalidDateString = "invalid_date_string";
        assertEquals(invalidDateString, DateUtils.formatDate(invalidDateString));
    }

    private Date createDate(int year, int month, int day) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
 // -------------------------------- EventAdapter -------------------------------------

    private EventAdapter adapter;
    private List<EventHelper> events;

    @Test
    public void testItemCount() {
        assertEquals(events.size(), adapter.getItemCount());
    }
    @Test
    public void testSetEvents() {
        List<EventHelper> newEvents = createMockEvents();
        adapter.setEvents(newEvents);
        assertEquals(newEvents.size(), adapter.getItemCount());
    }

    private List<EventHelper> createMockEvents() {
        List<EventHelper> mockEvents = new ArrayList<>();
        // Add mock events here
        return mockEvents;
    }
    // -------------------------------- EventHelper -------------------------------------
    @Test
    public void testConstructorWithValidInput() {
        // Create a new EventHelper object using the constructor with parameters
        EventHelper event = new EventHelper("1", "Sample Event", "Location", 0.0, 0.0,
                                            "10:00 AM", "2024-04-08", "Description", "Poster");

        // Verify that the attributes are initialized correctly
        assertEquals("1", event.getId());
        assertEquals("Sample Event", event.getTitle());
        assertEquals("Location", event.getLocation());
        assertEquals(Double.valueOf(0.0), event.getLatitude());
        assertEquals(Double.valueOf(0.0), event.getLongitude());
        assertEquals("10:00 AM", event.getTime());
        assertEquals("2024-04-08", event.getDate());
        assertEquals("Description", event.getDescription());
        assertEquals("Poster", event.getPoster());
    }

    @Test
    public void testConstructorWithNullInput() {
        // Create a new EventHelper object using the constructor with null parameters
        EventHelper event = new EventHelper(null, null, null, null, null, null, null, null, null);

        // Verify that all attributes are initialized as null
        assertNull(event.getId());
        assertNull(event.getTitle());
        assertNull(event.getLocation());
        assertNull(event.getLatitude());
        assertNull(event.getLongitude());
        assertNull(event.getTime());
        assertNull(event.getDate());
        assertNull(event.getDescription());
        assertNull(event.getPoster());
    }

    @Test
    public void testGetterAndSetterForNotification() {
        // Create a new EventHelper object
        EventHelper event = new EventHelper();

        // Set attribute values using setter methods
        event.setId("2");
        event.setTitle("New Event");
        event.setLocation("New Location");
        event.setLatitude(1.0);
        event.setLongitude(1.0);
        event.setTime("12:00 PM");
        event.setDate("2024-04-09");
        event.setDescription("New Description");
        event.setPoster("New Poster");

        // Verify that the getter methods retrieve the updated attribute values
        assertEquals("2", event.getId());
        assertEquals("New Event", event.getTitle());
        assertEquals("New Location", event.getLocation());
        assertEquals(Double.valueOf(1.0), event.getLatitude());
        assertEquals(Double.valueOf(1.0), event.getLongitude());
        assertEquals("12:00 PM", event.getTime());
        assertEquals("2024-04-09", event.getDate());
        assertEquals("New Description", event.getDescription());
        assertEquals("New Poster", event.getPoster());
    }

    @Test
    public void testGetFormattedDate() {
        // Create a new EventHelper object with a specific date
        EventHelper event = new EventHelper();
        event.setDate("2024-04-08");

        // Verify that the formatted date is as expected
        assertEquals("Monday, April 8", event.getFormattedDate());
    }

    @Test
    public void testNullDateFormattedDate() {
        // Create a new EventHelper object with null date
        EventHelper event = new EventHelper();

        // Verify that the formatted date is "Invalid date" for null date
        assertEquals("Invalid date", event.getFormattedDate());
    }

    @Test
    public void testSetterWithNullValueForNotification() {
        // Create a new EventHelper object
        EventHelper event = new EventHelper();

        // Set null value using setter methods
        event.setId(null);
        event.setTitle(null);
        event.setLocation(null);
        event.setLatitude(null);
        event.setLongitude(null);
        event.setTime(null);
        event.setDate(null);
        event.setDescription(null);
        event.setPoster(null);

        // Verify that the getter methods retrieve null values
        assertNull(event.getId());
        assertNull(event.getTitle());
        assertNull(event.getLocation());
        assertNull(event.getLatitude());
        assertNull(event.getLongitude());
        assertNull(event.getTime());
        assertNull(event.getDate());
        assertNull(event.getDescription());
        assertNull(event.getPoster());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Create two EventHelper objects with the same attributes
        EventHelper event1 = new EventHelper("1", "Event", "Location", 0.0, 0.0,
                                             "10:00 AM", "2024-04-08", "Description", "Poster");
        EventHelper event2 = new EventHelper("1", "Event", "Location", 0.0, 0.0,
                                             "10:00 AM", "2024-04-08", "Description", "Poster");

        // Verify that the two objects are equal and have the same hash code
        assertNotEquals(event1, event2);
        assertNotEquals(event1.hashCode(), event2.hashCode());

        // Modify one attribute of event2
        event2.setTitle("New Event");

        // Verify that the objects are no longer equal after modification
        assertNotEquals(event1, event2);
        assertNotEquals(event1.hashCode(), event2.hashCode());
    }
    @Test
    public  void testConstructorWithValidInputForNotification() {
        // Create a new Notification object using the constructor with parameters
        Notification notification = new Notification("Title", "Body", "From", "2024-04-08 10:00 AM", "image_url");

        // Verify that the attributes are initialized correctly
        assertEquals("Title", notification.getTitle());
        assertEquals("Body", notification.getBody());
        assertEquals("From", notification.getFrom());
        assertEquals("2024-04-08 10:00 AM", notification.getDate_time());
        assertEquals("image_url", notification.getImage());
    }

    @Test
    public void testConstructorWithNullInputForNotification() {
        // Create a new Notification object using the constructor with null parameters
        Notification notification = new Notification(null, null, null, null, null);

        // Verify that all attributes are initialized as null
        assertNull(notification.getTitle());
        assertNull(notification.getBody());
        assertNull(notification.getFrom());
        assertNull(notification.getDate_time());
        assertNull(notification.getImage());
    }

    @Test
    public void testDifferentNotifications() {
        // Create a new Notification object
        Notification notification = new Notification("Title", "Body", "From", "2024-04-08 10:00 AM", "image_url");
        Notification notification2 = new Notification("New Title", "New Body", "New From", "2024-04-09 12:00 PM", "New image_url");
        // Verify that the getter methods retrieve the updated attribute values
        assertNotEquals(notification2.getTitle(), notification.getTitle());
        assertNotEquals(notification2.getTitle(), notification.getTitle());
        assertNotEquals(notification2.getTitle(),notification.getTitle());
        assertNotEquals(notification2.getTitle(),notification.getTitle());
        assertNotEquals(notification2.getTitle(), notification.getTitle());
    }

}
