package com.example.oscar.radio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        ProgramFragment pFragment = new ProgramFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("day", index + 1);
        pFragment.setArguments(bundle);

        return pFragment;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 7;
    }

}