package com.lamkansing.matternote;


import android.app.FragmentTransaction;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.transition.Slide;


import com.melnykov.fab.FloatingActionButton;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * List all notebooks in the app
 */
public class NotebookFragment extends Fragment {
    private static final String LOG_TAG = "matternote";

    private static final String SHOWCASE_NOTEBOOK = "showcasenotebook";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notebook, container, false);
        setRetainInstance(true);

        if (getActivity()!=null && getActivity().getActionBar() !=null){
            getActivity().getActionBar().setTitle(R.string.app_name);
        }

        // exit transition of this fragment from back stack
        Slide slideExit = new Slide();
        slideExit.setSlideEdge(Gravity.RIGHT);
        slideExit.excludeTarget(fabaddnotebook, true);
        slideExit.addTarget(R.id.notebookList);
        setExitTransition(slideExit);

        // reenter transition of this fragment from back stack
        Slide slideReenter = new Slide();
        slideReenter.setSlideEdge(Gravity.RIGHT);
        slideReenter.excludeTarget(fabaddnotebook, true);
        slideReenter.addTarget(R.id.notebookList);

        // bug on android framework,
        // For whatever reason, the transition are called at reverse order,
        // reenter first, than return, temp solution to set the start overhead maintain
        // the order
        slideReenter.setStartDelay(800);
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
                Cursor c = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
                c.moveToPosition(position);
                String notebookName = c.getString(1);

                Fragment fragment = NoteListFragment.newInstance(notebookName);

                // enter transition of new fragment
                Slide slideEnter = new Slide();
                slideEnter.setSlideEdge(Gravity.LEFT);
                slideEnter.excludeTarget(R.id.fabnotelist, true);
                slideEnter.addTarget(R.id.listView);
                slideEnter.setStartDelay(400);
                fragment.setEnterTransition(slideEnter);

                // return transition of new fragment
                Slide slideReturn = new Slide();
                slideReturn.setSlideEdge(Gravity.LEFT);
                slideReturn.excludeTarget(R.id.fabnotelist, true);
                slideReturn.addTarget(R.id.listView);

                fragment.setReturnTransition(slideReturn);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // teach user to add notebook
        presentShowcaseView(1000);

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
        Log.d(LOG_TAG, "notebookfragment onpause");

        if (mCursor!=null)
            mCursor.close();
        if(db!=null)
            db.close();

        super.onPause();
    }

    /*
     * query all notebooks in the app
     */
    void loadDBReturnCursor(){
        SQLiteOpenHelper mDbHelper = new NotebookDBHelper(getActivity());
        db = mDbHelper.getReadableDatabase();

        mCursor = db.query(NotebookDBHelper.TABLE_NAME, new String[]{NotebookDBHelper.COLUMN_ID,
                NotebookDBHelper.COLUMN_NOTEBOOK_NAME}, null,null
                , NotebookDBHelper.COLUMN_NOTEBOOK_NAME, null, null);

        Log.d(LOG_TAG, "mCursor count" + mCursor.getCount());
    }

    private void presentShowcaseView(int withDelay) {
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(fabaddnotebook)
                .setDismissText("GOT IT")
                .setContentText("Add new notebook by clicking")
                .setDelay(withDelay)
                .singleUse(SHOWCASE_NOTEBOOK)
                .show();
    }

}
