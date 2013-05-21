package com.moment.classes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Moment implements Parcelable {
	
	private int id;
	private String name;
	private String description;
	private String infoLieu;
	private String infoTransport;
	private Date dateDebut;
	private Date dateFin;
	private String hashtag;
	//private Adresse adresse;
	private String adresse;
	private String keyBitmap;
	private String url_cover;
	private User owner;
	private int state = 4;
	private int guests_number =0;
	private int guests_coming = 0;
	private int guests_not_coming = -1;
	private ArrayList<Chat> chats;
	private ArrayList<Photo> photos;
	
	public Moment(){
			this.chats = new ArrayList<Chat>();
			this.photos = new ArrayList<Photo>();
	}
	
	/**
	 * Getter and Setter
	 * 
	 */
	
	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getInfoLieu(){
		return this.infoLieu;
	}
	
	public void setInfoLieu(String infoLieu){
		this.infoLieu = infoLieu;
	}
	
	public String getInfoTransport(){
		return this.infoTransport;
	}
	
	public void setInfoTransport(String infoTransport){
		this.infoTransport = infoTransport;
	}
	
	public Date getDateDebut(){
		return this.dateDebut;
	}
	
	public void setDateDebut(Date dateDebut){
		this.dateDebut = dateDebut;
	}
	
	public Date getDateFin(){
		return this.dateFin;
	}
	
	public void setDateFin(Date dateFin){
		this.dateFin = dateFin;
	}
	
	public String getHashtag(){
		return this.hashtag;
	}
	
	public void setHashtag(String hashtag){
		this.hashtag = hashtag;
	}
	
	
	public String getUrlCover(){
		return this.url_cover;
	}
	
	public void setUrlCover(String url_cover){
		this.url_cover = url_cover;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getKeyBitmap() {
		return keyBitmap;
	}


	public void setKeyBitmap(String keyBitmap) {
		this.keyBitmap = keyBitmap;
	}
	


	public User getOwner() {
		return owner;
	}


	public void setOwner(User owner) {
		this.owner = owner;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}
	
	public void setGuestsNumber(int guests_number){
		this.guests_number = guests_number;
	}
	
	public int getGuestsNumber(){
		return this.guests_number;
	}
	
	public void setGuestsComing(int guests_coming){
		this.guests_coming = guests_coming;
	}
	
	public int getGuestsComing(){
		return this.guests_coming;
	}
	
	public void setGuestsNotComing(int guests_not_coming){
		this.guests_not_coming = guests_not_coming;
	}
	
	public int getGuestsNotComing(){
		return this.guests_not_coming;
	}
	
	public ArrayList<Chat> getChats() {
		return chats;
	}


	public void setChats(ArrayList<Chat> chats) {
		this.chats = chats;
	}
	
	public ArrayList<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<Photo> photos) {
		this.photos = photos;	
	}
	
	
	/**
	 * On ajoute un chat ��� la liste
	 */
	
	public void addChat(Chat chat){
		this.chats.add(chat);
	}
	
	
	/**
	 * Cr���ation ou r���cup���ration de moment ��� travers du JSON
	 */
	
	
	public void setMomentFromJson(JSONObject moment) throws JSONException{
		this.id = moment.getInt("id");
		this.name = moment.getString("name");
		this.adresse = moment.getString("address");
		this.state = moment.getInt("user_state");
		
		
		//Date de d���but
		String[] dateDedutTemp = moment.getString("startDate").split("-"); 
		GregorianCalendar dateDebutGreg = new GregorianCalendar(Integer.parseInt(dateDedutTemp[0]), Integer.parseInt(dateDedutTemp[1]), Integer.parseInt(dateDedutTemp[2]));
		this.dateDebut = dateDebutGreg.getTime();
		
		//Date de fin
		String[] dateFinTemps = moment.getString("endDate").split("-"); 
		GregorianCalendar dateFinGreg = new GregorianCalendar(Integer.parseInt(dateFinTemps[0]), Integer.parseInt(dateFinTemps[1]), Integer.parseInt(dateFinTemps[2]));
		this.dateFin = dateFinGreg.getTime();
		
		
		if (moment.has("cover_photo_url")){
			this.url_cover = moment.getString("cover_photo_url");
		}
		
		//Invites
		this.guests_number = moment.getInt("guests_number");
		this.guests_coming = moment.getInt("guests_coming");
		this.guests_not_coming = moment.getInt("guests_not_coming");
		
		if(moment.has("hashtag")) this.hashtag = moment.getString("hashtag");
		if(moment.has("description")) this.description = moment.getString("description");
		
		if (moment.has("owner")){
			JSONObject owner = moment.getJSONObject("owner");
			this.owner = new User();
			if(owner.has("email")) this.owner.setEmail(owner.getString("email"));
			if(owner.has("firstname")) this.owner.setFirstname(owner.getString("firstname"));
			if(owner.has("lastname")) this.owner.setLastname(owner.getString("lastname"));
			if(owner.has("profile_picture_url")) this.owner.setPicture_profile_url(owner.getString("profile_picture_url"));
			if(owner.has("id")) this.owner.setId(owner.getInt("id"));
		}
		
		
	}
	
	/**
	 * Renvoit l'object sous la forme d'un JSONObject
	 * @return
	 * @throws JSONException
	 * @throws FileNotFoundException 
	 */
	
	public RequestParams getMomentRequestParams(Context context) throws JSONException {
		
		RequestParams momentPrams = new RequestParams();
		
		momentPrams.put("name", this.name);
		momentPrams.put("address", this.adresse);
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(this.dateDebut);
		momentPrams.put("startDate", ""+startDate.get(Calendar.YEAR)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+startDate.get(Calendar.DAY_OF_MONTH));
		System.out.println("startDate "+startDate.get(Calendar.YEAR)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+startDate.get(Calendar.DAY_OF_MONTH));
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(this.dateFin);
		momentPrams.put("endDate", ""+endDate.get(Calendar.YEAR)+"-"+(endDate.get(Calendar.MONTH)+1)+"-"+endDate.get(Calendar.DAY_OF_MONTH));
		
		if (this.description != null) momentPrams.put("description", this.description);
		
		if(this.keyBitmap!=null){
			File image = context.getFileStreamPath("cover_picture");
			try {
				momentPrams.put("photo", image);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		
		
		return momentPrams;
	}
	
	
	
	
	/**
	 * Fonction qui va chercher sur le serveur afin les infos sur ce Moment et compare avec ses propres infos
	 * 
	 */
	
	public void updateInfos(Moment tempMoment){
		

		if(!name.equals(tempMoment.getName())) name = tempMoment.getName();
		if(!adresse.equals(tempMoment.getAdresse())) adresse = tempMoment.getAdresse();
		if(dateDebut!=tempMoment.getDateDebut()) dateDebut = tempMoment.getDateDebut();
		if(dateFin!=tempMoment.getDateFin()) dateFin = tempMoment.getDateFin();
		if((tempMoment.getDescription()!=null)&&(description!=tempMoment.getDescription())) description = tempMoment.getDescription();
		if(guests_number!=tempMoment.getGuestsNumber()) guests_number=tempMoment.getGuestsNumber();
		if(guests_coming!=tempMoment.getGuestsComing()) guests_coming = tempMoment.getGuestsComing();
		if(guests_not_coming!=tempMoment.getGuestsNotComing()) guests_not_coming = tempMoment.getGuestsNotComing();
		if((tempMoment.getHashtag()!=null)&&(!hashtag.equals(tempMoment.getHashtag()))) hashtag = tempMoment.getHashtag();
		if(state!=tempMoment.getState()) state = tempMoment.getState();
		
		
	}
	
	
	/**
	 * Met une date sous le format YYYY-MM-DD
	 * @param date
	 */
	public String getDateInFormat(Date date){
		
		Calendar dateTemp = Calendar.getInstance();
		dateTemp.setTime(date);
		
		return ""+dateTemp.get(Calendar.YEAR)+"-"+(dateTemp.get(Calendar.MONTH)+1)+"-"+dateTemp.get(Calendar.DAY_OF_MONTH);
		
		
	}
	
	/**
	 * Fonction qui prend en param une date sous le format YYYY-MM-DD et renvoit une Date
	 * @param dateString
	 * @return
	 */
	
	public Date castDate(String dateString){
		
		String[] dateStringTemp = dateString.split("-"); 
		GregorianCalendar dateTempGreg = new GregorianCalendar(Integer.parseInt(dateStringTemp[0]), Integer.parseInt(dateStringTemp[1]), Integer.parseInt(dateStringTemp[2]));
		return dateTempGreg.getTime();
		
	}
	
	
	
	public void printCover(final ImageView targetView, final Boolean isRounded){
		// On recupere la photo et on l'affiche lorsque on l'a
		System.out.println("Print cover at address : "+ url_cover);
		
		AsyncHttpClient client = new AsyncHttpClient();
    	String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
    	client.get(url_cover, new BinaryHttpResponseHandler(allowedContentTypes) {
    		
    	    @Override
    	    public void onSuccess(byte[] fileData) {
    	        
    	    	
    	    	InputStream is = new ByteArrayInputStream(fileData);
    	    	Bitmap bmp = BitmapFactory.decodeStream(is);
    	    	AppMoment.getInstance().addBitmapToMemoryCache("cover_moment_"+name.toLowerCase(), bmp);
    	    	keyBitmap = "cover_moment_"+name.toLowerCase();
    	    	
    	    	if (isRounded) targetView.setImageBitmap(Images.getRoundedCornerBitmap(bmp));
    	    	else targetView.setImageBitmap(bmp);
    	    }
    	    
    	    @Override
    	    public void handleFailureMessage(Throwable e, byte[] responseBody) {
    	    	Log.d("RATEE", "RATEE");
    	        onFailure(e, responseBody);
    	    }
    	});
	}
	
	
	
	/*
	 * Parcelable Functions
	 */
	
	public Moment(Parcel in){
		//in.readParcelable(Adresse.class.getClassLoader());
		this.adresse = in.readString();
		this.name = in.readString();
		this.description = in.readString();
		this.infoLieu = in.readString();
		this.infoTransport = in.readString();
		this.dateDebut = new Date(in.readLong());
		this.dateFin = new Date(in.readLong());
		this.hashtag = in.readString();
		this.keyBitmap = in.readString();
	}
	
	 public static final Parcelable.Creator<Moment> CREATOR = new Parcelable.Creator<Moment>() {
	        @Override
			public Moment createFromParcel(Parcel in) {
	            return new Moment(in);
	        }
	 
	        @Override
			public Moment[] newArray(int size) {
	            return new Moment[size];
	        }
	    };

	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		//out.writeParcelable(adresse, flags);
		out.writeString(adresse);
		out.writeString(this.name);
		out.writeString(description);
		out.writeString(infoLieu);
		out.writeString(infoTransport);
		out.writeLong(dateDebut.getTime());
		out.writeLong(dateFin.getTime());
		out.writeString(hashtag);
		out.writeString(keyBitmap);
		
	}



	
}
