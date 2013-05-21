package com.moment.infos.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.moment.CreationDetailsActivity;
import com.moment.R;
import com.moment.classes.Moment;

public class CreationStep2Fragment extends Fragment {
	
	private Moment moment;
	private CreationDetailsActivity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("Test", "hello2");
		activity = ((CreationDetailsActivity)getActivity());
		this.moment = activity.getMoment();
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_creation_moment_2, container, false);

		if(this.moment.getDescription()!=null){
			EditText description = (EditText)view.findViewById(R.id.creation_moment_description);
			description.setText(this.moment.getDescription());
		}
		
		if(this.moment.getAdresse()!=null){
			EditText adresse = (EditText)view.findViewById(R.id.creation_moment_adresse);
			adresse.setText(this.moment.getAdresse());
		}
		
		if(this.moment.getInfoLieu()!=null){
			EditText infosLieu = (EditText)view.findViewById(R.id.creation_moment_infos_lieu);
			infosLieu.setText(this.moment.getInfoLieu());
		}
		
		if(this.moment.getHashtag()!=null){
			EditText hashtag = (EditText)view.findViewById(R.id.creation_moment_hashtag);
			hashtag.setText(this.moment.getHashtag());
		}
		
		
		
		//On prepare le listening de tous les champs oibligatoires
		EditText descriptionEdit = (EditText)view.findViewById(R.id.creation_moment_description);
    	EditText adresseEdit = (EditText)view.findViewById(R.id.creation_moment_adresse);
    	EditText infosLieuEdit = (EditText)view.findViewById(R.id.creation_moment_infos_lieu);

    	descriptionEdit.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {}
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
            	if(count >0) CreationDetailsActivity.validateDescription = 1;
            	else CreationDetailsActivity.validateDescription = 0;
            	
            	System.out.println("CA BOUGE");
            	
            	CreationDetailsActivity.validateSecondFields();
            }
        }); 
    	
    	
    	adresseEdit.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {}
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
            	if(count >0) CreationDetailsActivity.validateAdress = 1;
            	else CreationDetailsActivity.validateAdress = 0;
            	
            	CreationDetailsActivity.validateSecondFields();
            }
        }); 
    	
    	
    	infosLieuEdit.addTextChangedListener(new TextWatcher(){
            @Override
			public void afterTextChanged(Editable s) {}
            @Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
            	if(count >0) CreationDetailsActivity.validateInfosLieu = 1;
            	else CreationDetailsActivity.validateInfosLieu = 0;
            	
            	CreationDetailsActivity.validateSecondFields();
            }
        }); 
		
		
		return view;
	}
	
	
	
	
	/**
	 * Fonction qui permet de recupérer la date de début stocker dans le text view
	 * @return dateDebut
	 */
	
	public String getDateDebut(){
		
		TextView dateDebut = (TextView)getView().findViewById(R.id.spinner_date_debut);
		
		return dateDebut.toString();
		
	}
	


}