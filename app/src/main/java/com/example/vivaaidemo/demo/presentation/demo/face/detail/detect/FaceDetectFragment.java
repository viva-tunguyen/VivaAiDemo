package com.example.vivaaidemo.demo.presentation.demo.face.detail.detect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
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
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.R;
import com.example.vivaaidemo.databinding.FragmentFaceDetectBinding;
import com.example.vivaaidemo.databinding.LayoutOcrDetailResultBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.common.FileUtil;
import com.example.vivaaidemo.demo.common.NetworkUtil;
import com.example.vivaaidemo.demo.presentation.demo.face.image.ImagePreviewFragment;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;

public class FaceDetectFragment extends BaseFragment<FragmentFaceDetectBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final String FACE_IMAGE_TYPE = "image/*";
    private static final int REQUEST_CODE_ENABLE_LOCATION = 201;
    private final int FACE_REGISTER_REQ_PERMISSION_BELOW_11_CODE = 300;
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private static final String TAG = FaceDetectFragment.class.getSimpleName();
    private FaceDetectViewModel viewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher, permissionLauncher;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceDetectFragment() {
        super(FragmentFaceDetectBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FaceDetectViewModel.class);
        binding.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.log.setVisibility(logVisibility);
        binding.logTitle.setVisibility(logVisibility);

        boolean ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ENABLE_LOCATION);
        } else {
            viewModel.setLocation(requireContext(), requireActivity());
        }

        networkChangeReceiver = new NetworkUtil(() -> {
            viewModel.setNetwork(requireContext());
        });
        requireContext().registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri fileUri = data.getData();
                            String path = new FileUtil(requireContext(), requireActivity()).getPath(fileUri);
                            if (!TextUtils.isEmpty(path)) {
                                Glide.with(requireContext()).load(path).into(binding.imageEnable.icon);
                                binding.imageEnable.path.setText(path);

                                binding.imageDisable.getRoot().setVisibility(View.GONE);
                                binding.imageEnable.getRoot().setVisibility(View.VISIBLE);

                                binding.imageEnable.icon.setOnClickListener(view1 -> {
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

        binding.imageDisable.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType(FACE_IMAGE_TYPE);
            imagePickerLauncher.launch(intent);
        });
        binding.imageEnable.delete.setOnClickListener(v -> {
            binding.imageDisable.getRoot().setVisibility(View.VISIBLE);
            binding.imageEnable.getRoot().setVisibility(View.GONE);
        });

        binding.detect.setOnClickListener(v -> {
            viewModel.setLocation(requireContext(), requireActivity());
            String image = binding.imageEnable.path.getText().toString();
            String mac = binding.networkMacContent.getText().toString();
            String ip = binding.networkIpContent.getText().toString();
            String name = binding.networkNameContent.getText().toString();
            String latitude = binding.locationLatitudeContent.getText().toString();
            String longitude = binding.locationLongitudeContent.getText().toString();
            viewModel.faceDetect(requireContext(), image, mac, ip, name, latitude, longitude);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ENABLE_LOCATION);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                    permissionLauncher.launch(intent);
                } catch (Exception e) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ENABLE_LOCATION);
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
        viewModel.getLocation().observe(requireActivity(), location -> {
            if (location != null) {
                String lat = String.valueOf(location.getLatitude());
                String lon = String.valueOf(location.getLongitude());
                binding.locationLatitudeContent.setText(lat);
                binding.locationLongitudeContent.setText(lon);
            } else {
                binding.locationLatitudeContent.setText("");
                binding.locationLongitudeContent.setText("");
            }
        });
        viewModel.getNetwork().observe(requireActivity(), network -> {
            if (network != null) {
                String name = String.valueOf(network.getName());
                String ip = String.valueOf(network.getIpAddress());
                String mac = String.valueOf(network.getMacAddress());
                binding.networkNameContent.setText(name);
                binding.networkIpContent.setText(ip);
                binding.networkMacContent.setText(mac);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setNetwork(requireContext());
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
            case REQUEST_CODE_ENABLE_LOCATION:
                if (grantResults.length > 0) {
                    boolean ACCESS_FINE_LOCATION = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ACCESS_COARSE_LOCATION = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (!(ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION)) {
                        Snackbar.make(binding.getRoot(), "Allow permission for location access!", Snackbar.LENGTH_SHORT)
                                .show();
                        binding.locationLatitudeContent.setText("");
                        binding.locationLongitudeContent.setText("");
                    }
                }
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

    /* **********************************************************************
     * Class
     ********************************************************************** */
    public enum CellularType {
        /* **********************************************************************
         * Area : Type
         ********************************************************************** */
        M2G("2G"),
        M3G("3G"),
        M4G("4G"),
        M5G("5G");

        /* **********************************************************************
         * Area : Variable
         ********************************************************************** */
        private final String tag;

        /* **********************************************************************
         * Area : Constructor
         ********************************************************************** */
        CellularType(String tag) {
            this.tag = tag;
        }

        /* **********************************************************************
         * Area : Function
         ********************************************************************** */
        public String getTag() {
            return tag;
        }
    }
}