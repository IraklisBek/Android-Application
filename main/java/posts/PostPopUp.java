package com.example.irakl_000.maps.posts;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.server.ServerRequest;
import com.example.irakl_000.maps.user.UserProfile;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import java.io.IOException;
import java.util.List;

public class PostPopUp extends AppCompatActivity {
    Geocoder geocoder;
    ServerRequest serverRequest;
    String postID, location;
    public static String EXTRA_USER_PROFILE_ID = "com.example.irakl_000.maps.USER_PROFILE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_text_post_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.9));
        final Intent intent = getIntent();
        Profile profile = Profile.getCurrentProfile();
        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.picTextPost);
        profilePictureView.setProfileId(intent.getStringExtra(Post.EXTRA_TEXT_FB_ID));
        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(PostPopUp.this, UserProfile.class);
                intent2.putExtra(EXTRA_USER_PROFILE_ID, intent.getStringExtra(Post.EXTRA_TEXT_FB_ID));
                startActivity(intent2);
            }
        });

        String post = intent.getStringExtra(Post.EXTRA_TEXT_POST_POST);
        location = intent.getStringExtra(Post.EXTRA_TEXT_POST_TITLE);
        String username = intent.getStringExtra(Post.EXTRA_TEXT_FB_USERNAME);
        String lng = intent.getStringExtra(Post.EXTRA_TEXT_LNG);
        Double lngD = Double.valueOf(lng);
        String lat = intent.getStringExtra(Post.EXTRA_TEXT_LNG);
        Double latD = Double.valueOf(lat);
        postID = intent.getStringExtra(Post.EXTRA_TEXT_POST_ID);


        /*List<Address> geoResult = findGeocoder(latD, lngD);
        String stringThisAddress = "";
        if(geoResult != null) {
            List<String> geoStringResult = new ArrayList<String>();
            for (int i = 0; i < geoResult.size(); i++) {
                Address thisAddress = geoResult.get(i);
                stringThisAddress = "";
                for (int a = 0; a < thisAddress.getMaxAddressLineIndex(); a++) {
                    stringThisAddress += thisAddress.getAddressLine(a) + "\n";
                }

                stringThisAddress +=
                        "CountryName: " + thisAddress.getCountryName() + "\n"
                                + "CountryCode: " + thisAddress.getCountryCode() + "\n"
                                + "AdminArea: " + thisAddress.getAdminArea() + "\n"
                                + "FeatureName: " + thisAddress.getFeatureName();
                geoStringResult.add(stringThisAddress);
            }
        }*/

        TextView titleView = (TextView) findViewById(R.id.textPostTitle);
        TextView postView = (TextView) findViewById(R.id.textPostPost);
        TextView usernameView = (TextView) findViewById(R.id.textPostUsername);
        TextView placeView = (TextView) findViewById(R.id.placeDet);
        final Button follow = (Button) findViewById(R.id.follow);
        final ServerRequest serverRequest = new ServerRequest(PostPopUp.this);

        serverRequest.checkIfFollowingInBackground(intent.getStringExtra(Post.EXTRA_TEXT_FB_ID), follow, this);
        follow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                serverRequest.addFollowersInBackground(intent.getStringExtra(Post.EXTRA_TEXT_FB_ID), postID, false, follow, PostPopUp.this);
            }
        });

        //serverRequest = new ServerRequest(this);
        titleView.setText(location);
        postView.setText(post);
        usernameView.setText(username);
        placeView.setText("Address");


    }
    public void follow() {

    }


    public void rateOne(View view){
        ServerRequest serverRequest = new ServerRequest(PostPopUp.this);
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        serverRequest.rateInBackground(location, 1,id,postID);

    }

    public void rateTwo(View view){
        ServerRequest serverRequest = new ServerRequest(PostPopUp.this);
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        serverRequest.rateInBackground(location, 2,id,postID);

    }
    public void rateThree(View view){
        ServerRequest serverRequest = new ServerRequest(PostPopUp.this);
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        serverRequest.rateInBackground(location, 3,id,postID);

    }
    public void rateFour(View view){
        ServerRequest serverRequest = new ServerRequest(PostPopUp.this);
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        serverRequest.rateInBackground(location, 4,id,postID);

    }

    public void rateFive(View view){
        ServerRequest serverRequest = new ServerRequest(PostPopUp.this);
        Profile profile = Profile.getCurrentProfile();
        String id = profile.getId();
        serverRequest.rateInBackground(location, 5,id,postID);

    }

    private List<Address> findGeocoder(Double lat, Double lon){
        final int maxResults = 5;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return addresses;
    }

}
