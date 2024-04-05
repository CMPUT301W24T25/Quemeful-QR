package com.android.quemeful_qr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UnitTest {

    private FirebaseFirestore firestore;
    private CollectionReference eventsRef;
    private Context context;
    private List<EventHelper> events;

    private ViewGroup parent;
    private View itemView;
    private EventsTodayAdapter.EventViewHolder viewHolder;
    private EventHelper event;

    private AdminDashboardAdapter adapter;
    private List<Map<String, Object>> dataList;
    private String testDeviceId;

    private boolean testIsAdmin;

    @Before
    public void setUp() {
      firestore = FirebaseFirestore.getInstance();
      eventsRef = firestore.collection("events");
        context = ApplicationProvider.getApplicationContext();
        events = new ArrayList<>();

        itemView = View.inflate(ApplicationProvider.getApplicationContext(), R.layout.eventstodaycard, null);
        viewHolder = new EventsTodayAdapter.EventViewHolder(itemView);

        event = new EventHelper(
                "eventId1",
                "Test Event1",
                "Test Location1",
                "Test Time1",
                "Test Date1",
                "Test Description1",
                "Test Poster1",
                "Test token1"
        );
        events.add(event);

        dataList = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "John");
        data.put("lastName", "Doe");
        data.put("avatarUrl", "https://example.com/avatar.png");
        dataList.add(data);

        adapter = new AdminDashboardAdapter(ApplicationProvider.getApplicationContext(), dataList);


        testDeviceId = "test_device_id";

    }


    // ------------------- CreateNewEventActivity -------------------
    @Test
    public void addNewEvent_Success() throws ExecutionException, InterruptedException {
      // Mock data
      String eventName = "Test Event";

      // Create a new event
      EventHelper event = new EventHelper(
              "eventId",
              "Test Event",
              "Test Location",
              "Test Time",
              "Test Date",
              "Test Description",
              "Test Poster",
              "Test token"
      );

      // Add the event to Firestore
      Tasks.await(eventsRef.document(event.getId()).set(event));

      // Retrieve the added event from Firestore
      DocumentSnapshot documentSnapshot = Tasks.await(eventsRef.document(event.getId()).get());

      // Verify that the event is added correctly
      assertEquals(eventName, documentSnapshot.getString("title"));
      assertEquals("Test Location", documentSnapshot.getString("location"));
      assertEquals("Test Time", documentSnapshot.getString("time"));
      assertEquals("Test Date", documentSnapshot.getString("date"));
      assertEquals("Test Description", documentSnapshot.getString("description"));
      assertEquals("Test Poster", documentSnapshot.getString("poster"));


      // Delete the event from Firestore (cleanup)
      Tasks.await(eventsRef.document(event.getId()).delete());
    }


// ------------------- EventsTodayAdapter- -------------------

    @Test
    public void testItemCount() {
        events.add( new EventHelper(
                "eventId1",
                "Test Event1",
                "Test Location1",
                "Test Time1",
                "Test Date1",
                "Test Description1",
                "Test Poster1",
                "Test token1"
        ));
        events.add( new EventHelper(
                "eventId2",
                "Test Event2",
                "Test Location2",
                "Test Time2",
                "Test Date2",
                "Test Description2",
                "Test Poster2",
                "Test token2"
        ));

        EventsTodayAdapter adapter = new EventsTodayAdapter(context, events, null, testIsAdmin);
        assertEquals(events.size(), adapter.getItemCount());
    }
    @Test
    public void testEmptyItemCount() {

         List<EventHelper> localEvent = new ArrayList<>();
        EventsTodayAdapter adapter = new EventsTodayAdapter(context, localEvent, null, testIsAdmin);
        assertEquals(localEvent.size(), adapter.getItemCount());
    }

    @Test
    public void testViewHolder() {

        EventsTodayAdapter adapter = new EventsTodayAdapter(ApplicationProvider.getApplicationContext(), events, null, testIsAdmin);
        adapter.onBindViewHolder(viewHolder, 0);
        EventsTodayAdapter.EventViewHolder viewHolder = new EventsTodayAdapter.EventViewHolder(itemView);

        assertNotNull(viewHolder);
        assertNotNull(viewHolder.eventImage);
        assertNotNull(viewHolder.eventTitle);
        assertNotNull(viewHolder.eventLocation);
        assertNotNull(viewHolder.eventTime);
    }

    @Test
    public void testViewBinding() {
        EventsTodayAdapter adapter = new EventsTodayAdapter(ApplicationProvider.getApplicationContext(), events, null, testIsAdmin);
        adapter.onBindViewHolder(viewHolder, 0);

        assertEquals(event.getTitle(), viewHolder.eventTitle.getText().toString());
        assertEquals(event.getLocation(), viewHolder.eventLocation.getText().toString());
        assertEquals(event.getTime(), viewHolder.eventTime.getText().toString());

    }


    // ------------------- admin_dashboard_adapter -------------------
    @Test
    public void testAdapterCreationforadmin_dashboard() {
        assertNotNull(adapter);
    }

    @Test
    public void testItemCountforadmin_dashboard() {
        assertEquals(dataList.size(), adapter.getItemCount());
    }

    @Test
    public void testViewHolderforadmin_dashboard() {
        ViewGroup parent = new ViewGroup(ApplicationProvider.getApplicationContext()) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {}
        };
        View itemView = LayoutInflater.from(ApplicationProvider.getApplicationContext()).inflate(R.layout.admin_dashboard_content, parent, false);
        AdminDashboardAdapter.ViewHolder viewHolder = new AdminDashboardAdapter.ViewHolder(itemView);

        assertNotNull(viewHolder);
        assertNotNull(viewHolder.textViewName);
        assertNotNull(viewHolder.imageViewAvatar);
        assertNotNull(viewHolder.imageViewDelete);
    }

//    @Test
//    public void testSendNotification() {
//        // Create test data
//        String title = "Test Title";
//        String description = "Test Description";
//
//        // Call the method under test
//        Announcement fragment = new Announcement("events", "Title");
//        fragment.sendNotification(title, description);
//
//
//    }
    @Test
    public void testValidCreation() {
        Attendee attendee = new Attendee("1", "John", "Doe", 0);
        assertEquals("1", attendee.getId());
        assertEquals("John", attendee.getFirstName());
        assertEquals("Doe", attendee.getLastName());
        assertEquals(0, attendee.getCheckedIn());
    }

    @Test
    public void testGettersAndSetters() {
        Attendee attendee = new Attendee("1", "John", "Doe", 0);
        attendee.setId("2");
        attendee.setFirstName("Jane");
        attendee.setLastName("Smith");
        attendee.setCheckedIn(1);
        assertEquals("2", attendee.getId());
        assertEquals("Jane", attendee.getFirstName());
        assertEquals("Smith", attendee.getLastName());
        assertEquals(1, attendee.getCheckedIn());
    }

    @Test()
    public void testNullParameters() {
        try {
            new Attendee(null, null, null, null);
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testEmptyStrings() {
        Attendee attendee = new Attendee("", "", "", 0);
        assertNotNull(attendee.getId());
        assertNotNull(attendee.getFirstName());
        assertNotNull(attendee.getLastName());
    }

    @Test
    public void testNegativeCheckedInValue() {
        Attendee attendee = new Attendee("1", "John", "Doe", 0);
        attendee.setCheckedIn(-1);
        assertNotEquals(0, attendee.getCheckedIn());
    }

    @Test
    public void testMaximumCheckedInValue() {
        Attendee attendee = new Attendee("1", "John", "Doe", 0);
        attendee.setCheckedIn(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, attendee.getCheckedIn());
    }

    @Test
    public void testEqualsMethod() {
        Attendee attendee1 = new Attendee("1", "John", "Doe", 0);
        Attendee attendee2 = new Attendee("1", "John", "Doe", 0);
        Attendee attendee3 = new Attendee("2", "Jane", "Smith", 1);
        assertNotEquals(attendee1, attendee2);
        assertFalse(attendee1.equals(attendee3));
    }

    @Test
    public void testHashCodeMethod() {
        Attendee attendee1 = new Attendee("1", "John", "Doe", 0);
        Attendee attendee2 = new Attendee("1", "John", "Doe", 0);
        Attendee attendee3 = new Attendee("2", "Jane", "Smith", 1);
        assertNotEquals(attendee1.hashCode(), attendee2.hashCode());
        assertNotEquals(attendee1.hashCode(), attendee3.hashCode());
    }




}
