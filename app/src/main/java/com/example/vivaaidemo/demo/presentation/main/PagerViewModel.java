package com.example.vivaaidemo.demo.presentation.main;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.R;
import com.example.vivaaidemo.demo.common.BaseViewModel;
import com.example.vivaaidemo.demo.presentation.common.BaseTab;
import com.example.vivaaidemo.demo.presentation.demo.DemoFragment;
import com.example.vivaaidemo.demo.presentation.setting.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class PagerViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<List<PagerTab>> tabs;

    /* **********************************************************************
     * Init
     ********************************************************************** */
    public PagerViewModel() {
        List<PagerTab> tabs = new ArrayList<PagerTab>() {{
            add(new PagerTab(0, Type.DEMO, R.string.demo, R.drawable.ic_demo, null, DemoFragment.class));
            add(new PagerTab(1, Type.SETTING, R.string.setting, R.drawable.ic_setting, null, SettingFragment.class));
        }};
        this.tabs = new MutableLiveData<>(tabs);
    }

    public MutableLiveData<List<PagerTab>> getTabs() {
        return tabs;
    }

    public PagerTab getTab(int position) {
        try {
            List<PagerTab> pagerTabs = tabs.getValue();
            if (pagerTabs != null) {
                return pagerTabs.get(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* **********************************************************************
     * Class
     ********************************************************************** */
    public enum Type {DEMO, SETTING}

    public static class PagerTab extends BaseTab<Type> {
        public PagerTab(int index, Type type, int title, int icon, Fragment fragment, Class<?> clazz) {
            super(index, type, title, icon, fragment, clazz);
        }
    }
}
