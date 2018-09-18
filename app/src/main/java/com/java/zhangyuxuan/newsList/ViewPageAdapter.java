package com.java.zhangyuxuan.newsList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> fragments;

    public ViewPageAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

    public CharSequence getPageTitle(int position)
    {
        return ((NewsListFragment)(fragments.get(position))).getmType();
    }

    public int getCount()
    {
        return fragments != null ? fragments.size() : 0;
    }

    public void setFragments(ArrayList<Fragment> fragments)
    {
        this.fragments = fragments;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}

