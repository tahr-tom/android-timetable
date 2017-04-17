package com.example.thomas.timetable;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    DefaultAdapter adapter;
    ViewPager viewPager;
    Timetable mTimetable = new Timetable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fetch the timetable from main activity

        if (getIntent().getParcelableExtra("timetable") != null) {

            mTimetable = getIntent().getParcelableExtra("timetable");

        } else {
            //-----------------------------------Hard coded data for testing-------------------------------------
            Activity act1 = new Activity("Borrow a travel book from library", 5,
                    1, 0,
                    DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 10, 0),
                    DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 13, 0), Timetable.NO_PREREQUISITE);
            act1.addPeriod(DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 14, 0),
                    DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 20, 0));
            act1.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 13, 0));
            act1.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 14, 0),
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 20, 0));

            Activity act2 = new Activity("Haircut for wedding", 2,
                    2, 0,
                    DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 10, 0),
                    DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 18, 0), Timetable.NO_PREREQUISITE);
            act2.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 18, 0));

            Activity act3 = new Activity("Buy a gift for wedding", 8,
                    1, 0,
                    DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 10, 0),
                    DateHelper.getDate(DateHelper.SUNDAY_WEEKDAY, 18, 0), Timetable.NO_PREREQUISITE);
            act3.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 18, 0));

            Activity act4 = new Activity("Attend the wedding banquet", 100,
                    3, 0,
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 18, 0),
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 21, 0), 2);
            act4.addPrerequisite(3);

            Activity act5 = new Activity("Eat all day", 80,
                    1, 0,
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 10, 0),
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 13, 0), Timetable.NO_PREREQUISITE);
            act5.addPeriod(DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 14, 0),
                    DateHelper.getDate(DateHelper.MONDAY_WEEKDAY, 20, 0));

            mTimetable.addActivity(act1);
            mTimetable.addActivity(act2);
            mTimetable.addActivity(act3);
            mTimetable.addActivity(act4);
            mTimetable.addActivity(act5);
//-----------------------------------Hard coded data for testing-------------------------------------

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new DefaultAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Log.d("Numbers of activities", String.valueOf(mTimetable.getUnsortedActivities().size()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.schedule_activities:
                if (mTimetable.getUnsortedActivities().size() == 0) {
                    // if there is no activity, notice the user
                    Toast.makeText(this, "You have to add at least one activity!", Toast.LENGTH_SHORT).show();
                } else {
                    // get set activities from timetable
                    ArrayList<Activity> allActivities = mTimetable.getSetActivities();

                    // remove activities that don't have a final period
                    for (Iterator<Activity> iterator = allActivities.iterator();
                         iterator.hasNext(); ) {
                        Activity activity = iterator.next();
                        if (activity.getFinalPeriod() == null) {
                            iterator.remove();
                        }
                    }

                    // sort the remaining activities by time
                    Collections.sort(allActivities, new Comparator<Activity>() {
                        @Override
                        public int compare(Activity o1, Activity o2) {
                            return o1.getFinalPeriod().getStart().compareTo(o2.getFinalPeriod().getStart());
                        }
                    });

                    // list to store all activities
                    ArrayList<ArrayList<Activity>> weekdayActivities = new ArrayList<>();

                    // use 7 arraylist to store each weekday activities
                    // order: SUN --> MON --> TUE --> WED --> THU --> FRI --> SAT
                    for (int i = 1; i <= 7; i++) {
                        ArrayList<Activity> activities = new ArrayList<>();
                        weekdayActivities.add(activities);
                    }

                    // distribute activities by its weekday
                    for (Activity activity : allActivities) {
                        switch (activity.getFinalPeriod().getStart().getDayOfWeek()) {
                            case DateHelper.SUNDAY_WEEKDAY:
                                weekdayActivities.get(0).add(activity);
                                break;
                            case DateHelper.MONDAY_WEEKDAY:
                                weekdayActivities.get(1).add(activity);
                                break;
                            case DateHelper.TUESDAY_WEEKDAY:
                                weekdayActivities.get(2).add(activity);
                                break;
                            case DateHelper.WEDNESDAY_WEEKDAY:
                                weekdayActivities.get(3).add(activity);
                                break;
                            case DateHelper.THURSDAY_WEEKDAY:
                                weekdayActivities.get(4).add(activity);
                                break;
                            case DateHelper.FRIDAY_WEEKDAY:
                                weekdayActivities.get(5).add(activity);
                                break;
                            case DateHelper.SATURDAY_WEEKDAY:
                                weekdayActivities.get(6).add(activity);
                                break;
                        }
                    }

//                    Log.i("Sort btn", String.valueOf(mTimetable.getUnsortedActivities().size()));
//                    Log.i("Sort btn", "Monday" + String.valueOf(mondayActivities.size()));
                    // using new adapter to update views
                    WeekdayAdapter adapter = new WeekdayAdapter(this, getSupportFragmentManager());

                    // for ever weekday
                    for (ArrayList<Activity> arrayList : weekdayActivities) {
                        if (!arrayList.isEmpty()) { // if that day has activities
                            // create new weekday fragment and supply activities for that day
                            Fragment fragment = new WeekdayFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("activities", arrayList);
                            fragment.setArguments(bundle);

                            // add that weekday fragment to the adapter
                            adapter.add(fragment);

                        } else { // if there is no activity for that day
                            // add the old empty fragment to the adapter
                            adapter.add(new DefaultFragment());
                        }
                    }
//                    if (!sundayActivities.isEmpty()) {
//                        Fragment sundayFragment = new WeekdayFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("activities", sundayActivities);
//                        sundayFragment.setArguments(bundle);
//                        adapter.add(sundayFragment);
//                    } else {
//                        adapter.add(new DefaultFragment());
//                    }
//                    if (!mondayActivities.isEmpty()) {
//                        Fragment mondayFragment = new WeekdayFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("activities", mondayActivities);
//                        mondayFragment.setArguments(bundle);
//                        adapter.add(mondayFragment);
//                    } else {
//                        adapter.add(new DefaultFragment());
//                    }
//                    if (!tuesdayActivities.isEmpty()) {
//                        Fragment tuesdayFragment = new WeekdayFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("activities", tuesdayActivities);
//                        tuesdayFragment.setArguments(bundle);
//                        adapter.add(tuesdayFragment);
//                    } else {
//                        adapter.add(new DefaultFragment());
//                    }
//                    if (!wednesdayActivities.isEmpty()) {
//                        Fragment wednesdayFragment = new WeekdayFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("activities", wednesdayActivities);
//                        wednesdayFragment.setArguments(bundle);
//                        adapter.add(wednesdayFragment);
//                    } else {
//                        adapter.add(new DefaultFragment());
//                    }
//                    if (!thursdayActivities.isEmpty()) {
//                        Fragment thursdayFragment = new WeekdayFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("activities", thursdayActivities);
//                        thursdayFragment.setArguments(bundle);
//                        adapter.add(thursdayFragment);
//                    } else {
//                        adapter.add(new DefaultFragment());
//                    }
//                    if (!fridayActivities.isEmpty()) {
//                        Fragment fridayFragment = new WeekdayFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("activities", fridayActivities);
//                        fridayFragment.setArguments(bundle);
//                        adapter.add(fridayFragment);
//                    } else {
//                        adapter.add(new DefaultFragment());
//                    }
//                    if (!saturdayActivities.isEmpty()) {
//                        Fragment saturdayFragment = new WeekdayFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("activities", saturdayActivities);
//                        saturdayFragment.setArguments(bundle);
//                        adapter.add(saturdayFragment);
//                    } else {
//                        adapter.add(new DefaultFragment());
//                    }
//                    Log.i("Sort bnt", "view pager adapter reset");
                    // set the new adapter to the view pager
                    viewPager.setAdapter(adapter);

                    // get the actual number of activities that the program has scheduled
                    int setActivities = 0;
                    for (ArrayList<Activity> activities : weekdayActivities) {
                        setActivities += activities.size();
                    }

                    // get the total number of activities that user has input
                    int totalActivities = mTimetable.getUnsortedActivities().size();


                    if (setActivities == totalActivities) { // if all activities has been scheduled
                        Toast.makeText(this, "Scheduled all activities", Toast.LENGTH_SHORT).show();
                    } else { // if only some activities has been scheduled
                        Toast.makeText(this, "Schedule " + String.valueOf(setActivities)
                                + " activities out of " + String.valueOf(totalActivities)
                                + " activities", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;

            case R.id.add_activity:
//                Toast.makeText(this, "Add activity btn clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("timetable", mTimetable);
                startActivity(intent);
                return true;

            case R.id.import_activities:
//                Toast.makeText(this, "Import activities btn clicked", Toast.LENGTH_SHORT).show();
                // TODO: add functionality to import btn
                return true;

            case R.id.export_timetable:
//                Toast.makeText(this, "Export timetable btn clicked", Toast.LENGTH_SHORT).show();
                // TODO: add functionality to export btn
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
