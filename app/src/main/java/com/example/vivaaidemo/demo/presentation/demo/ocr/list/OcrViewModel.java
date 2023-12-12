package com.example.vivaaidemo.demo.presentation.demo.ocr.list;

import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.R;
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
            add(OcrType.VISA);
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
        ID_CARD("Vietnamese ID Card", R.drawable.ic_identification), PASSPORT("Passport", R.drawable.ic_identification), HEALTH_INSURANCE("Health Insurance", R.drawable.ic_identification), IELTS_CERTIFICATE("Ielts Certificate", R.drawable.ic_identification), VISA("Visa", R.drawable.ic_identification),
        // Vehicle
        VEHICLE_LICENSE("Vehicle License", R.drawable.ic_vehicle), VEHICLE_REGISTRATION("Vehicle Registration", R.drawable.ic_vehicle), VEHICLE_INSPECTION("Vehicle Inspection", R.drawable.ic_vehicle),
        // Sucking
        PAYMENT_ORDER("Payment Order", R.drawable.ic_business), BUSINESS_REGISTRATION("Business Registration", R.drawable.ic_business), DATA_TABLE("Data Table", R.drawable.ic_general), BILL("Bill", R.drawable.ic_general), KEY_VALUE_PAIR("Key Value Pair", R.drawable.ic_general);

        public String title;
        public int icon;

        OcrType(String title, int icon) {
            this.title = title;
            this.icon = icon;
        }
    }
}
