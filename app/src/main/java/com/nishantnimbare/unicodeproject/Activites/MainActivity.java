package com.nishantnimbare.unicodeproject.Activites;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.nishantnimbare.unicodeproject.Model.Restaurant;
import com.nishantnimbare.unicodeproject.R;
import com.nishantnimbare.unicodeproject.Util.DataHandler;
import com.nishantnimbare.unicodeproject.Util.ItemClickListener;
import com.nishantnimbare.unicodeproject.Util.fetchData;
import com.nishantnimbare.unicodeproject.Util.myAdapter;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  implements DataHandler,ItemClickListener{

    private static final String TAG = "MainActivity";

    public ArrayList<Restaurant> restaurants=new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ProgressBar progressBar;
    fetchData fetch;
    Spinner spinner;
    ArrayAdapter arrAdapter;
    Context context;

    String currentCityId="3";  //mumbai ,default


    final int MAX_RESTAURANTS=100;   //zomato api only allows max 100 restaurants

    boolean isLoading=false;         //specifies whether request is being processed

    int currentItems,totalItems,scrollOutItems=0;
       /*totalItems are the total items in the adapter's arraylist
         currentItems  are the item visible on the screen
         scrolloutItems are the items tha are scrolled out of the screen*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);

        final GridLayoutManager gridManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridManager);

        adapter=new myAdapter(restaurants,this,this);  //there are no restaurants in the array at start
        recyclerView.setAdapter(adapter);

        //on scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridManager.getChildCount();
                totalItems = gridManager.getItemCount();
                scrollOutItems = gridManager.findFirstVisibleItemPosition();

       //         Log.e(TAG, "onScrolled: ci "+currentItems+",ti "+totalItems+",soi "+scrollOutItems);

                if(totalItems==MAX_RESTAURANTS){

                    //user has reached the end
                    Toast.makeText(MainActivity.this, "you have reached the end", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onScrolled: 100 items done");

                }
                else if(dy>0 && (totalItems==currentItems+scrollOutItems)){

                 //dy>0 means that the user is going down

                 // (currentItems+scrollOutItems)  gives the total no of items seen by the user
                 //(totalItems==currentItems+scrollOutItems) means that the user has already seen all the items in the adapter's arraylist

                    if(isLoading){
                        //request has already being send , do nothing
                    }else{
                        isLoading=true;
                        progressBar.setVisibility(View.VISIBLE);

                        Toast.makeText(MainActivity.this, "getting new data", Toast.LENGTH_SHORT).show();

                        //fetch more data
                        fetchData fetch =new fetchData(restaurants);
                        fetch.setDatahandler(MainActivity.this);
                        fetch.execute(currentCityId,Integer.toString(totalItems));
                        Log.e(TAG, "onScrolled: getting new items");
                    }

                }
            }
        });



        //progressBar
        progressBar=(ProgressBar)findViewById(R.id.progress);

        //Spinner
        spinner=(Spinner)findViewById(R.id.spinner);

        arrAdapter=ArrayAdapter.createFromResource(this,R.array.spinner_arr,android.R.layout.simple_spinner_item);
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0: currentCityId="3";                   //mumbai
                                    break;
                    case 1: currentCityId="13";                  //goa
                                    break;
                    case 2: currentCityId="1";                  // delhi
                                    break;
                    case 3: currentCityId="7";                 //chennai
                                    break;
                    case 4: currentCityId="4";                 //bengaluru
                                    break;
                }
                requestCityChange(currentCityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                //the default city is mumbai
            }
        });


    }


    @Override
    public void giveData( ArrayList<Restaurant> restaurants) {
        this.restaurants=restaurants;

        Log.e(TAG,"giveData: no of objects"+Integer.toString(restaurants.size()));

        isLoading=false;

        //hiding the progress bar
        progressBar.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(int position) {

        //launching Details activity here
        Intent i= new Intent(MainActivity.this,Details.class);

        //passing the restaurant object through intent
        i.putExtra("restaurant",restaurants.get(position));

        startActivityForResult(i,0);

        Log.e(TAG, "onItemClicked: "+Integer.toString(position+1)+"th item clicked");

    }


    public void requestCityChange(String cityId){

        //removing old data
        restaurants.removeAll(restaurants);
        adapter.notifyDataSetChanged();

        //showing progressbar
        progressBar.setVisibility(View.VISIBLE);

        //getting data from Asynctask
        fetch= new fetchData(restaurants);
        fetch.setDatahandler(this);
        fetch.execute(cityId,"0");

        Log.e(TAG, "data fetch done");

    }
}
