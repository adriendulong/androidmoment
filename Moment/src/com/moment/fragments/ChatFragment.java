package com.moment.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.R;
import com.moment.activities.MomentInfosActivity;
import com.moment.classes.MomentApi;
import com.moment.classes.RoundTransformation;
import com.moment.models.Chat;
import com.moment.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatFragment extends Fragment {

    private View view;
    private LayoutInflater inflater;
    private Long momentId;
    private PullToRefreshScrollView scrollChat;
    private LinearLayout layoutChat;

    private int nextPage;
    private final Transformation roundTrans = new RoundTransformation();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        this.inflater = inflater;

        if (view != null) {
            layoutChat = (LinearLayout) view.findViewById(R.id.chat_message_layout);
            scrollChat = (PullToRefreshScrollView) view.findViewById(R.id.scroll_chat);
        }

        scrollChat.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(((MomentInfosActivity)getActivity()).getMomentId()!=null){
            this.momentId = ((MomentInfosActivity)getActivity()).getMomentId();
            initChat();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.e("SAVEINSTANCE", "Chats");
        savedInstanceState.putBoolean("Sleep", true);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void messageRight(Chat chat){

        LinearLayout layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_droite, null);

        TextView message = null;
        TextView heure   = null;
        TextView autheur = null;
        ImageView userImage = null;

        if (chatDroit != null) {
            message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
            autheur = (TextView)chatDroit.findViewById(R.id.autheur);
            heure = (TextView)chatDroit.findViewById(R.id.heure);
        }

        if (message != null) {
            message.setText(chat.getMessage());
        }

        if (autheur != null) {
            autheur.setText(chat.getUser().getFirstName());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);

        if (heure != null) {
            heure.setText(""+hh+":"+mm);
        }

        if (chatDroit != null) {
            userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        }

        if(AppMoment.getInstance().checkInternet())
            Picasso.with(getActivity()).load(chat.getUser().getPictureProfileUrl()).transform(roundTrans).into(userImage);

        if (chatDroit != null) {
            layoutChat.addView(chatDroit);
        }

    }

    private void messageRight(Chat chat, int index){

        LinearLayout layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_droite, null);

        TextView message = null;
        TextView heure   = null;
        TextView autheur = null;
        ImageView userImage = null;

        if (chatDroit != null) {
            message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
            autheur = (TextView)chatDroit.findViewById(R.id.autheur);
            heure = (TextView)chatDroit.findViewById(R.id.heure);
        }

        if (message != null) {
            message.setText(chat.getMessage());
        }

        if (autheur != null) {
            autheur.setText(chat.getUser().getFirstName());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);

        if (heure != null) {
            heure.setText(""+hh+":"+mm);
        }

        if (chatDroit != null) {
            userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        }

        if(AppMoment.getInstance().checkInternet())
            Picasso.with(getActivity()).load(chat.getUser().getPictureProfileUrl()).transform(roundTrans).into(userImage);

        if (chatDroit != null) {
            layoutChat.addView(chatDroit, index);
        }

    }

    private void messageLeft(Chat chat){
        layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        scrollChat = (PullToRefreshScrollView)view.findViewById(R.id.scroll_chat);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_gauche, null);

        TextView message = null;
        TextView autheur = null;
        TextView heure = null;
        ImageView userImage = null;

        if (chatDroit != null) {
            message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
            autheur = (TextView)chatDroit.findViewById(R.id.autheur);
            heure   = (TextView)chatDroit.findViewById(R.id.heure);
            userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        }

        if (message != null) {
            message.setText(chat.getMessage());
        }

        if (autheur != null) {
            autheur.setText(chat.getUser().getFirstName());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);

        if (heure != null) {
            heure.setText(""+hh+":"+mm);
        }

        if(AppMoment.getInstance().checkInternet())
            Picasso.with(getActivity()).load(chat.getUser().getPictureProfileUrl()).transform(roundTrans).into(userImage);

        if (chatDroit != null) {
            layoutChat.addView(chatDroit);
        }
    }

    private void messageLeft(Chat chat, int index){
        layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
        scrollChat = (PullToRefreshScrollView)view.findViewById(R.id.scroll_chat);
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_gauche, null);

        TextView message = null;
        TextView autheur = null;
        TextView heure = null;
        ImageView userImage = null;

        if (chatDroit != null) {
            message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
            autheur = (TextView)chatDroit.findViewById(R.id.autheur);
            heure = (TextView) chatDroit.findViewById(R.id.heure);
            userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        }

        if (message != null) {
            message.setText(chat.getMessage());
        }

        if (autheur != null) {
            autheur.setText(chat.getUser().getFirstName());
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(chat.getDate());
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);

        if (heure != null) {
            heure.setText(""+hh+":"+mm);
        }

        if(AppMoment.getInstance().checkInternet())
            Picasso.with(getActivity()).load(chat.getUser().getPictureProfileUrl()).transform(roundTrans).into(userImage);


        if (chatDroit != null) {
            layoutChat.addView(chatDroit, index);
        }
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
                jsonChats = getChatsFromURL(MomentApi.BASE_URL + momentId + "/" + nextPage);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(jsonChats == null){
                return null;
            }

            if(jsonChats.has("next_page"))
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

            if (chats != null) {
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
            }
            return tempChats;
        }


        @Override
        protected void onPostExecute(ArrayList<Chat> chats) {
            if(chats != null)
            {
                int index = 0;
                for(Chat chat : chats){
                    if (chat.getUser().getId().equals(AppMoment.getInstance().user.getId())) {
                        messageRight(chat, index);

                    } else {
                        messageLeft(chat, index);
                    }
                    index ++;
                }
            }

            scrollChat.onRefreshComplete();
            super.onPostExecute(chats);
        }

        private JSONObject getChatsFromURL(String url) throws JSONException {
            try {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                httpclient.setCookieStore(MomentApi.myCookieStore);
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Content-type", "application/json");
                InputStream inputStream;
                String result;
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                result = sb.toString();

                return new JSONObject(result);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }


    /**
     * Function called when the activity got the Moment
     */

    public void createFragment(Long momentId){
        this.momentId = momentId;

        initChat();
    }

    /**
     * Function that takes care to init the chats
     */

    private void initChat(){
        Log.d("CHATFRAGMENT", "INIT");
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

                            if(tempChat.getUser().getId().equals(AppMoment.getInstance().user.getId())){
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

}