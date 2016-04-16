

package com.example.irakl_000.maps.posts;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.irakl_000.maps.PlaceJSONParser;
import com.example.irakl_000.maps.spinner.CustomAdapter;
import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.spinner.SpinnerModel;
import com.example.irakl_000.maps.maps.MapStateManager;
import com.example.irakl_000.maps.maps.MapsActivity;
import com.example.irakl_000.maps.server.ServerRequest;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Post extends FragmentActivity implements OnMapReadyCallback{ //AppCompatActivity gia menu


    /**************  Intialize Variables *************/
    private static final String GOOGLE_API_KEY = "AIzaSyCUf32B0mJQ7TS4l-KF0ou-XizNGtmurc8";
    //ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<>();
    public  ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
    TextView output;
    CustomAdapter adapter;
    Post activity = null;
    String Company="er";
    String placeIcon;

    private GoogleMap mMap;
    private MapStateManager mapm = new MapStateManager();
    public static String EXTRA_TEXT_POST_TITLE = "com.example.irakl_000.maps.TITLE";
    public static String EXTRA_TEXT_POST_POST = "com.example.irakl_000.maps.POST";
    public static String EXTRA_TEXT_FB_ID = "com.example.irakl_000.maps.FB_ID";
    public static String EXTRA_TEXT_FB_USERNAME = "com.example.irakl_000.maps.FB_USERNAME";
    public static String EXTRA_TEXT_LNG = "com.example.irakl_000.maps.LNG";
    public static String EXTRA_TEXT_LAT = "com.example.irakl_000.maps.LAT";
    public static String EXTRA_TEXT_KIND= "com.example.irakl_000.maps.KIND";
    public static String EXTRA_TEXT_POST_ID= "com.example.irakl_000.maps.POST_ID";


    public void cursor(EditText et){
        et.setCursorVisible(true);
        et.requestFocus();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_post2);
        FacebookSdk.sdkInitialize(getApplicationContext());
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.textPic);
        profilePictureView.setProfileId(profile.getId());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final EditText et = (EditText) findViewById(R.id.textPost);
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cursor(et);
                return false;
            }
        });
        /*final EditText et2 = (EditText) findViewById(R.id.textTitle);
        et2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cursor(et2);
                return false;
            }
        });*/

        activity  = this;

        Spinner SpinnerExample = (Spinner)findViewById(R.id.eventKind);
        output                  = (TextView)findViewById(R.id.output);

        // Set data in arraylist
        MapStateManager mgr = new MapStateManager(this);

        setListData(mgr.getUserPosition(this));

        // Resources passed to adapter to get image
        Resources res = getResources();

        // Create custom adapter object ( see below CustomAdapter.java )
        adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res);
        // Set adapter to spinner
        SpinnerExample.setAdapter(adapter);
        // Listener called when spinner item selected
        SpinnerExample.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
                // your code here

                output = (TextView)findViewById(R.id.output);
                // Get selected row data to show on screen
                Company = ((TextView) v.findViewById(R.id.company)).getText().toString();
                ImageView image = ((ImageView) v.findViewById(R.id.image));
                SpinnerModel tempValues = (SpinnerModel) CustomListViewValuesArr.get(position);
                placeIcon = tempValues.getImage();

                //String OutputMsg = "Selected Company : \n\n"+Company;
                //output.setText(OutputMsg);

                //Toast.makeText(
                  //      getApplicationContext(),OutputMsg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapm.gotoUserLocation(mapm.calculateZoomLevel(100, 4), mMap, this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onStop(){
        super.onStop();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mMap);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void insertTextPost(String placeIcon, String place, String body, String username, String fbID, String timestamp, LatLng ll){
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.storePostInBackground(placeIcon, place, body, username, fbID, timestamp, ll);
    }

    public void gotoMapsActivity(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        EditText post = (EditText) findViewById(R.id.textPost);
        String thePost = post.getText().toString();
        String location = Company.toLowerCase();
        if(location.equals("Select your location")){
            location="";
        }
        Profile profile = Profile.getCurrentProfile();
        MapStateManager mapm = new MapStateManager();

        insertTextPost(placeIcon, location, thePost, profile.getName(), profile.getId(), String.valueOf(System.currentTimeMillis()), mapm.getUserPosition(this));
        startActivity(intent);
    }

    public void setListData(LatLng loc)
    {
        SpinnerModel sched = new SpinnerModel();
        CustomListViewValuesArr.add(sched);
        String[] types= {"bar", "airport", "atm", "bank", "cafe", "library", "museum"};
        //for(int i=0; i<types.length; i++) {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location=" + loc.latitude + "," + loc.longitude);
            sb.append("&radius=20000");
            sb.append("&types=bar");
            sb.append("&sensor=true");
            sb.append("&key=" + GOOGLE_API_KEY);

            // Creating a new non-ui thread task to download json data
            PlacesTask placesTask = new PlacesTask();

            // Invokes the "doInBackground()" method of the class PlaceTask
            placesTask.execute(sb.toString());
        //}

    }


    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
                Log.d("dada1", data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }
    public void parserTaskInBackground(){

    }
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        public ParserTask(){

        }

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exceptionnnnnnn",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){

            // Clears all the existing markers
            //mGoogleMap.clear();

            for(int i=0;i<list.size();i++){

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place

                double lat = Double.parseDouble(hmPlace.get("lat"));
                Log.d("tithemore", String.valueOf(lat));
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");
                String icon = hmPlace.get("icon");


                SpinnerModel sched = new SpinnerModel();
                sched.setCompanyName(name /*+ " : " + vicinity*/);
                sched.setImage(icon);
                CustomListViewValuesArr.add(sched);

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name + " : " + vicinity);

                // Placing a marker on the touched position
                //mGoogleMap.addMarker(markerOptions);
            }
        }
    }





    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("dada1Esception ", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}