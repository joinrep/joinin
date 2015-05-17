package com.zpi.team.joinin.ui.details;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zpi.team.joinin.R;

public class InDetailEventActivity extends ActionBarActivity{

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            FragmentManager fm = getFragmentManager();
            android.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
            if (fragment == null) {
                fragment = new InDetailEventFragment();
                fm.beginTransaction()
                        .add(R.id.fragmentContainer, fragment)
                        .commit();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        else if(id == R.id.action_organizer){
            InDetailEventFragment details =  (InDetailEventFragment)getFragmentManager().findFragmentById(R.id.fragmentContainer);
            details.showOrganizerInfo();
        }
        return super.onOptionsItemSelected(item);
    }
}
