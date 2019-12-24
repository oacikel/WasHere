package com.example.washere.Views.Activities.New_User_Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.washere.R;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener {

    private static String LOG_TAG = "OCUL - NewUserActivity";
    EditText editTextSelectUserName, editTextSelectEMail, editTextSelectPassword, editTextReEnterPassword;
    Button buttonCreateNewAccount;
    NewUserActivityViewModel newUserActivityViewModel;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__user_);
        initViews();
        setOnClickListeners();
        newUserActivityViewModel = ViewModelProviders.of(this).get(NewUserActivityViewModel.class);
        newUserActivityViewModel.init();

        newUserActivityViewModel.isNewUserCreated().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isNewUserCreated) {
                if(isNewUserCreated){
                    //New User Created Successfully Go Back To The Login Page
                    showToastMessage();
                    startLoginActivity();
                }else{
                    showToastMessage();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == buttonCreateNewAccount) {
            if (!editTextReEnterPassword.getText().toString().equals("") &&
                    !editTextSelectEMail.getText().toString().equals("") &&
                    !editTextSelectPassword.getText().toString().equals("") &&
                    !editTextSelectUserName.getText().toString().equals("")) {
                //All text fields are filled
                if (editTextReEnterPassword.getText().toString().equals(editTextSelectPassword.getText().toString())) {
                    //Passwords are equal create account
                    newUserActivityViewModel.createNewAccount(editTextSelectEMail.getText().toString(),editTextSelectPassword.getText().toString(),editTextSelectUserName.getText().toString());
                } else {
                    //Passwords are misplaced
                    editTextReEnterPassword.setText("");
                    editTextSelectPassword.setText("");
                    Toast.makeText(this, "Please make sure that you entered your password correctly", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void initViews() {
        buttonCreateNewAccount = findViewById(R.id.buttonCreateNewAccount);
        editTextSelectUserName = findViewById(R.id.editTextSelectUserName);
        editTextSelectEMail = findViewById(R.id.editTextSelectEMail);
        editTextSelectPassword = findViewById(R.id.editTextSelectPassword);
        editTextReEnterPassword = findViewById(R.id.editTextReEnterPassword);
    }

    public void setOnClickListeners() {
        buttonCreateNewAccount.setOnClickListener(this);
    }

    public void startLoginActivity() {
        this.startActivity(intent);
    }

    private void showToastMessage(){
        if (newUserActivityViewModel.getMessage()!=null){
            Toast.makeText(this,newUserActivityViewModel.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
