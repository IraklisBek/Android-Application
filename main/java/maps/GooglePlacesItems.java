package com.example.irakl_000.maps.maps;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Irakl_000 on 25/3/2016.
 */
public class GooglePlacesItems implements  Parcelable {
    private String postid, fbid, body, kind, username, lng, lat;
    public GooglePlacesItems(String postid, String fbid, String body, String kind, String username, String lng, String lat){
        this.postid = postid;
        this.fbid=fbid;
        this.body = body;
        this.kind = kind;
        this.username = username;
        this.lng=lng;
        this.lat = lat;
    }

    private GooglePlacesItems(Parcel in) {
        postid=in.readString();
        fbid=in.readString();
        body = in.readString();
        kind = in.readString();
        username = in.readString();
        lng=in.readString();
        lat=in.readString();
    }

    /*public String getPostid(){
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
    }*/
    public static final Creator<GooglePlacesItems> CREATOR = new Creator<GooglePlacesItems>() {
        @Override
        public GooglePlacesItems createFromParcel(Parcel in) {
            return new GooglePlacesItems(in);
        }

        @Override
        public GooglePlacesItems[] newArray(int size) {
            return new GooglePlacesItems[size];
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
    }
}
