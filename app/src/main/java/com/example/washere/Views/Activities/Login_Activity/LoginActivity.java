package com.example.washere.Views.Activities.Login_Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.washere.R;
import com.example.washere.Views.Activities.Map_Activity.MapActivity;
import com.example.washere.Views.Activities.New_User_Activity.NewUserActivity;
import com.example.washere.models.eLoginState;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static String LOG_TAG ="OCUL- LoginActivity";
    private LoginActivityViewModel loginActivityViewModel;
    private ImageView imageViewAppIcon, imageViewMailIcon, imageViewPasswordIcon;
    private TextView textViewForgotPassword, textViewSignUp;
    private Button buttonLogin, buttonGoogleLogin;
    private EditText editTextEmail, editTextPassword;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_01);
        Log.i(LOG_TAG,"Created Login Activity");
        initViews();
        setOnClickListeners();
        loginActivityViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel.class);
        loginActivityViewModel.init();

        //Observers
        loginActivityViewModel.getLoginState().observe(this, new Observer<eLoginState>() {
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

    }

    @Override
    public void onClick(View v) {
        if (v == textViewSignUp) {
            startNewUserActivity();
        }
        if (v == buttonLogin) {
            if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                String eMail = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                loginActivityViewModel.signInUser(eMail, password);
            }
        }
    }

    public void initViews() {
        imageViewAppIcon = findViewById(R.id.imageViewAppIcon);
        imageViewMailIcon = findViewById(R.id.imageViewMailIcon);
        imageViewPasswordIcon = findViewById(R.id.imageViewPasswordIcon);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    public void setOnClickListeners() {
        textViewForgotPassword.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        buttonGoogleLogin.setOnClickListener(this);
        editTextEmail.setOnClickListener(this);
        editTextPassword.setOnClickListener(this);
    }

    public void startMainActivity() {
        intent = new Intent(this, MapActivity.class);
        this.startActivity(intent);
    }

    public void startNewUserActivity() {
        intent = new Intent(this, NewUserActivity.class);
        this.startActivity(intent);
    }

    private void showToastMessage(){
        if (loginActivityViewModel.getMessage()!=null){
            Toast.makeText(this,loginActivityViewModel.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
