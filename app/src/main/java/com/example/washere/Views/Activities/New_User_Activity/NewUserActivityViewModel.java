package com.example.washere.Views.Activities.New_User_Activity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.washere.models.User;
import com.example.washere.repositories.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class NewUserActivityViewModel extends AndroidViewModel {

    private static String LOG_TAG = "OCUL - NewUserActivityVM";
    private UserRepository userRepository;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String message;

    public NewUserActivityViewModel(@NonNull Application application) {
        super(application);

    }

    public void init() {
        userRepository = UserRepository.getInstance();
        firebaseAuth = userRepository.getFirebaseAuth();
    }

    public MutableLiveData<Boolean> isNewUserCreated() {
        return userRepository.getIsNewUserCreated();
    }

    public void setIsNewUserCreated(Boolean a) {
        userRepository.getIsNewUserCreated().postValue(a);
    }

    public String getMessage() {
        return message;
    }

    public void createNewAccount(String email, String password, final String userName) {

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
                                                createCurrentUser();
                                                setIsNewUserCreated(true);
                                                message=("Welcome abroad "+firebaseAuth.getCurrentUser().getDisplayName()+" !");
                                            } else {
                                                Log.e(LOG_TAG, "User profile failed to update.");
                                                setIsNewUserCreated(false);
                                                message=("Failed to create new user... "+task.getException().getMessage());
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(LOG_TAG, "Failed to create user with eMail. "+ task.getException().getMessage());
                            message=("Failed to create new user... "+task.getException().getMessage());
                            setIsNewUserCreated(false);
                        }
                    }
                });

    }
    private void createCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        User user = new User();
        user.setFirebaseUser(firebaseUser);
        user.setUserName(firebaseUser.getDisplayName());
        userRepository.setCurrentUser(user);
    }
}
