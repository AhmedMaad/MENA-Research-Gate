package com.maad.menaresearchgate.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.auth.User;
import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.data.Validation;
import com.maad.menaresearchgate.data.GeneralUserHandler;
import com.maad.menaresearchgate.data.UserModel;
import com.maad.menaresearchgate.databinding.FragmentSignupBinding;
import com.maad.menaresearchgate.ui.activities.RegisterActivity;


public class SignupFragment extends Fragment {

    private int googleSignUpKey = 0;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentSignupBinding signupBinding = FragmentSignupBinding.inflate(inflater, container, false);

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
                        UserModel userModel = new UserModel();
                        userModel.addNewUser(email, password, new GeneralUserHandler() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getContext(), R.string.user_added, Toast.LENGTH_SHORT).show();
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

        signupBinding.btnGoogleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, googleSignUpKey);
            }
        });
        return signupBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == googleSignUpKey) {
            // The Task returned from this call is always completed, no need to attach a listener
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getContext(), R.string.user_added, Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.d("json", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(getContext(), R.string.user_failed, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
