package com.example.irakl_000.maps.posts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.maps.ClusterItems;
import com.example.irakl_000.maps.maps.RecommendedItems;
import com.example.irakl_000.maps.server.GetCallback;
import com.example.irakl_000.maps.server.ServerRequest;
import com.example.irakl_000.maps.user.UserProfile;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import org.bson.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RecommendedLocations extends AppCompatActivity {

    ScrollView scrollView;
    LinearLayout contentnView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cluster_posts);
        contentnView = new LinearLayout(this);
        // SET ORIENTATION TO VERTICAL
        contentnView.setOrientation(LinearLayout.VERTICAL);
        // SET CONTENT VIEW
        setContentView(contentnView);
        // RELATIVE LAYOUT WITH VIEWS
        RelativeLayout relativeLayoutWithView = this.withChildRelativeLayout();
        // ADD VIEWS IN RELATIVE LAYOUT AND ARRANGE
        arrangedChildView(relativeLayoutWithView);
        scrollView = new ScrollView(this);
        scrollView.addView(relativeLayoutWithView);
        // ADD LAYOUT TO mainLayout
        contentnView.addView(scrollView);

    }

    RelativeLayout withChildRelativeLayout(){

        RelativeLayout relativeLayout = new RelativeLayout(this);

        // CREATE PARAM FOR SIZE
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );

        // APPLY PARAM
        relativeLayout.setLayoutParams(params);
        relativeLayout.setPadding(16, 16, 16, 16);
        // SET BACKGROUND COLOR
        //relativeLayout.setBackgroundColor(Color.parseColor("#457BE4"));
        relativeLayout.setBackgroundColor(Color.WHITE);
        return relativeLayout;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    private void arrangedChildView(final RelativeLayout relativeLayoutWithView){
        final Intent intent = getIntent();
        ArrayList<RecommendedItems> post = intent.getParcelableArrayListExtra("oOo");
        final HashMap<String, ArrayList<HashMap<String, String>>> locsPosts = new HashMap<>();
        HashMap<String, String> locIcon = new HashMap<>();

        final HashMap<Integer, TextView> viewPostsView = new HashMap<>();
        HashMap<Integer, TextView> locView = new HashMap<>();
        HashMap<Integer, ImageView> iconsView = new HashMap<>();
        final HashMap<Integer, View> lines = new HashMap<>();
        int i=0;
        for(final RecommendedItems item : post) {
            String loc = item.getLocation();
            locIcon.put(loc, item.getPlaceIcon());
            Profile profile = Profile.getCurrentProfile();
            //if(!profile.getId().equals(item.getFbid())) {
                if (locsPosts.containsKey(loc)) {
                    ArrayList<HashMap<String, String>> getPosts = locsPosts.get(loc);
                    HashMap<String, String> getPostsInfo = new HashMap<>();
                    getPostsInfo.put("body", item.getBody());
                    getPostsInfo.put("fbid", item.getFbid());
                    getPostsInfo.put("username", item.getUsername());
                    getPostsInfo.put("date", item.getDate());
                    getPosts.add(getPostsInfo);
                    Log.d("loc112", "loc " + loc + "user " + item.getUsername());
                    locsPosts.put(loc, getPosts);
                } else {
                    ArrayList<HashMap<String, String>> getPosts = new ArrayList<>();
                    HashMap<String, String> getPostsInfo = new HashMap<>();
                    getPostsInfo.put("body", item.getBody());
                    getPostsInfo.put("fbid", item.getFbid());
                    getPostsInfo.put("username", item.getUsername());
                    getPostsInfo.put("date", item.getDate());
                    getPosts.add(getPostsInfo);
                    Log.d("loc111","loc " + loc + "user " + item.getUsername());
                    locsPosts.put(loc, getPosts);
                }
            //}
        }
        for(final String loc : locsPosts.keySet()) {
            Log.d("thelastone", "location: " +loc);
            for(HashMap<String, String> aa : locsPosts.get(loc)) {
                for(String a : aa.keySet()){
                    Log.d("thelastone", "info: " + a +  " value: " + aa.get(a));
                }
            }

        }

            for(final String loc : locsPosts.keySet()){
            lines.put(i, new View(this));
            lines.get(i).setId(View.generateViewId());

            viewPostsView.put(i, new TextView(this));
            viewPostsView.get(i).setId(View.generateViewId());
            viewPostsView.get(i).setTextColor(Color.parseColor("#63619C"));
            viewPostsView.get(i).setTextSize(20);
            viewPostsView.get(i).setText("View posts");
            final int finalI = i;
            viewPostsView.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<HashMap<String, String>> getPosts = locsPosts.get(loc);
                    int j = 0;
                    HashMap<Integer, ProfilePictureView> profileView = new HashMap<>();
                    HashMap<Integer, TextView> usernameView = new HashMap<>();
                    HashMap<Integer, View> linesView = new HashMap<>();
                    HashMap<Integer, TextView> bodyView = new HashMap<>();
                    HashMap<Integer, TextView> dateView = new HashMap<>();
                    Log.d("pioeisai", String.valueOf(finalI));
                    for (final HashMap<String, String> post : getPosts) {
                        linesView.put(j, new View(RecommendedLocations.this));
                        linesView.get(j).setId(View.generateViewId());

                        profileView.put(j, new ProfilePictureView(RecommendedLocations.this));
                        profileView.get(j).setId(View.generateViewId());
                        profileView.get(j).setProfileId(post.get("fbid"));
                        profileView.get(j).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent2 = new Intent(RecommendedLocations.this, UserProfile.class);
                                intent2.putExtra(PostPopUp.EXTRA_USER_PROFILE_ID, post.get("fbid"));
                                startActivity(intent2);
                            }
                        });

                        usernameView.put(j, new TextView(RecommendedLocations.this));
                        usernameView.get(j).setId(View.generateViewId());
                        usernameView.get(j).setTextColor(Color.parseColor("#98bcd8"));
                        usernameView.get(j).setTextSize(15);
                        usernameView.get(j).setText(post.get("username"));

                        dateView.put(j, new TextView(RecommendedLocations.this));
                        dateView.get(j).setId(View.generateViewId());
                        dateView.get(j).setTextColor(Color.parseColor("#98bcd8"));
                        dateView.get(j).setTextSize(15);
                        dateView.get(j).setText(post.get("date"));

                        bodyView.put(j, new TextView(RecommendedLocations.this));
                        bodyView.get(j).setId(View.generateViewId());
                        bodyView.get(j).setTextColor(Color.parseColor("#373737"));
                        bodyView.get(j).setTextSize(20);
                        bodyView.get(j).setText(post.get("body"));
                        j++;
                    }

                    for (int ii = 0; ii < getPosts.size(); ii++) {
                        RelativeLayout.LayoutParams paramsLine2 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);

                        RelativeLayout.LayoutParams paramsUsername = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        RelativeLayout.LayoutParams paramsProfileImg = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        RelativeLayout.LayoutParams paramsBody = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        RelativeLayout.LayoutParams paramsDate = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (ii == 0) {
                            paramsProfileImg.addRule(RelativeLayout.BELOW, viewPostsView.get(finalI).getId());
                        } else {
                            paramsProfileImg.addRule(RelativeLayout.BELOW, linesView.get(ii - 1).getId());
                        }
                        paramsLine2.addRule(RelativeLayout.BELOW, bodyView.get(ii).getId());
                        linesView.get(ii).setBackgroundColor(Color.parseColor("#98bcd8"));
                        paramsLine2.setMargins(6, 6, 6, 6);
                        paramsProfileImg.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
                        profileView.get(ii).setLayoutParams(paramsProfileImg);
                        profileView.get(ii).getLayoutParams().width = 80;
                        profileView.get(ii).getLayoutParams().height = 80;
                        paramsUsername.addRule(RelativeLayout.ALIGN_TOP, profileView.get(ii).getId());
                        paramsUsername.addRule(RelativeLayout.END_OF, profileView.get(ii).getId());
                        paramsDate.addRule(RelativeLayout.BELOW, usernameView.get(ii).getId());
                        paramsDate.addRule(RelativeLayout.END_OF, profileView.get(ii).getId());
                        paramsBody.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
                        paramsBody.addRule(RelativeLayout.BELOW, profileView.get(ii).getId());

                        relativeLayoutWithView.addView(usernameView.get(ii), paramsUsername);
                        relativeLayoutWithView.addView(dateView.get(ii), paramsDate);
                        relativeLayoutWithView.addView(profileView.get(ii), paramsProfileImg);
                        relativeLayoutWithView.addView(bodyView.get(ii), paramsBody);
                        relativeLayoutWithView.addView(linesView.get(ii), paramsLine2);


                    }

                    RelativeLayout.LayoutParams paramsLine = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    paramsLine.addRule(RelativeLayout.BELOW, bodyView.get(j - 1).getId());
                    paramsLine.setMargins(6, 6, 6, 16);
                    lines.get(finalI).setLayoutParams(paramsLine);
                }
            });


            locView.put(i, new TextView(this));
            locView.get(i).setId(View.generateViewId());
            locView.get(i).setTextColor(Color.parseColor("#6D9C61"));;
            locView.get(i).setTextSize(25);
            locView.get(i).setText(loc.substring(0, 1).toUpperCase() + loc.substring(1));

            iconsView.put(i, new ImageView(this));
            iconsView.get(i).setId(View.generateViewId());
            new DownloadImageTask(iconsView.get(i))
                    .execute(locIcon.get(loc));




            i++;
        }
        for(int ii=0; ii<locsPosts.keySet().size(); ii++) {
            RelativeLayout.LayoutParams paramsLine = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            RelativeLayout.LayoutParams paramsImg = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams paramsViewPosts = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            if(ii==0) {
                paramsText.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
            }else{
                paramsText.addRule(RelativeLayout.BELOW, lines.get(ii-1).getId());
            }
            paramsText.setMargins(6, 6, 6, 16);
            paramsImg.setMargins(6, 6, 6, 16);
            paramsLine.setMargins(6, 6, 6, 16);
            paramsViewPosts.setMargins(6, 6, 6, 16);

            lines.get(ii).setBackgroundColor(Color.parseColor("#98bcd8"));

            locView.get(ii).setLayoutParams(paramsText);
            iconsView.get(ii).setLayoutParams(paramsImg);

            paramsImg.addRule(RelativeLayout.BELOW, locView.get(ii).getId());
            paramsViewPosts.addRule(RelativeLayout.BELOW, iconsView.get(ii).getId());
            paramsLine.addRule(RelativeLayout.BELOW, viewPostsView.get(ii).getId());

            relativeLayoutWithView.addView(iconsView.get(ii), paramsImg);
            relativeLayoutWithView.addView(locView.get(ii), paramsText);
            relativeLayoutWithView.addView(viewPostsView.get(ii), paramsViewPosts);
            relativeLayoutWithView.addView(lines.get(ii), paramsLine);
        }

        /*for(int ii=1; ii<=locsPosts.keySet().size(); ii++) {
            RelativeLayout.LayoutParams paramsLine = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            RelativeLayout.LayoutParams paramsImg = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(ii-1==0) {
                paramsImg.setMargins(6, 6, 6, 16);
                paramsImg.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                paramsKind.addRule(RelativeLayout.ALIGN_TOP, imageViews.get(ii-1).getId());
                paramsKind.addRule(RelativeLayout.END_OF, imageViews.get(ii-1).getId());
            }else{
                paramsKind.setMargins(6, 22, 6, 16);
                paramsImg.addRule(RelativeLayout.BELOW, imageViews.get(ii-2).getId());
                paramsKind.addRule(RelativeLayout.BELOW, imageViews.get(ii-2).getId());
                paramsKind.addRule(RelativeLayout.END_OF, imageViews.get(ii-1).getId());
            }
            paramsText.addRule(RelativeLayout.BELOW, kindViews.get(ii-1).getId());
            paramsText.addRule(RelativeLayout.END_OF, imageViews.get(ii - 1).getId());
            paramsText.setMargins(12, -8, 0, 0);

            paramsImg.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
            paramsImg.setMargins(0, 32, 0, 0);

            //paramsText.height = 68;

            lines.get(ii - 1).setBackgroundColor(Color.parseColor("#98bcd8"));
            paramsLine.addRule(RelativeLayout.BELOW, imageViews.get(ii - 1).getId());
            paramsLine.setMargins(0, 16, 0, 0);

            imageViews.get(ii-1).setLayoutParams(paramsImg);
            imageViews.get(ii-1).getLayoutParams().width = 80;
            imageViews.get(ii-1).getLayoutParams().height = 80;

            relativeLayoutWithView.addView(imageViews.get(ii - 1), paramsImg);
            relativeLayoutWithView.addView(kindViews.get(ii - 1), paramsKind);
            relativeLayoutWithView.addView(textViews.get(ii-1), paramsText);
            relativeLayoutWithView.addView(lines.get(ii - 1), paramsLine);
        }


        for(final RecommendedItems item : post){
            lines.put(i, new View(this));

            kindViews.put(i, new TextView(this));
            kindViews.get(i).setId(View.generateViewId());
            kindViews.get(i).setTextColor(Color.BLACK);
            kindViews.get(i).setTextSize(20);

            textViews.put(i, new TextView(this));
            textViews.get(i).setId(View.generateViewId());
            textViews.get(i).setTextColor(Color.GRAY);
            textViews.get(i).setTextSize(16);

            imageViews.put(i, new ProfilePictureView(this));
            imageViews.get(i).setId(View.generateViewId());
            imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 = new Intent(RecommendedLocations.this, UserProfile.class);
                    intent2.putExtra(PostPopUp.EXTRA_USER_PROFILE_ID, item.getFbid());
                    startActivity(intent2);
                }
            });
            String body = item.getBody();
            String kind = item.getKind();//.substring(0, 1).toUpperCase() + doc.get("title").toString().substring(1);;
            if (kind.equals("")) {
                kind = "General";
            } else {
                kind = item.getKind().substring(0, 1).toUpperCase() + item.getKind().substring(1);
                ;
            }
            if (body.length() > 3) {
                body = body.substring(0, 3) + "...";
            }
            kindViews.get(i).setText(kind);
            kindViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecommendedLocations.this, PostPopUp.class);
                    intent.putExtra(Post.EXTRA_TEXT_POST_ID, item.getPostid());
                    intent.putExtra(Post.EXTRA_TEXT_POST_TITLE, item.getKind());
                    intent.putExtra(Post.EXTRA_TEXT_POST_POST, item.getBody());
                    intent.putExtra(Post.EXTRA_TEXT_FB_ID, item.getFbid());
                    intent.putExtra(Post.EXTRA_TEXT_FB_USERNAME, item.getUsername());
                    intent.putExtra(Post.EXTRA_TEXT_LNG, item.getLng());
                    intent.putExtra(Post.EXTRA_TEXT_LAT, item.getLat());
                    startActivity(intent);
                }
            });
            textViews.get(i).setText(body);
            textViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecommendedLocations.this, PostPopUp.class);
                    intent.putExtra(Post.EXTRA_TEXT_POST_ID, item.getPostid());
                    intent.putExtra(Post.EXTRA_TEXT_POST_TITLE, item.getKind());
                    intent.putExtra(Post.EXTRA_TEXT_POST_POST, item.getBody());
                    intent.putExtra(Post.EXTRA_TEXT_FB_ID, item.getFbid());
                    intent.putExtra(Post.EXTRA_TEXT_FB_USERNAME, item.getUsername());
                    intent.putExtra(Post.EXTRA_TEXT_LNG, item.getLng());
                    intent.putExtra(Post.EXTRA_TEXT_LAT, item.getLat());
                    startActivity(intent);
                }
            });*/
                    /*String mDrawableName = doc.get("title").toString() + "icon";
                    if (mDrawableName.equals("icon")) {
                        mDrawableName = "movieicon";
                    }
                    int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
                    Drawable dr = ContextCompat.getDrawable(getApplicationContext(), resID);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();*/
            /*String profileID = item.getFbid();
            imageViews.get(i).setProfileId(profileID);
            i++;
            //Log.d("elaela", item.getID());
        }*/

    }

}
