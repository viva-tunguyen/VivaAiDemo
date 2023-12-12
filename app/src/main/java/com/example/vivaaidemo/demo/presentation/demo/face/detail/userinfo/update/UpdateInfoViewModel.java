package com.example.vivaaidemo.demo.presentation.demo.face.detail.userinfo.update;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;

public class UpdateInfoViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<String> message;
    private final MutableLiveData<String> results;
    private final MutableLiveData<String> data;
    private final MutableLiveData<Boolean> progress;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public UpdateInfoViewModel() {
        this.data = new MutableLiveData<>();
        this.results = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.progress = new MutableLiveData<>(false);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    protected void clear() {
        data.postValue(null);
        results.postValue(null);
        message.postValue(null);
        progress.postValue(null);
    }

    public MutableLiveData<String> getData() {
        return data;
    }

    public MutableLiveData<String> getResults() {
        return results;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Boolean> getProgress() {
        return progress;
    }


    public void update(String employeeCode, String email, String name, String teleChatId) {
        if (TextUtils.isEmpty(employeeCode) || TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(teleChatId)) {
            message.setValue("Please fill all text");
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
                message.postValue(String.format("Error[%s] : %s", code, msg));
            }
        };
        manager.runBackground(() -> manager.face().updateUserInfo(callback, employeeCode, email, name, teleChatId));
    }
}
