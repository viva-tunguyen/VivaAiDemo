package com.example.vivaaidemo.demo.presentation.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.vivaaidemo.databinding.FragmentPagerBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PagerFragment extends BaseFragment<FragmentPagerBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private PagerViewModel viewModel;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public PagerFragment() {
        super(FragmentPagerBinding::inflate);
    }

    /* **********************************************************************
     * Life Cycle
     ********************************************************************** */
    @Override
    @SuppressLint("UseCompatLoadingForDrawables")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PagerAdapter adapter = new PagerAdapter(requireActivity());
        binding.viewpager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewpager, (tab, position) -> {
            PagerViewModel.PagerTab item = viewModel.getTab(position);
            tab.setText(getResources().getString(item.getTitle()));
            tab.setIcon(getResources().getDrawable(item.getIcon()));
        });
        tabLayoutMediator.attach();
        binding.viewpager.setUserInputEnabled(false);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                binding.viewpager.setCurrentItem(position);
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(PagerViewModel.class);
        viewModel.getTabs().observe(requireActivity(), adapter::setTabs);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
}
