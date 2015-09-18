package com.lamkansing.matternote;

import android.app.Instrumentation;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.CursorMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.espresso.assertion.LayoutAssertions;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;

import static android.support.test.espresso.Espresso.onData;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest {

    String newNotebookName;

    @Rule
    public ActivityTestRule<Notebooks> mActivityRule =
            new ActivityTestRule<>(Notebooks.class);

    @Before
    public void initialize() {
        // random gen 7 characters as new notebook name
        Random r = new Random();
        char c ;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<7; i++){
            c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }

        newNotebookName = sb.toString();

        Log.d("espresso test", "newNotebookName is " + newNotebookName);
    }

    @Test
    public void testNotebook(){
        createNewNotebookAndNewContent();
        updateContent();
    }

    public void createNewNotebookAndNewContent() {
        onView(withId(R.id.fabaddnotebook)).check(matches(isDisplayed()));
        onView(withId(R.id.fabaddnotebook)).perform(click());

        onView(withId(R.id.editText_notebookname)).check(matches(isDisplayed()));
        onView(withId(R.id.editText_notebookname)).perform(typeText(newNotebookName),
                closeSoftKeyboard());
        onView(withText("save")).perform(click());

        // check the edit area is ok
        onView(withId(R.id.editText1)).check(matches(isDisplayed()));
        onView(withId(R.id.editText1)).check((matches(withHint(R.string.edit_text_hint))));

        // add new content
        onView(withId(R.id.editText1)).perform(typeText("new content"), closeSoftKeyboard());

        // closeSoftKeyboard() take time
        try{
        Thread.sleep(1000);
        } catch (Exception e){

        }
        // save new content
        onView(withId(R.id.fab)).check(matches(isClickable()));
        ////

        onView(withId(R.id.fab)).perform(click());


        // pass back and new content on the list
        // ViewAction.pressBack don't work
        Espresso.pressBack();

        // new content on the list
        onView(withId(R.id.notebookList)).check(matches(isDisplayed()));

        // todo how abot delete
        try{
            Thread.sleep(1000);
        } catch (Exception e){

        }

        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, newNotebookName))
                .check(matches(isDisplayed()));


        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, newNotebookName))
                .perform(click());
        onView(withId(R.id.singlelinetextview)).check(LayoutAssertions.noEllipsizedText());
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTE_CONTENT, "new content"))
                .check(matches(isDisplayed()));
    }

    /*
    @Test
    public void updateContent(){
        // one by one or what???/??

        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, newNotebookName))
                .check(matches(isDisplayed()));

        // display the content need update
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, newNotebookName))
                .perform(click());
        // if the content is too long to the listview, it cannot check
        /// what are you doing.....
        onView(withId(R.id.singlelinetextview)).check(LayoutAssertions.noEllipsizedText());

        //onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTE_CONTENT, newNotebookName))
        //        .check(LayoutAssertions.noEllipsizedText());
        //onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTE_CONTENT, newNotebookName))
        //        .check(matches(withText("new content")));


        // compare content on view and edit mode

    }*/

    @Test
    public void updateNotSaved(){
        // todo add a toast to let user know content not saved
    }

    @Test
    public void newContentOnExistNotebook(){

    }


}
