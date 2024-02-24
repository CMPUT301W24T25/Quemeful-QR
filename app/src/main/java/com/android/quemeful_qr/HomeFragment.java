//https://stackoverflow.com/a/42428384 Daniel Garcia. CC BY-SA 3.0. Published Feb 23, 2017. Accessed Feb 22, 2024.
//https://stackoverflow.com/a/46438757 EJK. CC BY-SA 3.0. Published Sept 27, 2017. Accessed Feb 22, 2024.
//https://stackoverflow.com/a/52902533 Alex Mamo. CC BY-SA 4.0. Published Oct 20, 2018. Accessed Feb 22, 2024.
package com.android.quemeful_qr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    Button scanQR_button;
    TextView textView;

    EditText addEventEditText;
    Button generateQR_button;
    ImageView imageView;
    private FirebaseFirestore db;

    private CollectionReference eventsRef;
    ArrayList<Event> eventDataList;

    private Button deleteEventButton;
    private String eventUUID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(error != null){
//                    Log.e("Firestore", error.toString());
//                    return;
//                }
//                if(querySnapshots != null) {
//                    eventDataList.clear();
//                    for(QueryDocumentSnapshot doc: querySnapshots){
//                        String eventUUID = doc.getId();
//                        String eventName = doc.getString("Event Name");
//                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventUUID, eventName));
//                        eventDataList.add(new Event(eventUUID, eventName));
//                    }
//
//                }
//            }
//        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home,null);
//        setContentView(R.layout.fragment_home);
        Button addEventButton = view.findViewById(R.id.add_event_button);
        scanQR_button = view.findViewById(R.id.scanQR);
        textView = view.findViewById(R.id.text);
        addEventEditText = view.findViewById(R.id.add_event_edit_text);
        generateQR_button = view.findViewById(R.id.generateQR);
        imageView = view.findViewById(R.id.qr_code);
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        eventDataList = new ArrayList<>();


        addEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                eventUUID = UUID.randomUUID().toString();
                String eventName = addEventEditText.getText().toString();
                Event event = new Event(eventUUID, eventName);
                addNewEvent(event);
            }

        });


        generateQR_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try {
                    String uuid = UUID.randomUUID().toString(); //make random uuid
                    BitMatrix bitMatrix = multiFormatWriter.encode(uuid,
                            BarcodeFormat.QR_CODE, 300,300);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                    addEventEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                } catch(WriterException e){
                    throw new RuntimeException(e);
                }
            }
        });
        scanQR_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan a QR code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();

            }
        });
        return view;




    }

    private void addNewEvent(Event event) {
// Add the event to the Firestore collection
//        String id = db.collection("events").document().getId();
//        db.collection("events").document(id).set(event);

        String eventName = addEventEditText.getText().toString();
        if (eventUUID.matches("") || eventName.matches("")){ //empty string
            Toast myToast = Toast.makeText(getActivity(), "please enter an event", Toast.LENGTH_SHORT);
            myToast.show();
        } else {
            HashMap<String, String> data = new HashMap<>();
            data.put("Event UUID", event.getEventUUID());
            data.put("Event Name", event.getEventName());
            eventsRef
                    .document(db.collection("events").document().getId())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firestore", "DocumentSnapshot successfully written!");
                            Log.d("UUID", "the UUID is " + event.getEventUUID());
                        }
                    });
            addEventEditText.setText(""); //clears the editText for next entry
            addEventEditText.requestFocus(); // sets cursor back to addCityEditText
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);

        if (intentResult != null) {
            String contents = intentResult.getContents();
            if (contents != null) {
                textView.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }
}