package com.example.vivaaidemo.demo.presentation.demo.face.list;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vivaaidemo.R;
import com.example.vivaaidemo.databinding.FragmentFaceBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;

public class FaceFragment extends BaseFragment<FragmentFaceBinding> {

    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private FaceAdapter adapter;
    private FaceViewModel viewModel;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceFragment() {
        super(FragmentFaceBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        adapter = new FaceAdapter(requireContext(), (item, position) -> {
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = item.getFragment();
            transaction.replace(R.id.tabContainer, fragment, null).addToBackStack(null).commit();
        });
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(FaceViewModel.class);
        viewModel.getTabs().observe(requireActivity(), data -> adapter.setTabs(data));

    }
}