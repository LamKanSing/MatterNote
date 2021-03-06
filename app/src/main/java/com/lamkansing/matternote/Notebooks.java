package com.lamkansing.matternote;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Notebooks extends Activity implements InputNotebookNameDialogFragment.NoticeDialogListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebooks);

        if (getFragmentManager().findFragmentById(R.id.container) == null){
            Fragment fragment = NotebookFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }


        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setLogo(R.drawable.ic_action_m);
        getActionBar().setDisplayUseLogoEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notebooks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // no action bar icon

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(String notebookName){
        //  pass directly to the singlenotefragment to make new note
        long newRowId = addNewNoteList(notebookName);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, SingleNoteFragment.newInstance(Long.toString(newRowId), true));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getFragmentManager().findFragmentById(R.id.container);
        if(f instanceof SingleNoteFragment) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            boolean edittextChange = sharedPref.getBoolean(SingleNoteFragment.PREF_EDITTEXT_CHANGE, false);
            if (edittextChange){
                Toast.makeText(this, R.string.toast_note_change_notsave, Toast.LENGTH_LONG).show();
            }
        }
        super.onBackPressed();

    }

    long addNewNoteList(String newNotebookName){
        // add a new empty note to the db
        SQLiteOpenHelper mDbHelper = new NotebookDBHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotebookDBHelper.COLUMN_NOTE_CONTENT,"");
        values.put(NotebookDBHelper.COLUMN_NOTEBOOK_NAME, newNotebookName);
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

        if (db!=null)
            db.close();

        return newRowId;
    }



}
