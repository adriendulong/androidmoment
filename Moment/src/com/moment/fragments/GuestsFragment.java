package com.moment.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.moment.AppMoment;
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

    private Tracker mGaTracker;
    private GoogleAnalytics mGaInstance;

    public GuestsFragment(int positionFragment) {
        position = positionFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mGaInstance = GoogleAnalytics.getInstance(getActivity());


        mGaTracker = mGaInstance.getTracker(AppMoment.getInstance().GOOGLE_ANALYTICS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.activity_invitations_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_view_contacts);


        Myonclicklistneer myClickList = new Myonclicklistneer();
        listView.setOnItemClickListener(myClickList);


        users = new ArrayList<User>();


        adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
        listView.setAdapter(adapter);



        Bundle args = getArguments();
        position = args.getInt(POSITION);


        if (position == 0) {
            Log.d("POSITION FRAGMENT", "" + position);
        } else if (position == 1) {
            Log.d("POSITION FRAGMENT", "" + position);
        } else if (position == 2) {
            Log.d("POSITION FRAGMENT", "" + position);
        }

        if (savedInstanceState != null) {
            users = savedInstanceState.getParcelableArrayList("users");
            adapter = new InvitationsAdapter(getActivity().getApplicationContext(), R.layout.invitations_cell, users);
            listView.setAdapter(adapter);
        }

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        mGaTracker.sendView("/GuestsFragment");
    }



    public void updateListGuests(ArrayList<User> guests) {
        Log.d("FRAGMENT" + position, "UPDATE LISTE");
        users.clear();
        for (int i = 0; i < guests.size(); i++) {
            users.add(guests.get(i));
        }
        adapter.notifyDataSetChanged();
    }




    class Myonclicklistneer implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("users", users);
    }
}