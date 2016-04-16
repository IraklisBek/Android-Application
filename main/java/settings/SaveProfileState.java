package com.example.irakl_000.maps.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveProfileState {
    private final static String THE_RATIO="10";
    private final static String MUSIC="MUSIC";
    private final static String PARTY="PARTY";
    private final static String CONCERT="CONCERT";
    private final static String SPORTS="SPORTS";
    private final static String MEETUPS="MEETUPS";
    private final static String CINEMA="CINEMA";
    private final static String SCIENCE="SCIENCE";
    private final static String GENERAL="GENERAL";
    private final static String RECOMMENDED_POSTS="RECOMMENDED_POSTS";
    private final static String ON_DESTROY="ON_DESTROY";
    private final static String CHANGED="CHANGED";
    private SharedPreferences userPrefs;
    private static final String PREFS_NAME="userProfileState3";

    public SaveProfileState(Context context){
        userPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveChangesState(int ratio, boolean music, boolean concert, boolean party, boolean sports, boolean meetups, boolean cinema, boolean science, boolean general, boolean recommendedPosts){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean(CHANGED, true);
        editor.putInt(THE_RATIO, ratio);
        editor.putBoolean(MUSIC, music);
        editor.putBoolean(PARTY, party);
        editor.putBoolean(CONCERT, concert);
        editor.putBoolean(SPORTS, sports);
        editor.putBoolean(MEETUPS, meetups);
        editor.putBoolean(CINEMA, cinema);
        editor.putBoolean(SCIENCE, science);
        editor.putBoolean(GENERAL, general);
        editor.putBoolean(RECOMMENDED_POSTS, recommendedPosts);
        editor.commit();
    }

    public void saveRatio(int ratio){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putInt(THE_RATIO, ratio);
        editor.commit();
    }
    public void setChanged(boolean changed){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean(CHANGED, changed);
        editor.commit();
    }
    public void setDestroy(boolean destroyed){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putBoolean(ON_DESTROY, destroyed);
        editor.commit();
    }
    public  boolean getChanged(){ return userPrefs.getBoolean(ON_DESTROY, false); }

    public  boolean getIfDestroy(){ return userPrefs.getBoolean(ON_DESTROY, false); }

    public int getRatio(){
        return userPrefs.getInt(THE_RATIO, 0);
    }

    public boolean getMusicCheck(){
        return userPrefs.getBoolean(MUSIC, true);
    }

    public boolean getPartyCheck(){
        return userPrefs.getBoolean(PARTY, true);
    }

    public boolean getConcertCheck(){
        return userPrefs.getBoolean(CONCERT, true);
    }

    public boolean getCinemaCheck(){
        return userPrefs.getBoolean(CINEMA, true);
    }

    public boolean getScienceCheck(){
        return userPrefs.getBoolean(SCIENCE, true);
    }

    public boolean getGeneralCheck(){
        return userPrefs.getBoolean(GENERAL, true);
    }

    public boolean getMeetUpsCheck(){
        return userPrefs.getBoolean(MEETUPS, true);
    }

    public boolean getSportsCheck(){
        return userPrefs.getBoolean(SPORTS, true);
    }

    public boolean getRecommendedPostsCheck(){ return userPrefs.getBoolean(RECOMMENDED_POSTS, false); }
}
