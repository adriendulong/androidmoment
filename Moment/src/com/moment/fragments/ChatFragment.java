package com.moment.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.activities.MomentInfosActivity;
import com.moment.classes.ChatAdapter;
import com.moment.classes.MomentApi;
import com.moment.classes.RoundTransformation;
import com.moment.models.Chat;
import com.moment.models.Photo;
import com.moment.models.User;
import com.moment.util.ImageCache;
import com.moment.util.ImageFetcher;
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
import java.text.DateFormatSymbols;
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
    private TextView defaultTextChat;

    private Tracker mGaTracker;
    private GoogleAnalytics mGaInstance;

    private ArrayList<Chat> chats;
    private ListView mChatsList;
    private ChatAdapter adapter;

    private static final String IMAGE_CACHE_DIR = "profile";
    private ImageFetcher mImageFetcher;
    private int mImageThumbSize;

    private final static String TAG = "ChatsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGaInstance = GoogleAnalytics.getInstance(getActivity());
        mGaTracker = mGaInstance.getTracker(AppMoment.getInstance().GOOGLE_ANALYTICS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatsList = (ListView)view.findViewById(R.id.chats_list);
        defaultTextChat = (TextView) view.findViewById(R.id.default_text_chat);


        if(savedInstanceState!=null){
            chats = savedInstanceState.getParcelableArrayList("chats");
            if(BuildConfig.DEBUG) Log.d(TAG, "Saved");

            if(chats!=null){
                initViewForChats();
                adapter = new ChatAdapter(getActivity(), R.layout.chat_message_droite, chats, mImageFetcher);
                mChatsList.setAdapter(adapter);
            }
        }

        //Image Fetcher
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_profile);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.1f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.picto_photo_vide);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);





        /*
        this.inflater = inflater;

        if (view != null) {
            layoutChat = (LinearLayout) view.findViewById(R.id.chat_message_layout);
            scrollChat = (PullToRefreshScrollView) view.findViewById(R.id.scroll_chat);

        }

        scrollChat.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                EasyTracker.getTracker().sendEvent("Chat", "scroll_refresh", "Load old chats", null);
                new GetDataTask().execute();
            }
        });*/

        if(chats==null){

            chats = new ArrayList<Chat>();
            adapter = new ChatAdapter(getActivity(), R.layout.chat_message_droite, chats, mImageFetcher);
            mChatsList.setAdapter(adapter);

            if (((MomentInfosActivity) getActivity()).getMomentId() != null) {
                this.momentId = ((MomentInfosActivity) getActivity()).getMomentId();
                Log.d("CHAT", "INIT");
                initChat();

            }
        }


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mGaTracker.sendView("/ChatFragment");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(chats!=null) savedInstanceState.putParcelableArrayList("chats", chats);
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<Chat>> {

        @Override
        protected void onPreExecute() {
            if (nextPage < 2) {
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

            if (jsonChats == null) {
                return null;
            }

            if (jsonChats.has("next_page"))
                try {
                    if (jsonChats.getInt("next_page") >= nextPage)
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
            if (chats != null) {
                int index = 0;
                for (Chat chat : chats) {
                    if (chat.getUser().getId().equals(AppMoment.getInstance().user.getId())) {
                        messageRight(chat, index);

                    } else {
                        messageLeft(chat, index);
                    }
                    index++;
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
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();

                return new JSONObject(result);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }*/


    public void createFragment(Long momentId) {
        this.momentId = momentId;

        initChat();
    }


    private void initChat() {
        Log.d("CHATFRAGMENT", "INIT");

        //Withou internet
        if (!AppMoment.getInstance().checkInternet()) {
            List<Chat> tempChats = AppMoment.getInstance().chatDao.loadAll();

            for (Chat c : tempChats) {

                User user = AppMoment.getInstance().userDao.load(c.getUserId());

                c.setUser(user);

                if (c.getMomentId() == momentId) {
                    chats = (ArrayList<Chat>)tempChats;
                    adapter.notifyDataSetChanged();
                }
            }
        }

        if (AppMoment.getInstance().checkInternet()) {
            if(BuildConfig.DEBUG) Log.d(TAG, "Download");
            MomentApi.get("lastchats/" + momentId, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {

                        JSONArray chatsJSON;

                        if (!response.isNull("next_page")) {
                            if (response.getInt("next_page") >= nextPage)
                                nextPage = response.getInt("next_page");
                            else
                                nextPage = 0;
                        }

                        chatsJSON = response.getJSONArray("chats");

                        ArrayList<Chat> tempChats = new ArrayList<Chat>();

                        if (chatsJSON.length() > 0) defaultTextChat.setVisibility(View.GONE);


                        for (int i = 0; i < chatsJSON.length(); i++) {

                            Chat tempChat = new Chat();

                            tempChat.chatFromJSON(chatsJSON.getJSONObject(i));
                            User user = tempChat.getUser();

                            if (AppMoment.getInstance().chatDao.load(tempChat.getId()) == null) {
                                tempChat.setMomentId(momentId);
                                AppMoment.getInstance().chatDao.insert(tempChat);
                                if (AppMoment.getInstance().userDao.load(user.getId()) == null)
                                    AppMoment.getInstance().userDao.insert(user);
                            }

                            AppMoment.getInstance().chatDao.insertOrReplace(tempChat);

                            tempChats.add(tempChat);
                        }

                        AppMoment.getInstance().user.getMomentById(momentId).getChats().addAll(tempChats);

                        if(AppMoment.getInstance().user != null)
                        {
                            AppMoment.getInstance().userDao.update(AppMoment.getInstance().user);
                        }

                        chats.addAll(tempChats);
                        adapter.notifyDataSetChanged();
                        mChatsList.smoothScrollToPosition(chats.size()-1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void newMessage(Chat chat){
        chats.add(chat);
        initViewForChats();
        adapter.notifyDataSetChanged();
        mChatsList.smoothScrollToPosition(chats.size()-1);
    }

    public void initViewForChats(){
        if(chats.size()>0) defaultTextChat.setVisibility(View.GONE);
    }



}