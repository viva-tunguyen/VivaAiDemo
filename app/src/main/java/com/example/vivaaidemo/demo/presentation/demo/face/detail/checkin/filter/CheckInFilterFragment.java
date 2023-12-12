package com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.filter;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.R;
import com.example.vivaaidemo.databinding.FragmentFaceCheckinFilterBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.common.Utility;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.CheckinAdapter;
import com.google.android.gms.common.AccountPicker;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class CheckInFilterFragment extends BaseFragment<FragmentFaceCheckinFilterBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private static final String TAG = CheckInFilterFragment.class.getSimpleName();
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private CheckInFilterViewModel viewModel;
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private CheckinAdapter adapter;
    private ActivityResultLauncher<Intent> accountPickerLauncher;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public CheckInFilterFragment() {
        super(FragmentFaceCheckinFilterBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CheckInFilterViewModel.class);
        binding.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.log.setVisibility(logVisibility);
        binding.logTitle.setVisibility(logVisibility);

        binding.field.codeContent.setText("108878");
        binding.field.fromContent.setText("01-11-2021");
        binding.field.toContent.setText("5-12-2023");
        viewModel.addEmail("nguyenmanhcuong02@channelvn.net");

        binding.field.email.setOnClickListener(v -> {
            AccountPicker.AccountChooserOptions options =
                    new AccountPicker.AccountChooserOptions.Builder()
                            .setAllowableAccountsTypes(Collections.singletonList("com.google"))
                            .build();
            Intent intent = AccountPicker.newChooseAccountIntent(options);

            accountPickerLauncher.launch(intent);
        });
        binding.field.fromContent.setOnClickListener(v -> {
            String text = binding.field.toContent.getText().toString();
            long time = Utility.dateToMillisecond(text, DATE_FORMAT);
            datePicker(binding.field.fromContent, 0, time);
        });
        binding.field.toContent.setOnClickListener(view1 -> {
            String text = binding.field.fromContent.getText().toString();
            long time = Utility.dateToMillisecond(text, DATE_FORMAT);
            datePicker(binding.field.toContent, time, 0);
        });
        binding.field.checkOption.setChecked(true);
        binding.detect.setOnClickListener(v -> {
            String eCode = binding.field.codeContent.getText().toString();
            String fromDate = binding.field.fromContent.getText().toString();
            String toDate = binding.field.toContent.getText().toString();
            List<String> emails = viewModel.getEmails().getValue();
            viewModel.checkInHistoryFilter(eCode, fromDate, toDate, emails);
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

                adapter = new CheckinAdapter(requireContext(), (item, position) -> {
                });
                binding.resultList.setAdapter(adapter);
                binding.resultList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

                viewModel = new ViewModelProvider(requireActivity()).get(CheckInFilterViewModel.class);
                viewModel.getData().observe(requireActivity(), d -> adapter.setList(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        viewModel.getEmails().observe(requireActivity(), emails -> {
            binding.field.emailSelection.removeAllViews();
            for (String email : emails) {
                Chip chip = new Chip(requireContext());
                chip.setText(email);
                chip.setChipBackgroundColorResource(R.color.colorPrimary);
                chip.setCloseIconVisible(true);
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                chip.setOnCloseIconClickListener(v -> {
                    viewModel.removeEmail(email);
                    binding.field.emailSelection.removeView(v);
                });
                binding.field.emailSelection.addView(chip);
            }
        });

        accountPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                            viewModel.addEmail(accountName);
                        }
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
    private void datePicker(TextInputEditText view, long min, long max) {
        final Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (v, year, monthOfYear, dayOfMonth) -> {
            String text = String.format("%s-%s-%s", dayOfMonth, monthOfYear + 1, year);
            view.setText(text);
        }, y, m, d);
        datePickerDialog.show();
        try {
            if (min > 0) {
                datePickerDialog.getDatePicker().setMinDate(min);
            }
            if (max > 0) {
                datePickerDialog.getDatePicker().setMaxDate(max);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}