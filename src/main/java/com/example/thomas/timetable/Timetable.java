package com.example.thomas.timetable;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

class Timetable implements Parcelable {


    // Int for no prerequisite --> ID = 0
    final static Integer NO_PREREQUISITE = 0;

    // List for adding all activities, unsorted
    private ArrayList<Activity> unsortedActivities;

    // List for sorted activities according priority and prerequisite
    private ArrayList<Activity> sortedActivities;

    // List for activities that have benn assigned time, ready for creating actual timetable
    private ArrayList<Activity> setActivities;

    // List for storing periods that have been used;
    private ArrayList<Interval> usedPeriods;

    private String shortWeekdayFormatString = "EEE";

    private String LongWeekdayFormatString = "EEEE";


    // Constructor, assign all lists with a ArrayList
    Timetable() {
        unsortedActivities = new ArrayList<>();
        sortedActivities = new ArrayList<>();
        setActivities = new ArrayList<>();
        usedPeriods = new ArrayList<>();
    }

    /**
     * Return a String that format an availablePeriod, which include a startTime and a endTime into a string
     * for example 10:00 - 13:00
     *
     * @param availablePeriod that needs to be formatted
     * @return a formatted string in a pattern of HH:MM - HH:MM
     */
    String availablePeriodFormatter(Interval availablePeriod) {
        return availablePeriod.getStart().toString(shortWeekdayFormatString) + "\n" +
                availablePeriod.getStart().toString(Helper.dateTimeFormatter) + " - " +
                availablePeriod.getEnd().toString(Helper.dateTimeFormatter);
    }

    /**
     * Return a String that format all information of an activity that needs to be display
     *
     * @param activity that needs to be formatted
     * @return a formatted String
     */
    String activityFormatter(Activity activity) {
        String activityString = String.valueOf(activity.getActivityNumber()) + "\n" +
                activity.getActivityTitle() + "\n" +
                String.valueOf("Priority: " + activity.getPriority()) + "\n" +
                "Duration: " + activity.getDuration().toString(Helper.durationFormatter) + "\n" +
                "Available periods\n";
        for (Interval availablePeriod : activity.getAvailablePeriod()) {
            activityString += availablePeriodFormatter(availablePeriod);
            activityString += "\n";
        }
        activityString += "Perquisites:\n";
        if (activity.getPrerequisite().isEmpty()) {
            activityString += "Nil\n";
        } else {
            for (Integer prerequisite : activity.getPrerequisite()) {
                activityString += prerequisite + "\n";
            }

        }
        activityString += "Final Period:\n";
        if (activity.getFinalPeriod() != null) {
            activityString += availablePeriodFormatter(activity.getFinalPeriod()) + "\n";
        } else {
            activityString += "Not set\n";
        }
        return activityString;
    }

    void addActivity(Activity activity) {
        unsortedActivities.add(activity);
    }

    ArrayList<Activity> getUnsortedActivities() {
        return unsortedActivities;
    }

    ArrayList<Activity> getSortedActivities() {
        // Make sure the sorted list is empty
        this.sortedActivities.clear();

        // List that holds IDs of added activities
        ArrayList<Integer> addedActivities = new ArrayList<>();

        // Temp list that copy all activities in unsorted list, prevent changing its content
        ArrayList<Activity> tempArrayList = new ArrayList<>();

        // Copy all activities from unsorted to temp
        for (Activity activity : this.unsortedActivities) {
            tempArrayList.add(activity);
        }

        // Sort activities by priority

        // Sort temp with comparator (ascending)
        Collections.sort(tempArrayList, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getPriority().compareTo(o2.getPriority());
            }
        });

        // Reverse the order of activities (ascending --> descending)
        Collections.reverse(tempArrayList);

        // Add all the activities that DON'T have prerequisite
        // and store their id for upcoming delete
        for (Activity activity : tempArrayList) {
            if (activity.getPrerequisite().isEmpty()) {
                sortedActivities.add(activity);
                addedActivities.add(activity.getActivityNumber());
            }
        }

        // Clean up the temp, removing activities that have be added into sorted
        for (Integer integer : addedActivities) {

            // This version can't be used since lambda expression is not supported
            // tempArrayList.removeIf(activity -> integer == activity.getActivityNumber());

            // Using iterator to remove added activities in temp
            for (Iterator actIterator = tempArrayList.iterator();
                 actIterator.hasNext(); ) {
                Activity activity = (Activity) actIterator.next();
                if (activity.getActivityNumber() == integer) {
                    actIterator.remove();
                }
            }
        }

        // Clear the activities ID
        addedActivities.clear();


        // Add all activities that have prerequisite from temp to sorted
        for (Activity activity : tempArrayList) {
            sortedActivities.add(activity);
        }
        return this.sortedActivities;
    }

    private ArrayList<Interval> getUsedPeriods() {
        return usedPeriods;
    }

    ArrayList<Activity> getSetActivities() {
        this.getSortedActivities();
        // Make sure the sorted list is empty
        this.setActivities.clear();

        // Temp list that copy all activities in unsorted list, prevent changing its content
        ArrayList<Activity> tempArrayList = new ArrayList<>();

        // Copy all activities from unsorted to temp
        for (Activity activity : this.sortedActivities) {
            tempArrayList.add(activity);
        }
        // --------------DO NOT CHANGE THE CODE ABOVE-------------------------------------------


        // ------------------------------------------------------------------------------------------
        // Test code for single activity
//        int index = 4;
//        ArrayList<AvailablePeriod> act1AvailablePeriod = tempArrayList.get(index).getAvailablePeriod();
//        Period act1Duration = tempArrayList.get(index).getDuration();
//        for (AvailablePeriod actPeriod : act1AvailablePeriod) {
//            DateTime lowerLimit = actPeriod.getStartTime();
//            DateTime upperLimit = actPeriod.getEndTime().minus(act1Duration);
//            boolean doneForThisAvailablePeriod = false;
//            while (!doneForThisAvailablePeriod) {
//                AvailablePeriod period = new AvailablePeriod(lowerLimit, lowerLimit.plus(act1Duration));
//                boolean used = false;
//                if (lowerLimit.isEqual(upperLimit) | lowerLimit.isAfter(upperLimit)) {
//                    doneForThisAvailablePeriod = true;
//                }
//                for (AvailablePeriod usedPeriod : this.getUsedPeriods()) {
//                    if (period.equals(usedPeriod)) {
//                        used = true;
//                        break;
//                    }
//                }
//                if (!used) {
//                    usedPeriods.add(period);
//                    doneForThisAvailablePeriod = true;
//                    tempArrayList.get(index).setFinalPeriod(period);
//                } else {
//                    lowerLimit.plusMinutes(10);
//                }
//            }
//        }
        // ------------------------------------------------------------------------------------------

        for (Activity activity : tempArrayList) { // For each activity in temp
//            System.out.println("Handling Activity index: " + tempArrayList.indexOf(activity));

            // If activity's prerequisite have been set a final period OR activity don't have any prerequisite
            if (fulfillPrerequisite(activity)) {
                // Fetch all available period for that activity
                ArrayList<Interval> activityAvailablePeriod = activity.getAvailablePeriod();
                // Fetch duration for that activity
                Period duration = activity.getDuration();
                // Set a flag to indicate whether the activity is finish
                boolean doneForThisActivity = false;

                while (!doneForThisActivity) {
                    // For every period
                    for (Interval activityPeriod : activityAvailablePeriod) {
                        // Lower limit for the period
                        DateTime lowerLimit = activityPeriod.getStart();
                        // Upper limit for the period, which is end - duration
                        DateTime upperLimit = activityPeriod.getEnd().minus(duration);
                        // Set a flag to indicate whether the period is finish
                        boolean doneForThisAvailablePeriod = false;

                        while (!doneForThisAvailablePeriod) {
                            // Create a final period from
                            Interval period = new Interval(lowerLimit, lowerLimit.plus(duration));
                            // Set a flag to indicate whether the period has been used
                            boolean used = false;

                            // If lower limit equals or exceed upper limit
                            if (lowerLimit.isEqual(upperLimit) | lowerLimit.isAfter(upperLimit)) {
                                // set true to exit this period
                                doneForThisAvailablePeriod = true;

                                // If this is the last period in available period
                                if (activityAvailablePeriod.indexOf(activityPeriod) == activityAvailablePeriod.size() - 1) {
                                    // set true to exit this activity (NO final period has been set)
                                    doneForThisActivity = true;
                                }
                            }

                            // for every used period
                            for (Interval usedPeriod : this.getUsedPeriods()) {

                                // If that period has been used
                                if (period.overlaps(usedPeriod)) {
                                    // Set true to prevent the if block below from executing
                                    used = true;
                                    // exit the for loop immediately
                                    break;
                                }
                            }
                            // If this period has not been used
                            if (!used) {
                                // add this period to used period
                                usedPeriods.add(period);
                                // final period found --> done
                                doneForThisAvailablePeriod = true;
                                // final period found --> done
                                doneForThisActivity = true;
                                // set this final period to the activity
                                activity.setFinalPeriod(period);
                            } else { // if the period has been used
                                // add 10 minutes to lowerLimit and try again
                                lowerLimit = lowerLimit.plusMinutes(10);
                            }
                        }
                    }
                }
            }
        }

        // Add all activities that have prerequisite from temp to sorted
        for (Activity activity : tempArrayList) {
            if (activity.getFinalPeriod() != null) {
                setActivities.add(activity);
            }
        }

        return this.setActivities;
    }

    /**
     * @param activity is the activity wanted to be check
     * @return boolean indicate whether the prerequisite(s) have been fulfilled
     * prerequisite' final period set OR NO prerequisite --> true
     * prerequisite' final period has not been set --> false
     */
    private boolean fulfillPrerequisite(Activity activity) {
        // fetch all prerequisites of the activity
        ArrayList<Integer> prerequisites = activity.getPrerequisite();
        // If the activity don't have a prerequisite
        if (prerequisites.isEmpty()) {
            // exit and return true early
            return true;
        }

        // set a flag indicate the status of prerequisite(s) fulfillment
        boolean fulfill = false;

        // copy the prerequisites list into another list
        ArrayList<Integer> fulfillResult = new ArrayList<>();
        for (Integer integer : prerequisites) {
            fulfillResult.add(integer);
        }

        for (Integer prerequisite : prerequisites) {
            for (Activity setActivity : unsortedActivities) {
                if (setActivity.getActivityNumber() == prerequisite) {
                    fulfillResult.remove(prerequisite);
                }
            }
        }

        if (fulfillResult.isEmpty()) {
            fulfill = true;
        }
        return fulfill;

    }

    protected Timetable(Parcel in) {
        if (in.readByte() == 0x01) {
            unsortedActivities = new ArrayList<Activity>();
            in.readList(unsortedActivities, Activity.class.getClassLoader());
        } else {
            unsortedActivities = null;
        }
        if (in.readByte() == 0x01) {
            sortedActivities = new ArrayList<Activity>();
            in.readList(sortedActivities, Activity.class.getClassLoader());
        } else {
            sortedActivities = null;
        }
        if (in.readByte() == 0x01) {
            setActivities = new ArrayList<Activity>();
            in.readList(setActivities, Activity.class.getClassLoader());
        } else {
            setActivities = null;
        }
        if (in.readByte() == 0x01) {
            usedPeriods = new ArrayList<Interval>();
            in.readList(usedPeriods, Interval.class.getClassLoader());
        } else {
            usedPeriods = null;
        }
        shortWeekdayFormatString = in.readString();
        LongWeekdayFormatString = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (unsortedActivities == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(unsortedActivities);
        }
        if (sortedActivities == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(sortedActivities);
        }
        if (setActivities == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(setActivities);
        }
        if (usedPeriods == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(usedPeriods);
        }
        dest.writeString(shortWeekdayFormatString);
        dest.writeString(LongWeekdayFormatString);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Timetable> CREATOR = new Parcelable.Creator<Timetable>() {
        @Override
        public Timetable createFromParcel(Parcel in) {
            return new Timetable(in);
        }

        @Override
        public Timetable[] newArray(int size) {
            return new Timetable[size];
        }
    };
}