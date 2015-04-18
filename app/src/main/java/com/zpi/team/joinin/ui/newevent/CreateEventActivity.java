package com.zpi.team.joinin.ui.newevent;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.ui.main.EventsFragment;

public class CreateEventActivity extends ActionBarActivity  {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_toolbar_close);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();

            }
        });

        FragmentManager fm = getFragmentManager();
        android.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new CreateEventFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            Log.d("zapisz", "saved");
            CreateEventFragment form =  (CreateEventFragment)getFragmentManager().findFragmentById(R.id.fragmentContainer);

            if(form.isFilled()){
                Event event = form.saveNewEvent();
                SessionStorage.getInstance().setNewlyCreated(event);
                setResult(RESULT_OK);
                finish();
            }
            else{
                form.highlightInputs();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
