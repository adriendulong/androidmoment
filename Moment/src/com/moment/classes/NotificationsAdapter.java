package com.moment.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.moment.R;
import com.moment.models.User;
import com.moment.models.Notification;

import java.util.ArrayList;

/**
 * Created by adriendulong on 20/06/13.
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> {

    Context context;
    int layoutResourceId;
    ArrayList<Notification> data = new ArrayList<Notification>();

    public NotificationsAdapter(Context context, int layoutResourceId, ArrayList<Notification> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        NotifHolder holder = null;

        if(row == null)
        {

            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = vi.inflate(layoutResourceId, parent, false);

            /*
            holder = new NotifHolder();

            holder.txtFirstname = (TextView)row.findViewById(R.id.firstname_invitations);
            holder.txtLastname = (TextView)row.findViewById(R.id.lastname_invitations);
            holder.photo_thumbnail = (ImageView)row.findViewById(R.id.photo_invitation);
            holder.bg = row.findViewById(R.id.bg_cell_invitations);
            */
            row.setTag(holder);
        }
        else
        {
            holder = (NotifHolder)row.getTag();
            /*
            holder.photo_thumbnail = (ImageView)row.findViewById(R.id.photo_invitation);
            holder.bg = row.findViewById(R.id.bg_cell_invitations);
            */


        }


        /*
        Notification notif = data.get(position);
        holder.txtFirstname.setText(user.getFirstName());
        holder.txtLastname.setText(user.getLastName());
        holder.photo_thumbnail.setImageResource(R.drawable.back_goldphoto);
        holder.bg.setBackgroundResource(R.drawable.background);
        */


        return row;
    }

    static class NotifHolder
    {
        int typeId;
        TextView txtFirstname;
        TextView txtLastname;
        ImageView photo_thumbnail;
        View bg;
    }
}