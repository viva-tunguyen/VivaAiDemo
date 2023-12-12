package com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.list;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;

import java.util.List;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;
import vcc.viva.ai.bin.entity.face.CheckIn;

public class CheckInViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<String> message;
    private final MutableLiveData<List<CheckIn.CheckinDetail>> data;
    private final MutableLiveData<Boolean> progress;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public CheckInViewModel() {
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

    public MutableLiveData<List<CheckIn.CheckinDetail>> getData() {
        return data;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Boolean> getProgress() {
        return progress;
    }

    public void checkInHistory(String number) {
        if (TextUtils.isEmpty(number)) {
            message.setValue("Please fill some text");
            return;
        }

        progress.postValue(true);
        VccAiRequestCallback callback = new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                progress.postValue(false);
                try {
                    CheckIn data = (CheckIn) result.getData();
                    CheckInViewModel.this.data.postValue(data.data);
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
        manager.runBackground(() -> manager.face().checkInHistory(callback, Integer.parseInt(number)));
    }
}
