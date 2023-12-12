package com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.list;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.databinding.FragmentFaceCheckinBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.CheckinAdapter;
import com.google.android.material.snackbar.Snackbar;


public class CheckInFragment extends BaseFragment<FragmentFaceCheckinBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private static final String TAG = CheckInFragment.class.getSimpleName();
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private CheckInViewModel viewModel;
    private CheckinAdapter adapter;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public CheckInFragment() {
        super(FragmentFaceCheckinBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CheckInViewModel.class);

        binding.log.setVisibility(logVisibility);
        binding.logTitle.setVisibility(logVisibility);

        binding.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.field.recordNumber.setText("5");
        binding.recordNumberPicker.setMinValue(5);
        binding.recordNumberPicker.setMaxValue(100);

        binding.recordNumberPicker.setOnValueChangedListener((numberPicker, i, i1) -> {
            binding.recordNumberPicker.setValue(numberPicker.getValue());
            binding.field.recordNumber.setText(String.valueOf(numberPicker.getValue()));
        });
        binding.detect.setOnClickListener(v -> {
            String image = binding.field.recordNumber.getText().toString();
            viewModel.checkInHistory(image);
        });

        viewModel.getMessage().observe(requireActivity(), message -> {
            try {
                if (!TextUtils.isEmpty(message)) {
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        viewModel.getProgress().observe(requireActivity(), isShow -> {
            try {
                if (isShow == null) {
                    binding.progress.setVisibility(View.INVISIBLE);
                    return;
                }
                int visibility = isShow ? View.VISIBLE : View.INVISIBLE;
                binding.progress.setVisibility(visibility);
                binding.detect.setEnabled(!isShow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        viewModel.getData().observe(requireActivity(), data -> {
            try {
                if (data == null) return;
                Log.d(TAG, "get data msg: " + gson.toJson(data));
                binding.log.setText(gson.toJson(data));

                if (data.size() > 0) {
                    adapter = new CheckinAdapter(requireContext(), (item, position) -> {});
                    binding.resultList.setAdapter(adapter);
                    binding.resultList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

                    viewModel = new ViewModelProvider(requireActivity()).get(CheckInViewModel.class);
                    viewModel.getData().observe(requireActivity(), d -> adapter.setList(data));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.field.recordNumber.setText("5");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.clear();
    }
}