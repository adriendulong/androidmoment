package com.moment.infos.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.loopj.android.http.JsonHttpResponseHandler;
import com.moment.MomentInfosActivity.Exchanger;
import com.moment.R;
import com.moment.classes.AppMoment;
import com.moment.classes.Chat;
import com.moment.classes.MomentApi;

public class ChatFragment extends Fragment {
	
	public View view;
	public LayoutInflater inflater;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("Test", "hello");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		Log.e("SAVEINSTANCE", "Chats");
		savedInstanceState.putBoolean("Sleep", true);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.e("PAUSE", "Chats");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.e("RESUME", "Chats");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		Log.e("STOP", "Chats");
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.e("START", "Chats");
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.fragment_chat, container, false);	
		this.inflater = inflater;
		
		Log.d("CHAT", "Chat cr�ation");
		
		MomentApi.get("lastchats/"+Exchanger.idMoment, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
            	try {
            		
			    	
					JSONArray chats;
			    	chats = response.getJSONArray("chats");
			    	Log.d("Chats", chats.toString());
			    	
			    	//On itnialise la liste qui recevra tous les chats
			    	ArrayList<Chat> tempChats = new ArrayList<Chat>();
			    	
			    	
			    	//On parcourt les chats
			    	for(int i=0;i<chats.length();i++){
			    		
			    		Chat tempChat = new Chat();
			    		tempChat.chatFromJSON(chats.getJSONObject(i));
			    		
			    		if(tempChat.getUser().getId() ==  AppMoment.getInstance().user.getId()) messageRight(tempChat);
			    		else messageLeft(tempChat);
			    		System.out.println("ID temp :"+ tempChat.getUser().getId());
			    		System.out.println("ID user :"+ AppMoment.getInstance().user.getId());
			    		
			    		//On l'ajoute dans la liste globale de chat
			    		tempChats.add(tempChat);
			    		
			    		
			    		
			    	}
			    	
			    	Exchanger.moment.setChats(tempChats);
			    	
					
				} catch (JSONException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
		return view;
	}
		
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		Log.d("DESTROY", "DESTROY");
			
	}
	
	
	
	
	
	/**
     * Poste le message de droite (utilisateur de l'application)
     * @param messagePost
     */
    
    public void messageRight(Chat chat){
    	
    	//On recupere le Linear Layout dans lequel on va ins�rer notre message
    	LinearLayout layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
    	ScrollView scrollChat = (ScrollView)view.findViewById(R.id.scroll_chat);
    	
    	//On recupere le template
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_droite,
                null);
        
        //On insere le texte dans le template
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());
        
        //On insere la photo du user
        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        chat.getUser().printProfilePicture(userImage, true);
        
        
        //On vient ins�rer le message de droite au fil de conversation
        layoutChat.addView(chatDroit);
        //scrollChat.scrollTo(0, (scrollChat.getMaxScrollAmount()+chatDroit.getHeight()));
        
        //On scroll vers la bas 
        new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)view.findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);
        
        
        
        
    	
    }
    
    
    /**
     * Poste le message de gauche 
     * @param messagePost
     */
    
    public void messageLeft(Chat chat){
    	
    	//On recupere le Linear Layout dans lequel on va ins�rer notre message
    	LinearLayout layoutChat = (LinearLayout)view.findViewById(R.id.chat_message_layout);
    	ScrollView scrollChat = (ScrollView)view.findViewById(R.id.scroll_chat);
    	
    	//On recupere le template
        LinearLayout chatDroit = (LinearLayout) inflater.inflate(R.layout.chat_message_gauche,
                null);
        
        //On insere le texte dans le template
        TextView message = (TextView)chatDroit.findViewById(R.id.chat_message_text);
        message.setText(chat.getMessage());
        
        //On insere la photo du user
        ImageView userImage = (ImageView)chatDroit.findViewById(R.id.photo_user);
        chat.getUser().printProfilePicture(userImage, true);
        
        //On vient ins�rer le message de droite au fil de conversation
        layoutChat.addView(chatDroit);
        //scrollChat.scrollTo(0, (scrollChat.getMaxScrollAmount()+chatDroit.getHeight()));
        
        //On scroll vers la bas 
        new Handler().postDelayed((new Runnable(){

        	@Override
			public void run(){
        		ScrollView scrollChat = (ScrollView)view.findViewById(R.id.scroll_chat);
        		scrollChat.fullScroll(View.FOCUS_DOWN);
        	}

        }), 200);
        
    	
    }
    

	

	
}