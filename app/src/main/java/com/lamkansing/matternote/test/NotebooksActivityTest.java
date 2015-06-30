package com.lamkansing.matternote.test;

import android.test.ActivityInstrumentationTestCase2;

import com.lamkansing.matternote.Notebooks;
import com.melnykov.fab.FloatingActionButton;
import com.lamkansing.matternote.R;

/**
 * Created by line on 6/29/15.
 */
public class NotebooksActivityTest extends ActivityInstrumentationTestCase2<Notebooks>{

    private Notebooks mFirstTestActivity;
    private FloatingActionButton fabaddnotebook;


    public NotebooksActivityTest(){
        super(Notebooks.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mFirstTestActivity = getActivity();
        fabaddnotebook = (FloatingActionButton)mFirstTestActivity.findViewById(R.id.fabaddnotebook);
    }

    public void test_preconditions() {
        assertNotNull("mFirstTestActivity is null", mFirstTestActivity);
        assertNotNull("fab is null", fabaddnotebook);
    }



}
