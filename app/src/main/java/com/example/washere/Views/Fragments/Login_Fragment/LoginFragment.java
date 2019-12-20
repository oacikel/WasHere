package com.example.washere.Views.Fragments.Login_Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.washere.R;

public class LoginFragment extends Fragment {
    private static String LOG_TAG = "OCUL - Login Fragment";
    private LoginFragmentViewModel loginFragmentViewModel;

    public LoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOtherObjects();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_login,container,false);
    }

    private void initOtherObjects(){
        loginFragmentViewModel= ViewModelProviders.of(this).get(LoginFragmentViewModel.class);
    }
}
