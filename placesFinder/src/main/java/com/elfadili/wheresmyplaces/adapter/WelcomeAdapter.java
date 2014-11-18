package com.elfadili.wheresmyplaces.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.elfadili.wheresmyplaces.fragment.FragmentStep1;
import com.elfadili.wheresmyplaces.fragment.FragmentStep2;
import com.elfadili.wheresmyplaces.fragment.FragmentStep3;
import com.elfadili.wheresmyplaces.fragment.FragmentStep4;

public class WelcomeAdapter extends FragmentPagerAdapter {
    public WelcomeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new FragmentStep1();
            case 1:
                return new FragmentStep2();
            case 2:
                return new FragmentStep3();
            case 3:
                return new FragmentStep4();

            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}