package com.android.quemeful_qr;

import static android.service.controls.ControlsProviderService.TAG;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LimitAttendeeDialogFragment extends DialogFragment {

    String attendeeLimit, eventId;
    private TextInputEditText limitInput;

    public LimitAttendeeDialogFragment(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_limit_attendee_dialog, container, false);

        limitInput = view.findViewById(R.id.enter_limit);
        // get the user input limit
        attendeeLimit = limitInput.getText().toString();

        Button saveLimit = view.findViewById(R.id.save_limit_button);
        saveLimit.setOnClickListener(v -> {
            // save the limit to firebase
            AddLimitToFirebase(attendeeLimit, eventId);
            dismiss();
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
                                // display dialog
                                showUpdateLimitDialog();
                            }
                        }

                    }
                });
    }

    /**
     * This method is used to display a dialog to the user if user wants to update the attendee limit,
     * when the attendee limit field in firebase already exists.
     */
    private void showUpdateLimitDialog() {
        if(getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("An attendee limit already exists for this Event");
            builder.setMessage("Do you want to update limit?");
            builder.setPositiveButton("Update", (dialog, which) -> {
                // update firebase
                String updatedLimit = limitInput.getText().toString();
                AddLimitToFirebase(updatedLimit, eventId);
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                // don't update dismiss
                dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}