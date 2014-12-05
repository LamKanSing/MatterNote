package com.lamkansing.matternote;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Notebooks extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebooks);
        if (savedInstanceState == null) {

            // todo dumy note id
            getFragmentManager().beginTransaction()
                    .add(R.id.container, SingleNoteFragment.newInstance("1"))
                    .commit();
        }

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setLogo(R.drawable.ic_action_addsql);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_addsql){
            NotebookDBHelper databaseHelper = new NotebookDBHelper(this);
            SQLiteDatabase database = databaseHelper.getWritableDatabase();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            ContentValues values = new ContentValues();
            values.put(NotebookDBHelper.COLUMN_COVER_COLOR, 1);
            values.put(NotebookDBHelper.COLUMN_NOTEBOOK_NAME,"testing title");
            values.put(NotebookDBHelper.COLUMN_NOTE_CONTENT,"testing content");
            values.put(NotebookDBHelper.COLUMN_LASTEDIT, dateFormat.format(date));
            long newRowId;
            newRowId = database.insert(
                    NotebookDBHelper.TABLE_NAME,
                    null,
                    values);

            Log.d("matternote", "new row id" + newRowId);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
