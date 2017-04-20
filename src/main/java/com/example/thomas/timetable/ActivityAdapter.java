package com.example.thomas.timetable;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

class ActivityAdapter extends ArrayAdapter<Activity> {
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        Activity currentActivity = getItem(position);

        TextView startTimeTextView = (TextView) listItemView.findViewById(R.id.item_start);
        assert currentActivity != null;
        startTimeTextView.setText(currentActivity.getFinalPeriod().getStart().toString(Helper.dateTimeFormatter));

        TextView endTimeTextView = (TextView) listItemView.findViewById(R.id.item_end);
        endTimeTextView.setText(currentActivity.getFinalPeriod().getEnd().toString(Helper.dateTimeFormatter));

        TextView title = (TextView) listItemView.findViewById(R.id.item_title);
        title.setText(currentActivity.getActivityTitle());

        return listItemView;

    }

    // the arraylist passed in should only include that weekday' activities and sort by time (ascending)
    ActivityAdapter(Context context, ArrayList<Activity> sortedActivities) {
        super(context, 0, sortedActivities);
    }

}
