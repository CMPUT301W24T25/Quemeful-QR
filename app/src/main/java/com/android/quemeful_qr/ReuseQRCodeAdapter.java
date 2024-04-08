package com.android.quemeful_qr;

import static android.service.controls.ControlsProviderService.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a custom adapter class associated with the ReuseQRCodeFragment,
 * used to handle the re-use QR codes data.
 */
public class ReuseQRCodeAdapter extends RecyclerView.Adapter<ReuseQRCodeAdapter.ReuseViewHolder> {

    // initialize the list to store all the generated event check-in QR code's uri strings
    List<String> checkInUriList = new ArrayList<>();

    // initialize firebase db and reference to upload the selected QR code uri to the respective document as field
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference reference = db.collection("events");

    String eventId, selectedUri;

    // item click listener for getting the selected QR code uri string from the list
    private onUriItemClickListener listener;
    private onCheckInQRExistListener listener1;

    /**
     * This is a interface to listen to the selected item on the check-in QR codes' uri string list.
     */
    public interface onUriItemClickListener {
        void onUriItemClick(View view, int position, String uri);
    }

    /**
     * This is a method implementing the item click listener interface.
     * @param listener listener to get the selected QR code uri string.
     */
    public void setOnUriItemClickListener(onUriItemClickListener listener) {
        this.listener = listener;
    }

    public interface onCheckInQRExistListener {
        void onCheckInQRExist();
    }

    public void setOnCheckInQRExistListener(onCheckInQRExistListener listener1) {
        this.listener1 = listener1;
    }

    /**
     * This is a constructor for this adapter with eventId as parameter.
     * @param EventId the event with the specific eventId passed by the fragment to the adapter.
     */
    public ReuseQRCodeAdapter(String EventId){
        // assign the eventId value to the global instance for access by other methods
        eventId = EventId;
        // retrieve all documents in the collection
        reference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document:task.getResult()) {
                    // get the check in QR code field from each document
                    String checkInUri = document.getString("CheckIn QR Code");
                    if(checkInUri != null){
                        checkInUriList.add(checkInUri); // add the uri to the list
                        notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "no CheckIn QR Code field found");
                    }
                }
            } else {
                Log.d(TAG, "Could not retrieve documents");
            }
        });
    }

    /**
     * This is getter method for eventId within this adapter,
     * to access the eventId passed by the fragment, in other methods of this class.
     * @return the eventId
     */
    public String getEventId(){
        return eventId;
    }

    /**
     * This method is used to inflate and set the view for each item of the list.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return item view
     */
    @NonNull
    @Override
    public ReuseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_view_reuse_qr_items,parent,false);
        return new ReuseViewHolder(itemView);
    }

    /**
     * This method is used to bind data to the view for each item of the list.
     * (presenting/customizing of each list item, how that is to be displayed to the user)
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReuseViewHolder holder, int position) {
        if(!checkInUriList.isEmpty()) {
            // bind the selected uri to the xml recycler view
            holder.uriItemText.setText(String.format("Event QR Code %d", (position + 1)));
        } else {
            Log.d(TAG, "existing QR code's list is empty");
        }
    }

    /**
     * This method is used to get the size of the list.
     * @return size
     */
    @Override
    public int getItemCount() {
        int size = 0;
        if(checkInUriList != null) {
            size =  checkInUriList.size();
        } else {
            Log.d(TAG, "check in uri list is empty");
        }
        return size;
    }

    /**
     * This is the view holder child class used to initialize all view instances,
     * get the selected QR uri string, and
     * add it to the associated event with eventId document in the firebase,
     * by calling the uploadToFirebase method.
     */
    public class ReuseViewHolder extends RecyclerView.ViewHolder {
        TextView uriItemText;
        String EventId;
        public ReuseViewHolder(@NonNull View itemView) {
            super(itemView);
            // initialize the xml variables
            uriItemText = itemView.findViewById(R.id.existing_eventQR);
            // retrieve the eventId for the newly created event
            EventId = getEventId();
            // handle item click listener
            itemView.setOnClickListener(v -> {
                if(listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        selectedUri = checkInUriList.get(position);
                        // pass the selected uri to the listener for further display
                        listener.onUriItemClick(itemView, position, selectedUri);
                        // for this selected uri item,
                        // use this to update event document with eventId as field CheckIn QR Code
                        uploadToFirebase(selectedUri, EventId);
                    }
                }
            });
        }

        /**
         * This method is used to add the selected QR code uri as the check in Qr code field
         * for the associated event document in the firebase.
         * @param selectedUri the QR code uri string selected from the list of check-in qr code uri strings.
         * @param event_id the event id by which the associated event is identified.
         */
        public void uploadToFirebase(String selectedUri, String event_id){
        // use the eventId to check if field CheckIn QR Code is null, if null add, if not display toast message
            // get the document with the event id.
            reference.document(event_id).get().addOnSuccessListener(documentSnapshot -> {
                // if document trying to retrieve exists
                if(documentSnapshot.exists()){
                    EventHelper event = documentSnapshot.toObject(EventHelper.class);
                    if (event != null) {
                        // if the event exists retrieve data
                        if(documentSnapshot.getData() != null) {
                            // assign the data to a compatible type variable
                            Map<String, Object> eventData = new HashMap<>(documentSnapshot.getData());
                            // retrieve the CheckIn QR Code field
                            String checkInQR = (String) eventData.get("CheckIn QR Code");
                            // if the field already exists ie. not null then display a toast
                            if(checkInQR != null) {
                                Toast.makeText(itemView.getContext(), "CheckIn QR Code Already Exists", Toast.LENGTH_SHORT).show();
                                listener1.onCheckInQRExist();
                            } else {
                                // if the field does not exist add a new field
                                Map<String, Object> eventCheckIn = new HashMap<>();
                                eventCheckIn.put("CheckIn QR Code", selectedUri);
                                db.collection("events")
                                        .document(event_id)
                                        .update(eventCheckIn)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG,"Event successfully updated with check in QR field."))
                                        .addOnFailureListener(e -> {
                                    // handle fail to update event document with specific eventId
                                    Log.d(TAG, "failed to add event check in QR Code field to document eventId in events collection.");
                                });
                            }
                } // handle else if documentSnapshot.getData() is null
            } else {
                        // display toast if the event is not found in the database
                        Toast.makeText(itemView.getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    }
        } // handle else if documentSnapshot does not exist
    }).addOnFailureListener(e -> {
        // handle fail to get document with event_id
        });

} // uploadToFirebase method closing

    } // ReuseQRViewHolder class closing

} // adapter closing
