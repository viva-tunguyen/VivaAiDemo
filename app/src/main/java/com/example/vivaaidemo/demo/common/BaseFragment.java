package com.example.vivaaidemo.demo.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;
import com.google.gson.Gson;

public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    protected final Inflate<VB> inflate;
    protected VB binding;
    protected NetworkUtil networkChangeReceiver;
    protected final Gson gson = new Gson();

    /* **********************************************************************
     * Constructor
     ********************************************************************** */

    public BaseFragment(Inflate<VB> inflate) {
        this.inflate = inflate;
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = inflate.invoke(inflater, container, false);
        return binding.getRoot();
    }

    /* **********************************************************************
     * Class
     ********************************************************************** */
    public interface Inflate<VB> {
        VB invoke(LayoutInflater inflater, ViewGroup container, boolean attachToRoot);
    }
}
