package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.stakers.utility.GroupObject;
import com.example.stakers.utility.MemberApproval;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recyclerNotifyView;
    private static final String TAG = "NotificationActivity";
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    SharedPreferences pref;
    DatabaseReference databaseReference, approvalRef, groupRef;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerNotifyView = findViewById(R.id.recyclerNotifyView);
        recyclerNotifyView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerNotifyView.setHasFixedSize(true);

        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode
        String currentUserEmail = pref.getString(pref_email, "");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        approvalRef = databaseReference.child("Member_approval");
        groupRef = databaseReference.child("Group");

        loadNotification(currentUserEmail);

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

        //create route to User layout
        ImageView userBtn = findViewById(R.id.h_userImg);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
            }
        });
    }

    private void loadNotification(String data) {
        //search2 Query data from Member_approval
        Query query = approvalRef.orderByChild("receiverEmail").equalTo(data);
        //Query from class Member_approval pass Get Set
        //Log.e("loadNotification",data);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final ArrayList<MemberApproval> list = new ArrayList();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    MemberApproval memberApproval = postSnapshot.getValue(MemberApproval.class);
                    if (memberApproval != null) {
                        Log.e("loadNotification", memberApproval.getGroupName());
                        list.add(memberApproval);
                    }
                    Query query2 = groupRef.orderByChild("groupName").equalTo(memberApproval.getGroupName());
                    query2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            ArrayList<GroupObject> list2 = new ArrayList();
                            for (DataSnapshot postSnapshot2: snapshot2.getChildren()) {
                                GroupObject groupObject = postSnapshot2.getValue(GroupObject.class);
                                if (groupObject != null) {
                                    list2.add(groupObject);
                                }
                            }
                            adapter = new NotificationAdapter(list, list2);
                            recyclerNotifyView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadNotification", "loadNotification:onCancelled");
            }
        });
        recyclerNotifyView.setAdapter(adapter);
    }
}