package com.example.stakers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "DataUserActivity";
    SharedPreferences pref;
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    private TextView dsEmailText,dsDobText,dsGenderText;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private String userID;
    private Uri uri;
    private ImageView userIV;
    private FirebaseStorage storage;
    private StorageReference storageReference, ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode

        //UI Initialization
        dsEmailText = findViewById(R.id.rcEmailText);
        dsDobText = findViewById(R.id.rcDobText);
        dsGenderText = findViewById(R.id.rcGenderText);
        userIV = findViewById(R.id.userIV);

        //Database initialization
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        userID = mUser.getUid();

        //Database Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Users").child("user_image_path");

        mDatabase.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userActivity = dataSnapshot.getValue(User.class);

                if (userActivity != null) {
                    String ImageUri = userActivity.getUser_image_path();
                    String email = userActivity.getEmail();
                    String dob = userActivity.getDob();
                    String gender = userActivity.getGender();

                    Glide.with(getApplicationContext()).load(ImageUri).into(userIV);
                    //Picasso.get().load(ImageUri).into(userIV);
                    dsEmailText.setText(email);
                    dsDobText.setText(dob);
                    dsGenderText.setText(gender);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        //create route to EditUser layout
        Button editUser_bnt = findViewById(R.id.editUser_bnt);
        editUser_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditUserActivity.class));
            }
        });

        //create route to back profile layout
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
            }
        });
    }
}