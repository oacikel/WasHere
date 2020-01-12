package ocul.longestlovestoryever.washere.Views.Fragments.Register_Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import ocul.longestlovestoryever.washere.R;
import ocul.longestlovestoryever.washere.Views.Activities.Map_Activity.MapActivity;
import ocul.longestlovestoryever.washere.models.eLoginState;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private static String LOG_TAG = "OCUL - RegisterFragment";
    private RegisterFragmentViewModel registerFragmentViewModel;
    private TextInputEditText textInputEditTextCreateUserName, textInputEditTextCreateEMail, textInputEditTextCreatePassword, textInputEditTextReEnterPassword;
    private MaterialButton materialButtonSignUp;
    private Intent intent;

    public RegisterFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOtherObjects();
        registerFragmentViewModel.init();

        registerFragmentViewModel.getLoginState().observe(this, new Observer<eLoginState>() {
            @Override
            public void onChanged(eLoginState loginState) {
                if (loginState == eLoginState.USER_CREATED) {
                    registerFragmentViewModel.createCurrentUser();
                    registerFragmentViewModel.signInUser(registerFragmentViewModel.geteMail(), registerFragmentViewModel.getPassWord());
                } else if (loginState == eLoginState.USER_CREATED_ERROR) {

                } else if (loginState == eLoginState.LOGIN_SUCCESS) {
                    startMainActivity();
                } else if (loginState == eLoginState.LOGIN_FAILED) {
                    showToastMessage();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(view);
        setOnClickListeners();
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == materialButtonSignUp) {
            if (!textInputEditTextCreateUserName.getText().toString().equals("") &&
                    !textInputEditTextCreateEMail.getText().toString().equals("") &&
                    !textInputEditTextCreatePassword.getText().toString().equals("") &&
                    !textInputEditTextReEnterPassword.getText().toString().equals("")) {
                //All text fields are filled
                if (textInputEditTextCreatePassword.getText().toString().equals(textInputEditTextReEnterPassword.getText().toString())) {
                    //Passwords are equal create account
                    registerFragmentViewModel.createNewAccount(textInputEditTextCreateEMail.getText().toString(), textInputEditTextCreatePassword.getText().toString(), textInputEditTextCreateUserName.getText().toString());
                } else {
                    //Passwords are misplaced
                    textInputEditTextCreatePassword.setText("");
                    textInputEditTextReEnterPassword.setText("");
                    Toast.makeText(getActivity(), "Please make sure that you entered your password correctly", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void initOtherObjects() {
        registerFragmentViewModel = ViewModelProviders.of(this).get(RegisterFragmentViewModel.class);
    }

    public void initViews(View view) {
        textInputEditTextCreateUserName = view.findViewById(R.id.textInputEditTextCreateUserName);
        textInputEditTextCreateEMail = view.findViewById(R.id.textInputEditTextCreateEMail);
        textInputEditTextCreatePassword = view.findViewById(R.id.textInputEditTextCreatePassword);
        textInputEditTextReEnterPassword = view.findViewById(R.id.textInputEditTextReEnterPassword);
        materialButtonSignUp = view.findViewById(R.id.materialButtonSignUp);
    }


    private void setOnClickListeners() {
        materialButtonSignUp.setOnClickListener(this);
    }

    private void showToastMessage() {
        if (registerFragmentViewModel.getMessage() != null) {
            Toast.makeText(getActivity(), registerFragmentViewModel.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void startMainActivity() {
        intent = new Intent(getActivity(), MapActivity.class);
        this.startActivity(intent);
    }


}
