package com.moment.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moment.R;
import com.moment.models.Notification;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by adriendulong on 20/06/13.
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> {

    Context context;
    int layoutResourceId;
    ArrayList<Notification> data = new ArrayList<Notification>();
    private int type;
    private final Transformation roundTrans = new RoundTransformation();

    private int NOTIFICATIONS = 0;
    private int INVITATIONS = 1;

    public NotificationsAdapter(Context context, int layoutResourceId, ArrayList<Notification> data, int type) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NotifHolder holder = null;
        Notification notif = data.get(position);


        if(row == null)
        {

            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = vi.inflate(layoutResourceId, parent, false);


            holder = new NotifHolder();

            //Notifications list
            if(type==NOTIFICATIONS){
                holder.imageNotif = (ImageView)row.findViewById(R.id.image_notif);
                holder.textNotif = (TextView)row.findViewById(R.id.text_notif);
            }
            //Invitations list
            else{
                holder.imageMoment = (ImageView)row.findViewById(R.id.moment_image_notif);
                holder.nameMoment = (TextView)row.findViewById(R.id.title_moment_notif);
                holder.nbGuest = (TextView)row.findViewById(R.id.nb_guests_moment_notif);
            }

            row.setTag(holder);


        }
        else
        {
            holder = (NotifHolder)row.getTag();

        }


        //If it is a Photo notif
        if(notif.getTypeNotif()==2){
            String message = getContext().getString(R.string.notif_photo)+" "+notif.getMoment().getName();
            holder.textNotif.setText(message);
            holder.imageNotif.setImageResource(R.drawable.picto_photo_volet);

        }
        //New Chat
        else if(notif.getTypeNotif()==3){
            String message = getContext().getString(R.string.notif_message)+" "+notif.getMoment().getName();
            holder.textNotif.setText(message);
            holder.imageNotif.setImageResource(R.drawable.picto_message_volet);
        }
        else if(notif.getTypeNotif()==0){
            holder.nameMoment.setText(notif.getMoment().getName());
            holder.nbGuest.setText(""+notif.getMoment().getGuestNumber());

            //Image
            if(notif.getMoment().getUrlCover()!= null){
                Picasso.with(context).load(notif.getMoment().getUrlCover()).resize(100,100).transform(roundTrans).into(holder.imageMoment);
            }
        }


        return row;
    }

    static class NotifHolder
    {
        int typeId;
        TextView textNotif;
        ImageView imageNotif;
        ImageView imageMoment;
        TextView nameMoment;
        TextView nbGuest;

    }
}