package com.moment.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.moment.R;
import com.moment.classes.InvitationsAdapter;
import com.moment.models.User;

import java.util.ArrayList;

public class GuestsFragment extends Fragment {

    private int position;
    public static final String POSITION = "Position";
    private ListView listView;
    private ArrayList<User> users;
    private InvitationsAdapter adapter;

    public GuestsFragment(int positionFragment){
        position = positionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){

        //Wee get the list view
        View rootView = inflater.inflate(R.layout.activity_invitations_fragment, container, false);
        listView = (ListView)rootView.findViewById(R.id.list_view_contacts);

        //Listener of the list view
        Myonclicklistneer myClickList = new Myonclicklistneer();
        listView.setOnItemClickListener(myClickList);

        //We init the array list of users
        users = new ArrayList<User>();

        //We set the adapter for the list
        adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
        listView.setAdapter(adapter);




        //Handle the position
        Bundle args = getArguments();
        position = args.getInt(POSITION);

        //Depeending on the position of the fragment the list will be different.
        if(position==0){
            Log.d("POSITION FRAGMENT", ""+position);
        }
        else if(position==1){
            Log.d("POSITION FRAGMENT", ""+position);
        }
        else if(position==2){
            Log.d("POSITION FRAGMENT", ""+position);
        }

        return rootView;
    }



    @Override
    public void onStart(){
        super.onStart();

    }

    /**
     * Function which reload the list when the user list has been updated
     */

    public void updateListGuests(ArrayList<User> guests){
        Log.d("FRAGMENT"+position, "UPDATE LISTE");
        users.clear();
        for(int i=0;i<guests.size();i++){
            users.add(guests.get(i));
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * Listeneer of the list
     */

    class Myonclicklistneer implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

        }
    }
}