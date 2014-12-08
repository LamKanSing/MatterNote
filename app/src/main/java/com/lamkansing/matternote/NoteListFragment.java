package com.lamkansing.matternote;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NOTEBOOKTITLE = "notebooktitle";

    private String notebookTitle;


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
