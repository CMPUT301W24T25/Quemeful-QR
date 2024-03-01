package com.android.quemeful_qr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class FirebaseActivity extends AppCompatActivity {
    Button scanQR_button;
    TextView textView;

    EditText addEventEditText;
    Button generateQR_button;
    ImageView imageView;
    private FirebaseFirestore db;

    private CollectionReference eventsRef;
    ArrayList<Event> eventDataList;
    private Button addEventButton;

    private Button deleteEventButton;
    private String eventUUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        addEventButton = findViewById(R.id.add_event_button);
        scanQR_button = findViewById(R.id.scanQR);
        textView = findViewById(R.id.text);
        addEventEditText = findViewById(R.id.add_event_edit_text);
        generateQR_button = findViewById(R.id.generateQR);
        imageView = findViewById(R.id.qr_code);
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        eventDataList = new ArrayList<>();


        addEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                eventUUID = UUID.randomUUID().toString();
                String eventName = addEventEditText.getText().toString();
                Event event = new Event(eventUUID, eventName,"imageuri");
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
                IntentIntegrator intentIntegrator = new IntentIntegrator(FirebaseActivity.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan a QR code");
                intentIntegrator.setPrompt("Scan a QR code");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();

            }
        });




    }

    private void addNewEvent(Event event) {
// Add the event to the Firestore collection

        String eventName = addEventEditText.getText().toString();
        if (eventUUID.matches("") || eventName.matches("")){ //empty string
            Toast myToast = Toast.makeText(FirebaseActivity.this, "please enter an event", Toast.LENGTH_SHORT);
            myToast.show();
        } else {
            HashMap<String, Object> data = new HashMap<>();
//            data.put("Event QR id", event.getEventUUID());
            HashMap<String, String> nestedData = new HashMap<>();
            nestedData.put("Event Name", event.getEventName());
            nestedData.put("Event Date", "14-03-2024");
            data.put(event.getEventUUID(), nestedData);


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
