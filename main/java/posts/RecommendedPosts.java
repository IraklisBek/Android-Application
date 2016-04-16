package com.example.irakl_000.maps.posts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.facebook.login.widget.ProfilePictureView;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendedPosts extends AppCompatActivity {

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

    private void arrangedChildView(RelativeLayout relativeLayoutWithView){
        final Intent intent = getIntent();
        ArrayList<RecommendedItems> post = intent.getParcelableArrayListExtra("oOo");
        final HashMap<Integer, TextView> kindViews = new HashMap<>();
        final HashMap<Integer, TextView> textViews = new HashMap<>();
        final HashMap<Integer,ProfilePictureView> imageViews = new HashMap<>();
        final HashMap<Integer, View> lines = new HashMap<>();
        int i=0;

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
                    Intent intent2 = new Intent(RecommendedPosts.this, UserProfile.class);
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
                    Intent intent = new Intent(RecommendedPosts.this, PostPopUp.class);
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
                    Intent intent = new Intent(RecommendedPosts.this, PostPopUp.class);
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
                    /*String mDrawableName = doc.get("title").toString() + "icon";
                    if (mDrawableName.equals("icon")) {
                        mDrawableName = "movieicon";
                    }
                    int resID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
                    Drawable dr = ContextCompat.getDrawable(getApplicationContext(), resID);
                    Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();*/
            String profileID = item.getFbid();
            imageViews.get(i).setProfileId(profileID);
            i++;
            //Log.d("elaela", item.getID());
        }

        for(int ii=1; ii<=textViews.keySet().size(); ii++) {
            RelativeLayout.LayoutParams paramsLine = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            RelativeLayout.LayoutParams paramsImg = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams paramsKind = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(ii-1==0) {
                paramsKind.setMargins(6, 6, 6, 16);
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
    }

}
