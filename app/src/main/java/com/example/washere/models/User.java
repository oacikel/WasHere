package com.example.washere.models;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {
    private FirebaseUser firebaseUser;
    private String userName;
    private ArrayList<Was>privateWasList;
    private ArrayList<Was>publicWasList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Was> getPrivateWasList() {
        return privateWasList;
    }

    public void setPrivateWasList(ArrayList<Was> privateWasList) {
        this.privateWasList = privateWasList;
    }

    public ArrayList<Was> getPublicWasList() {
        return publicWasList;
    }

    public void setPublicWasList(ArrayList<Was> publicWasList) {
        this.publicWasList = publicWasList;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }
}
