package com.moment.classes;

/**
 * Created by adriendulong on 10/07/13.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moment.R;
import com.moment.models.Moment;
import com.moment.util.CommonUtilities;
import com.moment.util.ImageFetcher;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            row.setTag(holder);
        }
        else
        {
            holder = (MomentHolder)row.getTag();
        }

        mImageFetcher.loadImage(moment.getUrlCover(), holder.coverRound, true);
        holder.nameMoment.setText(moment.getName());
        holder.imageRoundedButton.setTag(moment.getId());

        Date dt = CommonUtilities.dateFormatISO.parseDateTime(moment.getDateDebut()).toDate();
        holder.dateMoment.setText(CommonUtilities.dateFormatFullMonth.format(dt));
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
