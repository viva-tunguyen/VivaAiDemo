package com.example.vivaaidemo.demo.presentation.demo;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.R;
import com.example.vivaaidemo.demo.common.BaseViewModel;
import com.example.vivaaidemo.demo.presentation.common.BaseTab;
import com.example.vivaaidemo.demo.presentation.common.EmptyFragment;
import com.example.vivaaidemo.demo.presentation.demo.ocr.list.OcrFragment;
import com.example.vivaaidemo.demo.presentation.demo.speech.SpeechFragment;

import java.util.ArrayList;
import java.util.List;

public class DemoViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<List<DemoTab>> tabs;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public DemoViewModel() {
        List<DemoTab> tabs = new ArrayList<DemoTab>() {{
            add(new DemoTab(0, DemoType.SPEECH, R.string.speech, null, SpeechFragment.class));
            add(new DemoTab(1, DemoType.OCR, R.string.ocr, null, OcrFragment.class));
            add(new DemoTab(2, DemoType.FACE, R.string.face, null, EmptyFragment.class));
        }};
        this.tabs = new MutableLiveData<>(tabs);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    public MutableLiveData<List<DemoTab>> getTabs() {
        return tabs;
    }

    public DemoTab getTab(int position) {
        try {
            List<DemoTab> demoTabs = tabs.getValue();
            if (demoTabs != null) {
                return demoTabs.get(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* **********************************************************************
     * Inner Class
     ********************************************************************** */
    public enum DemoType {SPEECH, OCR, FACE}

    public static class DemoTab extends BaseTab<DemoType> {
        public DemoTab(int index, DemoType type, int title, Fragment fragment, Class<?> clazz) {
            super(index, type, title, fragment, clazz);
        }
    }
}
