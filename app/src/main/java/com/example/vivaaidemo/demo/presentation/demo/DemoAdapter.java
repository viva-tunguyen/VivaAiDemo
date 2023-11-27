package com.example.vivaaidemo.demo.presentation.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vivaaidemo.demo.presentation.common.EmptyFragment;

import java.util.List;

public class DemoAdapter extends FragmentStateAdapter {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private List<DemoViewModel.DemoTab> tabs;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public DemoAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /* **********************************************************************
     * Override
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
        DemoViewModel.DemoTab tab = tabs.get(position);
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
    public void setTabs(List<DemoViewModel.DemoTab> tabs) {
        this.tabs = tabs;
        notifyDataSetChanged();
    }
}