package com.moment.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Adresse implements Parcelable {
	
	private String numeroRue;
	private int codePostal;
	private String ville;
	
	public Adresse(String numeroRue, int codePostal, String ville){
		this.numeroRue = numeroRue;
		this.codePostal = codePostal;
		this.ville = ville;
	}
	
	public Adresse(Parcel in){
		this.numeroRue = in.readString();
		this.ville = in.readString();
		this.codePostal = in.readInt();
	}
	
	public void setNumeroRue(String numeroRue){
		this.numeroRue = numeroRue;
	}
	
	public String getNumeroRue(){
		return this.numeroRue;
	}
	
	public int getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(int codePostal) {
		this.codePostal = codePostal;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}
	
	public static final Parcelable.Creator<Adresse> CREATOR = new Parcelable.Creator<Adresse>() {
        @Override
		public Adresse createFromParcel(Parcel in) {
            return new Adresse(in);
        }
 
        @Override
		public Adresse[] newArray(int size) {
            return new Adresse[size];
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
		out.writeString(numeroRue);
		out.writeString(ville);
		out.writeInt(codePostal);
	}

}
