package com.maad.menaresearchgate.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.maad.menaresearchgate.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle(R.string.my_profile);
    }
}
