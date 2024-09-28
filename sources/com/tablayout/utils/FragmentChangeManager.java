package com.tablayout.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.Iterator;

public class FragmentChangeManager {
    private int mContainerViewId;
    private int mCurrentTab;
    private FragmentManager mFragmentManager;
    private ArrayList<Fragment> mFragments;

    public FragmentChangeManager(FragmentManager fm, int containerViewId, ArrayList<Fragment> fragments) {
        this.mFragmentManager = fm;
        this.mContainerViewId = containerViewId;
        this.mFragments = fragments;
        initFragments();
    }

    private void initFragments() {
        Iterator<Fragment> it = this.mFragments.iterator();
        while (it.hasNext()) {
            Fragment fragment = it.next();
            this.mFragmentManager.beginTransaction().add(this.mContainerViewId, fragment).hide(fragment).commit();
        }
        setFragments(0);
    }

    public void setFragments(int index) {
        for (int i = 0; i < this.mFragments.size(); i++) {
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            Fragment fragment = this.mFragments.get(i);
            if (i == index) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commit();
        }
        this.mCurrentTab = index;
    }

    public int getCurrentTab() {
        return this.mCurrentTab;
    }

    public Fragment getCurrentFragment() {
        return this.mFragments.get(this.mCurrentTab);
    }
}
