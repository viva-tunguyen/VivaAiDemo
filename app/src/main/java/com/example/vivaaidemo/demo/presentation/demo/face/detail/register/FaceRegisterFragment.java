package com.example.vivaaidemo.demo.presentation.demo.face.detail.register;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.R;
import com.example.vivaaidemo.databinding.FragmentFaceRegisterBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.common.FileUtil;
import com.example.vivaaidemo.demo.presentation.demo.face.image.ImagePreviewFragment;
import com.google.android.material.snackbar.Snackbar;


public class FaceRegisterFragment extends BaseFragment<FragmentFaceRegisterBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final String FACE_IMAGE_TYPE = "image/*";
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private final int FACE_REGISTER_REQ_PERMISSION_BELOW_11_CODE = 300;
    private static final String TAG = FaceRegisterFragment.class.getSimpleName();
    private FaceRegisterViewModel viewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher, permissionLauncher;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceRegisterFragment() {
        super(FragmentFaceRegisterBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FaceRegisterViewModel.class);

        binding.log.setVisibility(logVisibility);
        binding.logTitle.setVisibility(logVisibility);

        binding.back.setOnClickListener(view -> getParentFragmentManager().popBackStack());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri fileUri = data.getData();
                            String path = new FileUtil(requireContext(), requireActivity()).getPath(fileUri);
                            if (!TextUtils.isEmpty(path)) {
                                Glide.with(requireContext()).load(fileUri).into(binding.imageEnable.icon);
                                binding.imageEnable.path.setText(path);

                                binding.imageDisable.getRoot().setVisibility(View.GONE);
                                binding.imageEnable.getRoot().setVisibility(View.VISIBLE);

                                binding.imageEnable.icon.setOnClickListener(view -> {
                                    FragmentManager manager = requireActivity().getSupportFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    Fragment fragment = ImagePreviewFragment.newInstance(fileUri);
                                    transaction.add(R.id.tabContainer, fragment, null).addToBackStack(null).commit();
                                });
                            } else {
                                Snackbar.make(binding.getRoot(), "Cannot find image absolute path", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (Environment.isExternalStorageManager()) {
                                Log.d(TAG, "registerForActivityResult: " + "All permissions allowed");
                            } else {
                                Snackbar.make(binding.getRoot(), "Allow permission for storage access!", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                });

        binding.imageDisable.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType(FACE_IMAGE_TYPE);
            imagePickerLauncher.launch(intent);
        });

        binding.imageEnable.delete.setOnClickListener(view -> {
            binding.imageDisable.getRoot().setVisibility(View.VISIBLE);
            binding.imageEnable.getRoot().setVisibility(View.GONE);
        });

        binding.nameContent.setText("Nguyen The Tung");
        binding.emailContent.setText("08062002thetung@gmail.com");
        binding.eCodeContent.setText("008602");
        binding.faceMaskOption.setChecked(false);

        binding.detect.setOnClickListener(view -> {
            String image = binding.imageEnable.path.getText().toString();
            String name = binding.nameContent.getText().toString();
            String email = binding.emailContent.getText().toString();
            String eCode = binding.eCodeContent.getText().toString();
            boolean isFaceMask = binding.faceMaskOption.isChecked();
            viewModel.face(requireContext(), image, name, email, eCode, isFaceMask);
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
        viewModel.getPermissions().observe(requireActivity(), permissions -> {
            if (permissions == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                    permissionLauncher.launch(intent);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    permissionLauncher.launch(intent);
                }
            } else {
                String[] array = new String[permissions.size()];
                permissions.toArray(array);
                ActivityCompat.requestPermissions(requireActivity(), array, FACE_REGISTER_REQ_PERMISSION_BELOW_11_CODE);
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
        viewModel.getResult().observe(requireActivity(), results -> {
            Log.d(TAG, "get data: " + results);
            binding.log.setText(results);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FACE_REGISTER_REQ_PERMISSION_BELOW_11_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                        Log.d(TAG, "onRequestPermissionsResult: " + "All permissions allowed");
                    } else {
                        Snackbar.make(binding.getRoot(), "Allow permission for storage access!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            default:
                Log.d(TAG, "onRequestPermissionsResult: " + "Cannot find corresponded code for permission");
                break;
        }
    }
}