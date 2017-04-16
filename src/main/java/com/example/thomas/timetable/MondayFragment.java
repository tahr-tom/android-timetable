package com.example.thomas.timetable;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTimeComparator;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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
            ArrayList<Activity> setActivities = savedInstanceState.getParcelable("sorted");
            ArrayList<Activity> mondayActivities = new ArrayList<>();
            for(Activity activity: setActivities) {
                if (activity.getFinalPeriod().getStart().getDayOfWeek() == DateHelper.MONDAY_WEEKDAY) {
                    mondayActivities.add(activity);
                }
            }
            ActivityAdapter adapter = new ActivityAdapter(getActivity(), mondayActivities);
            ListView listView = (ListView) rootView.findViewById(R.id.list);
            listView.setAdapter(adapter);
        }
//        Testing if custom array adapter work
//        Timetable mTimetable = new Timetable();
//        Activity act1 = new Activity("Borrow a travel book from library", 5,
//                1, 0,
//                DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 10, 0),
//                DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 13, 0), Timetable.NO_PREREQUISITE);
//        act1.addPeriod(DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 14, 0),
//                DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 20, 0));
//        act1.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 13, 0));
//        act1.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 14, 0),
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 20, 0));
//
//        Activity act2 = new Activity("Haircut for wedding", 2,
//                2, 0,
//                DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 10, 0),
//                DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 18, 0), Timetable.NO_PREREQUISITE);
//        act2.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 18, 0));
//
//        Activity act3 = new Activity("Buy a gift for wedding", 8,
//                1, 0,
//                DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 10, 0),
//                DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 18, 0), Timetable.NO_PREREQUISITE);
//        act3.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 18, 0));
//
//        Activity act4 = new Activity("Attend the wedding banquet", 100,
//                3, 0,
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 18, 0),
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 21, 0), 2);
//        act4.addPrerequisite(3);
//
//        Activity act5 = new Activity("Eat all day", 80,
//                1, 0,
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 13, 0), Timetable.NO_PREREQUISITE);
//        act5.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 14, 0),
//                DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 20, 0));
//
//        mTimetable.addActivity(act1);
//        mTimetable.addActivity(act2);
//        mTimetable.addActivity(act3);
//        mTimetable.addActivity(act4);
//        mTimetable.addActivity(act5);
//        View rootView = inflater.inflate(R.layout.activity_list, container, false);
//        ArrayList<Activity> setActivities = mTimetable.getSetActivities();
//        ArrayList<Activity> mondayActivities = new ArrayList<>();
//        for (Activity activity : setActivities) {
//            if (activity.getFinalPeriod() != null) {
//                if (activity.getFinalPeriod().getStart().getDayOfWeek() == DateHelper.MONDAY_WEEKDAY) {
//                    mondayActivities.add(activity);
//                }
//            }
//        }
//        Collections.sort(mondayActivities, new Comparator<Activity>() {
//            @Override
//            public int compare(Activity o1, Activity o2) {
//                return o1.getFinalPeriod().getStart().compareTo(o2.getFinalPeriod().getStart());
//            }
//        });
//        Log.i("Monday Fragment", String.valueOf(mondayActivities.size()));
//        ActivityAdapter adapter = new ActivityAdapter(getActivity(), mondayActivities);
//        ListView listView = (ListView) rootView.findViewById(R.id.list);
//        listView.setAdapter(adapter);
        return rootView;
    }
}
