package com.zpi.team.joinin.ui.newevent;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zpi.team.joinin.R;
import com.zpi.team.joinin.database.SessionStorage;
import com.zpi.team.joinin.entities.Event;
import com.zpi.team.joinin.ui.events.EventsRecyclerFragment;
import com.zpi.team.joinin.ui.events.MyOwnEventsFragment;
import com.zpi.team.joinin.ui.events.TabEventsRecyclerAdapter;

public class CreateEventActivity extends ActionBarActivity  {
    public final static int CREATE_EVENT_REQUEST = 1;
    public final static int EDIT_MY_EVENT_REQUEST = 3;
    private int mMode;
    private Toolbar mToolbar;
    private Intent mData;

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
                Log.d("CreateEventActivity", "setResult(), canceled");
                finish();

            }
        });

        mData = getIntent();
        if(mData.getIntExtra("request code", -1) == EDIT_MY_EVENT_REQUEST){
            mMode = EDIT_MY_EVENT_REQUEST;
            getSupportActionBar().setTitle(getResources().getString(R.string.title_edit_event));
        }
        else if(mData.getIntExtra("request code", -1) == CREATE_EVENT_REQUEST)
            mMode = CREATE_EVENT_REQUEST ;



        FragmentManager fm = getFragmentManager();
        android.app.Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new CreateEventFragment();
//            if(mMode == EDIT_MY_EVENT_REQUEST) fragment.setArguments(mData.getExtras());
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

            CreateEventFragment form = (CreateEventFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
            if(mMode == CREATE_EVENT_REQUEST) {
                Log.d("CreateEventActivity", "CREATE, ok");

                if (form.isFilled()) {
                    Event event = form.saveNewEvent();
                    SessionStorage.getInstance().setNewlyCreated(event);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    form.highlightInputs();
                }
            }else if(mMode == EDIT_MY_EVENT_REQUEST) {
                Log.d("CreateEventActivity", "EDIT, ok");
                form.editEvent();
                setResult(RESULT_OK);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
