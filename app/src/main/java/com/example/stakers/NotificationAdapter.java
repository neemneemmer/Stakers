package com.example.stakers;

import android.content.SharedPreferences;
import android.text.Html;
import android.text.format.DateFormat;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.core.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
                ArrayList<MemberApproval> memberApprovals;
                private ArrayList<GroupObject> groupDataSet;
                SharedPreferences pref;
                private static final String pref_uid = "uidKey";
                private static final String pref_email = "emailKey";
                private static final String pref_dateofbirth = "dobKey";
                private static final String pref_gender = "genderKey";
                private static final String pref_userimage = "userImageKey";
                private static final String pref_islogin = "isLoginKey";
                DatabaseReference databaseReference, groupApproval, groupRef;

                public NotificationAdapter() {

                }

                public NotificationAdapter(ArrayList<MemberApproval> memberApprovals, ArrayList<GroupObject> groupDataSet) {
                                this.memberApprovals = memberApprovals;
                                this.groupDataSet = groupDataSet;
                }
                @NonNull
                @Override
                public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                                View listItem = layoutInflater.inflate(R.layout.invite_group_template, parent, false);
                                NotificationViewHolder viewHolder = new NotificationViewHolder(listItem);

                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                groupApproval = databaseReference.child("Member_approval");
                                groupRef = databaseReference.child("Group");
                                pref = listItem.getContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode

                                return viewHolder;
                }

                @Override
                public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
                                final MemberApproval memberApproval = memberApprovals.get(position);
                                final String currentUserEmail = pref.getString(pref_email, "");
                                final String currentUserPic = pref.getString(pref_userimage, "");
                                final String groupName = memberApproval.getGroupName();
                                final String receiverEmail = memberApproval.getReceiverEmail();
                                final String senderEmail = memberApproval.getReceiverEmail();
                                final String senderPic = memberApproval.getSenderPic();
                                final String timestamp = memberApproval.getTimestamp();
                                final String groupCoverPic = memberApproval.getGroupPicCover();
                                final int typeNotify = memberApproval.getTypeNotify();
                                final String groupUid = memberApproval.getGroupUid();
//                                String approvalKey = groupApproval.push().getKey();
//                                Log.e("approvalKey", approvalKey);
                                String textDetail1 = "<span>" + senderEmail + "</span>" + "<span style='color:#A9008D;'> ได้พยายามขอเข้าร่วมกลุ่ม </span>" + "<span>" + groupName + "</span>";
                                String textDetail2 = "<span style='color:#A9008D;'> การขอเข้าร่วมกลุ่ม </span>" + "<span>" + groupName + "</span>" + "<span style='color:#A9008D;'> ของคุณได้รับการอนุมัติแล้ว </span>";
                                String textDetail3 = "<span>" + senderEmail + "</span><span style='color:#A9008D;'> ได้เข้าร่วมกลุ่ม </span>" + "<span>" + groupName + "</span><span style='color:#A9008D;'> แล้ว</span>";
                                //หัวหน้ากลุ่ม
                                if (receiverEmail.equals(currentUserEmail)) {
                                                //มีคนขอเข้าร่วม
                                                if (typeNotify == 1) {
                                                                holder.notifyTypeTV.setText("คำเชิญเข้ากลุ่ม");
                                                                Picasso.get().load(senderPic).into(holder.senderPicNoti);
                                                                holder.senderEmailNoti.setText(Html.fromHtml(textDetail1));
                                                                holder.timestampTextNoti.setText(timestamp);
                                                                holder.toGroupBtn.setVisibility(View.GONE);
                                                                holder.acceptBtn.setVisibility(View.VISIBLE);
                                                                holder.denyBtn.setVisibility(View.VISIBLE);
                                                                holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                                int typeNotifyNew = 2;
                                                                                                Long groupTS = System.currentTimeMillis()/1000;
                                                                                                Calendar cal = Calendar.getInstance(Locale.getDefault());
                                                                                                cal.setTimeInMillis(groupTS * 1000L);
                                                                                                String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                                                                                                groupApproval.child(groupUid).child("timestamp").setValue(date);
                                                                                                groupApproval.child(groupUid).child("typeNotify").setValue(typeNotifyNew);
//                                                                                                for (GroupObject groupObject : groupDataSet) {
//                                                                                                                List<StakerMember> listMember = new ArrayList<>();
//                                                                                                                StakerMember stakerMember = new StakerMember(false, senderEmail, senderPic);
//                                                                                                                int index = groupObject.getStakerMember().size();
//                                                                                                                Log.d("index: XXX", String.valueOf(index));
//                                                                                                                listMember.add(groupObject.getStakerMember())
//                                                                                                                listMember.add(stakerMember);
//                                                                                                                groupRef.child(groupObject.getGroupUid()).child("stakerMember").setValue(listMember);
//                                                                                                }

                                                                                }
                                                                });
                                                                holder.denyBtn.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                                groupApproval.removeValue();
                                                                                }
                                                                });
                                                } else if (typeNotify == 2) {
                                                                //Do nothing
                                                                holder.notifyTypeTV.setText("อนุมัติเรียบร้อย");
                                                                Picasso.get().load(groupCoverPic).into(holder.senderPicNoti);
                                                                holder.senderEmailNoti.setText(Html.fromHtml(textDetail3));
                                                                holder.timestampTextNoti.setText(timestamp);
                                                                holder.toGroupBtn.setVisibility(View.VISIBLE);
                                                                holder.acceptBtn.setVisibility(View.GONE);
                                                                holder.denyBtn.setVisibility(View.GONE);
                                                }
                                }else if (senderEmail.equals(currentUserEmail)) {
                                                //คนขอเข้า
                                                if (typeNotify == 1) {
                                                                //รออนุมัติ
                                                }else if (typeNotify == 2) {
                                                                //อนุมัติแล้ว
                                                                holder.notifyTypeTV.setText("การอนุมัติ");
                                                                Picasso.get().load(groupCoverPic).into(holder.senderPicNoti);
                                                                holder.senderEmailNoti.setText(Html.fromHtml(textDetail2));
                                                                holder.timestampTextNoti.setText(timestamp);
                                                                holder.toGroupBtn.setVisibility(View.VISIBLE);
                                                                holder.acceptBtn.setVisibility(View.GONE);
                                                                holder.denyBtn.setVisibility(View.GONE);
                                                }
                                }
                }

                @Override
                public int getItemCount() {
                                return memberApprovals.size();
                }
}
