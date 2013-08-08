package com.moment.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.CreationDetailsActivity;
import com.moment.models.Moment;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CreationStep1Fragment extends Fragment {
    private Moment moment;
    private Button dateDebut, dateFin, heureDebut, heureFin;

    private Tracker mGaTracker;
    private GoogleAnalytics mGaInstance;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Test", "hello");
        this.moment = ((CreationDetailsActivity) getActivity()).getMoment();

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
        View view = inflater.inflate(R.layout.fragment_creation_moment_1, container, false);

        TextView name = (TextView) view.findViewById(R.id.creation_moment_name);
        name.setText(this.moment.getName());

        //Buttons
        //Buttons
        dateDebut = (Button) view.findViewById(R.id.date_debut_button);
        heureDebut = (Button) view.findViewById(R.id.heure_debut_button);
        dateFin = (Button) view.findViewById(R.id.date_fin_button);
        heureFin = (Button) view.findViewById(R.id.heure_fin_button);

        if (this.moment.getKeyBitmap() != null) {
            ImageView photo_moment = (ImageView) view.findViewById(R.id.creation_moment_image);
            Bitmap photo_moment_bitmap = AppMoment.getInstance().getBitmapFromMemCache(this.moment.getKeyBitmap());
            if (photo_moment_bitmap != null) photo_moment.setImageBitmap(photo_moment_bitmap);
            else if (this.moment.getUrlCover() != null)
                Picasso.with(getActivity()).load(this.moment.getUrlCover()).into(photo_moment);
        }

        //On itnitialise la Date debut
        if (moment.getDateDebut() != null) {
            Log.d("Date Debut", "" + moment.getDateDebut().toString());

            Calendar calDebut = Calendar.getInstance();
            calDebut.setTime(moment.getDateDebut());
            int anneeDebut = calDebut.get(Calendar.YEAR);
            int moisDebut = calDebut.get(Calendar.MONTH);
            moisDebut += 1;
            int jourDebut = calDebut.get(Calendar.DAY_OF_MONTH);

            dateDebut.setText("" + jourDebut + "/" + moisDebut + "/" + anneeDebut);
            heureDebut.setText(calDebut.get(Calendar.HOUR) + ":" + calDebut.get(Calendar.MINUTE));
        }

        //On itnitialise la Date de fin
        if (moment.getDateFin() != null) {
            Log.d("Date Debut", "" + moment.getDateFin().toString());
            Calendar calFin = Calendar.getInstance();
            calFin.setTime(moment.getDateFin());
            int anneeFin = calFin.get(Calendar.YEAR);
            int moisFin = calFin.get(Calendar.MONTH);
            moisFin += 1;
            int jourFin = calFin.get(Calendar.DAY_OF_MONTH);

            dateFin.setText("" + jourFin + "/" + moisFin + "/" + anneeFin);
            heureFin.setText(calFin.get(Calendar.HOUR) + ":" + calFin.get(Calendar.MINUTE));
        }


        return view;
    }

    public Date getStartDate() {
        int jourDebut = Integer.parseInt(dateDebut.getText().toString().split("/")[0]);
        int moisDebut = Integer.parseInt(dateDebut.getText().toString().split("/")[1]) - 1;
        int anneeDebut = Integer.parseInt(dateDebut.getText().toString().split("/")[2]);
        GregorianCalendar calendarDebut;


        if (heureDebut.getText().toString().split(":").length == 2) {
            int heureDebutInt = Integer.parseInt(heureDebut.getText().toString().split(":")[0]);
            int minuteDebutInt = Integer.parseInt(heureDebut.getText().toString().split(":")[1]);
            calendarDebut = new GregorianCalendar(anneeDebut, moisDebut, jourDebut, heureDebutInt, minuteDebutInt);
        } else {
            calendarDebut = new GregorianCalendar(anneeDebut, moisDebut, jourDebut);
        }


        return new Date(calendarDebut.getTimeInMillis());
    }

    public Date getEndDate() {
        int jourFin = Integer.parseInt(dateFin.getText().toString().split("/")[0]);
        int moisFin = Integer.parseInt(dateFin.getText().toString().split("/")[1]) - 1;
        int anneeFin = Integer.parseInt(dateFin.getText().toString().split("/")[2]);
        GregorianCalendar calendarFin;


        if (heureFin.getText().toString().split(":").length == 2) {
            int heureFinInt = Integer.parseInt(heureFin.getText().toString().split(":")[0]);
            int minuteFinInt = Integer.parseInt(heureFin.getText().toString().split(":")[1]);
            calendarFin = new GregorianCalendar(anneeFin, moisFin, jourFin, heureFinInt, minuteFinInt);
        } else {
            calendarFin = new GregorianCalendar(anneeFin, moisFin, jourFin);
        }


        return new Date(calendarFin.getTimeInMillis());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGaTracker.sendView("/CreationStep1Fragment");
    }


}