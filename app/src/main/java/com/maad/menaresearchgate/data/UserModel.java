package com.maad.menaresearchgate.data;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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
                            userHandler.onSuccess(task);
                        else
                            userHandler.onFailure();
                    }
                });
    }

    //Integrating new user to Firebase from Google SDK
    public void registerWithGoogle(String idToken, final LoginHandler googleHandler) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        googleHandler.onSuccess(task);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        googleHandler.onFailure(e);
                    }
                });
    }

    //Creating a new user with Facebook SDK
    public void registerWithFacebook(LoginButton loginButton, CallbackManager manager, final FacebookHandler facebookHandler) {

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
    }

    //Authenticate Facebook login with Firebase
    public void handleFacebookAccessToken(AccessToken token, final GeneralUserHandler userHandler) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            userHandler.onSuccess(task);
                        else
                            userHandler.onFailure();
                    }
                });
    }

    //Login existing user with firebase authentication
    public void loginWithFirebase(String email, String password, final LoginHandler loginHandler) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginHandler.onSuccess(task);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginHandler.onFailure(e);
                    }
                });
    }

    //Verify email address after adding a new user with firebase authentication only
    public void verifyEmailAddress(FirebaseUser user, final GeneralUserHandler handler) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            handler.onSuccess(task);
                        else
                            handler.onFailure();
                    }
                });
    }


}