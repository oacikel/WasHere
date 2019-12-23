package com.example.washere.Views.Fragments.Login_Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.washere.R;
import com.example.washere.Views.Activities.Map_Activity.MapActivity;
import com.example.washere.Views.Activities.New_User_Activity.NewUserActivity;
import com.example.washere.models.eLoginState;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private static String LOG_TAG = "OCUL - Login Fragment";
    private LoginFragmentViewModel loginFragmentViewModel;
    private TextInputEditText textInputEditTextEnterEMail, textInputEditTextEnterPassword;
    private CheckBox checkBoxRememberMe;
    private TextView textViewForgotPassword;
    private MaterialButton materialButtonLogin;
    private Intent intent;


    public LoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        setOnClickListeners();
        initOtherObjects();
        loginFragmentViewModel.init();

        loginFragmentViewModel.getLoginState().observe(this, new Observer<eLoginState>() {
            @Override
            public void onChanged(eLoginState loginState) {
                if (loginState==eLoginState.LOGIN_SUCCESS) {
                    //Login succeeded continue with the map page
                    showToastMessage();
                    startMainActivity();
                } else if (loginState==eLoginState.LOGIN_FAILED){
                    showToastMessage();
                    //Login failed
                }
            }
        });

        return view;

    }

    @Override
    public void onClick(View v) {
        if (v == materialButtonLogin) {
            if (!textInputEditTextEnterEMail.getText().toString().equals("") && !textInputEditTextEnterPassword.getText().toString().equals("")) {
                String eMail = textInputEditTextEnterEMail.getText().toString();
                String password = textInputEditTextEnterPassword.getText().toString();
                loginFragmentViewModel.signInUser(eMail, password);
            }
        }
    }

    private void initOtherObjects() {
        loginFragmentViewModel = ViewModelProviders.of(this).get(LoginFragmentViewModel.class);
    }

    private void initViews(View view) {
        textInputEditTextEnterEMail = view.findViewById(R.id.textInputEditTextEnterEMail);
        textInputEditTextEnterPassword = view.findViewById(R.id.textInputEditTextEnterPassword);
        checkBoxRememberMe = view.findViewById(R.id.checkBoxRememberMe);
        textViewForgotPassword = view.findViewById(R.id.textViewForgotPassword);
        materialButtonLogin = view.findViewById(R.id.materialButtonLogin);
    }

    private void setOnClickListeners(){
        checkBoxRememberMe.setOnClickListener(this);
        materialButtonLogin.setOnClickListener(this);
    }

    public void startMainActivity() {
        intent = new Intent(getActivity(), MapActivity.class);
        this.startActivity(intent);
    }


    private void showToastMessage(){
        if (loginFragmentViewModel.getMessage()!=null){
            Toast.makeText(getActivity(),loginFragmentViewModel.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
