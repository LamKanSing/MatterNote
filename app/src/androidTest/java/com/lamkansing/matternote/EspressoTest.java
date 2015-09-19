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
import static android.support.test.espresso.action.ViewActions.longClick;
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
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest {

    String newNotebookName;
    String newContent = "new Content";

    @Rule
    public ActivityTestRule<Notebooks> mActivityRule =
            new ActivityTestRule<>(Notebooks.class);

    @Before
    public void initialize() {

        newNotebookName = genSevenRanChar();

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
        onView(withId(R.id.editText1)).perform(typeText(newContent), closeSoftKeyboard());

        // closeSoftKeyboard() take time
        try{
        Thread.sleep(1000);
        } catch (Exception e){

        }
        // save new content
        onView(withId(R.id.fab)).check(matches(isClickable()));
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



    }

    public void updateContent(){
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, newNotebookName))
                .check(matches(isDisplayed()));

        // display the content need update
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, newNotebookName))
                .perform(click());

        // if the content is too long to the listview, it cannot check
        onView(withId(R.id.singlelinetextview)).check(LayoutAssertions.noEllipsizedText());
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTE_CONTENT, newContent))
                .check(matches(isDisplayed()));
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTE_CONTENT, newContent))
                .perform(click());

        // showview and title is displayed and have proper content, editview gone
        checkShowMode();
        onView(withId(R.id.title)).check(matches(withText(newNotebookName)));
        onView(withId(R.id.showview)).check(matches(withText(newContent)));

        onView(withId(R.id.showview)).perform(longClick());

        checkEditMode();

        // update content at edittext
        String update = genSevenRanChar();
        onView(withId(R.id.editText1)).perform(typeText(update),
                closeSoftKeyboard());
        onView(withId(R.id.fab)).perform(click());

        // the content on the list updated
        Espresso.pressBack();
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTE_CONTENT, newContent + update))
                .check(matches(isDisplayed()));
    }

    @Test
    public void updateNotSaved(){
        // todo add a toast to let user know content not saved
    }

    @Test
    public void newContentOnExistNotebook(){
        // add new Content on the notebook "testing 88"
        String targetNotebookName = "testing 88";
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, targetNotebookName))
                .check(matches(isDisplayed()));
        onData(CursorMatchers.withRowString(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, targetNotebookName))
                .perform(click());

        onView(withId(R.id.fabnotelist)).perform(click());

        // check the app in edit mode
        checkEditMode();


    }

    private String genSevenRanChar(){
        // random gen 7 characters
        Random r = new Random();
        char c ;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<7; i++){
            c = (char)(r.nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

    private void checkEditMode(){
        // action button and edittext Visibile
        onView(withId(R.id.title)).check(matches(not(isDisplayed())));
        onView(withId(R.id.showview)).check(matches(not(isDisplayed())));
        onView(withId(R.id.editText1)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    private void checkShowMode(){
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.showview)).check(matches(isDisplayed()));
        onView(withId(R.id.editText1)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())));

    }


}
