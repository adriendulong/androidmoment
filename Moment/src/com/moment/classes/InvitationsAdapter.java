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

import java.util.ArrayList;

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
        holder.txtFirstname.setText(user.getFirstName());
        holder.txtLastname.setText(user.getLastName());
        holder.photo_thumbnail.setImageResource(R.drawable.back_goldphoto);
        holder.bg.setBackgroundResource(R.drawable.background);
        
        if(user.getPhotoThumbnail()!=null){
        	System.out.println("NOT NULL");
            holder.photo_thumbnail.setImageBitmap(Images.getRoundedCornerBitmap(user.getPhotoThumbnail()));
        }
        else if(user.getFbPhotoUrl()!=null){
        	Images.printImageFromUrl(holder.photo_thumbnail, true, user.getFbPhotoUrl());
        }
        else if(user.getPictureProfileUrl()!=null){
            //TODO : Verifier si existe pas dans le cache ?
            Images.printImageFromUrl(holder.photo_thumbnail, true, user.getPictureProfileUrl());
        }
        else{
        	holder.photo_thumbnail.setImageResource(R.drawable.back_goldphoto);
        }
        
        if(user.getIsSelect()) holder.bg.setBackgroundColor(context.getResources().getColor(R.color.orange));

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