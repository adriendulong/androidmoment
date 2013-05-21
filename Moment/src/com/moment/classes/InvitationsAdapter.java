package com.moment.classes;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moment.R;

public class InvitationsAdapter extends ArrayAdapter<User>{

    Context context; 
    int layoutResourceId;    
    ArrayList<User> data = new ArrayList<User>();
    
    public InvitationsAdapter(Context context, int layoutResourceId, ArrayList<User> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;
        
        if(row == null)
        {
        	
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = vi.inflate(layoutResourceId, parent, false);
            
            holder = new UserHolder();
            
            holder.txtFirstname = (TextView)row.findViewById(R.id.firstname_invitations);
            holder.txtLastname = (TextView)row.findViewById(R.id.lastname_invitations);
            holder.photo_thumbnail = (ImageView)row.findViewById(R.id.photo_invitation);
            holder.bg = row.findViewById(R.id.bg_cell_invitations);
            
            row.setTag(holder);
        }
        else
        {
            holder = (UserHolder)row.getTag();
            holder.photo_thumbnail = (ImageView)row.findViewById(R.id.photo_invitation);
            holder.bg = row.findViewById(R.id.bg_cell_invitations);
            
          
        }
        
        User user = data.get(position);
        holder.txtFirstname.setText(user.getFirstname());
        holder.txtLastname.setText(user.getLastname());
        holder.photo_thumbnail.setImageResource(R.drawable.back_goldphoto);
        holder.bg.setBackgroundResource(R.drawable.background);
        
        if(user.getPhoto_thumbnail()!=null){
        	System.out.println("NOT NULL");
            holder.photo_thumbnail.setImageBitmap(Images.getRoundedCornerBitmap(user.getPhoto_thumbnail()));
        }
        else if(user.getFb_photo_url()!=null){
        	Images.printImageFromUrl(holder.photo_thumbnail, true, user.getFb_photo_url());
        }
        else{
        	holder.photo_thumbnail.setImageResource(R.drawable.back_goldphoto);
        }
        
        if(user.getIs_selected()) holder.bg.setBackgroundColor(context.getResources().getColor(R.color.orange));

        
        
        
        return row;
    }
    
    static class UserHolder
    {
    	boolean isSelected;
        TextView txtFirstname;
        TextView txtLastname;
        ImageView photo_thumbnail;
        View bg;
    }
}