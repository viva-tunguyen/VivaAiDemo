package com.example.vivaaidemo.demo.presentation.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vivaaidemo.demo.presentation.common.EmptyFragment;

import java.util.List;

public class PagerAdapter extends FragmentStateAdapter {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private List<PagerViewModel.PagerTab> tabs;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public PagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /* **********************************************************************
     * Life Cycle
     ********************************************************************** */
    @Override
    public int getItemCount() {
        try {
            return tabs.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        PagerViewModel.PagerTab tab = tabs.get(position);
        try {
            Fragment fragment = tab.getFragment();
            if (fragment == null) {
                fragment = (Fragment) tab.getClazz().getConstructor().newInstance();
                tab.setFragment(fragment);
                tabs.set(position, tab);
            }
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
            return new EmptyFragment();
        }
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    public void setTabs(List<PagerViewModel.PagerTab> tabs) {
        this.tabs = tabs;
        notifyDataSetChanged();
    }
}

