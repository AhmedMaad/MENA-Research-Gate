package com.maad.menaresearchgate.data.research;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.maad.menaresearchgate.data.GeneralHandler;

import java.io.Serializable;
import java.util.ArrayList;

import static com.maad.menaresearchgate.data.research.ResearchConstants.RESEARCH_INTEREST_COLLECTION;
import static com.maad.menaresearchgate.data.research.ResearchConstants.TOKENS;
import static com.maad.menaresearchgate.data.research.ResearchConstants.TOKENS_ARRAY_DOCUMENT;

// In case we wanted to implement "parcelable" instead of "serializable":
// https://android-arsenal.com/details/1/903#user-content-restoring-the-view-state
public class ResearchModel implements Serializable {

    private String title;
    private String researchAbstract;
    private String publicationName;
    private int publicationYear;
    private String DOI;
    //private String file;
    private String coAuthors;
    private ArrayList<String> researchInterestTokens;
    //This member is used to retrieve the token on the CustomTokenView
    private String token;

    public ResearchModel(String title, String researchAbstract, String publicationName
            , int publicationYear, String DOI, String coAuthors, ArrayList<String> researchInterestTokens) {
        this.title = title;
        this.researchAbstract = researchAbstract;
        this.publicationName = publicationName;
        this.publicationYear = publicationYear;
        this.DOI = DOI;
        this.coAuthors = coAuthors;
        this.researchInterestTokens = researchInterestTokens;
    }

    //This constructor is used when retrieving all of the researchInterests list from the server
    /*public ResearchModel(ArrayList<String> researchInterestTokens) {
        this.researchInterestTokens = researchInterestTokens;
    }*/

    public ResearchModel() {
    }

    //This constructor is used in the CustomTokenView
    public ResearchModel(String token) {
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public String getResearchAbstract() {
        return researchAbstract;
    }

    public String getPublicationName() {
        return publicationName;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public String getDOI() {
        return DOI;
    }

    public String getCoAuthors() {
        return coAuthors;
    }

    public ArrayList<String> getResearchInterestTokens() {
        return researchInterestTokens;
    }

    public String getToken() {
        return token;
    }

    /*- Read array first
    - after user write all keywords
    - check if new keywords exists to be added to the array
    - then push the array to the cloud firestore
    * */

    /**
     * This method adds new tokens written by the user which don't exist before on the cloud
     *
     * @param tokens:        List of String for the "old" and "new" added tokens
     * @param tokensHandler: Interface to handle the call outside UI thread
     */
    public void updateTokens(final ArrayList<String> tokens, final TokensHandler tokensHandler) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference reference = db.collection(RESEARCH_INTEREST_COLLECTION)
                .document(TOKENS_ARRAY_DOCUMENT);
        reference
                .update(TOKENS, tokens)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tokensHandler.onSuccess(aVoid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tokensHandler.onFailure(e);
            }
        });

    }

    public void readTokens(final GeneralHandler handler) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(RESEARCH_INTEREST_COLLECTION)
                .document(TOKENS_ARRAY_DOCUMENT);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                        handler.onSuccess(task);
                    else
                        Log.d("json", "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.onFailure(e);
            }
        });
    }

}
