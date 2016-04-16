package com.example.irakl_000.maps.maps;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Irakl_000 on 29/11/2015.
 */
public class MapStateManager {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";
    private static final String BEARING = "bearing";
    private static final String TILT = "tilt";
    private static final String MAPTYPE = "MAPTYPE";


    private static final String PREFS_NAME="mapCameraState";//definition for shared

    private SharedPreferences mapStatePrefs;// save data on the device

    private Double ln[] = new Double[2];
    public MapStateManager(Context context){
        mapStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public MapStateManager(){

    }
    public void saveMapState(GoogleMap map){
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        CameraPosition position = map.getCameraPosition();
        editor.putFloat(LATITUDE, (float) position.target.latitude);
        editor.putFloat(LONGITUDE, (float) position.target.longitude);
        editor.putFloat(TILT, position.tilt);
        editor.putFloat(ZOOM, position.zoom);
        editor.putFloat(BEARING, position.bearing);

        editor.commit();
    }

    public CameraPosition getSavedCameraPosition(){
        double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
        double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);

        if (latitude==0){
            return null;
        }
        LatLng target = new LatLng(latitude, longitude);

        float zoom = mapStatePrefs.getFloat(ZOOM, 0);
        float bearing = mapStatePrefs.getFloat(BEARING, 0);
        float tilt = mapStatePrefs.getFloat(TILT, 0);

        CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
        return position;
    }

    /*public float calculateZoomLevel(int km) {
        float zoom = (float) (19 - Math.log(km * 5.508));
        return zoom;
    }*/

    public float calculateZoomLevel(int screenWidth, int ratio) {

        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > ratio*1000) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        //Log.i("ADNAN", "zoom level = "+zoomLevel);
        return zoomLevel;
    }

    public LatLng getUserPosition(Context context){
        GPSTracker loc = new GPSTracker(context);
        Double userLng = loc.getLongitude();
        Double userLat = loc.getLatitude();
        LatLng location = new LatLng(userLat, userLng);

        return location;
    }

    public void gotoUserLocation(float theZoom, GoogleMap mMap, Context context){

        GPSTracker loc = new GPSTracker(context);
        Double userLng = loc.getLongitude();
        Double userLat = loc.getLatitude();
        LatLng location = new LatLng(userLat, userLng);
        CameraUpdate go = CameraUpdateFactory.newLatLngZoom(location, theZoom);
        mMap.moveCamera(go);
    }

    public void clearThem(){
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        editor.clear();
        editor.commit();
    }
}
