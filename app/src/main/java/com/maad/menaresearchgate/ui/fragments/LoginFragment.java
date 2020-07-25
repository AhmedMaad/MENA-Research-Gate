package com.maad.menaresearchgate.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.data.GeneralHandler;
import com.maad.menaresearchgate.data.LoginHandler;
import com.maad.menaresearchgate.data.UserModel;
import com.maad.menaresearchgate.data.Validation;
import com.maad.menaresearchgate.databinding.FragmentLoginBinding;
import com.maad.menaresearchgate.ui.activities.HomeActivity;
import com.maad.menaresearchgate.ui.activities.RegisterActivity;

public class LoginFragment extends Fragment {
    private int visibilityCounter = 0;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final FragmentLoginBinding loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        TextView loginSignupTv = getActivity().findViewById(R.id.tv_login_signup_with);
        loginSignupTv.setText(R.string.log_in_with);
        loginBinding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visibilityCounter % 2 == 0) {
                    loginBinding.llResetContainer.setVisibility(View.VISIBLE);
                    ++visibilityCounter;
                } else {
                    loginBinding.llResetContainer.setVisibility(View.GONE);
                    ++visibilityCounter;
                }
            }
        });

        loginBinding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupFragment signupFragment = new SignupFragment();
                ((RegisterActivity) getActivity()).openRegisterFragment(signupFragment);
            }
        });

        loginBinding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginBinding.etEmail.getText().toString();
                String password = loginBinding.etPassword.getText().toString();

                Validation validation = new Validation();
                int validationResult = validation.validateFields(email, password);
                switch (validationResult) {
                    case 0:
                        Toast.makeText(getContext(), R.string.missing_fileds, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        loginBinding.pbLogin.setVisibility(View.VISIBLE);
                        loginBinding.btnSignin.setVisibility(View.INVISIBLE);
                        UserModel userModel = new UserModel();
                        userModel.loginWithFirebase(email, password, new LoginHandler() {
                            @Override
                            public <T> void onSuccess(Task<T> task) {
                                loginBinding.pbLogin.setVisibility(View.GONE);
                                loginBinding.btnSignin.setVisibility(View.VISIBLE);
                                if (task.isSuccessful()) {
                                    Object authResultGeneric = task.getResult();
                                    AuthResult authResult = AuthResult.class.cast(authResultGeneric);
                                    FirebaseUser user = authResult.getUser();
                                    Log.d("json", "Email found: " + user.getEmail());
                                    if (user.isEmailVerified()) {
                                        //Toast.makeText(getContext(), R.string.logged_successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), HomeActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else
                                        Toast.makeText(getContext(), R.string.verify_email, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getContext(), R.string.wrong_email_password, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                loginBinding.pbLogin.setVisibility(View.GONE);
                                loginBinding.btnSignin.setVisibility(View.VISIBLE);
                                Log.d("json", "Exception: " + e.getMessage());
                            }
                        });
                        break;
                }

            }
        });

        loginBinding.btnPasswordResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String writtenEmail = loginBinding.etResetEmail.getText().toString();
                Validation validation = new Validation();
                int validationResult = validation.validateFields(writtenEmail);
                switch (validationResult) {
                    case 0:
                        Toast.makeText(getContext(), R.string.missing_fileds, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        loginBinding.pbPasswordReset.setVisibility(View.VISIBLE);
                        loginBinding.btnPasswordResetLink.setVisibility(View.INVISIBLE);
                        UserModel userModel = new UserModel();
                        userModel.resetPassword(writtenEmail, new GeneralHandler() {
                            @Override
                            public <T> void onSuccess(Task<T> task) {
                                loginBinding.pbPasswordReset.setVisibility(View.INVISIBLE);
                                loginBinding.btnPasswordResetLink.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), R.string.check_email, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                loginBinding.pbPasswordReset.setVisibility(View.INVISIBLE);
                                loginBinding.btnPasswordResetLink.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), R.string.email_not_recorded, Toast.LENGTH_SHORT).show();
                                Log.d("json", "Error: " + e.getMessage());
                            }
                        });
                        break;
                }

            }
        });

        return loginBinding.getRoot();
    }
}
