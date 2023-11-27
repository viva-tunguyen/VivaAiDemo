package com.example.vivaaidemo.demo.presentation.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vivaaidemo.databinding.FragmentErrorBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;


public class ErrorFragment extends BaseFragment<FragmentErrorBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private String message;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public ErrorFragment(String message) {
        super(FragmentErrorBinding::inflate);
        this.message = message;
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!TextUtils.isEmpty(message)) {
            binding.content.setText(message);
        }

        binding.button.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }
}