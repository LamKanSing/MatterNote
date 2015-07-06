package com.lamkansing.matternote;

import android.test.ActivityInstrumentationTestCase2;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import android.support.test.espresso.action.ViewActions;

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

    public void testCreateNewNotebook(){
        onView(withId(R.id.fabaddnotebook)).check(matches(isDisplayed()));
        onView(withId(R.id.fabaddnotebook)).perform(ViewActions.click());

        // on dialog fragment, enter new notebook name
        onView(withId(R.id.editText_notebookname)).check(matches(isDisplayed()));
        onView(withId(R.id.editText_notebookname)).perform(ViewActions.typeText("testing"));
        onView(withText("save")).perform(ViewActions.click());
    }

    /*
    public void testClickListNotebook(){
        onData(allOf(is(instanceOf(String.class)), is("Americano")))
                .perform(click());
    }*/
}