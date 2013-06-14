package com.moment.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.classes.MomentApi;
import com.moment.models.Chat;
import com.moment.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    public View view;
    public LayoutInflater inflater;
    public Long momentId;

    PullToRefreshScrollView scrollChat;
    ScrollView mScrollView;
    LinearLayout layoutChat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        momentId = getActivity().getIntent().getLongExtra("id", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.inflater = inflater;

        layoutChat = (LinearLayout) view.findViewById(R.id.chat_message_layout);
        scrollChat = (PullToRefreshScrollView) view.findViewById(R.id.scroll_chat);
        scrollChat.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });

        mScrollView = scrollChat.getRefreshableView();

        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(!AppMoment.getInstance().checkInternet()){
            List<Chat> tempChats = AppMoment.getInstance().chatDao.loadAll();

            for(Chat c : tempChats){

                User user = AppMoment.getInstance().userDao.load(c.getUserId());

                c.setUser(user);

                if(c.getMomentId() == momentId){

                    if(c.getUserId() ==  AppMoment.getInstance().user.getId()){
                        messageRight(c);
                    }

                    else{
                        messageLeft(c);
                    }
                }
            }
        }

        if(AppMoment.getInstance().checkInternet()){
            MomentApi.get("lastchats/"+momentId, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONArray chats;
                        chats = response.getJSONArray("chats");

                        ArrayList<Chat> tempChats = new ArrayList<Chat>();

                        for(int i=0;i<chats.length();i++){

                            Chat tempChat = new Chat();

                            tempChat.chatFromJSON(chats.getJSONObject(i));
                            User user = tempChat.getUser();

                            if(AppMoment.getInstance().chatDao.load(tempChat.getId()) == null){
                                tempChat.setMomentId(momentId);
                                AppMoment.getInstance().chatDao.insert(tempChat);
                                if(AppMoment.getInstance().userDao.load(user.getId()) == null)
                                    AppMoment.getInstance().userDao.insert(user);
                            }

                            if(tempChat.getUser().getId() ==  AppMoment.getInstance().user.getId()){
                                messageRight(tempChat);
                            }

                            else{
                                messageLeft(tempChat);
                            }

                            tempChats.add(tempChat);
                        }

                        AppMoment.getInstance().user.getMomentById(momentId).getChats().addAll(tempChats);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.e("SAVEINSTANCE", "Chats");
        savedInstanceState.putBoolean("Sleep", true);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void messageRight(Chat chat){

        LinearLayout layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        PullToRefreshScrollView scrollChat = (PullToRefreshScrollView)view.findViewById(R.id.scroll_chat);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_droite, null);

        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());

        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        chat.getUser().printProfilePicture(userImage, true);

        layoutChat.addView(chatDroit);

        /*new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)view.findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);*/
    }

    public void messageLeft(Chat chat){
        layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        scrollChat = (PullToRefreshScrollView)view.findViewById(R.id.scroll_chat);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_gauche, null);
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());
        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        chat.getUser().printProfilePicture(userImage, true);
        layoutChat.addView(chatDroit);

       /* new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)view.findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);
        */
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Do some stuff here

            // Call onRefreshComplete when the list has been refreshed.
            scrollChat.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

}