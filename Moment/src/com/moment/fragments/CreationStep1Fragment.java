package com.moment.fragments;

import android.content.ContentResolver;
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
import com.moment.util.BitmapWorkerTask;
import com.moment.util.CommonUtilities;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

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

        dateDebut = (Button) view.findViewById(R.id.date_debut_button);
        heureDebut = (Button) view.findViewById(R.id.heure_debut_button);
        dateFin = (Button) view.findViewById(R.id.date_fin_button);
        heureFin = (Button) view.findViewById(R.id.heure_fin_button);

        ImageView photo_moment = (ImageView) view.findViewById(R.id.creation_moment_image);
        if (((CreationDetailsActivity)getActivity()).getCoverUri()!=null) {
            ContentResolver cr = getActivity().getContentResolver();
            BitmapWorkerTask task = new BitmapWorkerTask(photo_moment, 900,cr);
            task.execute(((CreationDetailsActivity)getActivity()).getCoverUri());

        }
        else if (this.moment.getUrlCover() != null)
            Picasso.with(getActivity()).load(this.moment.getUrlCover()).into(photo_moment);

        org.joda.time.DateTime dt;

        if (moment.getDateDebut() != null) {
            dt = CommonUtilities.dateFormatISO.parseDateTime(moment.getDateDebut());
            dateDebut.setText("" + dt.getDayOfMonth() + "/" + dt.getMonthOfYear() + "/" + dt.getYear());
            heureDebut.setText(dt.getHourOfDay() + ":" + dt.getMinuteOfHour());
        }

        if (moment.getDateFin() != null) {
            dt = CommonUtilities.dateFormatISO.parseDateTime(moment.getDateFin());
            dateFin.setText("" + dt.getDayOfMonth() + "/" + dt.getMonthOfYear() + "/" + dt.getYear());
            heureFin.setText(dt.getHourOfDay() + ":" + dt.getMinuteOfHour());
        }
        return view;
    }

    public DateTime getStartDate() {
        org.joda.time.DateTime dt;
        if(heureDebut.getText().toString().split(":").length>1) dt = CommonUtilities.dateFormatReverse.parseDateTime(dateDebut.getText().toString() + " " + heureDebut.getText().toString());
        else dt = CommonUtilities.dateFormatReverse.parseDateTime(dateDebut.getText().toString() + " 00:00");
        return dt;
    }

    public DateTime getEndDate() {
        org.joda.time.DateTime dt;
        if(heureFin.getText().toString().split(":").length>1) dt = CommonUtilities.dateFormatReverse.parseDateTime(dateFin.getText().toString() + " " + heureFin.getText().toString());
        else dt = CommonUtilities.dateFormatReverse.parseDateTime(dateFin.getText().toString() + " 00:00");
        return dt;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGaTracker.sendView("/CreationStep1Fragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("STEP 1", "DESTROY");
    }

}