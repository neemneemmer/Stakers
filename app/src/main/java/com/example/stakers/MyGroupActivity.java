package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stakers.utility.GroupObject;
import com.example.stakers.utility.StakerMember;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyGroupActivity extends AppCompatActivity {
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    SharedPreferences pref;
    RecyclerView m_recyclerView;
    MyGroupAdapter myGroupAdapter;
    DatabaseReference databaseReference;
    DatabaseReference groupRef;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        groupRef = databaseReference.child("Group");

        m_recyclerView = findViewById(R.id.m_recyclerView);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        m_recyclerView.setHasFixedSize(true);

        textView = findViewById(R.id.noJoinedGroupTV);
        textView.setVisibility(View.VISIBLE);

        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode
        String currentUserEmail = pref.getString(pref_email, "");
        LoadData(currentUserEmail);

        //to create group layout
        Button createGroupBtn = findViewById(R.id.createGroupBtn);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateGroupActivity.class));
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

        //create route to Notification layout
        ImageView notificationBtn = findViewById(R.id.h_notiImg);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });

        //create route to User layout
        ImageView userBtn = findViewById(R.id.h_userImg);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
            }
        });
    }

    private void LoadData(final String data) {
        Query query;
        query = groupRef.orderByChild("stakerMember");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("loadMyGroupPost", "loadMyGroupPost:onDataChange");
                ArrayList<GroupObject> list = new ArrayList();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    GroupObject groupObject = postSnapshot.getValue(GroupObject.class);
                    if (groupObject != null) {
                        Log.e("loadMyGroupPost", String.valueOf(groupObject.getStakerMember()));
                        for(StakerMember memList : groupObject.getStakerMember()){
                            if(memList.getUserEmail().equals(data)){
                                list.add(groupObject);
                            } else {
                                Log.e("loadMyGroupPost", groupObject.getGroupName() + " doesn't have him as a member");
                            }
                            if (list.size() != 0) {
                                textView.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        Log.e("loadMyGroupPost", "cannot getGroupName()");
                    }
                }
                myGroupAdapter = new MyGroupAdapter(list);
                m_recyclerView.setAdapter(myGroupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        m_recyclerView.setAdapter(myGroupAdapter);
    }
}