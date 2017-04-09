package com.example.thomas.timetable;

import android.app.*;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class EditorActivity extends AppCompatActivity {

    private Timetable mTimetable;
    private Spinner prioritySpinner;
    private Spinner durationHoursSpinner;
    private Spinner durationMinutesSpinner;
    private Spinner prerequisiteSpinner;
    private Spinner weekdaySpinner;
    private TimePickerDialog mStartTimePickerDialog;
    private TimePickerDialog mEndTimePickerDialog;
    private Button addPrerequisiteButton;
    private Button addAvailablePeriodButton;

    private ArrayList<Activity> prerequisitesList;
    private ArrayList<CharSequence> prerequisitesTitles;
    private ArrayList<Integer> prerequisitesForCurrentActivity;
    private ArrayList<Interval> availablePeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // fetch the timetable from main activity
        mTimetable = getIntent().getParcelableExtra("timetable");

        // fetch all activities from main activity
        prerequisitesList = mTimetable.getUnsortedActivities();
        prerequisitesTitles = getPrerequisitesTitles();

        prerequisitesForCurrentActivity = new ArrayList<>();
        availablePeriod = new ArrayList<>();

        // set up toolbar with back btn
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // populate content to all spinners
        setupPrioritySpinner();
        setupDurationSpinners();
        setupPrerequisiteSpinner(prerequisitesTitles);
        setupWeekdaySpinner();

        weekdaySpinner = (Spinner) findViewById(R.id.weekday);

        // define button for available period
        final Button startTimeButton = (Button) findViewById(R.id.start_time);
        final Button endTimeButton = (Button) findViewById(R.id.end_time);


        // set up prerequisites and available period btns
        addPrerequisiteButton = (Button) findViewById(R.id.add_prerequisite);
        addAvailablePeriodButton = (Button) findViewById(R.id.add_period);
        addPrerequisiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the selected prerequisite
                String selectedPrerequisite =
                        (String) prerequisiteSpinner.getItemAtPosition(prerequisiteSpinner.getSelectedItemPosition());

                // if user has selected a prerequisite
                if (selectedPrerequisite != null) {
                    int prerequisiteID = Timetable.NO_PREREQUISITE;

                    // find the id of that prerequisite activity
                    for (Activity activity : prerequisitesList) {
                        if (activity.getActivityTitle().equals(selectedPrerequisite)) {
                            prerequisiteID = activity.getActivityNumber();
                            break;
                        }
                    }

                    // add that id to this activity's prerequisite
                    prerequisitesForCurrentActivity.add(prerequisiteID);

                    // remove that added prerequisite title from title arraylist
                    for (Iterator iterator = prerequisitesTitles.iterator();
                         iterator.hasNext(); ) {
                        String title = (String) iterator.next();
                        if (title.equals(selectedPrerequisite)) {
                            iterator.remove();
                        }
                    }

                    // update the prerequisite spinner
                    setupPrerequisiteSpinner(prerequisitesTitles);

                    // toast to indicate that the prerequisite has been added
                    Toast.makeText(EditorActivity.this, "Added prerequisite: " + selectedPrerequisite, Toast.LENGTH_SHORT).show();

                    Log.v("AAA", String.valueOf(prerequisitesForCurrentActivity.size()));
                }
                // if user didn't select a prerequisite or the are no available prerequisite activity to select
                else {
                    // remind user that they have to select one
                    Toast.makeText(EditorActivity.this, "You didn't select any prerequisite!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addAvailablePeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weekdayName = (String) weekdaySpinner.getItemAtPosition(weekdaySpinner.getSelectedItemPosition());
                String startTimeText = (String) startTimeButton.getText();
                String endTimeText = (String) endTimeButton.getText();
                Log.i("ADA", startTimeText + "\n" + endTimeText);
                Log.i("weekdayname", weekdayName);
                if (!weekdayName.isEmpty() && !startTimeText.equals("Start") && !endTimeText.equals("End")) {
                    int weekday = DateHelper.getWeekdayByWeekdayName(weekdayName);
                    Log.i("weekday int", String.valueOf(weekday));
                    int[] startTime = DateHelper.getHourMinuteByTimeString(startTimeText);
                    int[] endTime = DateHelper.getHourMinuteByTimeString(endTimeText);
                    Log.i("ADA", Arrays.toString(startTime) + "\n" + Arrays.toString(endTime));
                    Interval period = new Interval(DateHelper.getDate(weekday, startTime[0], startTime[1]),
                            DateHelper.getDate(weekday, endTime[0], endTime[1]));
                    availablePeriod.add(period);
                    Toast.makeText(EditorActivity.this, "Available Period added:\n" + mTimetable.availablePeriodFormatter(period)
                            , Toast.LENGTH_SHORT).show();
                    Log.v("Added period:", mTimetable.availablePeriodFormatter(period));
                    weekdaySpinner.setSelection(0);
                    startTimeButton.setText(R.string.period_start);
                    endTimeButton.setText(R.string.period_end);
                } else if (weekdayName.isEmpty()) {
                    Toast.makeText(EditorActivity.this, "You didn't select any weekday!", Toast.LENGTH_SHORT).show();
                } else if (startTimeText.equals("Start")) {
                    Toast.makeText(EditorActivity.this, "You didn't select a start time!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // set start time dialog for starting time in available period
        mStartTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTimeButton.setText(formatTime(hourOfDay, minute));

                // if duration has been set
                if (hasDuration()) {

                    // get duration values from spinners
                    String durationHourText = (String) durationHoursSpinner
                            .getItemAtPosition(durationHoursSpinner.getSelectedItemPosition());
                    String durationMinuteText = (String) durationMinutesSpinner
                            .getItemAtPosition(durationMinutesSpinner.getSelectedItemPosition());

                    // get endTime array
                    int[] endTime = getEndTimeByDuration(hourOfDay, minute,
                            Integer.valueOf(durationHourText),
                            Integer.valueOf(durationMinuteText));
                    // add duration to starting time and set up ending time in available period
                    // actual value (user can't see without click the btn)
                    mEndTimePickerDialog.updateTime(endTime[0], endTime[1]);
                    // also set text for btn so they can see it
                    endTimeButton.setText(formatTime(endTime[0], endTime[1]));

                }
            }
        }, 9, 0, true);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartTimePickerDialog.show();
            }
        });


        // set end time dialog for ending time in available period
        mEndTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // this line is less likely to be executed since the code above already handled ending time
                endTimeButton.setText(formatTime(hourOfDay, minute));
            }
        }, 0, 0, true);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEndTimePickerDialog.show();
            }
        });


    }

    private ArrayList<CharSequence> getPrerequisitesTitles() {
        ArrayList<CharSequence> prerequisitesTitles = new ArrayList<>();
        for (Activity activity : prerequisitesList) {
            prerequisitesTitles.add(activity.getActivityTitle());
        }
        return prerequisitesTitles;
    }

    private void setupPrerequisiteSpinner(ArrayList<CharSequence> prerequisitesTitles) {
        prerequisiteSpinner = (Spinner) findViewById(R.id.prerequisite);
        ArrayAdapter<CharSequence> prerequisiteAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, prerequisitesTitles);
        prerequisiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prerequisiteSpinner.setAdapter(prerequisiteAdapter);
    }

    private void setupPrioritySpinner() {
        prioritySpinner = (Spinner) findViewById(R.id.priority);
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource
                (this, R.array.priority, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);
    }

    private void setupDurationSpinners() {
        durationHoursSpinner = (Spinner) findViewById(R.id.duration_hours);
        ArrayAdapter<CharSequence> durationHoursAdapter = ArrayAdapter.createFromResource
                (this, R.array.duration_hours, android.R.layout.simple_spinner_item);
        durationHoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationHoursSpinner.setAdapter(durationHoursAdapter);

        durationMinutesSpinner = (Spinner) findViewById(R.id.duration_minutes);
        ArrayAdapter<CharSequence> durationMinutesAdapter = ArrayAdapter.createFromResource
                (this, R.array.duration_minutes, android.R.layout.simple_spinner_item);
        durationMinutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationMinutesSpinner.setAdapter(durationMinutesAdapter);
    }

    private void setupWeekdaySpinner() {
        Spinner weekdaySpinner = (Spinner) findViewById(R.id.weekday);
        ArrayAdapter<CharSequence> weekdayAdapter = ArrayAdapter.createFromResource
                (this, R.array.weekdays_abbreviation, android.R.layout.simple_spinner_item);
        weekdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekdaySpinner.setAdapter(weekdayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveActivity() {
        //TODO finish this method
    }

    private String formatTime(int hour, int minute) {
        String hourText = String.valueOf(hour);
        String minuteText = String.valueOf(minute);
        if (hour < 10) {
            hourText = "0" + hourText;
        }
        if (minute < 10) {
            minuteText = "0" + minuteText;
        }
        return hourText + " : " + minuteText;
    }

    // get a end time int array
    private int[] getEndTimeByDuration(int startTimeHour, int startTimeMinute, int durationHour, int durationMinute) {
        int endHour = startTimeHour + durationHour;
        int endMinute = startTimeMinute + durationMinute;
        if (endMinute >= 60) {
            endMinute -= 60;
            endHour++;
        }
        if (endHour > 23 && endMinute > 0) { // User either enter a wrong period or a wrong starting time
            Toast.makeText(this, "Something wrong with the duration or the starting time!", Toast.LENGTH_SHORT).show();
            endHour = 0;
        }
        return new int[]{
                endHour, endMinute
        };
    }

    // Check if duration has been set
    private boolean hasDuration() {
        return durationMinutesSpinner.getSelectedItem() != null && durationHoursSpinner.getSelectedItem() != null;
    }

}
