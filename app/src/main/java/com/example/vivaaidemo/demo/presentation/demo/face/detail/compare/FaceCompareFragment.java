package com.example.vivaaidemo.demo.presentation.demo.face.detail.compare;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.vivaaidemo.databinding.FragmentFaceCompareBinding;
import com.example.vivaaidemo.databinding.LayoutOcrDetailResultBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.common.FileUtil;
import com.example.vivaaidemo.demo.presentation.demo.face.image.ImagePreviewFragment;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;

public class FaceCompareFragment extends BaseFragment<FragmentFaceCompareBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final String FACE_IMAGE_TYPE = "image/*";
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private final int FACE_REGISTER_REQ_PERMISSION_BELOW_11_CODE = 300;
    private static final String TAG = FaceCompareFragment.class.getSimpleName();
    private FaceCompareViewModel viewModel;
    private ActivityResultLauncher<Intent> firstImagePicker, secondImagePicker, permissionLauncher;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceCompareFragment() {
        super(FragmentFaceCompareBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FaceCompareViewModel.class);

        binding.log.setVisibility(logVisibility);
        binding.logTitle.setVisibility(logVisibility);

        binding.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        firstImagePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri fileUri = data.getData();
                            View enable = binding.image1Enable.getRoot();
                            View disable = binding.image1Disable.getRoot();
                            ImageView icon = binding.image1Enable.icon;
                            TextView path = binding.image1Enable.path;
                            imageHandle(fileUri, enable, disable, icon, path);

                        }
                    }
                });
        secondImagePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri fileUri = data.getData();
                            View enable = binding.image2Enable.getRoot();
                            View disable = binding.image2Disable.getRoot();
                            ImageView icon = binding.image2Enable.icon;
                            TextView path = binding.image2Enable.path;
                            imageHandle(fileUri, enable, disable, icon, path);
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

        binding.image1Disable.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType(FACE_IMAGE_TYPE);
            firstImagePicker.launch(intent);
        });
        binding.image1Enable.delete.setOnClickListener(v -> {
            binding.image1Disable.getRoot().setVisibility(View.VISIBLE);
            binding.image1Enable.getRoot().setVisibility(View.GONE);
        });

        binding.image2Disable.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType(FACE_IMAGE_TYPE);
            secondImagePicker.launch(intent);
        });
        binding.image2Enable.delete.setOnClickListener(v -> {
            binding.image2Disable.getRoot().setVisibility(View.VISIBLE);
            binding.image2Enable.getRoot().setVisibility(View.GONE);
        });

        binding.compare.setOnClickListener(view1 -> {
            String image1 = binding.image1Enable.path.getText().toString();
            String image2 = binding.image2Enable.path.getText().toString();
            viewModel.compare(requireContext(), image1, image2);
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
                binding.compare.setEnabled(!isShow);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FACE_REGISTER_REQ_PERMISSION_BELOW_11_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
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

    private void createResultItem(String key, Object value) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LayoutOcrDetailResultBinding itemBinding = LayoutOcrDetailResultBinding.inflate(inflater, null, false);
        itemBinding.text.setText(String.format("%s = %s", key, value));

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        binding.texts.addView(itemBinding.getRoot(), params);
    }

    private void imageHandle(Uri image, View enableRoot, View disableRoot, ImageView enableIcon, TextView enableName) {
        String path = new FileUtil(requireContext(), requireActivity()).getPath(image);
        if (!TextUtils.isEmpty(path)) {
            disableRoot.setVisibility(View.GONE);
            enableRoot.setVisibility(View.VISIBLE);

            Glide.with(requireContext()).load(image).into(enableIcon);
            enableName.setText(path);

            enableIcon.setOnClickListener(v -> {
                FragmentManager manager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = ImagePreviewFragment.newInstance(image);
                transaction.add(R.id.tabContainer, fragment, null).addToBackStack(null).commit();
            });
        } else {
            Snackbar.make(binding.getRoot(), "Cannot find image absolute path", Snackbar.LENGTH_SHORT).show();
        }
    }
}