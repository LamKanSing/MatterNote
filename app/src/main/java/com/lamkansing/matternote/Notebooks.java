package com.lamkansing.matternote;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Notebooks extends Activity implements InputNotebookNameDialogFragment.NoticeDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebooks);

        if (getFragmentManager().findFragmentById(R.id.container) == null){
            Fragment fragment = NotebookFragment.newInstance();

            // todo you need a giude to tell user how to use your app....
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment).commit();
        }

        // todo you have three fragment, the saveinstancestate should transsacte between them
        // ex: when the app go to back stack when user look at note list fragment,
        // the app should let user to go back to note list fragment with saveinstancestate



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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // todo you have no action here, remove the icon pls
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
