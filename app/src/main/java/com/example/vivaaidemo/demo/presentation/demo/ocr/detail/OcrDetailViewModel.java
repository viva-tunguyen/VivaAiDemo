package com.example.vivaaidemo.demo.presentation.demo.ocr.detail;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;
import com.example.vivaaidemo.demo.common.Utility;
import com.example.vivaaidemo.demo.presentation.demo.ocr.list.OcrViewModel;

import java.util.ArrayList;
import java.util.List;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;
import vcc.viva.ai.bin.delegate.OcrDelegate;
import vcc.viva.ai.bin.entity.ocr.OCR;


public class OcrDetailViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<OcrViewModel.OcrType> type;
    private final MutableLiveData<OCR> data;
    private final MutableLiveData<String> message;
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
    public OcrDetailViewModel() {
        this.type = new MutableLiveData<>();
        this.data = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.progress = new MutableLiveData<>(false);
        this.permissions = new MutableLiveData<>();
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    protected void clear() {
        type.postValue(null);
        data.postValue(null);
        message.postValue(null);
        progress.postValue(null);
        permissions.postValue(null);
    }

    public MutableLiveData<OcrViewModel.OcrType> getType() {
        return type;
    }

    public MutableLiveData<OCR> getData() {
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

    public void setType(OcrViewModel.OcrType type) {
        this.type.postValue(type);
    }

    public void ocr(Context context, String link) {
        if (TextUtils.isEmpty(link)) {
            message.setValue("Please fill some text");
            return;
        }

        progress.postValue(true);
        VccAiRequestCallback callback = new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                progress.postValue(false);
                try {
                    OCR ocr = (OCR) result.getData();
                    data.postValue(ocr);
                    message.postValue("Detect Success");
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

        if (Utility.isUrl(link)) {
            get(callback, link);
        } else {
            if (checkPermission(context)) {
                post(callback, link);
            }
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

    private void get(VccAiRequestCallback callback, String link) {
        manager.runBackground(() -> {
            OcrDelegate delegate = manager.ocr();
            switch (type.getValue()) {
                case ID_CARD:
                    delegate.idCardUrl(callback, link);
                    break;
                case PASSPORT:
                    delegate.passportUrl(callback, link);
                    break;
                case HEALTH_INSURANCE:
                    delegate.healthInsuranceUrl(callback, link);
                    break;
                case IELTS_CERTIFICATE:
                    delegate.ieltsCertificateUrl(callback, link);
                    break;
                case VISA:
                    delegate.visaUrl(callback, link);
                    break;
                case VEHICLE_LICENSE:
                    delegate.vehicleLicenseUrl(callback, link);
                    break;
                case VEHICLE_REGISTRATION:
                    delegate.vehicleRegistrationUrl(callback, link);
                    break;
                case VEHICLE_INSPECTION:
                    delegate.vehicleInspectionUrl(callback, link);
                    break;
                case PAYMENT_ORDER:
                    delegate.paymentOrderUrl(callback, link);
                    break;
                case BUSINESS_REGISTRATION:
                    delegate.businessRegistrationUrl(callback, link);
                    break;
                case DATA_TABLE:
                    delegate.dataTableUrl(callback, link);
                    break;
                case BILL:
                    delegate.billUrl(callback, link);
                    break;
                case KEY_VALUE_PAIR:
                    delegate.keyValuePairUrl(callback, link);
                    break;
                default:
                    message.postValue("Type invalid");
                    break;
            }
        });
    }

    private void post(VccAiRequestCallback callback, String path) {
        manager.runBackground(() -> {
            OcrDelegate delegate = manager.ocr();
            switch (type.getValue()) {
                case ID_CARD:
                    delegate.idCardLocal(callback, path);
                    break;
                case PASSPORT:
                    delegate.passportLocal(callback, path);
                    break;
                case HEALTH_INSURANCE:
                    delegate.healthInsuranceLocal(callback, path);
                    break;
                case IELTS_CERTIFICATE:
                    delegate.ieltsCertificateLocal(callback, path);
                    break;
                case VISA:
                    delegate.visaLocal(callback, path);
                    break;
                case VEHICLE_LICENSE:
                    delegate.vehicleLicenseLocal(callback, path);
                    break;
                case VEHICLE_REGISTRATION:
                    delegate.vehicleRegistrationLocal(callback, path);
                    break;
                case VEHICLE_INSPECTION:
                    delegate.vehicleInspectionLocal(callback, path);
                    break;
                case PAYMENT_ORDER:
                    delegate.paymentOrderLocal(callback, path);
                    break;
                case BUSINESS_REGISTRATION:
                    delegate.businessRegistrationLocal(callback, path);
                    break;
                case DATA_TABLE:
                    delegate.dataTableLocal(callback, path);
                    break;
                case BILL:
                    delegate.billLocal(callback, path);
                    break;
                case KEY_VALUE_PAIR:
                    delegate.keyValuePairLocal(callback, path);
                    break;
                default:
                    message.postValue("Type invalid");
                    break;
            }
        });
    }
}
