package com.zpi.team.joinin.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zpi.team.joinin.R;

/**
 * Created by Arkadiusz on 2015-03-08.
 */
public class ParticipateEventsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participate_events, container, false);
        return view;
    }
}