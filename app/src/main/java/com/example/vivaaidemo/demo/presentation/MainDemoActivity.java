package com.example.vivaaidemo.demo.presentation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vivaaidemo.databinding.ActivityMainDemoBinding;
import com.example.vivaaidemo.demo.presentation.common.ErrorFragment;
import com.example.vivaaidemo.demo.presentation.main.PagerFragment;

import vcc.viva.ai.bin.VccAiManager;
import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiInitCallback;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;

public class MainDemoActivity extends AppCompatActivity {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final static String USER = "guest@gmail.com";
    private final static String PASS = "abcd1234";

    private ActivityMainDemoBinding binding;
    private VccAiManager manager = VccAiManager.getInstance();

    /* **********************************************************************
     * Life Cycle
     ********************************************************************** */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = VccAiManager.getInstance();
        managerInit();
    }

    private void managerInit() {
        manager.runBackground(() -> manager.init(this, new VccAiInitCallback() {
            @Override
            public void success() {
                managerRegisterUser();
            }

            @Override
            public void fail() {
                openErrorFragment("Can not init VccAiManager");
            }
        }));
    }

    private void managerRegisterUser() {
        manager.runBackground(() -> manager.registerUser(new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.add(binding.tabContainer.getId(), new PagerFragment(), null).commit();
            }

            @Override
            public void fail(int code, String message) {
                openErrorFragment(String.format("register user[%s] with pass[%s] fail", USER, PASS));
            }
        }, USER, PASS));
    }

    private void openErrorFragment(String message) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(binding.tabContainer.getId(), new ErrorFragment(message), null).commit();
    }
}

