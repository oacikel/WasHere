package com.example.washere.Views.Fragments.Register_Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.washere.R;

public class RegisterFragment extends Fragment {
    private static String LOG_TAG = "OCUL - RegisterFragment";
    private RegisterFragmentViewModel registerFragmentViewModel;

    public RegisterFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOtherObjects();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    public void initOtherObjects() {
        registerFragmentViewModel = ViewModelProviders.of(this).get(RegisterFragmentViewModel.class);
    }
}
