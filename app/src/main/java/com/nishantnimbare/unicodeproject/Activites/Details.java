package com.nishantnimbare.unicodeproject.Activites;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nishantnimbare.unicodeproject.Model.Restaurant;
import com.nishantnimbare.unicodeproject.R;




public class Details extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Details";

    TextView bigTitle,rating,address,costForTwo;
    ImageView bigPoster;
    AppCompatButton visitZomato,visitMaps;

    Restaurant currentRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //initializing
        bigTitle=(TextView)findViewById(R.id.big_title);
        rating=(TextView)findViewById(R.id.rating);
        address=(TextView)findViewById(R.id.address);
        costForTwo=(TextView)findViewById(R.id.cost_for_two);
        bigPoster=(ImageView) findViewById(R.id.big_poster);
        visitZomato=(AppCompatButton)findViewById(R.id.zomato_url);
        visitMaps=(AppCompatButton) findViewById(R.id.visit_maps);

        //actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getting Restaurant from intent
        currentRes=(Restaurant)getIntent().getSerializableExtra("restaurant");

        //binding data
        Glide.with(this).load(currentRes.getImage()).centerCrop().into(bigPoster);

        bigTitle.setText(currentRes.getName());
        rating.setText(currentRes.getRating()+"/5");
        address.setText("address : "+currentRes.getAddress());
        costForTwo.setText("Cost For Two : "+currentRes.getAvg_cost_for_two()+" "+currentRes.getCurrency());

        //setting On click listener on buttons
        visitZomato.setOnClickListener(this);
        visitMaps.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.zomato_url){

            //launching web browser
            Uri uri= Uri.parse(currentRes.getUrl());

            Intent intent =new Intent(Intent.ACTION_VIEW,uri);
            startActivityForResult(intent,1);
        }
        else if(view.getId()==R.id.visit_maps){

            //launching locations in maps
            Uri locationUri = Uri.parse("geo:"+currentRes.getLatitude()+","+currentRes.getLongitude()+"?q="+currentRes.getName());

            Intent intent =new Intent(Intent.ACTION_VIEW,locationUri);
            startActivityForResult(intent,2);
        }
        else{
            Log.e(TAG, "onClick: id did not match");
        }
    }
}

