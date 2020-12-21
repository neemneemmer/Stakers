package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.stakers.utility.GroupObject;
import com.example.stakers.utility.MemberApproval;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    SharedPreferences pref;
    GroupAdapter adapter;
    DatabaseReference databaseReference, groupRef, groupApproval;
    StorageReference storageReference;
    RecyclerView recyclerView;
    ImageView mygroupBtn, notificationBtn, userBtn;
    CardView cv_1, cv_2, cv_3, cv_4, cv_5, cv_6;
    EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        groupRef = databaseReference.child("Group");
        groupApproval = databaseReference.child("Member_approval");
        storageReference = FirebaseStorage.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchET = findViewById(R.id.searchET);
        cv_1 = findViewById(R.id.cv_1);
        cv_2 = findViewById(R.id.cv_2);
        cv_3 = findViewById(R.id.cv_3);
        cv_4 = findViewById(R.id.cv_4);
        cv_5 = findViewById(R.id.cv_5);
        cv_6 = findViewById(R.id.cv_6);

        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode


        //create route to Mygroup layout
        mygroupBtn = findViewById(R.id.h_mygroupImg);
        mygroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyGroupActivity.class));
            }
        });

        //create route to Notification layout
        notificationBtn = findViewById(R.id.h_notiImg);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });

        //create route to User layout
        userBtn = findViewById(R.id.h_userImg);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        LoadData("");

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LoadData(charSequence.toString());
            }

            //check text in input search
            @Override
            public void afterTextChanged(Editable editable) {
//                editable.toString();

                LoadData(editable.toString());

            }
        });

        cv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData("Netflix");
            }
        });

        cv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData("iflix");
            }
        });

        cv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData("Youtube");
            }
        });

        cv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData("Viu");
            }
        });


        cv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData("Spotify");
            }
        });

        cv_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadData("Apple Music");
            }
        });

    }

    private void LoadData(String data) {
        //Query data from Group
        final String currentUserEmail = pref.getString(pref_email, "");
        Query query;
        if (data.equalsIgnoreCase("Netflix") || data.equalsIgnoreCase("iflix") ||
                        ((data.equalsIgnoreCase("Youtube") || data.equalsIgnoreCase("Viu")) ||
                                        data.equalsIgnoreCase("Spotify")) || data.equalsIgnoreCase("Apple Music")) {
            query = groupRef.orderByChild("groupType").equalTo(data);
        } else {
            query = groupRef.orderByChild("groupName").startAt(data).endAt(data + "\uf8ff");
        }

         query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("loadMainPost", "loadMainPost:onDataChange");
                final ArrayList<GroupObject> list = new ArrayList();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    GroupObject groupObject = postSnapshot.getValue(GroupObject.class);
                    //Log.e("loadPost", groupObject.getGroupName());
                    list.add(groupObject);
                }
                Query query2 = groupApproval.orderByChild("senderEmail").equalTo(currentUserEmail);
                query2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<MemberApproval> list2 = new ArrayList();
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            MemberApproval memberApproval = postSnapshot.getValue(MemberApproval.class);
                            list2.add(memberApproval);
                        }
                        adapter = new GroupAdapter(list, list2);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                adapter = new GroupAdapter(list);
//                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadPost", "loadPost:onCancelled");
            }
        });

        recyclerView.setAdapter(adapter);
    }
}