package com.example.vivaaidemo.demo.presentation.demo.face.detail.fas;

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

public class FasDetectViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<String> message;
    private final MutableLiveData<String> data;
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
    public FasDetectViewModel() {
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

    public MutableLiveData<String> getData() {
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

    public void fasDetect(Context context, String image) {
        if (TextUtils.isEmpty(image)) {
            message.setValue("Please fill the image");
            return;
        }

        progress.postValue(true);
        VccAiRequestCallback callback = new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                progress.postValue(false);
                try {
                    String msg = result.getMessage();
                    data.postValue(msg);
                    message.postValue(msg);
                } catch (Exception e) {
                    message.setValue(e.getMessage());
                }
            }

            @Override
            public void fail(int code, String msg) {
                progress.postValue(false);
                data.postValue(msg);
                message.postValue(String.format("Error[%s] : %s", code, msg));
            }
        };
        if (checkPermission(context)) {
            manager.runBackground(() -> manager.face().fasDetect(callback, image));
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
