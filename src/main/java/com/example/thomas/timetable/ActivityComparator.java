package com.example.thomas.timetable;

import java.util.Comparator;

class ActivityComparator implements Comparator<Activity> {
    @Override
    public int compare(Activity o1, Activity o2) {
        return o1.getPriority().compareTo(o2.getPriority());
    }
}

