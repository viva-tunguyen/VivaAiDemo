package com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.filter;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;
import vcc.viva.ai.bin.entity.face.CheckIn;

public class CheckInFilterViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<String> message;
    private final MutableLiveData<List<String>> emails;
    private final MutableLiveData<List<CheckIn.CheckinDetail>> data;
    private final MutableLiveData<Boolean> progress;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public CheckInFilterViewModel() {
        this.data = new MutableLiveData<>();
        this.emails = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.progress = new MutableLiveData<>(false);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    protected void clear() {
        data.postValue(null);
        List<String> empty = new ArrayList<>();
        emails.postValue(empty);
        message.postValue(null);
        progress.postValue(null);
    }

    public MutableLiveData<List<CheckIn.CheckinDetail>> getData() {
        return data;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<List<String>> getEmails() {
        return emails;
    }

    public MutableLiveData<Boolean> getProgress() {
        return progress;
    }

    public void addEmail(String email) {
        List<String> currentList = emails.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
            currentList.add(email);
            emails.postValue(currentList);
        } else if (!currentList.contains(email)) {
            currentList.add(email);
            emails.postValue(currentList);
        } else {
            message.postValue("This email has been added !");
        }
    }

    public void removeEmail(String email) {
        List<String> currentList = emails.getValue();
        if (currentList != null && currentList.contains(email)) {
            currentList.remove(email);
            emails.postValue(currentList);
        } else {
            message.postValue("Email not found !");
        }
    }

    public void checkInHistoryFilter(String employeeCode, String fromDate, String toDate, List<String> emails) {
        checkInHistoryFilter(employeeCode, fromDate, toDate, emails, true, 0, 10);
    }

    public void checkInHistoryFilter(String employeeCode, String fromDate, String toDate, List<String> emails, boolean check, int page, int limit) {
        if (TextUtils.isEmpty(employeeCode) || TextUtils.isEmpty(fromDate) || TextUtils.isEmpty(toDate) || emails.isEmpty()) {
            message.setValue("Please fill all fields");
            return;
        }

        progress.postValue(true);
        VccAiRequestCallback callback = new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                progress.postValue(false);
                try {
                    CheckIn data = (CheckIn) result.getData();
                    CheckInFilterViewModel.this.data.postValue(data.data);
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
        manager.runBackground(() -> manager.face().checkInHistoryFilter(callback, employeeCode, fromDate, toDate, emails, check, page, limit));
    }
}
