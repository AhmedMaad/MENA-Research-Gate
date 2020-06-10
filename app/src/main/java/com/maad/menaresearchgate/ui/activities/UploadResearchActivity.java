package com.maad.menaresearchgate.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.databinding.ActivityUploadResearchBinding;

import java.util.Calendar;

public class UploadResearchActivity extends AppCompatActivity {

    private ActivityUploadResearchBinding researchBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        researchBinding = ActivityUploadResearchBinding.inflate(getLayoutInflater());
        View view = researchBinding.getRoot();
        setContentView(view);

        int maxYear = Calendar.getInstance().get(Calendar.YEAR);
        final int min = 1825;
        researchBinding.sbYear.setMax(maxYear - min);
        researchBinding.etResearchYear.setText("" + min);
        researchBinding.sbYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = min + progress;
                researchBinding.etResearchYear.setText("" + value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
}
