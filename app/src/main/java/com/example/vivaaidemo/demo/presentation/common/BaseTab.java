package com.example.vivaaidemo.demo.presentation.common;

import androidx.fragment.app.Fragment;

public abstract class BaseTab<T> {
    private final int index;
    private final T type;
    private final int title;
    private final int icon;
    private Fragment fragment;

    private Class clazz;

    public BaseTab(int index, T type, int title, Fragment fragment, Class clazz) {
        this.index = index;
        this.type = type;
        this.title = title;
        this.icon = -1;
        this.fragment = fragment;
        this.clazz = clazz;
    }

    public BaseTab(int index, T type, int title, int icon, Fragment fragment, Class clazz) {
        this.index = index;
        this.type = type;
        this.title = title;
        this.icon = icon;
        this.fragment = fragment;
        this.clazz = clazz;
    }

    public int getIndex() {
        return index;
    }

    public T getType() {
        return type;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Class getClazz() {
        return clazz;
    }
}