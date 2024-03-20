package com.happymeals.Adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.happymeals.Screen1Activity;
import com.happymeals.Screen2Activity;
import com.happymeals.Screen3Activity;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Screen1Activity();
            case 1:
                return new Screen2Activity();
            case 2:
                return new Screen3Activity();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
