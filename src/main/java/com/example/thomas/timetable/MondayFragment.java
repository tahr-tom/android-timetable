package com.example.thomas.timetable;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;

public class MondayFragment extends Fragment {
    public MondayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.no_activity, container, false);
        if (savedInstanceState != null) {
            rootView = inflater.inflate(R.layout.activity_list, container, false);
            ArrayList<Activity> sortedActivities = savedInstanceState.getParcelable("sorted");
            ArrayList<Activity> mondayActivities = new ArrayList<>();
            for(Activity activity: sortedActivities) {
                if (activity.getFinalPeriod().getStart().getDayOfWeek() == DateHelper.MONDAY_WEEKDAY) {
                    mondayActivities.add(activity);
                }
            }
            ActivityAdapter adapter = new ActivityAdapter(getActivity(), mondayActivities);
            ListView listView = (ListView) rootView.findViewById(R.id.list);
            listView.setAdapter(adapter);
        }
        return rootView;
    }





}
