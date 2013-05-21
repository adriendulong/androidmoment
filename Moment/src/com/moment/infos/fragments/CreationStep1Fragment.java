package com.moment.infos.fragments;

import java.util.Calendar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moment.CreationDetailsActivity;
import com.moment.R;
import com.moment.classes.AppMoment;
import com.moment.classes.Moment;

public class CreationStep1Fragment extends Fragment {
	private Moment moment;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("Test", "hello");
		this.moment = ((CreationDetailsActivity)getActivity()).getMoment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_creation_moment_1, container, false);
		
		TextView name = (TextView)view.findViewById(R.id.creation_moment_name);
		name.setText(this.moment.getName());
		
		if(this.moment.getKeyBitmap()!=null){
			ImageView photo_moment = (ImageView)view.findViewById(R.id.creation_moment_image);
			Bitmap photo_moment_bitmap = AppMoment.getInstance().getBitmapFromMemCache(this.moment.getKeyBitmap());
			photo_moment.setImageBitmap(photo_moment_bitmap);
		}
		
		//On itnitialise la Date debut
			if(moment.getDateDebut()!=null){
				Log.d("Date Debut", ""+moment.getDateDebut().toString());
				
		        Calendar calDebut = Calendar.getInstance();
		        calDebut.setTime(moment.getDateDebut());
		        int anneeDebut = calDebut.get(Calendar.YEAR);
		        int moisDebut = calDebut.get(Calendar.MONTH);
		        moisDebut +=1;
		        int jourDebut = calDebut.get(Calendar.DAY_OF_MONTH);
		    	
		    	Button dateDebut = (Button)view.findViewById(R.id.date_debut_button);
		    	dateDebut.setText(""+jourDebut+"/"+moisDebut+"/"+anneeDebut);
			}
			
			//On itnitialise la Date de fin
			if(moment.getDateFin()!=null){
				Log.d("Date Debut", ""+moment.getDateFin().toString());
				Calendar calFin = Calendar.getInstance();
		        calFin.setTime(moment.getDateFin());
		        int anneeFin = calFin.get(Calendar.YEAR);
		        int moisFin = calFin.get(Calendar.MONTH);
		        moisFin += 1;
		        int jourFin = calFin.get(Calendar.DAY_OF_MONTH);
		    	
		        Button dateFin = (Button)view.findViewById(R.id.date_fin_button);
		    	dateFin.setText(""+jourFin+"/"+moisFin+"/"+anneeFin);
			}
		
		return view;
	}
	

	/**
	 * fonction utilisée afin de modifié la font de tous les champs
	 */
	
	public void modifyFont(){
		
	}

}