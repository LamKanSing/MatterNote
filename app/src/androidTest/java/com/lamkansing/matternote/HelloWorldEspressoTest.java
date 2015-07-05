package com.lamkansing.matternote;

import android.test.ActivityInstrumentationTestCase2;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

/**
 * Created by line on 7/5/15.
 */
public class HelloWorldEspressoTest extends ActivityInstrumentationTestCase2<Notebooks> {

    public HelloWorldEspressoTest() {
        super(Notebooks.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testListGoesOverTheFold() {
        onView(withText("Hello world!")).check(matches(isDisplayed()));
    }
}