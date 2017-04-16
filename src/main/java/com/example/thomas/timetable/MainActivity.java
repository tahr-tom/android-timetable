package com.example.thomas.timetable;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        WeekdayAdapter adapter = new WeekdayAdapter(this, getSupportFragmentManager());

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
                //TODO: add functionality to schedule btn
                if (mTimetable.getUnsortedActivities().size() == 0) {
                    Toast.makeText(this, "You have to add at least one activity!", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<Activity> setActivities = mTimetable.getSetActivities();
                    int viewPagerId = R.id.viewpager;
                    String[] weekdaysFragmentTags = new String[]{
                            getFragmentByViewPagerIndex(viewPagerId, 0),
                            getFragmentByViewPagerIndex(viewPagerId, 1),
                            getFragmentByViewPagerIndex(viewPagerId, 2),
                            getFragmentByViewPagerIndex(viewPagerId, 3),
                            getFragmentByViewPagerIndex(viewPagerId, 4),
                            getFragmentByViewPagerIndex(viewPagerId, 5),
                            getFragmentByViewPagerIndex(viewPagerId, 6)
                    };
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

    private String getFragmentByViewPagerIndex(int viewPagerID, int index) {
        return "android:switcher:" + viewPagerID + ":" + index;
    }
}
