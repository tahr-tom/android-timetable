package com.example.thomas.timetable;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class WeekdayFragment extends ListFragment {
    public WeekdayFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<Activity> activities = new ArrayList<>();
        if (getArguments() != null) {
            activities = getArguments().getParcelableArrayList("activities");

        }
        assert activities != null;
        ArrayAdapter<Activity> arrayAdapter = new
                ActivityAdapter(inflater.getContext(), activities);
        setListAdapter(arrayAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
