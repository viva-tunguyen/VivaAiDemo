package com.example.vivaaidemo.demo.presentation.common;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.vivaaidemo.R;
import com.example.vivaaidemo.databinding.CommonFragmentEmptyBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;


public class EmptyFragment extends BaseFragment<CommonFragmentEmptyBinding> {
    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public EmptyFragment() {
        super(CommonFragmentEmptyBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Glide.with(this).load(R.drawable.bg_coming_soon).into(binding.comingSoon);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
}
