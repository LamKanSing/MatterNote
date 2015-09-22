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
import android.text.method.ScrollingMovementMethod;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.*;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleNoteFragment extends Fragment implements TextWatcher {

    public static final String ARG_NOTEID = "noteid";
    public static final String ARG_IS_NEWNOTE= "newnote";

    private static final String STATE_EDITVIEW_MODE = "editViewMode";
    private static final String STATE_MODE_EDIT = "edit";
    private static final String STATE_MODE_VIEW = "view";

    public static final String PREF_EDITTEXT_CHANGE = "edittextchange";

    private static final String TAG_NAME= "matternote";

    private ViewGroup mSceneRoot;
    //private Scene mScene2;

    private SQLiteDatabase database;

    private String noteTitle = "", noteContent = "";
    private int notebookColor;
    String dateTime;


    TextView showView, titleView;
    FloatingActionButton fab;

    View square;
    EditText editView;

    // todo pls fix it..
    /*09-20 01:39:56.770  13449-13449/com.lamkansing.matternote W/IInputConnectionWrapper﹕ showStatusIcon on inactive InputConnection
09-20 01:40:01.891  13449-13463/com.lamkansing.matternote W/SQLiteConnectionPool﹕ A SQLiteConnection object for database '/data/data/com.lamkansing.matternote/databases/notebook.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed
*/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
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

        showView.setMovementMethod(new ScrollingMovementMethod());


        // set the noteid, mode and text-note-save on saveedInstanceState
        if (savedInstanceState!=null){
            String mode = savedInstanceState.getString(STATE_EDITVIEW_MODE, "something wrong");

            if (mode.equals(STATE_MODE_EDIT)){
                turnEditMode();
            }
        }

        showView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                turnEditMode();

                return true;

            }
        });

        // todo the scroll don't work stable on emsumlater, check it at real devcies
        // delete it or not
        // it don't work on real device
        showView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.d(TAG_NAME, "on scroll changed");

                // enlarge the textview to let more space for text
                TransitionManager.beginDelayedTransition(mSceneRoot);
                ViewGroup.LayoutParams params = square.getLayoutParams();
                int newSize = 0;
                params.height = newSize;
                square.setLayoutParams(params);


            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("matternote", "click catch");

                if (editView.getVisibility() == View.VISIBLE){
                    String textToSave = editView.getText().toString();
                    NotebookDBHelper databaseHelper = new NotebookDBHelper(getActivity());
                    database = databaseHelper.getWritableDatabase();

                    String noteid = getArguments().getString(ARG_NOTEID, "1");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();


                    // the note already exist, update note content
                    ContentValues values = new ContentValues();
                    values.put(NotebookDBHelper.COLUMN_NOTE_CONTENT,textToSave);
                    values.put(NotebookDBHelper.COLUMN_LASTEDIT, dateFormat.format(date));

                    String selection = NotebookDBHelper.COLUMN_ID + " LIKE ?";
                    String[] selectionArgs = { noteid };

                    int count = database.update(
                                NotebookDBHelper.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);

                    Log.d("matternote", "the count is " + count);

                    if (count == 1){
                        // db update success
                        Toast.makeText(getActivity(),R.string.toast_note_updated, Toast.LENGTH_LONG).show();

                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = sharedPref.edit();
                        ed.putBoolean(PREF_EDITTEXT_CHANGE, false);
                        ed.commit();
                    }else {
                        // db update failure
                        Toast.makeText(getActivity(),R.string.toast_note_update_error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        loadDB();
        showView.setText(noteContent);
        titleView.setText(noteTitle);

        // edit text is not change at the beginning
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putBoolean(PREF_EDITTEXT_CHANGE, false);
        ed.commit();

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
        }

    }

    @Override
    public void onPause() {
        if (database!=null)
            database.close();
        super.onPause();
    }


    void turnEditMode(){
        Activity mActivity = getActivity();
        if (mActivity!=null && mActivity.getActionBar() !=null){
            mActivity.getActionBar().setTitle(noteTitle);
        }

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

        editView.addTextChangedListener(this);


    }


    void loadDB() {
        NotebookDBHelper databaseHelper = new NotebookDBHelper(getActivity());
        database = databaseHelper.getReadableDatabase();
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
        }

        cursor.close();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("matter note", "afterTExtChanged called");
        // indicate that the edittext is modify
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putBoolean(PREF_EDITTEXT_CHANGE, true);
        ed.commit();
    }
}
