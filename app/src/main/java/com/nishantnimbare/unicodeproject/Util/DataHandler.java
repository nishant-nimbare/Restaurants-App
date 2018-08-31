package com.nishantnimbare.unicodeproject.Util;

import com.nishantnimbare.unicodeproject.Model.Restaurant;

import java.util.ArrayList;

//this interface gives the updated restaurants from asyncTasks to activity
public interface DataHandler {
    void giveData( ArrayList<Restaurant> restaurants);
}
