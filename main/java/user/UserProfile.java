package com.example.irakl_000.maps.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.maps.RecommendedItems;
import com.example.irakl_000.maps.posts.Post;
import com.example.irakl_000.maps.posts.PostPopUp;
import com.example.irakl_000.maps.server.GetCallback;
import com.example.irakl_000.maps.server.ServerRequest;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class UserProfile extends AppCompatActivity {
    ScrollView scrollView;
    LinearLayout contentnView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_profile);
        //setContentView(R.layout.activity_cluster_posts);

        Intent intent = getIntent();

        ServerRequest serverRequest = new ServerRequest(this);
        final String userid = intent.getStringExtra(PostPopUp.EXTRA_USER_PROFILE_ID);
        serverRequest.getUserPostsInBackground(userid, new GetCallback() {
            @Override
            public void done(ArrayList<Document> docs) {
                contentnView = new LinearLayout(UserProfile.this);
                // SET ORIENTATION TO VERTICAL
                contentnView.setOrientation(LinearLayout.VERTICAL);
                // SET CONTENT VIEW
                setContentView(contentnView);
                // RELATIVE LAYOUT WITH VIEWS
                //RelativeLayout relativeLayoutWithView = this.withChildRelativeLayout();
                RelativeLayout relativeLayoutWithView = new RelativeLayout(UserProfile.this);

                // CREATE PARAM FOR SIZE
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                // APPLY PARAM
                relativeLayoutWithView.setLayoutParams(params);
                relativeLayoutWithView.setPadding(16, 16, 16, 16);
                // SET BACKGROUND COLOR
                relativeLayoutWithView.setBackgroundColor(Color.parseColor("#457BE4"));
                //relativeLayoutWithView.setBackgroundColor(Color.WHITE);
                FacebookSdk.sdkInitialize(getApplicationContext());
                ProfilePictureView profilePictureView = new ProfilePictureView(UserProfile.this);
                profilePictureView.setProfileId(userid);
                profilePictureView.setId(View.generateViewId());
                final HashMap<Integer, ImageView> kindViews = new HashMap<>();
                final HashMap<Integer, TextView> textViews = new HashMap<>();
                final HashMap<Integer, TextView> dates = new HashMap<>();
                final HashMap<Integer, View> lines = new HashMap<>();

                int i = 0;

                for (Document post : docs) {
                    lines.put(i, new View(UserProfile.this));
                    lines.get(i).setId(View.generateViewId());

                    dates.put(i, new TextView(UserProfile.this));
                    dates.get(i).setId(View.generateViewId());
                    dates.get(i).setTextColor(Color.WHITE);
                    dates.get(i).setTextSize(18);
                    dates.get(i).setText(post.get("date").toString());

                    textViews.put(i, new TextView(UserProfile.this));
                    textViews.get(i).setId(View.generateViewId());
                    textViews.get(i).setTextColor(Color.BLACK);
                    textViews.get(i).setTextSize(16);
                    textViews.get(i).setText(post.get("body").toString());
                    kindViews.put(i, new ImageView(UserProfile.this));
                    kindViews.get(i).setId(View.generateViewId());


/*                    String mDrawableName = post.get("title").toString() + "icon";
                    if (mDrawableName.equals("icon")) {
                        mDrawableName = "movieicon";
                    }
                    int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
                    Drawable dr = ContextCompat.getDrawable(getApplicationContext(), resID);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                    Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 60, 60, true));
                    Bitmap bitmap2 = ((BitmapDrawable) d).getBitmap();

                    kindViews.put(i, new ImageView(UserProfile.this));
                    kindViews.get(i).setImageBitmap(bitmap2);
                    kindViews.get(i).setId(View.generateViewId());*/

                    i++;

                }
                RelativeLayout.LayoutParams paramsProfileImg = new RelativeLayout.LayoutParams(170, 170);
                paramsProfileImg.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                //paramsProfileImg.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                relativeLayoutWithView.addView(profilePictureView, paramsProfileImg);
                for (int j = 0; j < textViews.size(); j++) {
                    RelativeLayout.LayoutParams paramsLine = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    RelativeLayout.LayoutParams paramsDate = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    RelativeLayout.LayoutParams paramsKind= new RelativeLayout.LayoutParams(60, 60);

                    if (j == 0) {
                        paramsLine.addRule(RelativeLayout.BELOW, profilePictureView.getId());
                        //paramsLine.addRule(RelativeLayout.ALIGN_TOP, profilePictureView.getId());
                    } else {
                        paramsLine.addRule(RelativeLayout.BELOW, textViews.get(j - 1).getId());
                        //Log.d("cdcdcd", paramsLine.addRule(RelativeLayout.BELOW, lines.get(j - 1).getId()));
                    }

                    paramsKind.addRule(RelativeLayout.BELOW, dates.get(j).getId());


                    //paramsLine.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
                    lines.get(j).setBackgroundColor(Color.WHITE);
                    paramsLine.setMargins(0, 16, 0, 0);
                    lines.get(j).setLayoutParams(paramsLine);

                    paramsDate.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                    paramsDate.addRule(RelativeLayout.BELOW, lines.get(j).getId());

                    paramsText.addRule(RelativeLayout.BELOW, kindViews.get(j).getId());


                    relativeLayoutWithView.addView(lines.get(j), paramsLine);
                    relativeLayoutWithView.addView(dates.get(j), paramsDate);
                    relativeLayoutWithView.addView(textViews.get(j), paramsText);
                    relativeLayoutWithView.addView(kindViews.get(j), paramsKind);

                }

                // ADD VIEWS IN RELATIVE LAYOUT AND ARRANGE
                scrollView = new ScrollView(UserProfile.this);
                scrollView.addView(relativeLayoutWithView);
                // ADD LAYOUT TO mainLayout
                contentnView.addView(scrollView);


            }
        });

    }


    RelativeLayout withChildRelativeLayout(){

        RelativeLayout relativeLayout = new RelativeLayout(this);

        // CREATE PARAM FOR SIZE
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );

        // APPLY PARAM
        relativeLayout.setLayoutParams(params);
        relativeLayout.setPadding(16, 16, 16, 16);
        // SET BACKGROUND COLOR
        relativeLayout.setBackgroundColor(Color.parseColor("#98bcd8"));
        //relativeLayout.setBackgroundColor(Color.WHITE);
        return relativeLayout;
    }




}
