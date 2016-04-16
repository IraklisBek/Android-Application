package com.example.irakl_000.maps.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.irakl_000.maps.fblogin.Login;
import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.maps.MapsActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

public class Profile extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView textView;
    private CheckBox generalPosts, musicPosts, sportsPosts, cinemaPosts, sciencePosts, meetupPosts, partyPosts, concertPosts, recommendedPosts;
    private int ratio;

    private void initializeVariables() {
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        textView = (TextView) findViewById(R.id.textView1);
        musicPosts = (CheckBox) findViewById(R.id.musicOption);
        sportsPosts = (CheckBox) findViewById(R.id.sportsOption);
        cinemaPosts = (CheckBox) findViewById(R.id.cinemaOption);
        sciencePosts = (CheckBox) findViewById(R.id.scienceOption);
        meetupPosts = (CheckBox) findViewById(R.id.meetUpsOption);
        partyPosts = (CheckBox) findViewById(R.id.partyOption);
        concertPosts = (CheckBox) findViewById(R.id.concertOption);
        generalPosts = (CheckBox) findViewById(R.id.noKindOption);
        recommendedPosts = (CheckBox) findViewById(R.id.recommendedBox);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FacebookSdk.sdkInitialize(getApplicationContext());
        com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.settingsPic);
        profilePictureView.setProfileId(profile.getId());
        Log.d("asasas2", profile.getId());
        initializeVariables();
        SaveProfileState sps = new SaveProfileState(this);
        musicPosts.setChecked(sps.getMusicCheck());
        sportsPosts.setChecked(sps.getSportsCheck());
        cinemaPosts.setChecked(sps.getCinemaCheck());
        sciencePosts.setChecked(sps.getScienceCheck());
        meetupPosts.setChecked(sps.getMeetUpsCheck());
        partyPosts.setChecked(sps.getPartyCheck());
        concertPosts.setChecked(sps.getConcertCheck());
        generalPosts.setChecked(sps.getGeneralCheck());
        recommendedPosts.setChecked(sps.getRecommendedPostsCheck());
        int pr;
        if(sps.getRatio()==0){
            pr=10;
        }else{
            pr = sps.getRatio();
        }
        seekBar.setProgress(pr);
        textView.setText("Ratio: " + seekBar.getProgress() + " km");
        ratio=seekBar.getProgress();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                ratio = progressValue;
                SaveProfileState sps = new SaveProfileState(Profile.this);
                sps.saveRatio(ratio);
                //changed();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Ratio: " + ratio + " km");
            }
        });
        saveChangesOnCheckBoxes(generalPosts);
        saveChangesOnCheckBoxes(musicPosts);
        saveChangesOnCheckBoxes(sportsPosts);
        saveChangesOnCheckBoxes(cinemaPosts);
        saveChangesOnCheckBoxes(sciencePosts);
        saveChangesOnCheckBoxes(meetupPosts);
        saveChangesOnCheckBoxes(partyPosts);
        saveChangesOnCheckBoxes(concertPosts);
        saveChangesOnCheckBoxes(recommendedPosts);
        /*final ImageView button = (ImageView) findViewById(R.id.saveChanges);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveChanges();
                Context context = getApplicationContext();
                CharSequence text = "Your changes have been saved";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });*/


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SaveProfileState sps = new SaveProfileState(this);
        //if(sps.getChanged()) {
            sps.setChanged(false);
            sps.setDestroy(true);
        //}
        Log.d("desedw", String.valueOf(sps.getIfDestroy()));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void gotoLogin(View view){
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void saveChangesOnCheckBoxes(CheckBox checkBox){
        final SaveProfileState sps = new SaveProfileState(this);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sps.saveChangesState(ratio, musicPosts.isChecked(), concertPosts.isChecked(), partyPosts.isChecked(), sportsPosts.isChecked(), meetupPosts.isChecked(), cinemaPosts.isChecked(), sciencePosts.isChecked(), generalPosts.isChecked(), recommendedPosts.isChecked());
                //changed();
            }
        });
    }

    public void gotoMaps(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void changed(){
        SharedPreferences userPrefs;
        userPrefs = Profile.this.getSharedPreferences("userProfileState3", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean("CHANGED", true);
        editor.commit();
    }


}
