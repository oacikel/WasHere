package com.example.washere.Views.Fragments.Login_Fragment;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.models.User;
import com.example.washere.models.eLoginState;
import com.example.washere.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragmentViewModel extends AndroidViewModel {

    private FirebaseAuth firebaseAuth;
    private UserRepository userRepository;
    private String message;
    private static String LOG_TAG = ("OCUL- LoginFragmentVM");

    public LoginFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        userRepository = UserRepository.getInstance();
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        userRepository.setFirebaseAuth(firebaseAuth);

    }

    //Auth

    public MutableLiveData<eLoginState> getLoginState() {
        return userRepository.getLoginState();
    }

    public void setLoginState(eLoginState loginState) {
        userRepository.getLoginState().postValue(loginState);
    }

    public void signInUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "User Signed In With EMail");
                            createCurrentUser();
                            setLoginState(eLoginState.LOGIN_SUCCESS);
                            message = ("Welcome " + firebaseAuth.getCurrentUser().getDisplayName() + " !");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "Failed to sign in with eMail", task.getException());
                            setLoginState(eLoginState.LOGIN_FAILED);
                            message = ("Login failed... " + task.getException().getMessage());
                        }
                    }
                });
    }

    public String getMessage() {
        return message;
    }

    private void createCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = new User();
        user.setFirebaseUser(firebaseUser);
        user.setUserName(firebaseUser.getDisplayName());
        userRepository.setCurrentUser(user);
    }
}
