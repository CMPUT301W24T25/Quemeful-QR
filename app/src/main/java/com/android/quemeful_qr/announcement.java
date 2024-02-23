package com.android.quemeful_qr;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


import com.google.android.material.textfield.TextInputEditText;


import java.util.HashMap;

public class announcement extends Fragment {

    private String Eventid;

    public announcement(String Eventid) {
        this.Eventid = Eventid;
    }

    public static announcement newInstance(String Eventid) {
        return new announcement(Eventid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        TextInputEditText titleTextInput = view.findViewById(R.id.Title_text_input);
        TextInputEditText descriptionTextInput = view.findViewById(R.id.descriptionTextInput);

        Button post = view.findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTextInput.getText().toString();
                String description = descriptionTextInput.getText().toString();


                FCMSend.Builder build = new FCMSend.Builder(Eventid, true)
                        .setTitle("<Title>")
                        .setBody("<Message>");
                build.send();
            }
        });
        return view;
    }



}
