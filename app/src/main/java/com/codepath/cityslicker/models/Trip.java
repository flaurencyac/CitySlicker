package com.codepath.cityslicker.models;

import com.google.android.libraries.places.api.model.Place;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@ParseClassName("Trip")
public class Trip extends ParseObject {
    public static final String KEY_PLACES = "places";
    public static final String KEY_NAME = "tripName";
    public static final String KEY_COLLABORATORS = "collaborators";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_BUDGET = "budget";
    public static final String KEY_REGIONS = "regions";
    public static final String KEY_CITY_NAMES = "cityNames";
    private static final String SHOPPING_PREFERENCE = "shoppingPref";
    private static final String FOOD_PREFERENCE = "foodPref";
    private static final String ADULT_PREFERENCE = "adultPref";
    private static final String FAMILY_PREFERENCE = "familyPref";
    private static final String ATTRACTIONS_PREFERENCE = "attractionsPref";

    public void setPlaces(JSONArray allPlaces) { put(KEY_PLACES, allPlaces); }

    public ArrayList<String> getPlaces() {return (ArrayList<String>) get(KEY_PLACES); }

    public ArrayList<String> getRegions() {return (ArrayList<String>) get(KEY_REGIONS);}

    public void setCityNames(JSONArray cityNames) {put(KEY_CITY_NAMES, cityNames); }

    public ArrayList<String> getCityNames() {return (ArrayList<String>) get(KEY_CITY_NAMES); }

    public void setCollaborators(JSONArray collaborators) {put(KEY_COLLABORATORS, collaborators);}

    public void setOwner(ParseUser user) {put(KEY_OWNER, user);}

    public void setStartDate(Date date) {put(KEY_START_DATE, date);}

    public Date getStartDate() {return getDate(KEY_START_DATE);}

    public Date getEndDate() {return getDate(KEY_END_DATE);}

    public Integer getBudget() {return getInt(KEY_BUDGET);}

    public Integer getAdultPreference() {return getInt(ADULT_PREFERENCE);}

    public Integer getAttractionsPreference() {return getInt(ATTRACTIONS_PREFERENCE);}

    public Integer getFamilyPreference() {return getInt(FAMILY_PREFERENCE);}

    public Integer getFoodPreference() {return getInt(FOOD_PREFERENCE);}

    public Integer getShoppingPreference() {return getInt(SHOPPING_PREFERENCE);}

    public String getTripName() {return getString(KEY_NAME);}

    public void setEndDate(Date date) {put(KEY_END_DATE, date);}

    public void setBudget(Integer budget) {put(KEY_BUDGET, budget);}

    public void setRegions(JSONArray cities) {put(KEY_REGIONS, cities);}

    public void setTripName(String name) {put(KEY_NAME, name);}

    public void setShoppingPreference(Integer n) {put(SHOPPING_PREFERENCE, n);}

    public void setAdultPreference(Integer n) {put(ADULT_PREFERENCE, n);}

    public void setFamilyPreference(Integer n) {put(FAMILY_PREFERENCE, n);}

    public void setFoodPreference(Integer n) {put(FOOD_PREFERENCE, n);}

    public void setAttractionsPreference(Integer n) {put(ATTRACTIONS_PREFERENCE, n);}

    // Note: oneDimenLst has this format: ["[placeId1, placeId2, placeId3]", "[placeId1, placeId2]", "[placeId1, placeId2, placeId3]"]
    public static ArrayList<ArrayList<String>> parseForSpots(ArrayList<String> oneDimenLst) {
        ArrayList<ArrayList<String>> placeIds = new ArrayList<ArrayList<String>>();
        for(int i =0; i< oneDimenLst.size(); i++) {
            String placesInCity = oneDimenLst.get(i).substring(1, oneDimenLst.get(i).length()-1);
            ArrayList<String> temp = new ArrayList<String>();
            temp.addAll(Arrays.asList(placesInCity.replaceAll(" ", "").split(",")));
            placeIds.add(temp);
        }
        return placeIds;
    }
}
