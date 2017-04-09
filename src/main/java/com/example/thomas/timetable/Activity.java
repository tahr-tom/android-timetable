package com.example.thomas.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;

public class Activity implements Parcelable {
    // Initial id for first activity, auto increment by after create an activity
    private static int nextActivityNumber = 1;
    // DIFFERENT ID for each activity
    private int mActivityNumber;
    // Short text description for the activity
    private String mActivityTitle;
    // Showing importance of the activity, from 1 to 100
    private int mPriority;
    // Duration in hours and minutes
    private Period mDuration;
    // Storing all available periods
    private ArrayList<Interval> mAvailablePeriod;
    // Prerequisite for this activity
    private ArrayList<Integer> mPrerequisite;
    // final period
    private Interval mFinalPeriod;

    Activity(String activityTitle, Integer priority,
             int durationHours, int durationMinutes,
             DateTime start, DateTime end, Integer prerequisite) {
        mActivityNumber = nextActivityNumber;
        mActivityTitle = activityTitle;
        mPriority = priority;
        mDuration = new Period(durationHours, durationMinutes, 0, 0);
        mAvailablePeriod = new ArrayList<>();
        mAvailablePeriod.add(new Interval(start, end));
        mPrerequisite = new ArrayList<>();
        if (prerequisite != 0) { // If there is a prerequisite
            mPrerequisite.add(prerequisite); // Add that prerequisite to the arraylist
        }
        mFinalPeriod = null;
        nextActivityNumber++; // Increment next activity id by 1
    }

    Interval getFinalPeriod() {
        return mFinalPeriod;
    }


    void addPeriod(DateTime start, DateTime end) {
        mAvailablePeriod.add(new Interval(start, end));
    }

    void addPeriod(Interval period) {
        mAvailablePeriod.add(period);
    }

    void addPrerequisite(int prerequisite) {
        mPrerequisite.add(prerequisite);
    }

    ArrayList<Integer> getPrerequisite() {
        return mPrerequisite;
    }

    public void resetID() {
        nextActivityNumber = 1;
    }

    int getActivityNumber() {
        return mActivityNumber;
    }

    String getActivityTitle() {
        return mActivityTitle;
    }

    Integer getPriority() {
        return mPriority;
    }

    Period getDuration() {
        return mDuration;
    }

    ArrayList<Interval> getAvailablePeriod() {
        return mAvailablePeriod;
    }

    void setFinalPeriod(Interval finalPeriod) {
        this.mFinalPeriod = finalPeriod;
    }

    protected Activity(Parcel in) {
        mActivityNumber = in.readInt();
        mActivityTitle = in.readString();
        mPriority = in.readInt();
        mDuration = (Period) in.readValue(Period.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mAvailablePeriod = new ArrayList<Interval>();
            in.readList(mAvailablePeriod, Interval.class.getClassLoader());
        } else {
            mAvailablePeriod = null;
        }
        if (in.readByte() == 0x01) {
            mPrerequisite = new ArrayList<Integer>();
            in.readList(mPrerequisite, Integer.class.getClassLoader());
        } else {
            mPrerequisite = null;
        }
        mFinalPeriod = (Interval) in.readValue(Interval.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mActivityNumber);
        dest.writeString(mActivityTitle);
        dest.writeInt(mPriority);
        dest.writeValue(mDuration);
        if (mAvailablePeriod == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mAvailablePeriod);
        }
        if (mPrerequisite == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mPrerequisite);
        }
        dest.writeValue(mFinalPeriod);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Activity> CREATOR = new Parcelable.Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };
}