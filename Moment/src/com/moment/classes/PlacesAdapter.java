package com.moment.classes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.moment.R;
import com.moment.models.Place;

import java.util.ArrayList;

/**
 * Created by adriendulong on 22/05/13.
 */
public class PlacesAdapter extends BaseAdapter {

    Context context;
    int layoutResourceId;
    ArrayList<Place> places;

    public PlacesAdapter(Context context, ArrayList<Place> data, int resourceId) {
        this.context = context;
        this.places = data;
        this.layoutResourceId = resourceId;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Place getItem(int position) {
        return places.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PlacesHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);


            holder = new PlacesHolder();
            holder.mainInfo = (TextView)row.findViewById(R.id.place_main_info);
            holder.secondInfo = (TextView)row.findViewById(R.id.secondText_place);
            holder.thirdInfo = (TextView)row.findViewById(R.id.thirdText_place);
            holder.back = (LinearLayout)row.findViewById(R.id.back_places_cell);
            holder.places_pitco = (ImageView)row.findViewById(R.id.places_pitco);
            holder.comma_place = (TextView)row.findViewById(R.id.comma_place);

            row.setTag(holder);
        }
        else
        {
            holder = (PlacesHolder)row.getTag();
        }

        Place place = places.get(position);
        holder.mainInfo.setText(place.placeOne);
        holder.secondInfo.setText(place.placeTwo);
        holder.thirdInfo.setText(place.placeThree);

        if(position%2==0){
            holder.back.setBackgroundResource(R.drawable.bg_cellule);
            holder.back.setPadding(10, 10, 10,10);
            if(position==0){
                holder.places_pitco.setVisibility(View.VISIBLE);
                holder.comma_place.setVisibility(View.GONE);
                holder.secondInfo.setText(R.string.custome_loc);
            }
        }
        else{
            holder.back.setBackgroundResource(R.drawable.bg);
            holder.back.setPadding(10, 10, 10,10);
        }

        if(place.placeThree==null) holder.comma_place.setVisibility(View.INVISIBLE);
        else holder.comma_place.setVisibility(View.VISIBLE);


        return row;
    }

    public static class PlacesHolder
    {
        TextView mainInfo;
        TextView secondInfo;
        TextView thirdInfo;
        TextView comma_place;
        LinearLayout back;
        ImageView places_pitco;
    }
}