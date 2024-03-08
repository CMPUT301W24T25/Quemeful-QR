package com.android.quemeful_qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static org.mockito.Mockito.verify;

import android.content.Intent;
import android.widget.Button;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import com.android.quemeful_qr.CreateNewEventActivity;
import com.android.quemeful_qr.Events;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class EventsTest {

    @Test
    public void testEventCreationButton() {
        // Launch the fragment
        FragmentScenario<Events> scenario = launchInContainer(Events.class);
        scenario.moveToState(Lifecycle.State.STARTED).onFragment(fragment -> {
            // Simulate button click
            Button createEventButton = fragment.getView().findViewById(R.id.create_event_button);
            createEventButton.performClick();
        });
    }
}
