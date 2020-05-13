package com.maad.menaresearchgate.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.data.LoginHandler;
import com.maad.menaresearchgate.data.UserModel;
import com.maad.menaresearchgate.data.Validation;
import com.maad.menaresearchgate.databinding.FragmentLoginBinding;
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
                        UserModel userModel = new UserModel();
                        userModel.loginWithFirebase(email, password, new LoginHandler() {
                            @Override
                            public void onSuccess(Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    Log.d("json", "Logged in: " + task.getResult().getUser().getEmail());
                                else
                                    Toast.makeText(getContext(), R.string.wrong_email_password, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("json", "Exception: " + e.getMessage());
                            }
                        });
                        break;
                }

            }
        });

        return loginBinding.getRoot();
    }
}
