package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stakers.utility.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    SharedPreferences pref;
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    private TextView email_user_tv, logout_tv;
    private ImageView user_img;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private String uid;
    private FirebaseStorage storage;
    private StorageReference StorageRef, ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();

        //UI Initialization
        email_user_tv = findViewById(R.id.email_user_tv);
        logout_tv = findViewById(R.id.logout_tv);
        user_img = findViewById(R.id.user_img);

        //Database initialization
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        uid = mUser.getUid();

        //Database Storage
        storage = FirebaseStorage.getInstance();
        StorageRef = storage.getReference("Users").child("user_image_path");

        mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userActivity = dataSnapshot.getValue(User.class);

                if (userActivity != null) {

                    String ImageUri = dataSnapshot.child("user_image_path").getValue().toString();
                    String email = userActivity.getEmail();

                    Glide.with(getApplicationContext()).load(ImageUri).into(user_img);
                    //Picasso.get().load(ImageUri).into(user_img);
                    email_user_tv.setText(email);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });






        //create route to DataUser layout
        Button dataUser_Btn = findViewById(R.id.dataUser_Btn);
        dataUser_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
            }
        });

        //create route to Home layout
        ImageView homeBtn = findViewById(R.id.h_homeImg);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity = Home Layout
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        //create route to Mygroup layout
        ImageView mygroupBtn = findViewById(R.id.h_mygroupImg);
        mygroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyGroupActivity.class));
            }
        });

        //create route to Notification layout
        ImageView notificationBtn = findViewById(R.id.h_notiImg);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });

        //Logout
        logout_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.apply();
                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "You are signed out, we have missed you", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}