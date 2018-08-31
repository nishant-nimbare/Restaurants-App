package com.nishantnimbare.unicodeproject.Util;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.nishantnimbare.unicodeproject.Model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class fetchData extends AsyncTask<String ,Void ,ArrayList<Restaurant>>{

    private static final String TAG = "fetchData";
    ArrayList<Restaurant> restaurants;
    String finalJson;
    DataHandler datahandler;

    final String baseUrl="https://developers.zomato.com/api/v2.1/search";

    public void setDatahandler(DataHandler datahandler) {
        this.datahandler = datahandler;
    }

    public fetchData(ArrayList<Restaurant> restaurants) {
       this.restaurants=restaurants;
    }

    @Override
    protected ArrayList<Restaurant>  doInBackground(String... urls) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        String cityId=urls[0];
        String offset=urls[1];

        //building the url
            String urlString = Uri.parse(baseUrl)
                    .buildUpon()
                    .appendQueryParameter("entity_id", cityId)
                    .appendQueryParameter("entity_type", "city")
                    .appendQueryParameter("start", offset)        // start is the offset needed
                    .build()
                    .toString();

            try {

                URL Url = new URL(urlString);
                connection = (HttpURLConnection) Url.openConnection();
                connection.setRequestProperty("user-key", /* api key */);

                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                finalJson = buffer.toString();

                //getting data from json
                JSONObject result = new JSONObject(finalJson);
                JSONArray resArr = result.getJSONArray("restaurants");

                for (int i = 0; i < 20; i++) {
                    //api gives 20 restaurants at a time

                    JSONObject res = resArr.getJSONObject(i).getJSONObject("restaurant");
                    String name = res.getString("name");
                    String address = res.getJSONObject("location").getString("address");
                    String latitude = res.getJSONObject("location").getString("latitude");
                    String longitude = res.getJSONObject("location").getString("longitude");
                    String average_cost_for_two = res.getString("average_cost_for_two");
                    String currency = res.getString("currency");
                    String featured_image = res.getString("featured_image");
                    String url = res.getString("url");
                    String rating = res.getJSONObject("user_rating").getString("aggregate_rating");

                    restaurants.add(new Restaurant(name, address, latitude, longitude, average_cost_for_two, currency, featured_image, url, rating));
//                    Log.e(TAG, "doInBackground: restaurent added"+name);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        return restaurants;
    }

    @Override
    protected void onPostExecute(ArrayList<Restaurant>  restaurants) {
        super.onPostExecute(restaurants);

        //updating the restaurants array in the mainActivity
        datahandler.giveData(restaurants);

    }
}
