package com.example.thomas.timetable;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private static final int PICKFILE_REQUEST_CODE = 1;
    private DefaultAdapter adapter;
    private ViewPager viewPager;
    private Timetable mTimetable = new Timetable();
    private ArrayList<Activity> finalState = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fetch the timetable from main activity

        if (getIntent().getParcelableExtra("timetable") != null) {

            mTimetable = getIntent().getParcelableExtra("timetable");

//        } else {
//            //-----------------------------------Hard coded data for testing-------------------------------------
//            Activity act1 = new Activity("Borrow a travel book from library", 5,
//                    1, 0,
//                    Helper.getDate(Helper.SUNDAY_WEEKDAY, 10, 0),
//                    Helper.getDate(Helper.SUNDAY_WEEKDAY, 13, 0), Timetable.NO_PREREQUISITE);
//            act1.addPeriod(Helper.getDate(Helper.SUNDAY_WEEKDAY, 14, 0),
//                    Helper.getDate(Helper.SUNDAY_WEEKDAY, 20, 0));
//            act1.addPeriod(Helper.getDate(Helper.MONDAY_WEEKDAY, 10, 0),
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 13, 0));
//            act1.addPeriod(Helper.getDate(Helper.MONDAY_WEEKDAY, 14, 0),
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 20, 0));
//
//            Activity act2 = new Activity("Haircut for wedding", 2,
//                    2, 0,
//                    Helper.getDate(Helper.SUNDAY_WEEKDAY, 10, 0),
//                    Helper.getDate(Helper.SUNDAY_WEEKDAY, 18, 0), Timetable.NO_PREREQUISITE);
//            act2.addPeriod(Helper.getDate(Helper.MONDAY_WEEKDAY, 10, 0),
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 18, 0));
//
//            Activity act3 = new Activity("Buy a gift for wedding", 8,
//                    1, 0,
//                    Helper.getDate(Helper.SUNDAY_WEEKDAY, 10, 0),
//                    Helper.getDate(Helper.SUNDAY_WEEKDAY, 18, 0), Timetable.NO_PREREQUISITE);
//            act3.addPeriod(Helper.getDate(Helper.MONDAY_WEEKDAY, 10, 0),
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 18, 0));
//
//            Activity act4 = new Activity("Attend the wedding banquet", 100,
//                    3, 0,
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 18, 0),
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 21, 0), 2);
//            act4.addPrerequisite(3);
//
//            Activity act5 = new Activity("Eat all day", 80,
//                    1, 0,
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 10, 0),
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 13, 0), Timetable.NO_PREREQUISITE);
//            act5.addPeriod(Helper.getDate(Helper.MONDAY_WEEKDAY, 14, 0),
//                    Helper.getDate(Helper.MONDAY_WEEKDAY, 20, 0));
//
//            mTimetable.addActivity(act1);
//            mTimetable.addActivity(act2);
//            mTimetable.addActivity(act3);
//            mTimetable.addActivity(act4);
//            mTimetable.addActivity(act5);
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

    private void updateFragment() {
        // get set activities from timetable
        finalState = mTimetable.getSetActivities();

//        // remove activities that don't have a final period
//        for (Iterator<Activity> iterator = allActivities.iterator();
//             iterator.hasNext(); ) {
//            Activity activity = iterator.next();
//            if (activity.getFinalPeriod() == null) {
//                iterator.remove();
//            }
//        }

        // sort the remaining activities by time
        Collections.sort(finalState, new Comparator<Activity>() {
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
        for (Activity activity : finalState) {
            switch (activity.getFinalPeriod().getStart().getDayOfWeek()) {
                case Helper.SUNDAY_WEEKDAY:
                    weekdayActivities.get(0).add(activity);
                    break;
                case Helper.MONDAY_WEEKDAY:
                    weekdayActivities.get(1).add(activity);
                    break;
                case Helper.TUESDAY_WEEKDAY:
                    weekdayActivities.get(2).add(activity);
                    break;
                case Helper.WEDNESDAY_WEEKDAY:
                    weekdayActivities.get(3).add(activity);
                    break;
                case Helper.THURSDAY_WEEKDAY:
                    weekdayActivities.get(4).add(activity);
                    break;
                case Helper.FRIDAY_WEEKDAY:
                    weekdayActivities.get(5).add(activity);
                    break;
                case Helper.SATURDAY_WEEKDAY:
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

            } else { // if there is for that day
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

        // move viewpager position to the page that has activities
        int pagePosition = 0;
        for (int i = 0; i <= 6; i++) {
            if (!weekdayActivities.get(i).isEmpty()) { // if that weekday has activities
                pagePosition = i; // set position that that day
                break; // escape to make sure it is the first page that has activities
            }
        }
        viewPager.setCurrentItem(pagePosition);


        if (setActivities == totalActivities) { // if all activities have been scheduled
            Toast.makeText(this, "Scheduled all activities", Toast.LENGTH_SHORT).show();
        } else { // if only some activities has been scheduled
            Toast.makeText(this, "Schedule " + String.valueOf(setActivities)
                    + " activities out of " + String.valueOf(totalActivities)
                    + " activities", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.schedule_activities:
                if (mTimetable.getUnsortedActivities().size() == 0) {
                    // if there is no activity, notice the user
                    Toast.makeText(this, "You have to add at least one activity!", Toast.LENGTH_SHORT).show();
                } else {
                    updateFragment();
                }
                return true;

            case R.id.add_activity:
//                Toast.makeText(this, "Add activity btn clicked", Toast.LENGTH_SHORT).show();
                Intent addIntent = new Intent(MainActivity.this, EditorActivity.class);
                addIntent.putExtra("timetable", mTimetable);
                startActivity(addIntent);
                return true;

            case R.id.import_activities:
//                Toast.makeText(this, "Import activities btn clicked", Toast.LENGTH_SHORT).show();
                Intent importIntent = new Intent(Intent.ACTION_GET_CONTENT);
                importIntent.setType("text/plain");
                startActivityForResult(importIntent, PICKFILE_REQUEST_CODE);
                return true;

            case R.id.export_timetable:
//                Toast.makeText(this, "Export timetable btn clicked", Toast.LENGTH_SHORT).show();
                if (finalState.size() == 0) {
                    Toast.makeText(this, "The timetable don't have any activity!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (usingAndroidMOrAbove()) {
                    ActivityCompat.requestPermissions(this
                            , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    exportTimetableToTextFile(getTimetableStringForExport());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();

                    try {
                        String activitiesString = getStringFromUri(uri);
                        importActivitiesFromString(activitiesString);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // >= Android 4.4 (Kitkat)
    private String getStringFromUri(Uri uri) throws IOException {
        if (isExternalStorageReadable()) {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            assert inputStream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            inputStream.close();
            Log.i("getStringFromUri_Result", String.valueOf(stringBuilder.toString()));
            return stringBuilder.toString();
        }
        return null;
    }

    private void importActivitiesFromString(String text) {
        text = text.replace("\n", "");
        int total = Integer.parseInt(text.substring(0, 1));

        text = text.substring(3);
        for (int i = 1; i <= total; i++) {
            ArrayList<Interval> periods = new ArrayList<>();
            ArrayList<Integer> prerequisites = new ArrayList<>();

            String title = text.substring(0, text.indexOf(","));

            text = text.substring(text.indexOf(",") + 1);
            int priority = Integer.parseInt(text.substring(0, text.indexOf(",")));

            text = text.substring(text.indexOf(",") + 1);
            int durationHours = Integer.parseInt(text.substring(0, text.indexOf(":")));

            text = text.substring(text.indexOf(":") + 1);
            String durationMinutesText = text.substring(0, 2);
            int durationMinutes = Helper.getMinuteByMinuteString(durationMinutesText);

            text = text.substring(2 + 1);
            int totalPeriodsUpperIndexLimit = 3;
            String totalPeriodsText = text.substring(0, totalPeriodsUpperIndexLimit);
            while (totalPeriodsText.contains(" ")) {
                totalPeriodsUpperIndexLimit--;
                totalPeriodsText = text.substring(0, totalPeriodsUpperIndexLimit);
            }
            int totalPeriods = Integer.parseInt(totalPeriodsText);

            int totalPrerequisites = Integer.parseInt(text.substring(totalPeriodsUpperIndexLimit + 1, totalPeriodsUpperIndexLimit + 2));

            text = text.substring(totalPeriodsUpperIndexLimit + 2);

            for (int j = 1; j <= totalPeriods; j++) {
                String weekdayText = text.substring(0, 3);
                int weekday = Helper.getWeekdayByWeekdayName(weekdayText);

                String startTimeText = text.substring(4, 9);
                int[] startTime = Helper.getHourMinuteByTimeString(startTimeText);

                String endTimeText = text.substring(10, 15);
                int[] endTime = Helper.getHourMinuteByTimeString(endTimeText);

                periods.add(new Interval(Helper.getDate(weekday, startTime[0], startTime[1])
                        , Helper.getDate(weekday, endTime[0], endTime[1])));

                if (j != totalPeriods) {
                    text = text.substring(text.indexOf(",") + 1);
                } else if (totalPrerequisites != 0) {
                    text = text.substring(15);
                    String allPrerequisites;
                    if (i == total) {
                        allPrerequisites = text.replace(",", "");
                    } else {
                        allPrerequisites = text.substring(0, text.indexOf(",", totalPrerequisites) - 1)
                                .replace(",", "");
                        text = text.substring(text.indexOf(",", totalPrerequisites) + 1);
                    }

                    for (int k = 1; k <= totalPrerequisites; k++) {
                        prerequisites.add(Integer.valueOf(allPrerequisites.substring(k - 1, k)));
                    }

                } else {
                    text = text.substring(text.indexOf(",") + 1);
                }


            }

            Activity activity = new Activity(title, priority, durationHours, durationMinutes, periods, prerequisites);
            mTimetable.addActivity(activity);

        }
        updateFragment();
    }

    private String getTimetableStringForExport() {


        int total = finalState.size();
//        for (Activity activity : finalState) {
//            Log.i("exporting", mTimetable.activityFormatter(activity));
//        }
//        Log.i("size", String.valueOf(total));
        String export = String.valueOf(total) + "\n";
        for (int i = 0; i < total; i++) {
            Activity activity = finalState.get(i);
            export += activity.getActivityNumber()
                    + " "
                    + Helper.getWeekDayNameByWeekday(activity.getFinalPeriod().getStart().getDayOfWeek())
                    + " "
                    + activity.getFinalPeriod().getStart().toString(Helper.dateTimeFormatter)
                    + "\n";
        }
        return export;
    }

    private boolean usingAndroidMOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void exportTimetableToTextFile(String timetable) {
        if (isExternalStorageWritable()) {
            Log.i("export", "Clear to write to external fs");
            String fileName = "timetable_export_" + DateTime.now().toString(Helper.exportFileNameDateTimeFormatter) + ".txt";
            Log.i("filename", fileName);
            Log.i("exporting string", timetable);
            File root = Environment.getExternalStorageDirectory();
            Log.i("root dir", root.getPath());
            File file = new File(root, fileName);
            try {
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.append(timetable);
                outputStreamWriter.close();
                fileOutputStream.flush();

                fileOutputStream.close();
            } catch (IOException e) {
                Log.e("Exception", "Export failed" + e.toString());
            }
            Toast.makeText(this, "Timetable saved as\n" + root.toString() + File.separator + fileName,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportTimetableToTextFile(getTimetableStringForExport());

                } else {
                    Toast.makeText(this, "Unable to get permission to write!", Toast.LENGTH_SHORT).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }
}
