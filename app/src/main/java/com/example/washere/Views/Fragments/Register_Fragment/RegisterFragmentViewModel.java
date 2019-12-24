package com.example.washere.Views.Fragments.Register_Fragment;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

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
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragmentViewModel extends AndroidViewModel {

    private static String LOG_TAG = "OCUL - NewUserFragmentVM";
    private UserRepository userRepository;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String message;
    private String eMail;
    private String passWord;
    private String username;

    public RegisterFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        userRepository = UserRepository.getInstance();
        firebaseAuth = userRepository.getFirebaseAuth();

    }

    public MutableLiveData<Boolean> isNewUserCreated() {
        return userRepository.getIsNewUserCreated();
    }

    public String getMessage() {
        return message;
    }

    public void createNewAccount(final String email, final String password, final String userName) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "Created User With E-Mail");
                            user = firebaseAuth.getCurrentUser();
                            //Add the user name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(LOG_TAG, "User profile updated.");
                                                eMail = email;
                                                passWord = password;
                                                username = userName;
                                                message = ("Welcome " + firebaseAuth.getCurrentUser().getDisplayName() + " !");
                                                setLoginState(eLoginState.USER_CREATED);
                                            } else {
                                                setLoginState(eLoginState.USER_CREATED_ERROR);
                                                Log.e(LOG_TAG, "User profile failed to update.");
                                                message = ("Failed to create new user. Please try again ");
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            setLoginState(eLoginState.USER_CREATED_ERROR);
                            Log.e(LOG_TAG, "Failed to create user with eMail." + task.getException().getMessage());
                            message = ("Failed to create new user. Please try again ");
                        }
                    }
                });

    }

    public void createCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = new User();
        user.setFirebaseUser(firebaseUser);
        user.setUserName(firebaseUser.getDisplayName());
        userRepository.setCurrentUser(user);
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

    public MutableLiveData<eLoginState> getLoginState() {
        return userRepository.getLoginState();
    }

    public void setLoginState(eLoginState loginState) {
        userRepository.getLoginState().postValue(loginState);
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
