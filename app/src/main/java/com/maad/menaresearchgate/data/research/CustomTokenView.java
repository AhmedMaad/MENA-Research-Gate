package com.maad.menaresearchgate.data.research;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maad.menaresearchgate.R;
import com.maad.menaresearchgate.data.research.ResearchModel;
import com.tokenautocomplete.TokenCompleteTextView;

public class CustomTokenView extends TokenCompleteTextView<String> {

    public CustomTokenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * It is not clear in the library documentation
     * https://github.com/splitwise/TokenAutoComplete
     * but from tracing this method is called after
     * pressing ',' then the {@link #getViewForObject(String)} is called
     */
    @Override
    protected String defaultObject(String completionText) {
        //Oversimplified example of guessing if we have an email or not
        //int index = completionText.indexOf('@');
        // if (index == -1) {
        //return new ResearchModel(completionText, completionText.replace(" ", "") + "@example.com");
        return completionText;
        // }
        // else {
        //  return new ResearchModel(completionText.substring(0, index), completionText);
        //}
        // return null;
    }

    @Override
    protected View getViewForObject(String object) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) inflater.inflate(R.layout.research_token, (ViewGroup) getParent(), false);
        view.setText(object);
        return view;
    }



    /**
     * It is not clear in the library documentation
     * https://github.com/splitwise/TokenAutoComplete
     * but from tracing this method is called after
     * pressing ',' then the {@link #getViewForObject(ResearchModel)} is called
     */
  /*  @Override
    protected ResearchModel defaultObject(String completionText) {
        //Oversimplified example of guessing if we have an email or not
        //int index = completionText.indexOf('@');
        // if (index == -1) {
        //return new ResearchModel(completionText, completionText.replace(" ", "") + "@example.com");
        return new ResearchModel(completionText);
        // }
        // else {
        //  return new ResearchModel(completionText.substring(0, index), completionText);
        //}
        // return null;
    }

    @Override
    protected View getViewForObject(ResearchModel researchModel) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) inflater.inflate(R.layout.research_token, (ViewGroup) getParent(), false);
        view.setText(researchModel.getToken());
        return view;
    }*/

}
