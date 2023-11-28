package com.example.vivaaidemo.demo.presentation.demo.ocr.detail;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.databinding.FragmentOcrDetailBinding;
import com.example.vivaaidemo.databinding.LayoutOcrDetailResultBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.common.FileUtil;
import com.example.vivaaidemo.demo.common.Utility;
import com.example.vivaaidemo.demo.presentation.demo.ocr.list.OcrViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

public class OcrDetailFragment extends BaseFragment<FragmentOcrDetailBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final int OCR_DETAIL_REQ_IMAGE_CODE = 200;
    private final int OCR_DETAIL_REQ_PERMISSION_BELOW_11_CODE = 300;
    private final int OCR_DETAIL_REQ_PERMISSION_CODE = 301;
    private final int OCR_DETAIL_REQ_CODE = 200;
    private final String OCR_DETAIL_IMAGE_TYPE = "image/*";
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private static final String TAG = OcrDetailFragment.class.getSimpleName();
    private static final String ARG_TITLE = String.format("%s:key:title", TAG);

    private OcrDetailViewModel viewModel;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public OcrDetailFragment() {
        super(FragmentOcrDetailBinding::inflate);
    }

    public static OcrDetailFragment newInstance(OcrViewModel.OcrType type) {
        OcrDetailFragment fragment = new OcrDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, new Gson().toJson(type));
        fragment.setArguments(args);
        return fragment;
    }

    /* **********************************************************************
     * Life Cycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        Bundle bundle = getArguments();
        OcrViewModel.OcrType t = null;
        if (bundle != null) {
            t = gson.fromJson(bundle.getString(ARG_TITLE), OcrViewModel.OcrType.class);
        }
        if (t == null) {
            getParentFragmentManager().popBackStack();
            return;
        }

        binding.ocrField.log.setVisibility(logVisibility);
        binding.actionBar.back.setOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });
        binding.ocrField.file.setOnClickListener(file -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType(OCR_DETAIL_IMAGE_TYPE);
            this.startActivityForResult(intent, OCR_DETAIL_REQ_IMAGE_CODE);
        });
        binding.ocrField.detect.setOnClickListener(view -> {
            try {
                Utility.hideKeyboard(requireActivity());

                String link = binding.ocrField.textContent.getText().toString();
                viewModel.ocr(requireContext(), link);
                Glide.with(this).load(link).into(binding.ocrField.preview);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(OcrDetailViewModel.class);
        viewModel.setType(t);

        viewModel.getType().observe(requireActivity(), type -> {
            try {
                if (type != null) {
                    binding.actionBar.title.setText(type.title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    binding.ocrField.progress.setVisibility(View.INVISIBLE);
                    return;
                }
                int visibility = isShow ? View.VISIBLE : View.INVISIBLE;
                binding.ocrField.progress.setVisibility(visibility);
                binding.ocrField.detect.setEnabled(!isShow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        viewModel.getPermissions().observe(requireActivity(), permissions -> {
            if (permissions == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", requireActivity().getPackageName())));
                    startActivityForResult(intent, OCR_DETAIL_REQ_PERMISSION_CODE);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, OCR_DETAIL_REQ_PERMISSION_CODE);
                }
            } else {
                String[] array = new String[permissions.size()];
                permissions.toArray(array);
                ActivityCompat.requestPermissions(getActivity(), array, OCR_DETAIL_REQ_PERMISSION_BELOW_11_CODE);
            }
        });
        viewModel.getData().observe(requireActivity(), data -> {
            try {
                if (data == null) return;
                Log.d(TAG, "get data: ");

                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                binding.ocrField.texts.removeAllViews();
                if (data.data != null) {
                    Object object = data.data;
                    Log.d(TAG, "get data - data object : " + gson.toJson(object));

                    Field[] fields = object.getClass().getDeclaredFields();
                    for (Field item : fields) {
                        createResultItem(item.getName(), item.get(object));
                    }

                    binding.ocrField.log.setText(builder.toJson(object));
                } else {
                    JSONObject object = data.object;
                    Log.d(TAG, "get data - no data object, user json instead : " + gson.toJson(object));

                    Iterator<String> keys = object.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        createResultItem(key, object.get(key));
                    }

                    binding.ocrField.log.setText(builder.toJson(object));
                }

                Log.d(TAG, "get data: end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createResultItem(String key, Object value) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LayoutOcrDetailResultBinding itemBinding = LayoutOcrDetailResultBinding.inflate(inflater, null, false);
        itemBinding.text.setText(String.format("%s = %s", key, value));

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        binding.ocrField.texts.addView(itemBinding.getRoot(), params);
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
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OCR_DETAIL_REQ_IMAGE_CODE && resultCode == RESULT_OK) {
            try {
                Uri fileUri = data.getData();
                String path = new FileUtil(requireContext()).getPath(fileUri);
                Log.d(TAG, "onActivityResult: path absolute: " + path);
                binding.ocrField.textContent.setText(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == OCR_DETAIL_REQ_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Snackbar.make(binding.getRoot(), "Allow permission for storage access!", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case OCR_DETAIL_REQ_PERMISSION_BELOW_11_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                    } else {
                        Snackbar.make(binding.getRoot(), "Allow permission for storage access!", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
        }
    }
}