package com.example.irakl_000.maps.maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.posts.ClusterPosts;
import com.example.irakl_000.maps.posts.Post;
import com.example.irakl_000.maps.posts.Menu;
import com.example.irakl_000.maps.posts.PostPopUp;
import com.example.irakl_000.maps.server.GetCallback;
import com.example.irakl_000.maps.server.GetCallbackSinglePost;
import com.example.irakl_000.maps.server.ServerRequest;
import com.example.irakl_000.maps.settings.SaveProfileState;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback { //AppCompatActivity gia menu
    private GoogleMap mMap;
    private MapStateManager mapm = new MapStateManager();
    private ClusterManager<ClusterItems> mClusterManager;
    private SharedPreferences userID;
    private static final String PREFS_NAME="userProfileID";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testactivityyi", "onCreate()");
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Log.d("testactivityy", "In the onMapReady() event");
        //saveUserID();
        mMap = googleMap;
        ServerRequest serverRequest = new ServerRequest(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Profile profile = Profile.getCurrentProfile();
        serverRequest.updateOnline(profile.getId(), false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        int ratio = ratio();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        mapm.gotoUserLocation(mapm.calculateZoomLevel(width, ratio), mMap, this);

        setUpClusterer();
        onClusterItemClick(serverRequest);
        onClusterClick(serverRequest);
        FacebookSdk.sdkInitialize(getApplicationContext());
        SaveProfileState sps = new SaveProfileState(this);
        if(!sps.getRecommendedPostsCheck()) {
            serverRequest.getPostsInBackground(mMap, mClusterManager, mapm.getUserPosition(this), ratio);
        }else{
            serverRequest.getRecommendedPostsInBackground(mClusterManager, mapm.getUserPosition(this), ratio, null, null);
        }


    }



    private void setUpClusterer() {
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new CustomRenderer<>(this, mMap, mClusterManager));
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    private void onClusterItemClick(final ServerRequest serverRequest){
        mClusterManager
                .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterItems>() {

                    @Override
                    public boolean onClusterItemClick(ClusterItems item) {
                        Log.d("oioi2", "oioi2");
                        final String id = item.getID();
                        serverRequest.getSinglePostInBackground(id, new GetCallbackSinglePost() {

                            @Override
                            public void done(Document doc) {
                                Log.d("oioi", "oioi");
                                Intent intent = new Intent(MapsActivity.this, PostPopUp.class);
                                intent.putExtra(Post.EXTRA_TEXT_POST_ID, id);
                                intent.putExtra(Post.EXTRA_TEXT_POST_TITLE, doc.get("location").toString());
                                intent.putExtra(Post.EXTRA_TEXT_POST_POST, doc.get("body").toString());
                                intent.putExtra(Post.EXTRA_TEXT_FB_ID, doc.get("fbID").toString());
                                intent.putExtra(Post.EXTRA_TEXT_FB_USERNAME, doc.get("username").toString());
                                intent.putExtra(Post.EXTRA_TEXT_LNG, doc.get("lng").toString());
                                intent.putExtra(Post.EXTRA_TEXT_LAT, doc.get("lat").toString());
                                startActivity(intent);
                            }
                        });
                        return true;
                    }
                });
    }

    private void onClusterClick(final ServerRequest serverRequest){
        mClusterManager
                .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterItems>() {
                    @Override
                    public boolean onClusterClick(Cluster<ClusterItems> cluster) {
                        ArrayList<ClusterItems> listItems = new ArrayList<>();
                        Iterator<ClusterItems> items = cluster.getItems().iterator();
                        while (items.hasNext()) {
                            listItems.add(items.next());
                        }
                        Intent intent = new Intent(MapsActivity.this, ClusterPosts.class);
                        intent.putParcelableArrayListExtra("oO", listItems);
                        startActivity(intent);
                        return true;
                    }
                });
    }

    public int ratio(){
        SaveProfileState sps = new SaveProfileState(this);
        int ratio;
        if(sps.getRatio()==0){
            ratio=2;
        }else{
            ratio = sps.getRatio();
        }
        return ratio;
    }

    class CustomRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T>
    {
        public CustomRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
            //start clustering if at least 2 items overlap
            return cluster.getSize() > 1;
        }

    }

    public void gotoPosts(View view){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }
    @Override
    protected void onRestart() {
        Log.d("testactivityyi", "In the onRestart() event");
        super.onRestart();
        SaveProfileState sps = new SaveProfileState(this);
        if(sps.getIfDestroy()/*sps.getChanged()*/){
            mMap.clear();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            Log.d("desedw", String.valueOf(sps.getIfDestroy()));
            sps.setDestroy(false);
            //sps.setChanged(false);
        }
    }

    @Override
    protected void onDestroy(){
        Log.d("testactivityyi", "In the onDestroy() event");
        super.onDestroy();
    }
    @Override
    protected void onStart() {
        Log.d("testactivityyi", "In the onStart() event");
        super.onStart();
    }


    @Override
    protected void onStop(){
        Log.d("testactivityyi", "In the onStop() event");
        super.onStop();
        ServerRequest serverRequest = new ServerRequest(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Profile profile = Profile.getCurrentProfile();
        serverRequest.updateOnline(profile.getId(), true);
    }



    @Override
    protected void onPause() {
        Log.d("testactivityyi", "In the onPause() event");
        super.onPause();
        ServerRequest serverRequest = new ServerRequest(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Profile profile = Profile.getCurrentProfile();
        serverRequest.updateOnline(profile.getId(), true);
    }

    @Override
    protected void onResume(){
        Log.d("testactivityyi", "In the onResume() event");
        super.onResume();
    }



}
                    /*LatLng target = new LatLng(
                            (Double) document.get("lat")*1.0,
                            (Double) document.get("lng")*1.0);
                    String mDrawableName = document.get("title").toString() + "icon";
                    if (mDrawableName.equals("icon")) {
                        mDrawableName = "movieicon";
                    }
                    int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
                    Drawable dr = ContextCompat.getDrawable(getApplicationContext(), resID);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 60, 60, true));
                    Bitmap bitmap2 = ((BitmapDrawable) d).getBitmap();
             ********       mMap.addMarker(new MarkerOptions().snippet(String.valueOf(document.get("_id"))).position(target).title(document.get("title").toString()).icon(BitmapDescriptorFactory.fromBitmap(bitmap2)));
                    */
        /********   mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                final String id = arg0.getSnippet();
                serverRequest0.getSinglePostInBackground(id, new GetCallbackSinglePost() {
                    @Override
                    public void done(Document doc) {
                        Intent intent = new Intent(MapsActivity.this, PostPopUp.class);
                        intent.putExtra(Post.EXTRA_TEXT_POST_ID, id);
                        intent.putExtra(Post.EXTRA_TEXT_POST_TITLE, doc.get("title").toString());
                        intent.putExtra(Post.EXTRA_TEXT_POST_POST, doc.get("body").toString());
                        intent.putExtra(Post.EXTRA_TEXT_FB_ID, doc.get("fbID").toString());
                        intent.putExtra(Post.EXTRA_TEXT_FB_USERNAME, doc.get("username").toString());
                        intent.putExtra(Post.EXTRA_TEXT_LNG, doc.get("lng").toString());
                        intent.putExtra(Post.EXTRA_TEXT_LAT, doc.get("lat").toString());
                        startActivity(intent);
                    }
                });
                return true;
            }
        });*/

/*    //Searchhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh////     /*public void geoLocate(View v) throws IOException {
        hideSoftKeyboard(v);
        EditText et = (EditText) findViewById(R.id.search);
        String location = et.getText().toString();
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        //getFromLocationName(String locationName, int maxResults, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude)         //gia tin aktina
        Address add = list.get(0);//sto principal clud theater crusharei
        String locality = add.getLocality();
        Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();
        double lat = add.getLatitude();
        double lng = add.getLongitude();
        mapm.gotoUserLocation(lat, lng, mapm.calculateZoomLevel(),mMap, this);
    }
    private void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
/*
        if(initMap()){
            Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Map not available..", Toast.LENGTH_SHORT).show();
        }
 */
