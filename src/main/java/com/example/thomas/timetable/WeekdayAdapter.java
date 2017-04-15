package com.example.thomas.timetable;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class WeekdayAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public WeekdayAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SundayFragment();
            case 1:
                return new MondayFragment();
            case 2:
                return new TuesdayFragment();
            case 3:
                return new WednesdayFragment();
            case 4:
                return new ThursdayFragment();
            case 5:
                return new FridayFragment();
            case 6:
                return new SaturdayFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Sun";
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
            default:
                return null;
        }
    }
}
