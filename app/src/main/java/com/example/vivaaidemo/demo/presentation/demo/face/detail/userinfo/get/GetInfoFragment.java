package com.example.vivaaidemo.demo.presentation.demo.face.detail.userinfo.get;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.databinding.FragmentFaceGetInfoBinding;
import com.example.vivaaidemo.databinding.LayoutOcrDetailResultBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;

public class GetInfoFragment extends BaseFragment<FragmentFaceGetInfoBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private static final String TAG = GetInfoFragment.class.getSimpleName();
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private GetInfoViewModel viewModel;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public GetInfoFragment() {
        super(FragmentFaceGetInfoBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GetInfoViewModel.class);
        binding.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.log.setVisibility(logVisibility);
        binding.logTitle.setVisibility(logVisibility);

        binding.emailContent.setText("nguyenmanhcuong02@tech.admicro.vn");

        binding.get.setOnClickListener(v -> {
            String email = binding.emailContent.getText().toString();
            viewModel.get(email);
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
                binding.get.setEnabled(!isShow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        viewModel.getData().observe(requireActivity(), data -> {
            try {
                if (data == null) return;
                Log.d(TAG, "get data msg: " + gson.toJson(data));
                binding.log.setText(gson.toJson(data));

                binding.texts.removeAllViews();
                Log.d(TAG, "get data - data object : " + gson.toJson(data));

                Field[] fields = data.getClass().getDeclaredFields();
                for (Field item : fields) {
                    createResultItem(item.getName(), item.get(data));
                }
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
    private void createResultItem(String key, Object value) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LayoutOcrDetailResultBinding itemBinding = LayoutOcrDetailResultBinding.inflate(inflater, null, false);
        itemBinding.text.setText(String.format("%s = %s", key, value));

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        binding.texts.addView(itemBinding.getRoot(), params);
    }
}