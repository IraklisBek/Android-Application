package com.example.irakl_000.maps.posts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.maps.ClusterItems;
import com.example.irakl_000.maps.maps.MapStateManager;
import com.example.irakl_000.maps.maps.RecommendedItems;
import com.example.irakl_000.maps.server.GetCallback;
import com.example.irakl_000.maps.server.ServerRequest;
import com.example.irakl_000.maps.settings.Profile;
import com.example.irakl_000.maps.settings.SaveProfileState;
import com.facebook.FacebookSdk;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;

public class Menu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testactivityyii", "open");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_posts);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.85), (int) (height * 0.85));
        ServerRequest serverRequest = new ServerRequest(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        serverRequest.updateOnline(profile.getId(), false);
    }

    public void gotoPost(View v){
        Intent intent = new Intent(this, Post.class);
        startActivity(intent);
    }

    public void gotoSettings(View view){
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }


    public void gotoRec(View view){
        MapStateManager msm = new MapStateManager(this);
        ServerRequest serverRequest = new ServerRequest(this);
        SaveProfileState sps = new SaveProfileState(this);
        serverRequest.getRecommendedLocationsInBackground(null, msm.getUserPosition(this), sps.getRatio(), new GetCallback() {
            @Override
            public void done(ArrayList<Document> docs) {
                Iterator<Document> items = docs.iterator();
                ArrayList<RecommendedItems> listItems = new ArrayList<>();
                while (items.hasNext()) {
                    Document aDoc = items.next();
                    Log.d("tiexeis", aDoc.get("location").toString());

                    listItems.add(new RecommendedItems(aDoc.get("_id").toString(),
                            aDoc.get("fbID").toString(),
                            aDoc.get("body").toString(),
                            aDoc.get("location").toString(),
                            aDoc.get("username").toString(),
                            aDoc.get("lng").toString(),
                            aDoc.get("lat").toString(),
                            aDoc.get("location").toString(),
                            aDoc.get("placeicon").toString(),
                            aDoc.get("date").toString()));
                }
                Intent intent = new Intent(Menu.this, RecommendedLocations.class);
                intent.putParcelableArrayListExtra("oOo", listItems);
                startActivity(intent);

            }
        }, this);
        /*mClusterManager
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
                });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("testactivityyii", "close");
        ServerRequest serverRequest = new ServerRequest(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        serverRequest.updateOnline(profile.getId(), true);
    }


}
