package com.codepath.cityslicker;

import android.net.Uri;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PointOfInterest;
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
    private List<Place.Type> typesList;

    public PlaceParcelableObject() {}

    public PlaceParcelableObject(PointOfInterest poi) {
        name = poi.name;
        id = poi.placeId;
        latLng = poi.latLng;
    }

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
        typesList = place.getTypes();
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.name, this.id, this.address, this.phoneNumber});
        dest.writeDouble(this.numOfRatings);
        dest.writeParcelable(this.openingHours, flags);
        dest.writeParcelable(this.websiteUri, flags);
        dest.writeTypedArray(this.typesList.toArray(new Place.Type[0]), flags);
        dest.writeTypedArray(this.photoMetadataList.toArray(new PhotoMetadata[0]), flags);
    }

    protected PlaceParcelableObject(android.os.Parcel in) {
        name = in.readString();
        address = in.readString();
        id = in.readString();
        phoneNumber = in.readString();
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
        openingHours = in.readParcelable(OpeningHours.class.getClassLoader());
        typesList = in.createTypedArrayList(Place.Type.CREATOR);
    }

    public static final Parcelable.Creator<PlaceParcelableObject> CREATOR = new Parcelable.Creator<PlaceParcelableObject>() {
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

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}

