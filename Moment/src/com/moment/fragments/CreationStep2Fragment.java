package com.moment.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.CreationDetailsActivity;
import com.moment.models.Moment;

public class CreationStep2Fragment extends Fragment {

    private Moment moment;
    private CreationDetailsActivity activity;

    private Tracker mGaTracker;
    private GoogleAnalytics mGaInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Test", "hello2");
        activity = ((CreationDetailsActivity) getActivity());
        this.moment = activity.getMoment();

        mGaInstance = GoogleAnalytics.getInstance(getActivity());
        mGaTracker = mGaInstance.getTracker(AppMoment.getInstance().GOOGLE_ANALYTICS);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creation_moment_2, container, false);

        if (this.moment.getDescription() != null) {
            EditText description = (EditText) view.findViewById(R.id.creation_moment_description);
            description.setText(this.moment.getDescription());
        }

        if (this.moment.getAdresse() != null) {
            Button adresse = (Button) view.findViewById(R.id.creation_moment_adresse);
            adresse.setText(this.moment.getAdresse());
        }

        if (this.moment.getPlaceInformations() != null) {
            EditText infosLieu = (EditText) view.findViewById(R.id.creation_moment_infos_lieu);
            infosLieu.setText(this.moment.getPlaceInformations());
        }





        EditText descriptionEdit = (EditText) view.findViewById(R.id.creation_moment_description);

        EditText infosLieuEdit = (EditText) view.findViewById(R.id.creation_moment_infos_lieu);

        descriptionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) CreationDetailsActivity.validateDescription = 1;
                else CreationDetailsActivity.validateDescription = 0;

                System.out.println("CA BOUGE");

                CreationDetailsActivity.validateSecondFields();
            }
        }); 
    	



        infosLieuEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) CreationDetailsActivity.validateInfosLieu = 1;
                else CreationDetailsActivity.validateInfosLieu = 0;

                CreationDetailsActivity.validateSecondFields();
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGaTracker.sendView("/CreationStep2Fragment");
    }


}