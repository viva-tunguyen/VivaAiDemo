package com.example.vivaaidemo.demo.presentation.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vivaaidemo.databinding.FragmentSettingBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;

public class SettingFragment extends BaseFragment<FragmentSettingBinding> {
    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public SettingFragment() {
        super(FragmentSettingBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            PackageManager manager = requireContext().getPackageManager();
            String packageName = requireContext().getPackageName();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            String version = info.versionName;

            binding.versionNumber.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        binding.documentLink.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://mysoha.notion.site/T-i-li-u-t-ch-h-p-5480b9e710ef48b4a831332568214f27?pvs=4"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }
}

