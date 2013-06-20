package com.moment.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatFragment extends Fragment {

    public View view;
    public LayoutInflater inflater;
    public Long momentId;

    PullToRefreshScrollView scrollChat;
    ScrollView mScrollView;
    LinearLayout layoutChat;

    int nextPage;

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

                        if(!response.isNull("next_page"))
                        {
                            if(response.getInt("next_page") >= nextPage)
                                nextPage = response.getInt("next_page");
                            else
                                nextPage = 0;
                        }

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
        TextView autheur = (TextView)chatDroit.findViewById(R.id.autheur);
        TextView heure   = (TextView)chatDroit.findViewById(R.id.heure);

        message.setText(chat.getMessage());
        autheur.setText(chat.getUser().getFirstName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        heure.setText(""+hh+":"+mm);

        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);

        if(AppMoment.getInstance().checkInternet())
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

    public void messageRight(Chat chat, int index){

        LinearLayout layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        PullToRefreshScrollView scrollChat = (PullToRefreshScrollView)view.findViewById(R.id.scroll_chat);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_droite, null);

        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        TextView autheur = (TextView)chatDroit.findViewById(R.id.autheur);
        TextView heure   = (TextView)chatDroit.findViewById(R.id.heure);

        message.setText(chat.getMessage());
        autheur.setText(chat.getUser().getFirstName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        heure.setText(""+hh+":"+mm);

        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);

        if(AppMoment.getInstance().checkInternet())
            chat.getUser().printProfilePicture(userImage, true);

        layoutChat.addView(chatDroit, index);

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
        TextView autheur = (TextView)chatDroit.findViewById(R.id.autheur);
        TextView heure   = (TextView)chatDroit.findViewById(R.id.heure);

        message.setText(chat.getMessage());
        autheur.setText(chat.getUser().getFirstName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        heure.setText(""+hh+":"+mm);


        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);

        if(AppMoment.getInstance().checkInternet())
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

    public void messageLeft(Chat chat, int index){
        layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        scrollChat = (PullToRefreshScrollView)view.findViewById(R.id.scroll_chat);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_gauche, null);
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        TextView autheur = (TextView)chatDroit.findViewById(R.id.autheur);
        TextView heure   = (TextView)chatDroit.findViewById(R.id.heure);

        message.setText(chat.getMessage());
        autheur.setText(chat.getUser().getFirstName());

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        heure.setText(""+hh+":"+mm);

        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);

        if(AppMoment.getInstance().checkInternet())
            chat.getUser().printProfilePicture(userImage, true);

        layoutChat.addView(chatDroit, index);

       /* new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)view.findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);
        */
    }

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<Chat>> {

        @Override
        protected void onPreExecute(){
            if (nextPage < 2){
                scrollChat.onRefreshComplete();
                cancel(true);
            }
        }

        @Override
        protected ArrayList<Chat> doInBackground(Void... params) {

            JSONObject jsonChats = null;

            try {
                jsonChats = getChatsFromURL("http://api.appmoment.fr/lastchats/" + momentId + "/" + nextPage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!jsonChats.isNull("next_page"))
                try {
                    if(jsonChats.getInt("next_page") >= nextPage)
                        nextPage = jsonChats.getInt("next_page");
                    else
                        nextPage = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            JSONArray chats = null;
            try {
                chats = jsonChats.getJSONArray("chats");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<Chat> tempChats = new ArrayList<Chat>();

            for (int i = 0; i < chats.length(); i++) {

                Chat tempChat = new Chat();

                try {
                    tempChat.chatFromJSON(chats.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                User user = tempChat.getUser();

                if (AppMoment.getInstance().chatDao.load(tempChat.getId()) == null) {
                    tempChat.setMomentId(momentId);
                    AppMoment.getInstance().chatDao.insert(tempChat);
                    if (AppMoment.getInstance().userDao.load(user.getId()) == null)
                        AppMoment.getInstance().userDao.insert(user);
                }

                tempChats.add(tempChat);

            }
            return tempChats;
        }


        @Override
        protected void onPostExecute(ArrayList<Chat> chats) {
            scrollChat.onRefreshComplete();
            int index = 0;
            for(Chat chat : chats){
                if (chat.getUser().getId() == AppMoment.getInstance().user.getId()) {
                    messageRight(chat, index);

                } else {
                    messageLeft(chat, index);
                }
                index ++;
            }

            super.onPostExecute(chats);
        }

        private JSONObject getChatsFromURL(String url) throws JSONException {
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                httpclient.setCookieStore(MomentApi.myCookieStore);
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Content-type", "application/json");
                InputStream inputStream = null;
                String result = null;
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
                JSONObject jObject = new JSONObject(result);

                return jObject;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

}