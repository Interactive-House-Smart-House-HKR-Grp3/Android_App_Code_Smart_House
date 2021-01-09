package com.example.home_android_interface;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LivingRoom livingRoom = new LivingRoom();
                return livingRoom;
            case 1:
                Hallway hallway = new Hallway();
                return hallway;
            case 2:
                OutdoorDevices outdoorDevices = new OutdoorDevices();
                return outdoorDevices;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}