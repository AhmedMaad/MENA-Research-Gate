package com.maad.menaresearchgate.data;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserModel {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    //Creating a new user with Firebase Authentication
    public void addNewUser(String email, String password, final GeneralUserHandler userHandler) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            userHandler.onSuccess();
                        else
                            userHandler.onFailure();
                    }
                });
    }

    //Creating a new user with Facebook SDK
    public void registerWithFacebook(LoginButton loginButton,CallbackManager manager, final FacebookHandler facebookHandler) {

        loginButton.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookHandler.onSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                facebookHandler.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                facebookHandler.onFailure(error);
            }
        });




        /*LoginManager.getInstance().registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookHandler.onSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                facebookHandler.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                facebookHandler.onFailure(error);
            }
        });*/
    }


}