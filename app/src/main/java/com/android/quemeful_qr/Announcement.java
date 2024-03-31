package com.android.quemeful_qr;

import static androidx.test.InstrumentationRegistry.getContext;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This is a fragment class representing an announcement posting feature.
 * Users can input a title and description for an announcement and post it,
 * triggering a notification to be sent to a specific topic.
 */
public class Announcement extends Fragment {
    // The ID of the event/topic to which the notification will be sent.
    private String EventId;
    private String EventName;


    /**
     * This a constructor with EventId as parameter.
     * @param EventId The ID of the event/topic to which the notification will be sent.
     */
    public Announcement(String EventId, String EventName) {
        this.EventId = EventId;
        this.EventName = EventName;
    }

    /**
     * This method creates a new instance of the announcement fragment with the specified event ID.
     * @param EventId The ID of the event/topic to which the notification will be sent.
     * @return A new instance of the announcement Fragment.
     */
    public static Announcement newInstance(String EventId, String EventName) {

        return new Announcement(EventId, EventName);
    }

    /**
     * This is the onCreate method of this fragment class.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is used to create the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState This fragment's previously saved state, if any.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        TextInputEditText titleTextInput = view.findViewById(R.id.Title_text_input);
        TextInputEditText descriptionTextInput = view.findViewById(R.id.descriptionTextInput);

        Button post = view.findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            private sendNotif sendNotif = new sendNotif();

            @Override
            public void onClick(View v) {
                String title = titleTextInput.getText().toString();
                String description = descriptionTextInput.getText().toString();
                boolean response =  this.sendNotif.sendNotification(title ,description, "/topics/" + EventId, EventName);
                if ( response ) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Notification sent successfully", Toast.LENGTH_SHORT).show());
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "An Error Occurred. Please try again later.", Toast.LENGTH_SHORT).show());
                }



                titleTextInput.getText().clear();
                descriptionTextInput.getText().clear();
                DocumentReference notifs = FirebaseFirestore.getInstance().collection("events").document(EventId);
                Map<String,String> notification = new HashMap<>();
                notification.put("title", title);
                notification.put("body",description);
                notification.put("Name", EventName);
                LocalDateTime currentDate = LocalDateTime.now();
                DateTimeFormatter currrentDateFormatted = DateTimeFormatter.ofPattern("dd MMM HH:mm");
                String formattedDateTime = currentDate.format(currrentDateFormatted);
                notification.put("date", formattedDateTime);
                notifs.update("notifications", FieldValue.arrayUnion(notification));


            }
        });

        ImageButton back_button = view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });
        return view;
    }


} // Announcement fragment class closing
