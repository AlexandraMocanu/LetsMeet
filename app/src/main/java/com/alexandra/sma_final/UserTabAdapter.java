package com.alexandra.sma_final;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class UserTabAdapter extends FragmentStatePagerAdapter {

    public UserTabAdapter(FragmentManager fm){
        super(fm);
    }

    @Override public Fragment getItem(int position) {
        switch (position){
            case 0: return new ActiveRequests();
            case 1: return new Score();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Active Requests";
            case 1: return "Score";
            default: return null;
        }
    }
}
