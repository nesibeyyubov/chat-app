package com.nesib.chatapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> fragmentTitleList;

    public PagerAdapter(@NonNull FragmentManager fm, ArrayList<Fragment>fragmentList,ArrayList<String> fragmentTitleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.fragmentTitleList = fragmentTitleList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}
