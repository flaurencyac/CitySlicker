package com.codepath.cityslicker.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

@ParseClassName("Trip")
public class Trip extends ParseObject {
    public static final String KEY_PLACES = "places";
    public static final String KEY_NAME = "tripName";
    public static final String KEY_COLLABORATORS = "collaborators";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_PREFERENCES = "preferences";
    public static final String KEY_BUDGET = "budget";
    public static final String KEY_REGIONS = "regions";

    public void setPlaces(JSONArray allPlaces) { put(KEY_PLACES, allPlaces); }

    public JSONArray getPlaces() {return getJSONArray(KEY_PLACES); }

    public void setCollaborators(JSONArray collaborators) {put(KEY_COLLABORATORS, collaborators);}

    public void setOwner(ParseUser user) {put(KEY_OWNER, user);}

    public void setStartDate(Date date) {put(KEY_START_DATE, date);}

    public void setEndDate(Date date) {put(KEY_END_DATE, date);}

    public void setPreferences(JSONArray preferences) {put(KEY_PREFERENCES, preferences);}

    public void setBudget(Integer budget) {put(KEY_BUDGET, budget);}

    public void setRegions(JSONArray cities) {put(KEY_REGIONS, cities);}

    public void setTripName(String name) {put(KEY_NAME, name);}

}
