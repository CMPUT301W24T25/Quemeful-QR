package com.android.quemeful_qr;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.os.Build;

/**
 * Tests are ran in order just so a new activity doesn't have to open
 * everytime and it runs consecutively without repeating blocks of code
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Testing opening of app and entering main page
     */
    @Test
    public void a_testNoLogin() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.SYSTEM_ALERT_WINDOW");
        }

        onView(withId(R.id.getStartedButton)).check(matches(isDisplayed()));
        onView(withId(R.id.getStartedButton)).perform(click());
    }

    /**
     * Testing display of all events on home page
     */
    @Test
    public void b_testHomeFragment() {
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
        onView(withId(R.id.textViewDashboard)).check(matches(isDisplayed()));
    }

    /**
     * Testing click on an event on home page and going back to home page
     */
    @Test
    public void c_testEventToday() {
        onView(withId(R.id.eventsRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.eventsRecyclerView)).perform(click());
        onView(withId(R.id.backArrow)).perform(click());
        //onView(withId(R.id.eventsRecyclerView)).check(doesNotExist());
        //onView(withId(R.id.textViewEventPageTitle)).check(matches(isDisplayed()));
    }

    /**
     * Testing switching to second fragment -- Event page
     */
    @Test
    public void d_testEventFragment() {
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()));
        onView(withId(R.id.create_event_button)).check(doesNotExist());
        onView(withId(R.id.event_icon)).perform(click());
        onView(withId(R.id.create_event_button)).check(matches(isDisplayed()));
    }

    /**
     * Testing creation of new event with input validation and QR code generation
     */
    @Test
    public void e_testCreateEvent() {
        onView(withId(R.id.event_icon)).perform(click());
        onView(withId(R.id.create_event_button)).perform(click());
        onView(withId(R.id.header)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_button)).perform(click());

        onView(withId(R.id.create_event_button)).perform(click());
        onView(withId(R.id.enter_title)).perform(typeText("Event Title Test"));
        //onView(withId(R.id.add_poster_button)).perform(click());

        onView(withId(R.id.enter_event_details)).perform(typeText("Event Description Test"));
        onView(withId(R.id.enter_startDate)).perform(typeText("xxxx2024-03-08"));
        onView(withId(R.id.enter_endDate)).perform(typeText("xxxx2024-03-09"));
        onView(withId(R.id.enter_startTime)).perform(typeText("xxxx900am"));
        onView(withId(R.id.enter_endTime)).perform(typeText("900pm"));
        Espresso.closeSoftKeyboard();

//        onView(withId(R.id.QR_generate_button_for_createEvent)).perform(click());
//        onView(withId(R.id.EnterText)).perform(typeText("Event QR Test"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.generate)).perform(click());
//        onView(withId(R.id.backTool)).perform(click());
//        onView(withId(R.id.create_button)).check(matches(isDisplayed()));
    }

    /**
     * Testing switching to third fragment -- Profile page
     */
    @Test
    public void f_testProfileFragment() {
        onView(withId(R.id.editProfileButton)).check(doesNotExist());
        onView(withId(R.id.prof_icon)).perform(click());

        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
        onView(withId(R.id.editAvatarTextView)).check(doesNotExist());

        onView(withId(R.id.editProfileButton)).perform(click());
        onView(withId(R.id.editAvatarTextView)).check(matches(isDisplayed()));
    }

    /**
     * Testing editing of profile
     */
    @Test
    public void g_testEditProfile() {
        onView(withId(R.id.prof_icon)).perform(click());
        onView(withId(R.id.editProfileButton)).perform(click());

        onView(withId(R.id.backArrowImageView)).perform(click());
        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileButton)).perform(click());

        onView(withId(R.id.firstNameEditText)).perform(click());
        onView(withId(R.id.firstNameEditText)).perform(clearText());
        onView(withId(R.id.firstNameEditText)).perform(typeText("First Name Test"));

        onView(withId(R.id.lastNameEditText)).perform(click());
        onView(withId(R.id.lastNameEditText)).perform(clearText());
        onView(withId(R.id.lastNameEditText)).perform(typeText("Last Name Test"));

        onView(withId(R.id.editProfileButton)).perform(click());
        onView(withId(R.id.editProfileButton)).check(matches(isDisplayed()));
    }

    /**
     * Testing turning notifications off and on in profile
     */
    @Test
    public void g_testNotificationSwitch() {
        onView(withId(R.id.prof_icon)).perform(click());
        onView(withId(R.id.notificationSwitch)).perform(click());
        onView(withId(R.id.notificationSwitch)).check(matches(isChecked()));
        onView(withId(R.id.notificationSwitch)).perform(click());
        onView(withId(R.id.notificationSwitch)).check(matches(isNotChecked()));
    }
}
