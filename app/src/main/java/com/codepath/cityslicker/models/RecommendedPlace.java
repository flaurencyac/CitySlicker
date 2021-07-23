package com.codepath.cityslicker.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;

@Parcel
public class RecommendedPlace {
    private String name;
    private String vicinity;
    private Double weight;
    private Double rating;
    private Integer numRatings;
    private String placeId;
    private Integer photoWidth;
    private String photoRef;
    private Boolean openNow;

    public RecommendedPlace() {}

    public RecommendedPlace(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.vicinity = jsonObject.getString("vicinity");
            this.rating = jsonObject.getDouble("rating");
            this.numRatings = jsonObject.getInt("user_ratings_total");
            this.placeId = jsonObject.getString("place_id");
            this.photoRef = jsonObject.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
            this.photoWidth = jsonObject.getJSONArray("photos").getJSONObject(0).getInt("width");
            this.openNow = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setWeight(Integer weight) {
        this.weight = (weight * 10) + rating;
    }

    public static ArrayList<RecommendedPlace> sortRecommendedPlaces(ArrayList<RecommendedPlace> unsortedList) {

        return null;
    }

    public void getPhoto() {
        // TODO make place photo request
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getUserRatingsTotal() {
        return numRatings;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Integer getPhotoWidth() {
        return photoWidth;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public String getOpen() {
        if (openNow) {
            return "yes";
        } else {
            return "no";
        }
    }
}
