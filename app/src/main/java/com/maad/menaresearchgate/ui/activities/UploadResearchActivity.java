package com.maad.menaresearchgate.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.data.GeneralHandler;
import com.maad.menaresearchgate.data.research.ResearchModel;
import com.maad.menaresearchgate.data.research.TokensHandler;
import com.maad.menaresearchgate.databinding.ActivityUploadResearchBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static com.maad.menaresearchgate.data.research.ResearchConstants.CHOOSE_PDF_KEY;
import static com.maad.menaresearchgate.data.research.ResearchConstants.TOKENS;


public class UploadResearchActivity extends AppCompatActivity {

    private ActivityUploadResearchBinding researchBinding;
    private ResearchModel researchModel = new ResearchModel();
    private ArrayList<String> tokenArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        researchBinding = ActivityUploadResearchBinding.inflate(getLayoutInflater());
        View view = researchBinding.getRoot();
        setContentView(view);
        //To make a prefix for the research interest topics
        if (savedInstanceState == null) {
            researchBinding.etResearchInterests.setPrefix(getResources().getString(R.string.topics));
        }

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

        researchModel.readTokens(new GeneralHandler() {
            @Override
            public <T> void onSuccess(Task<T> task) {
                Object readTokenGeneric = task.getResult();
                DocumentSnapshot document = DocumentSnapshot.class.cast(readTokenGeneric);
                ArrayList<String> data = (ArrayList<String>) document.get(TOKENS);
                Log.d("json", "Data: " + data);
                ArrayAdapter adapter = new ArrayAdapter<>(UploadResearchActivity.this
                ,android.R.layout.simple_list_item_1, data);
                researchBinding.etResearchInterests.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("json", "Error reading tokens: " + e.getMessage());
            }
        });


        //First of all I need to retrieve all the keywords from the search keywords collection server
        /*ArrayList<String> researchModels = new ArrayList<>();
        //Making the adapter that will appear as "interests suggestions", it will be filled with the above array
        ArrayAdapter<String> adapter  = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, researchModels);

        CustomTokenView tokenView = findViewById(R.id.et_research_interests);;
        tokenView.setAdapter(adapter);*/

        researchBinding.etResearchInterests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d("json", "String: " + s.toString());
            }
        });

    }

    public void chooseFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, CHOOSE_PDF_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PDF_KEY && resultCode == RESULT_OK) {
            Uri fullFileUri = data.getData();
        }

    }

    public void uploadResearch(View view) {
        //1- validate fields 2- upload pdf 3- update tokens "if" needed  4-add new research
        ArrayList<String> x = new ArrayList<>();
        x.add("Android");
        x.add("Web");
        x.add("LOL");

        researchModel.updateTokens(x, new TokensHandler() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("json", "success");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("json", "Failed: " + e.getMessage());
            }
        });
    }
}
