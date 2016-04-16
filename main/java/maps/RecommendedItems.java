package com.example.irakl_000.maps.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Irakl_000 on 25/3/2016.
 */
public class RecommendedItems implements  Parcelable {
    private String postid, fbid, body, kind, username, lng, lat, location, placeicon, date;
    public RecommendedItems(String postid, String fbid, String body, String kind, String username, String lng, String lat, String location, String placeicon, String date){
        this.postid = postid;
        this.fbid=fbid;
        this.body = body;
        this.kind = kind;
        this.username = username;
        this.lng=lng;
        this.lat = lat;
        this.location = location;
        this.placeicon = placeicon;
        this.date = date;
    }

    private RecommendedItems(Parcel in) {
        postid=in.readString();
        fbid=in.readString();
        body = in.readString();
        kind = in.readString();
        username = in.readString();
        lng=in.readString();
        lat=in.readString();
        location=in.readString();
        placeicon=in.readString();
        date = in.readString();
    }

    public String getPostid(){
        return postid;
    }

    public String getFbid(){
        return fbid;
    }
    public String getBody(){
        return body;
    }
    public String getKind(){
        return kind;
    }
    public String getUsername(){
        return username;
    }
    public String getLng(){
        return lng;
    }
    public String getLat(){
        return lat;
    }
    public String getLocation(){
        return location;
    }

    public String getPlaceIcon(){
        return placeicon;
    }
    public String getDate(){
        return date;
    }

    public static final Creator<RecommendedItems> CREATOR = new Creator<RecommendedItems>() {
        @Override
        public RecommendedItems createFromParcel(Parcel in) {
            return new RecommendedItems(in);
        }

        @Override
        public RecommendedItems[] newArray(int size) {
            return new RecommendedItems[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postid);
        dest.writeString(fbid);
        dest.writeString(body);
        dest.writeString(kind);
        dest.writeString(username);
        dest.writeString(lng);
        dest.writeString(lat);
        dest.writeString(location);
        dest.writeString(placeicon);
        dest.writeString(date);
    }
}
