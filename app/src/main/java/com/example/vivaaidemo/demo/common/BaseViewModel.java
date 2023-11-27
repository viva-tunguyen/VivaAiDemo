package com.example.vivaaidemo.demo.common;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import vcc.viva.ai.bin.VccAiManager;

public abstract class BaseViewModel extends ViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    protected Gson gson;
    protected VccAiManager manager;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public BaseViewModel() {
        gson = new Gson();
        manager = VccAiManager.getInstance();
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
}
