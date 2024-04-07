package com.android.quemeful_qr;

import static android.service.controls.ControlsProviderService.TAG;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LimitAttendeeDialogFragment extends DialogFragment {

    String attendeeLimit, eventId;

    public LimitAttendeeDialogFragment(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_limit_attendee_dialog, container, false);

        TextInputEditText limitInput = view.findViewById(R.id.enter_limit);
        limitInput.setOnClickListener(v -> {
            attendeeLimit = limitInput.getText().toString();
        });

        Button saveLimit = view.findViewById(R.id.save_limit_button);
        saveLimit.setOnClickListener(v -> {
            // save the limit to firebase
            AddLimitToFirebase(attendeeLimit, eventId);
        });
        return view;
    }

    private void AddLimitToFirebase(String attendeeLimit, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(eventId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    EventHelper event = documentSnapshot.toObject(EventHelper.class);
                    if (event != null) {
                        // if the event exists retrieve data
                        if (documentSnapshot.getData() != null) {
                            // assign the data to a compatible type variable
                            Map<String, Object> eventData = new HashMap<>(documentSnapshot.getData());
                            String limitForAttendee = (String) eventData.get("Attendee Limit");
                            if (limitForAttendee == null) {
                                Map<String, Object> limit = new HashMap<>();
                                limit.put("Attendee Limit", attendeeLimit);
                                db.collection("events")
                                        .document(eventId)
                                        .update(limit)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully updated with check in QR field."))
                                        .addOnFailureListener(e -> {
                                            // handle fail to update event document with specific eventId
                                            Log.d(TAG, "failed to add event check in QR Code field to document eventId in events collection.");
                                        });
                            } else {
                            }
                        }

                    }
                });
    }
}