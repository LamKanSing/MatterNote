package com.lamkansing.matternote;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
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
public class SingleNoteFragment extends Fragment {

    private static final String TAG_NAME= "matternote";

    private ViewGroup mSceneRoot;
    private Scene mScene2;

    private SQLiteDatabase database;

    private String noteTitle = "", noteContent = "";
    private int notebookColor;
    String dateTime;


    // state the fragment at VIEW mode or EDIT mode
    private String editViewMode= "view";

    TextView showView, textview1, titleView;
    FloatingActionButton fab;

    View  square;
    EditText editView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SingleNoteFragment.
     */
    public static SingleNoteFragment newInstance(String noteid, boolean newnote) {
        SingleNoteFragment fragment = new SingleNoteFragment();
        Bundle args = new Bundle();
        args.putString("noteid", noteid);
        args.putBoolean("newnote", newnote);

        fragment.setArguments(args);
        return fragment;
    }

    public SingleNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_single_note_shown_1, container, false);
        mSceneRoot = (ViewGroup) rootView.findViewById(R.id.rootscence);
        showView = (TextView)mSceneRoot.findViewById(R.id.textView);
        editView = (EditText)mSceneRoot.findViewById(R.id.editText1);
        square = mSceneRoot.findViewById(R.id.frameLayout);
        titleView = (TextView)rootView.findViewById(R.id.title);
        fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
        mScene2 = Scene.getSceneForLayout(mSceneRoot, R.layout.fragment_single_note_edit, getActivity());


        textview1 = (TextView)rootView.findViewById(R.id.textView);
        textview1.setMovementMethod(new ScrollingMovementMethod());



        // set the noteid, mode and text-note-save on saveedInstanceState
        if (savedInstanceState!=null){
            String mode = savedInstanceState.getString("editViewMode", "something wrong");


            if (mode.equals("edit")){
                String getText = savedInstanceState.getString("editTextText", "something wrong");
                if (! getText.equals("something wrong")){
                    editView.setText(getText);
                    turnEditMode();
                }
            }else {
                // view mode, to nothing....
            }
        }



        textview1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                turnEditMode();

                return true;

            }
        });



        // todo the scroll don't work stable on emsumlater, check it at real devcies
        textview1.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
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

                    String noteid = getArguments().getString("noteid", "1");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();

                    if (getArguments().getBoolean("newnote")){
                        // the note is a new created note, do inset
                    }else {
                        // the note already exist, do update
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

                        // todo i cannot see the log and toast, what happen??
                        // but the text is saveed, ???
                        Log.d("matternote", "the count is " + count);

                        if (count == 1){
                            Toast.makeText(getActivity(),"Note Saved", Toast.LENGTH_LONG);
                        }else {
                            Toast.makeText(getActivity(),"Something Wrong", Toast.LENGTH_LONG);
                        }

                    }

                }
            }
        });
        


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editView.getVisibility() == View.VISIBLE){
            outState.putString("editViewMode", "edit");
            outState.putString("editTextText", editView.getText().toString());

        }else {
            outState.putString("editViewMode", "view");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadDB();

        textview1.setText(noteContent);
        titleView.setText(noteTitle);
    }

    void turnEditMode(){
        Activity mActivity = getActivity();
        if (mActivity!=null){
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

    }


    void loadDB() {
        NotebookDBHelper databaseHelper = new NotebookDBHelper(getActivity());
        database = databaseHelper.getReadableDatabase();
        String noteid = getArguments().getString("noteid", "1");
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
}
