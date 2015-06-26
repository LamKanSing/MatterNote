package com.lamkansing.matternote;


import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.transition.Slide;

import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotebookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotebookFragment extends Fragment
        {


    ListView listview;
    SQLiteDatabase db;
    Cursor mCursor;
    FloatingActionButton fabaddnotebook;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotebookFragment.
     */
    public static NotebookFragment newInstance() {
        NotebookFragment fragment = new NotebookFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public NotebookFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_notebook, container, false);
        setRetainInstance(true);

        if (getActivity()!=null && getActivity().getActionBar() !=null){
            getActivity().getActionBar().setTitle(R.string.app_name);
        }

        // todo you better create an enter anim once the app on create
        // the follow code work when notebookfragment -> notelistfragment
        // it don't work when notebookfragemtn start
        Slide slideExit = new Slide();
        slideExit.setSlideEdge(Gravity.RIGHT);
        slideExit.excludeTarget(fabaddnotebook, true);
        slideExit.addTarget(R.id.notebookList);
        setExitTransition(slideExit);

        Slide slideReenter = new Slide();
        slideReenter.setSlideEdge(Gravity.RIGHT);
        // the exclude target don't work for enenter/ return  fab
        slideReenter.excludeTarget(fabaddnotebook, true);
        slideReenter.addTarget(R.id.notebookList);

        // bug on android framework,
        // For whatever reason, the transition are called at reverse order,
        // reenter first, than return, temp solution to set the start overhead maintain
        // the order
        slideReenter.setStartDelay(900);
        setReenterTransition(slideReenter);

        fabaddnotebook = (FloatingActionButton)rootView.findViewById(R.id.fabaddnotebook);
        fabaddnotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputNotebookNameDialogFragment dialog = new InputNotebookNameDialogFragment();
                dialog.show(getFragmentManager(), "InputNotebookNameDialogFragment");
            }
        });

        listview = (ListView)rootView.findViewById(R.id.notebookList);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String notebookName = (String)parent.getItemAtPosition(position);

                Cursor c = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
                c.moveToPosition(position);
                String notebookName = c.getString(1);

                Fragment fragment = NoteListFragment.newInstance(notebookName);

                Slide slideEnter = new Slide();
                slideEnter.setSlideEdge(Gravity.LEFT);
                slideEnter.excludeTarget(R.id.fabnotelist, true);
                slideEnter.addTarget(R.id.listView);
                slideEnter.setStartDelay(400);
                fragment.setEnterTransition(slideEnter);

                Slide slideReturn = new Slide();
                slideReturn.setSlideEdge(Gravity.LEFT);
                // the exclude target don't work for enenter/ return  fab
                slideReturn.excludeTarget(R.id.fabnotelist, true);
                slideReturn.addTarget(R.id.listView);

                fragment.setReturnTransition(slideReturn);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        /*
        loadDBReturnCursor();

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                mCursor,
                new String[] { NotebookDBHelper.COLUMN_NOTEBOOK_NAME },
                new int[] { android.R.id.text1 });
        listview.setAdapter(adapter);
        */

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDBReturnCursor();

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                mCursor,
                new String[] { NotebookDBHelper.COLUMN_NOTEBOOK_NAME },
                new int[] { android.R.id.text1 });
        listview.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        if(db!=null)
            db.close();
        if (mCursor!=null)
            mCursor.close();
        super.onPause();
    }

    void loadDBReturnCursor(){
        SQLiteOpenHelper mDbHelper = new NotebookDBHelper(getActivity());
        db = mDbHelper.getReadableDatabase();

        mCursor = db.query(NotebookDBHelper.TABLE_NAME, new String[]{NotebookDBHelper.COLUMN_ID,
                NotebookDBHelper.COLUMN_NOTEBOOK_NAME}, null,null
                , NotebookDBHelper.COLUMN_NOTEBOOK_NAME, null, null);

        Log.d("matternote", "mCursor count" + mCursor.getCount());
    }





}
