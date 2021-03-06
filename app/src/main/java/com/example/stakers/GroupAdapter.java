package com.example.stakers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.stakers.utility.GroupObject;
import com.example.stakers.utility.MemberApproval;
import com.example.stakers.utility.StakerMember;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
                private ArrayList<GroupObject> groupDataSet;
                private ArrayList<MemberApproval> approvalArrayList;
                private static final String pref_uid = "uidKey";
                private static final String pref_email = "emailKey";
                private static final String pref_dateofbirth = "dobKey";
                private static final String pref_gender = "genderKey";
                private static final String pref_userimage = "userImageKey";
                private static final String pref_islogin = "isLoginKey";
                SharedPreferences pref;
                DatabaseReference databaseReference, groupRef, groupApproval;
                StorageReference storageReference;
                private String groupUid;
                private String groupCurrentMember, groupMaster, groupMasterPic;

                public GroupAdapter() {

                }

                public GroupAdapter(ArrayList<GroupObject> groupDataSet, ArrayList<MemberApproval> approvalArrayList) {
                                this.groupDataSet = groupDataSet;
                                this.approvalArrayList = approvalArrayList;
                }

                @Override
                public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                                View listItem = layoutInflater.inflate(R.layout.cardview_template, parent, false);
                                GroupViewHolder viewHolder = new GroupViewHolder(listItem);

                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                groupRef = databaseReference.child("Group");
                                groupApproval = databaseReference.child("Member_approval");
                                storageReference = FirebaseStorage.getInstance().getReference();
                                pref = listItem.getContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode

                                return viewHolder;
                }

                @Override
                public void onBindViewHolder(@NonNull final GroupViewHolder holder, int position) {
                                final GroupObject myGroupData = groupDataSet.get(position);
                                final String currentUserEmail = pref.getString(pref_email, "");
                                int g = 0;

                                Glide.with(holder.view.getContext()).load(myGroupData.getPic_cover()).into(holder.image_single_view);
                                holder.groupNameText.setText(myGroupData.getGroupName());
                                holder.groupSizeText.setText(String.valueOf((int)myGroupData.getGroupSize()));
                                holder.groupTypeText.setText(myGroupData.getGroupType());
                                holder.groupPriceText.setText(String.valueOf((int) myGroupData.getGroupPrice()));
                                //To set Group Master
                                for (StakerMember member : myGroupData.getStakerMember()){
                                                if(member.isMaster()){
                                                                groupMaster = member.getUserEmail();
                                                                groupMasterPic = member.getUserPic();
                                                                holder.groupOwnerText.setText(groupMaster);
                                                                Glide.with(holder.view.getContext()).load(groupMasterPic).into(holder.userIV);
                                                }
                                }
                                holder.timestampText.setText(myGroupData.getTimestamp());

                                //To set maximum member
                                groupCurrentMember = myGroupData.getStakerMember().size() + "/";
                                g = myGroupData.getStakerMember().size();

                                holder.groupMemText.setText(groupCurrentMember);
                                for (StakerMember member : myGroupData.getStakerMember()){
                                                if(member.getUserEmail().equalsIgnoreCase(currentUserEmail)){
                                                                if (member.isMaster()){//isMaster
                                                                                holder.isMaster.setVisibility(View.VISIBLE);
                                                                } else {
                                                                                holder.isMaster.setVisibility(View.GONE);
                                                                }//isMaster
                                                                holder.isMember.setVisibility(View.VISIBLE);
                                                } else  {
                                                                holder.isMember.setVisibility(View.GONE);
                                                }//isMember
                                }

                                //isPending needs to execute here
                                for(MemberApproval memberApproval : approvalArrayList){
                                                if (memberApproval.getSenderEmail().equals(currentUserEmail)) {
                                                                if (memberApproval.getGroupName().equals(myGroupData.getGroupName())) {
                                                                                switch (memberApproval.getTypeNotify()) {
                                                                                                case 1 :
                                                                                                                holder.isPending.setVisibility(View.VISIBLE);
                                                                                                                break;
                                                                                                case 2 :
                                                                                                                holder.isPending.setVisibility(View.GONE);
                                                                                                                break;
                                                                                }
                                                                } else {
                                                                                holder.isPending.setVisibility(View.GONE);
                                                                }
                                                } else {
                                                                holder.isPending.setVisibility(View.GONE);
                                                }
                                }//isPending


                                if ((int)myGroupData.getGroupSize() <= g) {
                                                holder.isFull.setVisibility(View.VISIBLE);
                                } //isFull

                                holder.view.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                                Context mView = view.getContext();
                                                                Intent intent = new Intent(mView, GroupInfoActivity.class);
                                                                intent.putExtra("GroupObject", myGroupData);
                                                                mView.startActivity(intent);
                                                }
                                });
                }

                @Override
                public int getItemCount() {
                                return groupDataSet.size();
                }
}