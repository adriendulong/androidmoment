package com.moment.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.moment.R;

public class SettingsActivity extends SherlockActivity implements View.OnClickListener{

    static int cpt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cpt = 0;

        ImageButton facebook = (ImageButton) findViewById(R.id.btn_fb);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                try {
                    getApplication().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW,Uri.parse("fb://page/277911125648059"));
                } catch (PackageManager.NameNotFoundException e) {
                    intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/appmoment"));
                }

                startActivity(intent);
            }
        });

        ImageButton twitter = (ImageButton) findViewById(R.id.btn_twit);

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/appmoment"));
                startActivity(intent);
            }
        });

        ImageButton coeur = (ImageButton) findViewById(R.id.coeur);

        coeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpt ++;
                if(cpt == 6){
                    cpt = 0;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appmoment.fr"));
                    startActivity(intent);
                }
            }
        });

        Button contact = (Button) findViewById(R.id.btn_contact);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "hello@appmoment.fr");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hello");
                intent.putExtra(Intent.EXTRA_TEXT, "Dear Moment,");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        Button feedback = (Button) findViewById(R.id.feedback);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mailto:hello@appmoment.fr"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hello");
                intent.putExtra(Intent.EXTRA_TEXT, "I would say that Moment is ...");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        Button cgu = (Button) findViewById(R.id.cgu);

        cgu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.appmoment.fr/cgu"));
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {}
}
