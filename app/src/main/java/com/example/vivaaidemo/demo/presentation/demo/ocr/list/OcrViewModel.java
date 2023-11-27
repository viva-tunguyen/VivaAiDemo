package com.example.vivaaidemo.demo.presentation.demo.ocr.list;

import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class OcrViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<List<OcrType>> data;

    /* **********************************************************************
     * Init
     ********************************************************************** */
    public OcrViewModel() {
        List<OcrType> list = new ArrayList() {{
            add(OcrType.ID_CARD);
            add(OcrType.PASSPORT);
            add(OcrType.HEALTH_INSURANCE);
            add(OcrType.IELTS_CERTIFICATE);
            add(OcrType.VEHICLE_LICENSE);
            add(OcrType.VEHICLE_REGISTRATION);
            add(OcrType.VEHICLE_INSPECTION);
            add(OcrType.PAYMENT_ORDER);
            add(OcrType.BUSINESS_REGISTRATION);
            add(OcrType.DATA_TABLE);
            add(OcrType.BILL);
            add(OcrType.KEY_VALUE_PAIR);
        }};
        this.data = new MutableLiveData<>(list);
    }

    public MutableLiveData<List<OcrType>> getData() {
        return data;
    }

    /* **********************************************************************
     * Inner Class
     ********************************************************************** */
    public enum OcrType {
        // General
        ID_CARD("Vietnamese ID Card"), PASSPORT("Passport"), HEALTH_INSURANCE("Health Insurance"), IELTS_CERTIFICATE("Ielts Certificate"), VISA("Visa"),
        // Vehicle
        VEHICLE_LICENSE("Vehicle License"), VEHICLE_REGISTRATION("Vehicle Registration"), VEHICLE_INSPECTION("Vehicle Inspection"),
        // Sucking
        PAYMENT_ORDER("Payment Order"), BUSINESS_REGISTRATION("Business Registration"), DATA_TABLE("Data Table"), BILL("Bill"), KEY_VALUE_PAIR("Key Value Pair");

        public String title;

        OcrType(String title) {
            this.title = title;
        }
    }
}
