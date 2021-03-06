package com.lamkansing.matternote;


import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 *  List note item on the specific notebook
 */
public class NoteListFragment extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_NOTEBOOKTITLE = "notebooktitle";

    private static final String LOG_TAG = "matternote";

    private static final String SHOWCASE_LIST = "showcaselist";

    private String notebookTitle;
    Cursor mCursor;
    SQLiteDatabase db;
    ListView mListView;

    // add new note floating action button
    FloatingActionButton addNoteFab;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param notebookTitle Parameter 1.
     * @return A new instance of fragment NoteListFragment.
     */
    public static NoteListFragment newInstance(String notebookTitle) {
        NoteListFragment fragment = new NoteListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOTEBOOKTITLE, notebookTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public NoteListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            notebookTitle = getArguments().getString(ARG_NOTEBOOKTITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        if (getActivity()!=null && getActivity().getActionBar()!=null)
            getActivity().getActionBar().setTitle(getArguments().getString(ARG_NOTEBOOKTITLE));

        View rootView = inflater.inflate(R.layout.fragment_note_list, container, false);

        mListView = (ListView)rootView.findViewById(R.id.listView);
        addNoteFab = (FloatingActionButton)rootView.findViewById(R.id.fabnotelist);

        // teach user about addnotefab
        presentShowcaseView(1000);

        // listener - list item is clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textview = (TextView)view.findViewById(R.id.listtiemnoteid);
                String noteid = textview.getText().toString();

                // set enter transition of new fragment and re-enter transition of this fragment
                setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                Fragment fragment = SingleNoteFragment.newInstance(noteid, false);
                fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // listener - floating action button is clicked
        addNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "fab on the list onclick");

                long newRowId = addNewNoteList();

                if (newRowId!= -1){
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, SingleNoteFragment.newInstance(Long.toString(newRowId), true));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else {
                    Log.d(LOG_TAG, "newRowId equal -1, database insert error" );
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCursor = loadDB();

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.firstlinelistitem,
                mCursor,
                new String[] { NotebookDBHelper.COLUMN_NOTE_CONTENT,
                        NotebookDBHelper.COLUMN_ID },
                new int[] { R.id.singlelinetextview, R.id.listtiemnoteid });

        mListView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "notelist fragment onpause");
        if(mCursor!=null)
            mCursor.close();
        if (db!=null)
            db.close();

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("notebookTitle", notebookTitle);
        super.onSaveInstanceState(outState);
    }

    /*
     * inert new row at database with empty content
     */
    private long addNewNoteList(){
        // add a new empty note to the db
        SQLiteOpenHelper mDbHelper = new NotebookDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotebookDBHelper.COLUMN_NOTE_CONTENT,"");
        values.put(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, getArguments().getString(ARG_NOTEBOOKTITLE));
        values.put(NotebookDBHelper.COLUMN_COVER_COLOR, 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        values.put(NotebookDBHelper.COLUMN_LASTEDIT, dateFormat.format(date));

        long newRowId;
        newRowId = db.insert(
                NotebookDBHelper.TABLE_NAME,
                null,
                values);

        Log.d(LOG_TAG, "new row id" + newRowId);

        if (db!=null)
            db.close();

        return newRowId;
    }

    /*
     * query notes for the notebooks
     */
    private Cursor loadDB(){
        SQLiteOpenHelper mDbHelper = new NotebookDBHelper(getActivity());
        db = mDbHelper.getReadableDatabase();

        String selection =  NotebookDBHelper.COLUMN_NOTEBOOK_NAME+" LIKE ?";

        mCursor = db.query(NotebookDBHelper.TABLE_NAME, null, selection,
                new String[]{getArguments().getString(ARG_NOTEBOOKTITLE)}, null, null, null);

        return mCursor;
    }

    private void presentShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(addNoteFab)
                .setDismissText("GOT IT")
                .setContentText("Add new note here by clicking")
                .setDelay(withDelay)
                .singleUse(SHOWCASE_LIST)
                .show();
    }
}


