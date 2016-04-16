package com.example.irakl_000.maps.server;

import android.os.Parcelable;

import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by Irakl_000 on 6/3/2016.
 */
public interface GetCallback {

    public abstract void done(ArrayList<Document> docs);


}
