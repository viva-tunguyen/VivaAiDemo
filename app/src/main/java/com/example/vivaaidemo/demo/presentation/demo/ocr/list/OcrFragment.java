package com.example.vivaaidemo.demo.presentation.demo.ocr.list;

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
import com.example.vivaaidemo.databinding.FragmentOcrBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.presentation.demo.ocr.detail.OcrDetailFragment;

public class OcrFragment extends BaseFragment<FragmentOcrBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private ItemOcrAdapter adapter;
    private OcrViewModel viewModel;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public OcrFragment() {
        super(FragmentOcrBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ItemOcrAdapter(requireContext(), (item, position) -> {
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = OcrDetailFragment.newInstance(item);
            transaction.replace(R.id.tabContainer, fragment, null).addToBackStack(null).commit();
        });
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(OcrViewModel.class);
        viewModel.getData().observe(requireActivity(), data -> adapter.setTabs(data));
    }
}