package com.maad.menaresearchgate.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.data.LoginHandler;
import com.maad.menaresearchgate.data.UserModel;

public class EmailVerificationActivity extends AppCompatActivity {

    private String email;
    private String password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        Toast.makeText(this, R.string.verify_email, Toast.LENGTH_SHORT).show();

        email = getIntent().getStringExtra("Email");
        password = getIntent().getStringExtra("Password");
        progressBar = findViewById(R.id.pb_verify_email);

    }

    public void verifyEmail(View view) {
        progressBar.setVisibility(View.VISIBLE);
        UserModel userModel = new UserModel();
        userModel.loginWithFirebase(email, password, new LoginHandler() {
            @Override
            public <T> void onSuccess(Task<T> task) {
                if (task.isSuccessful()) {
                    Object authResultGeneric = task.getResult();
                    AuthResult authResult = AuthResult.class.cast(authResultGeneric);
                    FirebaseUser user = authResult.getUser();
                    Log.d("json", "Email found: " + user.getEmail());
                    if (user.isEmailVerified()) {
                        //Go to home page
                        Intent intent = new Intent(EmailVerificationActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EmailVerificationActivity.this, R.string.verify_email, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.d("json", "Exception: " + e.getMessage());
            }
        });
    }
}
