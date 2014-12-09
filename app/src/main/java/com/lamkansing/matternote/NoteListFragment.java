package com.lamkansing.matternote;


import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NOTEBOOKTITLE = "notebooktitle";

    private String notebookTitle;
    Cursor mCursor;
    ListAdapter adapter;
    ListView mListView;
    FloatingActionButton fab;


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
        View rootView = inflater.inflate(R.layout.fragment_note_list, container, false);

        mListView = (ListView)rootView.findViewById(R.id.listView);
        fab = (FloatingActionButton)rootView.findViewById(R.id.fabnotelist);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // clear the actionbar title
                getActivity().getActionBar().setTitle("");

                TextView textview = (TextView)view.findViewById(R.id.listtiemnoteid);
                String noteid = textview.getText().toString();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, SingleNoteFragment.newInstance(noteid, false));
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();

                /*
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.container, SingleNoteFragment.newInstance(noteid, false))
                        .commit();*/
            }
        });

        getActivity().getActionBar().setTitle(getArguments().getString(ARG_NOTEBOOKTITLE));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("matternote", "fab on the list onclick");

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

                Log.d("matternote", "new row id" + newRowId);
                // navigate to singlenotefragment with new note id
                // todo turn the new fregment to edit mode....

                if (newRowId!= -1){
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, SingleNoteFragment.newInstance(Long.toString(newRowId), false));
                    transaction.addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                }else {
                    Log.d("matternote", "newRowId equal -1, insert error" );
                }


            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCursor = loadDB();

        // the doc said I should use  use LoaderManager with a CursorLoader.......

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(), // Context.
                R.layout.firstlinelistitem, // Specify the row template
                // to use (here, two
                // columns bound to the
                // two retrieved cursor
                // rows).
                mCursor, // Pass in the cursor to bind to.
                // Array of cursor columns to bind to.
                new String[] { NotebookDBHelper.COLUMN_NOTE_CONTENT,
                        NotebookDBHelper.COLUMN_ID },
                // Parallel array of which template objects to bind to those
                // columns.
                new int[] { R.id.singlelinetextview, R.id.listtiemnoteid });

        mListView.setAdapter(adapter);

    }

    Cursor loadDB(){
        SQLiteOpenHelper mDbHelper = new NotebookDBHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection =  NotebookDBHelper.COLUMN_NOTEBOOK_NAME+" LIKE ?";

        mCursor = db.query(NotebookDBHelper.TABLE_NAME, null, selection,
                new String[]{getArguments().getString(ARG_NOTEBOOKTITLE)}, null, null, null);

        return mCursor;
    }
}
