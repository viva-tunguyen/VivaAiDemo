package com.example.vivaaidemo.demo.presentation.demo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.vivaaidemo.databinding.FragmentDemoBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class DemoFragment extends BaseFragment<FragmentDemoBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private DemoViewModel viewModel;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public DemoFragment() {
        super(FragmentDemoBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DemoAdapter adapter = new DemoAdapter(requireActivity());
        binding.demoPager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.demoTab, binding.demoPager, (tabView, position) -> {
            DemoViewModel.DemoTab tab = viewModel.getTab(position);
            if (tab != null) {
                String text = getActivity().getString(tab.getTitle());
                tabView.setText(text);
            }
        });
        tabLayoutMediator.attach();

        binding.demoTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                binding.demoPager.setCurrentItem(position);
                binding.demoTab.selectTab(binding.demoTab.getTabAt(position));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(DemoViewModel.class);
        viewModel.getTabs().observe(requireActivity(), adapter::setTabs);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
}
