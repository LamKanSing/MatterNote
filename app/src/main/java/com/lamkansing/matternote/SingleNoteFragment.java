package com.lamkansing.matternote;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * View and edit single note in this fragment
 */
public class SingleNoteFragment extends Fragment implements TextWatcher {

    // the fragment initialization parameters
    public static final String ARG_NOTEID = "noteid";
    public static final String ARG_IS_NEWNOTE= "newnote";

    private static final String STATE_EDITVIEW_MODE = "editViewMode";
    private static final String STATE_MODE_EDIT = "edit";
    private static final String STATE_MODE_VIEW = "view";

    // state whether the content is changed
    public static final String PREF_EDITTEXT_CHANGE = "edittextchange";

    private static final String LOG_TAG= "matternote";

    private static final String SHOWCASE_SINGLE_FAB = "showcasesinglefab";
    private static final String SHOWCASE_SINGLE_LONG = "showcasesinglelong";

    private ViewGroup mSceneRoot;

    private String noteTitle = "", noteContent = "";
    private int notebookColor;
    String dateTime;

    TextView showView, titleView;
    FloatingActionButton fab;

    View square;
    EditText editView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @param noteid note's id at db
     * @param newnote whether it is new create note
     * @return A new instance of fragment SingleNoteFragment.
     */
    public static SingleNoteFragment newInstance(String noteid, boolean newnote) {
        SingleNoteFragment fragment = new SingleNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOTEID, noteid);
        args.putBoolean(ARG_IS_NEWNOTE, newnote);

        fragment.setArguments(args);
        return fragment;
    }

    public SingleNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        getActivity().getActionBar().setTitle("");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_single_note_shown_1, container, false);
        mSceneRoot = (ViewGroup) rootView.findViewById(R.id.rootscence);
        showView = (TextView)mSceneRoot.findViewById(R.id.showview);
        editView = (EditText)mSceneRoot.findViewById(R.id.editText1);
        square = mSceneRoot.findViewById(R.id.frameLayout);
        titleView = (TextView)rootView.findViewById(R.id.title);
        fab = (FloatingActionButton)rootView.findViewById(R.id.fab);


        // set the noteid, mode and text-note-save on savedInstanceState
        if (savedInstanceState!=null){
            String mode = savedInstanceState.getString(STATE_EDITVIEW_MODE, "something wrong");

            if (mode.equals(STATE_MODE_EDIT)){
                turnEditMode();
            }
        }

        // change to edit mode if long click show view
        showView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                turnEditMode();
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("matternote", "click catch");

                if (editView.getVisibility() == View.VISIBLE) {
                    String textToSave = editView.getText().toString();

                    int count = saveContent(textToSave);

                    if (count == 1) {
                        // db update success
                        Toast.makeText(getActivity(), R.string.toast_note_updated, Toast.LENGTH_LONG).show();

                        // after saveContent, content on editview up-to-date, change the state
                        markContentNotChange();
                    } else {
                        // db update failure
                        Toast.makeText(getActivity(), R.string.toast_note_update_error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // load the note at db and set it in the view
        loadDB();
        showView.setText(noteContent);
        titleView.setText(noteTitle);


        // edit text is not change at the beginning
        markContentNotChange();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editView.getVisibility() == View.VISIBLE){
            outState.putString(STATE_EDITVIEW_MODE, STATE_MODE_EDIT);
        }else {
            outState.putString(STATE_EDITVIEW_MODE, STATE_MODE_VIEW);
        }

        String noteid = getArguments().getString(ARG_NOTEID, "1");
        outState.putString("noteid", noteid);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().getBoolean(ARG_IS_NEWNOTE)) {
            turnEditMode();
        }else {
            // teach user to turn to edit mode by longclick
            presentEditLngClickShowcaseView(1000);
        }

    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "single notefragment onpause");

        super.onPause();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(LOG_TAG, "afterTExtChanged called");
        // indicate that the edittext is modify
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putBoolean(PREF_EDITTEXT_CHANGE, true);
        ed.commit();
    }

    private int saveContent(String textToSave){
        NotebookDBHelper databaseHelper = new NotebookDBHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        String noteid = getArguments().getString(ARG_NOTEID, "1");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();


        // the note already exist, update note content
        ContentValues values = new ContentValues();
        values.put(NotebookDBHelper.COLUMN_NOTE_CONTENT, textToSave);
        values.put(NotebookDBHelper.COLUMN_LASTEDIT, dateFormat.format(date));

        String selection = NotebookDBHelper.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {noteid};

        int count = database.update(
                NotebookDBHelper.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        database.close();

        Log.d("matternote", "the count is " + count);

        return count;
    }

    private void turnEditMode(){
        Activity mActivity = getActivity();
        if (mActivity!=null && mActivity.getActionBar() !=null){
            mActivity.getActionBar().setTitle(noteTitle);
        }

        // teach user to save content by fab
        presentFabShowcaseView(1000);

        // set the edittext visibile for longclick
        TransitionManager.beginDelayedTransition(mSceneRoot);
        square = mSceneRoot.findViewById(R.id.frameLayout);
        ViewGroup.LayoutParams params = square.getLayoutParams();
        int newSize = 0;
        params.height = newSize;
        square.setLayoutParams(params);

        String text = showView.getText().toString();
        editView.setText(text);

        showView.setVisibility(View.GONE);
        editView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        // watch whether content in editview change
        editView.addTextChangedListener(this);
    }

    private void loadDB() {
        NotebookDBHelper databaseHelper = new NotebookDBHelper(getActivity());
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String noteid = getArguments().getString(ARG_NOTEID, "1");
        Cursor cursor = database.query(
                NotebookDBHelper.TABLE_NAME,
                null,
                NotebookDBHelper.COLUMN_ID + "=?",
                new String[]{noteid}, null, null, null
        );

        if (cursor!=null && cursor.getCount()!=0){
            cursor.moveToFirst();
            int colNoteTitle = cursor.getColumnIndex(NotebookDBHelper.COLUMN_NOTEBOOK_NAME);
            int colNoteContent = cursor.getColumnIndex(NotebookDBHelper.COLUMN_NOTE_CONTENT);
            int colLastEditDate = cursor.getColumnIndex(NotebookDBHelper.COLUMN_LASTEDIT);
            int colColor = cursor.getColumnIndex(NotebookDBHelper.COLUMN_COVER_COLOR);

            noteTitle = cursor.getString(colNoteTitle);
            noteContent = cursor.getString(colNoteContent);
            notebookColor = cursor.getInt(colColor);
            dateTime = cursor.getString(colLastEditDate);
            cursor.close();
        }

        database.close();
    }

    private void markContentNotChange(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putBoolean(PREF_EDITTEXT_CHANGE, false);
        ed.commit();
    }

    private void presentFabShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(fab)
                .setDismissText("GOT IT")
                .setContentText("Save the content by clicking")
                .setDelay(withDelay)
                .singleUse(SHOWCASE_SINGLE_FAB)
                .show();
    }

    private void presentEditLngClickShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(editView)
                .setDismissText("GOT IT")
                .setContentText("Long Click to edit content")
                .setDelay(withDelay)
                .singleUse(SHOWCASE_SINGLE_LONG)
                .show();
    }
}
