package com.maad.menaresearchgate.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.data.FacebookHandler;
import com.maad.menaresearchgate.data.GoogleHandler;
import com.maad.menaresearchgate.data.UserModel;
import com.maad.menaresearchgate.databinding.ActivityMainBinding;
import com.maad.menaresearchgate.ui.fragments.LoginFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int googleSignUpKey = 0;
    private CallbackManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //setContentView(R.layout.activity_main);

        //To register a callback for Facebook integration
        manager = CallbackManager.Factory.create();

        LoginFragment loginFragment = new LoginFragment();
        openRegisterFragment(loginFragment);


        //printKeyHash();

    }

    public void openRegisterFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        transaction.replace(R.id.container_registeration, fragment);
        transaction.commit();
    }

    public void registerWithFacebook(View view) {
        Log.d("json", "Sign up button clicked");
        //TODO: de el mfrood kanet btt3ml ll fragment bas fa law msht8ltsh GOOGLE IT
        //signupBinding.facebookDefaultbutton.setFragment(SignupFragment.this);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email,public_profile"));
        binding.facebookDefaultbutton.performClick();

        UserModel userModel = new UserModel();
        userModel.registerWithFacebook(binding.facebookDefaultbutton, manager, new FacebookHandler() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("json",
                        "https://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?return_ssl_resources=1");
            }

            @Override
            public void onCancel() {
                Log.d("json", "Operation Cancelled");
            }

            @Override
            public void onFailure(FacebookException exception) {
                Log.d("json", "Facebook Failed: " + exception.getMessage());
                //Log.d("json", "Facebook Failure cause: " + );
                exception.printStackTrace();
            }
        });
    }

    public void registerWithGoogle(View view) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, googleSignUpKey);
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
                Log.d("json", "idToken: " + account.getIdToken());
                UserModel userModel = new UserModel();
                userModel.registerWithGoogle(account.getIdToken(), new GoogleHandler() {
                    @Override
                    public void onSuccess(Task<AuthResult> task) {
                        if (task.isSuccessful())
                            Toast.makeText(RegisterActivity.this, "Hello " + task.getResult().getUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(RegisterActivity.this, R.string.wrong_email_password, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("json", "Exception: " + e.getMessage());
                    }
                });
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.d("json", "signInResult:failed code=" + e.getStatusCode());
            }
        }

        //This line is for the Facebook functionality
        Log.d("json", "Manager state from onActivityResult: " + manager);
        manager.onActivityResult(requestCode, resultCode, data);

    }

    /*private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.maad.menaresearchgate",
                    PackageManager.GET_SIGNATURES);//change to your package name
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //this is your keyhash
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }*/

}
