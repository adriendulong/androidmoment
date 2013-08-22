package com.moment.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.AppMoment;
import com.moment.BuildConfig;
import com.moment.R;
import com.moment.activities.MomentInfosActivity;
import com.moment.classes.ChatAdapter;
import com.moment.classes.MomentApi;
import com.moment.classes.RoundTransformation;
import com.moment.models.Chat;
import com.moment.models.User;
import com.moment.util.ImageCache;
import com.moment.util.ImageFetcher;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private View view;
    private LayoutInflater inflater;
    private Long momentId;
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
        mChatsList.setSelector(android.R.color.transparent);
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

        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.btn_profilpic_up);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

        if(chats == null){

            chats = new ArrayList<Chat>();
            adapter = new ChatAdapter(getActivity(), R.layout.chat_message_droite, chats, mImageFetcher);
            mChatsList.setAdapter(adapter);

            if (((MomentInfosActivity) getActivity()).getMomentId() != null) {
                this.momentId = ((MomentInfosActivity) getActivity()).getMomentId();
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

    public void createFragment(Long momentId) {
        this.momentId = momentId;

        initChat();
    }

    private void initChat() {

        if (!AppMoment.getInstance().checkInternet()) {
            AppMoment.getInstance().user.getMomentById(momentId).resetChats();
            chats.addAll(AppMoment.getInstance().user.getMomentById(momentId).getChats());
            defaultTextChat.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            mChatsList.smoothScrollToPosition(chats.size()-1);
        } else {
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

                            tempChat.chatFromJSON(chatsJSON.getJSONObject(i), AppMoment.getInstance().user.getMomentById(momentId));
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