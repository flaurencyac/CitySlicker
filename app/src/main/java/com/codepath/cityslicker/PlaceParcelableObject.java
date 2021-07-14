package com.codepath.cityslicker;

import android.net.Uri;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class PlaceParcelableObject implements Parcelable {
    private String name;
    private String address;
    private String id;
    private String phoneNumber;
    private List<String> attributions;
    private Double rating;
    private Integer numOfRatings;
    private LatLng latLng;
    private Uri websiteUri;
    private List<PhotoMetadata> photoMetadataList;
    private OpeningHours openingHours;
    private AddressComponents addressComponents;
    private List<Place.Type> typesList;

    public PlaceParcelableObject() {}

    public PlaceParcelableObject(Place place) {
        name = place.getName();
        address = place.getAddress();
        id = place.getId();
        phoneNumber = place.getPhoneNumber();
        attributions = place.getAttributions();
        rating = place.getRating();
        numOfRatings = place.getUserRatingsTotal();
        latLng = place.getLatLng();
        websiteUri = place.getWebsiteUri();
        photoMetadataList = place.getPhotoMetadatas();
        openingHours = place.getOpeningHours();
        addressComponents = place.getAddressComponents();
        typesList = place.getTypes();
    }

    public PlaceParcelableObject(PointOfInterest poi) {
        name = poi.name;
        id = poi.placeId;
        latLng = poi.latLng;
        // TODO: use placeID to make a Place Details request to fill out the other fields
    }

    protected PlaceParcelableObject(android.os.Parcel in) {
        name = in.readString();
        address = in.readString();
        id = in.readString();
        phoneNumber = in.readString();
        attributions = in.createStringArrayList();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        if (in.readByte() == 0) {
            numOfRatings = null;
        } else {
            numOfRatings = in.readInt();
        }
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        websiteUri = in.readParcelable(Uri.class.getClassLoader());
        //photoMetadataList = in.createTypedArrayList(PhotoMetadata);
        openingHours = in.readParcelable(OpeningHours.class.getClassLoader());
        addressComponents = in.readParcelable(AddressComponents.class.getClassLoader());
        typesList = in.createTypedArrayList(Place.Type.CREATOR);
    }

    public static final Creator<PlaceParcelableObject> CREATOR = new Creator<PlaceParcelableObject>() {
        @Override
        public PlaceParcelableObject createFromParcel(android.os.Parcel in) {
            return new PlaceParcelableObject(in);
        }

        @Override
        public PlaceParcelableObject[] newArray(int size) {
            return new PlaceParcelableObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name, this.id});
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getAttributions() {
        return attributions;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getNumOfRatings() {
        return numOfRatings;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public List<PhotoMetadata> getPhotoMetadataList() {
        return photoMetadataList;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public AddressComponents getAddressComponents() {
        return addressComponents;
    }

    public List<Place.Type> getTypesList() {
        return typesList;
    }
}

