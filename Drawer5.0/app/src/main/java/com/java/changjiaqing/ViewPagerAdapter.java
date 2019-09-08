package com.java.changjiaqing;
/***
 * viewpager和fragment的adapter
 */

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> titleList;
    private List<NewsFragment> fragments;
    public ViewPagerAdapter(FragmentManager fm, List<NewsFragment> newsFragment, List<String> titleList) {
        super(fm);
        this.titleList = titleList;
        this.fragments=newsFragment;
    }

    @Override
    public int getCount() {
        return titleList.size();
    }

    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}