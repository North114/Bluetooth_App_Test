package com.test.dan.myactionbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by dan on 2015/11/12.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titleList;

    public MyFragmentPagerAdapter(FragmentManager fm,List<Fragment> fl,List<String> ls) {
        super(fm);
        this.fragmentList = fl;
        this.titleList = ls;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
        //return 0;
    }
}
