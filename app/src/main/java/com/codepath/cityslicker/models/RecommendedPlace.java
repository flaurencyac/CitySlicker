package com.codepath.cityslicker.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import java.util.ArrayList;
import java.util.Collections;

@Parcel
public class RecommendedPlace {
    private static final String TAG = "RecommendedPlace";

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

    private static ArrayList<RecommendedPlace> reverseList(ArrayList<RecommendedPlace> placesList) {
        ArrayList<RecommendedPlace> reversedList = new ArrayList<RecommendedPlace>();
        for (int i = 0; i<placesList.size();i++) {
            reversedList.add(placesList.get(placesList.size()-1-i));
        }
        return reversedList;
    }

    public static ArrayList<RecommendedPlace> quicksort(ArrayList<RecommendedPlace> lst, int start, int end) {
        if (start < end) {
            int pivot = start;
            int left = start +1;
            int right = end;
            double pivotValue = lst.get(pivot).getWeight();
            while (left <= right) {
                while (left <= end && pivotValue >= lst.get(left).getWeight()) {
                    left += 1;
                }
                while (right > start && pivotValue < lst.get(right).getWeight()) {
                    right -= 1;
                }
                if (left < right) {
                    Collections.swap(lst, left, right);
                }
            }
            Collections.swap(lst, pivot, left-1);
            quicksort(lst, start, right-1);
            quicksort(lst, right+1, end);
        }
        return lst;
    }

    public static ArrayList<RecommendedPlace> sortRecommendedPlaces(ArrayList<RecommendedPlace> unsortedList) {
        return reverseList(quicksort(unsortedList, 0, unsortedList.size()-1));
    }

    public void setWeight(Integer weight) {
        this.weight = (weight * 10) + rating;
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
