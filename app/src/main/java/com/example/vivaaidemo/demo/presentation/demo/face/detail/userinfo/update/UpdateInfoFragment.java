package com.example.vivaaidemo.demo.presentation.demo.face.detail.userinfo.update;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.databinding.FragmentFaceUpdateInfoBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.google.android.material.snackbar.Snackbar;

public class UpdateInfoFragment extends BaseFragment<FragmentFaceUpdateInfoBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private UpdateInfoViewModel viewModel;
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private static final String TAG = UpdateInfoFragment.class.getSimpleName();

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public UpdateInfoFragment() {
        super(FragmentFaceUpdateInfoBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(UpdateInfoViewModel.class);
        binding.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.log.setVisibility(logVisibility);
        binding.logTitle.setVisibility(logVisibility);

        binding.nameContent.setText("Nguyen Manh Cuong");
        binding.emailContent.setText("nguyenmanhcuong02@tech.admicro.vn");
        binding.eCodeContent.setText("108878");
        binding.teleChatContent.setText("1248720452");

        binding.update.setOnClickListener(view1 -> {
            String eCode = binding.eCodeContent.getText().toString();
            String email = binding.emailContent.getText().toString();
            String name = binding.nameContent.getText().toString();
            String teleChatId = binding.teleChatContent.getText().toString();
            viewModel.update(eCode, email, name, teleChatId);
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
                binding.update.setEnabled(!isShow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        viewModel.getData().observe(requireActivity(), data -> {
            try {
                if (data == null) return;
                binding.texts.setText(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        viewModel.getResults().observe(requireActivity(), res -> {
            try {
                if (res == null) return;
                Log.d(TAG, "get data: " + res);
                binding.log.setText(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.clear();
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
}