package com.moment.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.moment.R;

public class EditProfilActivity extends SherlockActivity implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modif_profile);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button modif = (Button) findViewById(R.id.modif);
        final EditText modif_prenom = (EditText) findViewById(R.id.modif_prenom);
        final EditText modif_nom = (EditText) findViewById(R.id.modif_nom);

        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modif_prenom.getText().toString();
                modif_nom.getText().toString();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
