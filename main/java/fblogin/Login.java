package com.example.irakl_000.maps.fblogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.irakl_000.maps.R;
import com.example.irakl_000.maps.maps.MapStateManager;
import com.example.irakl_000.maps.maps.MapsActivity;
import com.example.irakl_000.maps.server.ServerRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Map;

public class Login extends AppCompatActivity {
    private TextView textDet;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> callBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();

            Profile profile = getProfile();
            TextView tv = (TextView) findViewById(R.id.user_det);
            if(profile!=null){
                tv.setText("Welcome " + profile.getName());
            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testactivityyy", "In the onCreate() event");
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.fragment_login);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions("user_photos");
        //loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, callBack);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("testactivityyy", "In the onActivityResult() event");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Profile profile = getProfile();
        if(profile!=null){
            ServerRequest serverRequest00 = new ServerRequest(this);
            MapStateManager msm = new MapStateManager(this);
            serverRequest00.storeFbUserInBackground(getProfile().getId(), getProfile().getName(), "", "", "", "", msm.getUserPosition(this));
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("testactivityyy", "In the onDestroy() event");
        Profile profile = getProfile();
        if(profile==null){
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop(){
        Log.d("testactivityyy", "In the onStop() event");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("testactivityyy", "In the onPause() event");
        super.onPause();

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("testactivityyy", "In the onReStart() event");
    }

    @Override
    protected void onStart() {
        Log.d("testactivityyy", "In the onStart() event");
        super.onStart();
        Profile profile = getProfile();
        if(profile!=null){
            TextView tv = (TextView) findViewById(R.id.user_det);
            tv.setText("Welcome " + profile.getName());
            ServerRequest serverRequest00 = new ServerRequest(this);
            MapStateManager msm = new MapStateManager(this);
            serverRequest00.storeFbUserInBackground(getProfile().getId(), getProfile().getName(), "", "", "", "", msm.getUserPosition(this));
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    public Profile getProfile(){
        Profile profile = Profile.getCurrentProfile();
        return profile;
    }


}
