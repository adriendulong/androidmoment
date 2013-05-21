package com.moment.classes;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Chat  {
	
	private int id;
	private String message;
	private Date date;
	private User user;
	

	public Chat() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Chat(String message, User user, Date date) {
		// TODO Auto-generated constructor stub
		this.message = message;
		this.user = user;
		this.date = date;
	}
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
	
	
	/**
	 * Fonction qui � partir d'un objet JSON cr�� un objet Chat
	 * @param chatObject
	 */
	
	public void chatFromJSON(JSONObject chatObject){
		
		
		try {
			
			
			this.setId(chatObject.getInt("id"));
			this.setMessage(chatObject.getString("message"));
			
			
			//On transforme en date les timestamps
			long time = chatObject.getLong("time");
			//On multiplie time*1000 car Date Java attend des milliseconds
			time = time * 1000;
			this.setDate(new Date(time));
			
			//On recupere le user
			User user = new User();
			user.setUserFromJson(chatObject.getJSONObject("user"));
			this.setUser(user);
			
			
		} catch (JSONException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	
	

}
