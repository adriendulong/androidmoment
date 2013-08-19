package com.moment.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.moment.R;
import com.moment.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class InvitationsAdapter extends ArrayAdapter<User> implements Filterable{

    Context context; 
    int layoutResourceId;    
    ArrayList<User> data = new ArrayList<User>();
    private ArrayList<User> mOriginalValues;
    private UserFilter userFilter;
    private final Transformation roundTrans = new RoundTransformation();
    
    public InvitationsAdapter(Context context, int layoutResourceId, ArrayList<User> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public void modifyData(ArrayList<User> users){
        this.data = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;
        System.out.println("Position : "+position);
        
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
        if((user.getFirstName()==null)&&(user.getLastName()==null)){
            if(user.getEmail()!=null) holder.txtFirstname.setText(user.getEmail());
            else if(user.getNumTel()!=null) holder.txtFirstname.setText(user.getNumTel());
        }
        else{
            holder.txtFirstname.setText(user.getFirstName());
            holder.txtLastname.setText(user.getLastName());
        }
        holder.photo_thumbnail.setImageResource(R.drawable.back_goldphoto);
        holder.bg.setBackgroundColor(context.getResources().getColor(R.color.gris_chat));
        
        if(user.getPictureProfileUrl()!=null){
            Picasso.with(context).load(user.getPictureProfileUrl()).transform(roundTrans).resize(100,100).placeholder(R.drawable.picto_photo_vide).into(holder.photo_thumbnail);
        }
        else if(user.getFbPhotoUrl()!=null){
            Picasso.with(context).load(user.getFbPhotoUrl()).transform(roundTrans).resize(100,100).placeholder(R.drawable.picto_photo_vide).into(holder.photo_thumbnail);
        }
        else{
        	holder.photo_thumbnail.setImageResource(R.drawable.back_goldphoto);
        }
        
        if(user.getIsSelect()!=null){
            if (user.getIsSelect()) holder.bg.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }

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

    @Override
    public Filter getFilter() {
        if (userFilter == null)
            userFilter = new UserFilter();

        return userFilter;
    }


    private class UserFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
            ArrayList<User> FilteredUsers = new ArrayList<User>();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<User>(data); // saves the original data in mOriginalValues
            }

            /********
             *
             *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
             *  else does the Filtering and returns FilteredArrList(Filtered)
             *
             ********/
            if (constraint == null || constraint.length() == 0) {

                // set the Original result to return
                results.count = mOriginalValues.size();
                results.values = mOriginalValues;
            } else {
                constraint = constraint.toString().toLowerCase();

                for (int i = 0; i < mOriginalValues.size(); i++) {
                    String dataFirstname = mOriginalValues.get(i).getFirstName();
                    String dataLastName = mOriginalValues.get(i).getLastName();
                    if (dataFirstname.toLowerCase().startsWith(constraint.toString())) {
                        FilteredUsers.add(mOriginalValues.get(i));
                    }
                    else if (dataLastName!=null){
                        if (dataLastName.toLowerCase().startsWith(constraint.toString())) {
                            FilteredUsers.add(mOriginalValues.get(i));
                        }
                    }
                }


                if(FilteredUsers.size()==0){
                    User customUser = new User();
                    if(constraint.length()==1){
                        if(Character.isLetter(constraint.charAt(0))){
                            customUser.setEmail(constraint.toString());
                        }
                        else{
                            customUser.setNumTel(constraint.toString());
                        }
                    }
                    else{
                        if(Character.isLetter(constraint.charAt(1))){
                            customUser.setEmail(constraint.toString());
                        }
                        else{
                            customUser.setNumTel(constraint.toString());
                        }
                    }
                    FilteredUsers.add(customUser);

                 }
                // set the Filtered result to return
                results.count = FilteredUsers.size();
                results.values = FilteredUsers;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                //data = (ArrayList<User>) results.values;
                data.clear();
                for (User user : (ArrayList<User>)results.values){
                    data.add(user);
                }
                notifyDataSetChanged();
            }

        }

    }
}