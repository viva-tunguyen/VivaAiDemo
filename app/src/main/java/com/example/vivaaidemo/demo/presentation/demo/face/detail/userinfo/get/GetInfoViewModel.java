package com.example.vivaaidemo.demo.presentation.demo.face.detail.userinfo.get;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;
import vcc.viva.ai.bin.entity.face.UserInfo;

public class GetInfoViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<String> message;
    private final MutableLiveData<UserInfo> data;
    private final MutableLiveData<Boolean> progress;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public GetInfoViewModel() {
        this.data = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.progress = new MutableLiveData<>(false);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    protected void clear() {
        data.postValue(null);
        message.postValue(null);
        progress.postValue(null);
    }

    public MutableLiveData<UserInfo> getData() {
        return data;
    }
    public MutableLiveData<String> getMessage() {
        return message;
    }
    public MutableLiveData<Boolean> getProgress() {
        return progress;
    }

    public void get(String email) {
        if (TextUtils.isEmpty(email)) {
            message.setValue("Please fill some text");
            return;
        }

        progress.postValue(true);
        VccAiRequestCallback callback = new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                progress.postValue(false);
                try {
                    UserInfo data = (UserInfo) result.getData();
                    GetInfoViewModel.this.data.postValue(data);
                    String msg = result.getMessage();
                    message.postValue(msg);
                } catch (Exception e) {
                    message.postValue(e.getMessage());
                }
            }

            @Override
            public void fail(int code, String msg) {
                progress.postValue(false);
                message.postValue(String.format("Error[%s] : %s", code, msg));
            }
        };
        manager.runBackground(() -> manager.face().getUserInfo(callback, email));
    }
}
