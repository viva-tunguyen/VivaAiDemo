package com.example.vivaaidemo.demo.presentation.demo.face.detail.compare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;
import vcc.viva.ai.bin.entity.face.CompareFace;

public class FaceCompareViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<String> message;
    private final MutableLiveData<CompareFace> data;
    private final MutableLiveData<Boolean> progress;
    private final MutableLiveData<List<String>> permissions;

    private final List<String> requiredPermissions = new ArrayList<String>() {
        {
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    };

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceCompareViewModel() {
        this.data = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.progress = new MutableLiveData<>(false);
        this.permissions = new MutableLiveData<>();
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    protected void clear() {
        data.postValue(null);
        message.postValue(null);
        progress.postValue(null);
        permissions.postValue(null);
    }

    public MutableLiveData<CompareFace> getData() {
        return data;
    }


    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Boolean> getProgress() {
        return progress;
    }

    public MutableLiveData<List<String>> getPermissions() {
        return permissions;
    }

    public void compare(Context context, String image1, String image2) {
        if (TextUtils.isEmpty(image1) || TextUtils.isEmpty(image2)) {
            message.setValue("Please fill all image");
            return;
        }
        if (checkPermission(context)) {
            progress.postValue(true);
            VccAiRequestCallback callback = new VccAiRequestCallback() {
                @Override
                public void success(VccAiResult result) {
                    progress.postValue(false);
                    try {
                        CompareFace data = (CompareFace) result.getData();
                        FaceCompareViewModel.this.data.postValue(data);
                        String msg = result.getMessage();
                        message.postValue(msg);
                    } catch (Exception e) {
                        message.setValue(e.getMessage());
                    }
                }

                @Override
                public void fail(int code, String msg) {
                    progress.postValue(false);
                    message.postValue(String.format("Error[%s] : %s", code, msg));
                }
            };
            manager.runBackground(() -> manager.face().compare(callback, image1, image2));
        }
    }

    private boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            boolean isGrant = Environment.isExternalStorageManager();
            if (!isGrant) {
                permissions.postValue(new ArrayList<String>() {{
                    add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                }});
                progress.postValue(false);
            }
            return isGrant;
        } else {
            boolean result = true;
            List<String> missingPermission = new ArrayList<>();
            for (String permission : requiredPermissions) {
                boolean isGrant = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                result &= isGrant;
                if (!isGrant) {
                    missingPermission.add(permission);
                }
            }
            if (missingPermission.size() > 0) {
                permissions.postValue(missingPermission);
                progress.postValue(false);
            }
            return result;
        }
    }
}
