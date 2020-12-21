package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashAcitvity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    Handler handler;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    SharedPreferences pref;
    String st_email,st_password, st_dob, st_gender, st_profilepic, uid;
    boolean isLogin, checkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Database initialization
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode


        //to set handler time loading for next step
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pref.getBoolean(pref_islogin, false)) { //check if user is login
                    startActivity(new Intent(SplashAcitvity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashAcitvity.this, LoginActivity.class));
                }
            }//run
        }, 1000);


        // Hide both the navigation bar and the status bar.
        View decorView = getWindow().getDecorView();
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }//onCreate

}