package com.moment.classes;

/**
 * Created by adriendulong on 10/07/13.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.moment.AppMoment;
import com.moment.R;

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moment.R;
import com.moment.models.Moment;
import com.moment.models.Notification;
import com.moment.util.ImageCache;
import com.moment.util.ImageFetcher;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by adriendulong on 20/06/13.
 */
public class MomentsAdapter extends ArrayAdapter<Moment> {

    Context context;
    int layoutResourceId;
    List<Moment> data = new ArrayList<Moment>();
    private final Transformation roundTrans = new RoundTransformation();
    private ImageFetcher mImageFetcher;



    public MomentsAdapter(Context context, int layoutResourceId, List<Moment> data, ImageFetcher imagefetcher) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.mImageFetcher = imagefetcher;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MomentHolder holder = null;

        final Moment moment = data.get(position);
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(row == null)
        {
            row = vi.inflate(layoutResourceId, parent, false);
            holder = new MomentHolder();



            holder.coverRound = (ImageView)row.findViewById(R.id.cover_moment_timeline);
            holder.nameMoment = (TextView)row.findViewById(R.id.moment_name);
            holder.dateMoment = (TextView)row.findViewById(R.id.moment_date);
            holder.imageRoundedButton = (RelativeLayout)row.findViewById(R.id.cell_full);
            //holder.deleteMoment = (RelativeLayout)row.findViewById(R.id.delete_moment);




            row.setTag(holder);
        }
        else
        {
            holder = (MomentHolder)row.getTag();
        }

        //Picasso.with(context).load(moment.getUrlCover()).transform(roundTrans).placeholder(R.drawable.picto_photo_vide).into(holder.coverRound);
        mImageFetcher.loadImage(moment.getUrlCover(), holder.coverRound, true);
        holder.nameMoment.setText(moment.getName());
        holder.imageRoundedButton.setTag(moment.getId());
        //holder.deleteMoment.setTag(moment.getId());

        //Print date in right format
        Locale locale = Locale.getDefault();
        DateFormat dt=DateFormat.getDateInstance(DateFormat.LONG, locale);
        holder.dateMoment.setText(dt.format(moment.getDateDebut()));

        //Do we show delete button
        /*
        if(moment.getUserId()!= AppMoment.getInstance().user.getId()) holder.deleteMoment.setVisibility(View.INVISIBLE);
        else holder.deleteMoment.setVisibility(View.VISIBLE);
        */


        return row;
    }

    static class MomentHolder
    {
        ImageView coverRound;
        TextView nameMoment;
        TextView dateMoment;
        RelativeLayout deleteMoment, imageRoundedButton;

    }
}
