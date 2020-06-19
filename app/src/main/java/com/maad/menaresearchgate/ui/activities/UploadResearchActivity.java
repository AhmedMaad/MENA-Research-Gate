package com.maad.menaresearchgate.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maad.menaresearchgate.data.GeneralHandler;
import com.maad.menaresearchgate.data.research.ResearchModel;
import com.maad.menaresearchgate.data.research.TokensHandler;
import com.maad.menaresearchgate.databinding.ActivityUploadResearchBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static com.maad.menaresearchgate.data.research.ResearchConstants.CHOOSE_PDF_KEY;
import static com.maad.menaresearchgate.data.research.ResearchConstants.PDFS;
import static com.maad.menaresearchgate.data.research.ResearchConstants.TOKENS;


public class UploadResearchActivity extends AppCompatActivity {

    private ActivityUploadResearchBinding researchBinding;
    private ResearchModel researchModel = new ResearchModel();
    private ArrayList<String> tokensFromServer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        researchBinding = ActivityUploadResearchBinding.inflate(getLayoutInflater());
        View view = researchBinding.getRoot();
        setContentView(view);
        //To make a prefix for the research interest topics
        /*if (savedInstanceState == null) {
            researchBinding.etResearchInterests.setPrefix(getResources().getString(R.string.topics));
        }*/

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
                tokensFromServer = (ArrayList<String>) document.get(TOKENS);
                Log.d("json", "Data: " + tokensFromServer);
                ArrayAdapter adapter = new ArrayAdapter<>(UploadResearchActivity.this
                        , android.R.layout.simple_list_item_1, tokensFromServer);
                researchBinding.etResearchInterests.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("json", "Error reading tokens: " + e.getMessage());
            }
        });
    }

    public void chooseFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        //intent.setType("pdf/*"); momkn agrrbha lw el donya bazet
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, CHOOSE_PDF_KEY);
    }

    //1- Check the chosen file is pdf format, 2- Solve upload problem
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CHOOSE_PDF_KEY && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            //researchModel.uploadPDF(fileUri);

            String fileUriString = fileUri.toString();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            Log.d("json", "with: " + "file://" + fileUri.getLastPathSegment());
            Log.d("json", "without: " + fileUri.getLastPathSegment());

            StorageReference finalRef = storageRef.child(PDFS + "/" + "file://" + fileUri.getLastPathSegment());
            finalRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("json", "File uploaded successfully");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("json", "File not uploaded: " + e.getMessage());
                            Log.d("json", "File not uploaded: " + e.getLocalizedMessage());
                            Log.d("json", "File not uploaded: " + e.getCause().toString());
                            e.printStackTrace();
                        }
                    });


            /*File file = new File(fileUriString);
            String uri = file.getAbsolutePath();
            Log.d("json", "tmppp: " + uri);*/

            /*String x = fileUri.getPath();
            Uri m = Uri.fromFile(new File(x));
            researchModel.uploadPDF(m);*/

            String fileName = null;

            if (fileUriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(fileUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst())
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                } finally {
                    cursor.close();
                }
            } else if (fileUriString.startsWith("file://"))
                fileName = fileUri.getLastPathSegment();
            researchBinding.tvChosenPdf.setText(fileName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //1- validate fields 2- upload pdf 3- update tokens "if" needed  4-add new research
    public void uploadResearch(View view) {

        String writtenTokensString = researchBinding.etResearchInterests.getText().toString();
        ArrayList<String> newTokens = researchModel.extractNewTokens(tokensFromServer, writtenTokensString);
        ArrayList<String> allTokens = new ArrayList<>(tokensFromServer);
        if (newTokens != null) {
            allTokens.addAll(newTokens);
            researchModel.updateTokens(allTokens, new TokensHandler() {
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
}
