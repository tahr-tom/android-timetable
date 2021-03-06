package com.example.thomas.timetable;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import java.util.ArrayList;


class DefaultAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    DefaultAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    void setFragments(ArrayList<Fragment> fragments) {
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
//        if (mFragments.size() == 0) {
            return new DefaultFragment();
//        } else {
//            switch (position) {
//                case 0:
//                    return mFragments.get(0);
//                case 1:
//                    return mFragments.get(1);
//                case 2:
//                    return mFragments.get(2);
//                case 3:
//                    return mFragments.get(3);
//                case 4:
//                    return mFragments.get(4);
//                case 5:
//                    return mFragments.get(5);
//                case 6:
//                    return mFragments.get(6);
//                default:
//                    return null;
//            }

//        }
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return PagerAdapter.POSITION_NONE;
//    }

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
