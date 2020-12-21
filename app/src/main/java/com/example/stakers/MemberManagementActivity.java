package com.example.stakers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;

public class MemberManagementActivity extends AppCompatActivity {
    RecyclerView recycler_memberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_management);

        recycler_memberView = findViewById(R.id.recycler_memberView);
        recycler_memberView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_memberView.setHasFixedSize(true);


    }
}