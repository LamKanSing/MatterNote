package com.lamkansing.matternote;


import android.app.Activity;
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

    TextView textview1, titleView;

    View showView, square;
    EditText editView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SingleNoteFragment.
     */
    public static SingleNoteFragment newInstance(String noteid) {
        SingleNoteFragment fragment = new SingleNoteFragment();
        Bundle args = new Bundle();
        args.putString("noteid", noteid);

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
        showView = mSceneRoot.findViewById(R.id.textView);
        editView = (EditText)mSceneRoot.findViewById(R.id.editText1);
        square = mSceneRoot.findViewById(R.id.frameLayout);
        titleView = (TextView)rootView.findViewById(R.id.title);
        mScene2 = Scene.getSceneForLayout(mSceneRoot, R.layout.fragment_single_note_edit, getActivity());

        textview1 = (TextView)rootView.findViewById(R.id.textView);
        textview1.setMovementMethod(new ScrollingMovementMethod());
        Log.d(TAG_NAME, "hello world");

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

        showView.setVisibility(View.GONE);

        editView.setVisibility(View.VISIBLE);

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
