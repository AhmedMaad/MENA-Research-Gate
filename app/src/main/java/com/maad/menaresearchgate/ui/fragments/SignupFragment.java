package com.maad.menaresearchgate.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.maad.menaresearchgate.data.Validation;
import com.maad.menaresearchgate.data.GeneralUserHandler;
import com.maad.menaresearchgate.data.UserModel;
import com.maad.menaresearchgate.databinding.FragmentSignupBinding;
import com.maad.menaresearchgate.ui.activities.RegisterActivity;


public class SignupFragment extends Fragment {

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentSignupBinding signupBinding = FragmentSignupBinding.inflate(inflater, container, false);
        TextView loginSignupTv = getActivity(). findViewById(R.id.tv_login_signup_with);
        loginSignupTv.setText(R.string.sign_up_with);

        signupBinding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment loginFragment = new LoginFragment();
                ((RegisterActivity) getActivity()).openRegisterFragment(loginFragment);
            }
        });

        signupBinding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                boolean hasUppercase = !editable.toString().equals(editable.toString().toLowerCase());
                boolean hasSpecial = !editable.toString().matches("[A-Za-z ]*");

                if (editable.length() < 4)
                    signupBinding.containerUsernameEt.setError(getResources().getString(R.string.username_small_error));
                else if (editable.length() > 20)
                    signupBinding.containerUsernameEt.setError(getResources().getString(R.string.username_big_error));
                else if (hasUppercase || hasSpecial)
                    signupBinding.containerUsernameEt.setError(getResources().getString(R.string.username_error));
                else
                    signupBinding.containerUsernameEt.setError(null);
            }
        });

        signupBinding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                boolean hasLowercase = !editable.toString().equals(editable.toString().toUpperCase());
                boolean hasUppercase = !editable.toString().equals(editable.toString().toLowerCase());
                boolean hasSpecial = !editable.toString().matches("[A-Za-z0-9 ]*");

                if (editable.length() < 6)
                    signupBinding.containerPasswordEt.setError(getResources().getString(R.string.short_password_error));
                else if (!hasUppercase)
                    signupBinding.containerPasswordEt.setError(getResources().getString(R.string.upper_password_error));
                else if (!hasLowercase)
                    signupBinding.containerPasswordEt.setError(getResources().getString(R.string.lower_password_error));
                else if (!hasSpecial)
                    signupBinding.containerPasswordEt.setError(getResources().getString(R.string.special_password_error));
                else
                    signupBinding.containerPasswordEt.setError(null);
            }
        });

        signupBinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = signupBinding.etFirstName.getText().toString();
                String lastName = signupBinding.etLastName.getText().toString();
                String username = signupBinding.etUsername.getText().toString();
                String email = signupBinding.etEmail.getText().toString();
                String password = signupBinding.etPassword.getText().toString();

                Validation validation = new Validation();
                int validationResult = validation.validateFields(firstName, lastName, username, email, password);
                switch (validationResult) {
                    case 0:
                        Toast.makeText(getContext(), R.string.missing_fileds, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        final UserModel userModel = new UserModel();
                        userModel.addNewUser(email, password, new GeneralUserHandler() {
                            @Override
                            public <T> void onSuccess(Task<T> task) {
                                Object authResultGeneric = task.getResult();
                                AuthResult authResult = AuthResult.class.cast(authResultGeneric);
                                verifyEmail(authResult.getUser());
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getContext(), R.string.user_failed, Toast.LENGTH_SHORT).show();
                            }
                        });

                        break;
                }
            }
        });
        return signupBinding.getRoot();
    }

    private void verifyEmail(final FirebaseUser user) {
        UserModel userModel = new UserModel();
        userModel.verifyEmailAddress(user, new GeneralUserHandler() {
            @Override
            public <T> void onSuccess(Task<T> task) {
                Log.d("json", "Email sent to: " + user.getEmail());
                Toast.makeText(getContext(), R.string.verify_email, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Log.d("json", "Cannot verify email");
            }
        });
    }

}
