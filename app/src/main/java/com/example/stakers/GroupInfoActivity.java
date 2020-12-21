package com.example.stakers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.stakers.utility.GroupObject;
import com.example.stakers.utility.MemberApproval;
import com.example.stakers.utility.StakerMember;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

public class GroupInfoActivity extends AppCompatActivity {
    private static final String TAG = "GroupInfoActivity";
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    DatabaseReference databaseReference, groupRef, groupApproval;
    SharedPreferences pref;
    private float member;
    private BigDecimal avg_price;
    int member_info;
    private ConstraintLayout submitRequestLayout;
    private ConstraintLayout cancelRequestLayout;
    private int currentMember;
    private String receiverEmail;
    RelativeLayout gInfoLoading;
    TextView isFullTV;
    ImageView moreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        groupRef = databaseReference.child("Group");
        groupApproval = databaseReference.child("Member_approval");

        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode
        final String currentUserEmail = pref.getString(pref_email, "");
        final String currentUserPic = pref.getString(pref_userimage, "");

        //Init view
        final ImageView group_coverInfo = findViewById(R.id.group_coverInfo);
        final TextView groupTimestampTextInfo = findViewById(R.id.groupTimestampTextInfo);
        final TextView group_nameTextInfo = findViewById(R.id.group_nameTextInfo);
        final TextView group_DescriptionTextInfo = findViewById(R.id.group_DescriptionTextInfo);
        final TextView group_typeTextInfo = findViewById(R.id.group_typeTextInfo);
        final TextView group_packageTextInfo = findViewById(R.id.group_packageTextInfo);
        final ImageView master_profile = findViewById(R.id.master_profile);
        final TextView group_ownerTextInfo = findViewById(R.id.group_ownerTextInfo);
        final TextView staker_current_memberInfo = findViewById(R.id.staker_current_memberInfo);
        final TextView staker_max_memberInfo = findViewById(R.id.staker_max_memberInfo);
        final TextView group_priceTextInfo = findViewById(R.id.group_priceTextInfo);
        final ImageView group_img1 = findViewById(R.id.group_img1);
        final ImageView group_img2 = findViewById(R.id.group_img2);
        final ImageView group_img3 = findViewById(R.id.group_img3);
        final ImageView isMasterInfo = findViewById(R.id.isMasterInfo);
        final ImageView isMemberInfo = findViewById(R.id.isMemberInfo);
        ImageView MG_backBtn = findViewById(R.id.MG_backBtn);
        final ImageView isPendingInfo = findViewById(R.id.isPendingInfo);
        moreBtn = findViewById(R.id.MG_moreBtn);

        gInfoLoading = findViewById(R.id.gInfoLoading);
        gInfoLoading.setVisibility(View.GONE);

        isFullTV = findViewById(R.id.isFullTV);
        isFullTV.setVisibility(View.GONE);

        submitRequestLayout = findViewById(R.id.submitRequestLayout);
        submitRequestLayout.setVisibility(View.GONE);

        cancelRequestLayout = findViewById(R.id.cancelRequestLayout);
        cancelRequestLayout.setVisibility(View.GONE);

        final ConstraintLayout memberLayout = findViewById(R.id.memberLayout);
        memberLayout.setVisibility(View.GONE);
        Button memberBtn = findViewById(R.id.memberBtn);
        Button manageMemberBtn = findViewById(R.id.manageMemberBtn);
        manageMemberBtn.setVisibility(View.GONE);

        isMasterInfo.setVisibility(View.GONE);
        isMemberInfo.setVisibility(View.GONE);
        isPendingInfo.setVisibility(View.GONE);

        //get intent from card view
        GroupObject groupObject = (GroupObject) getIntent().getSerializableExtra("GroupObject");
        Log.e(TAG, String.valueOf(groupObject));
        final String groupName = groupObject.getGroupName();
        String timestamp = groupObject.getTimestamp();
        final String picCover = groupObject.getPic_cover();
        String groupDetail = groupObject.getGroupDetail();
        String groupType = groupObject.getGroupType();
        String groupPackage = groupObject.getGroupPackage();
        final String groupSize = String.valueOf((int)groupObject.getGroupSize());
        String pic1 = groupObject.getPic_img1();
        String pic2 = groupObject.getPic_img2();
        String pic3 = groupObject.getPic_img3();

        Glide.with(getApplicationContext()).load(picCover).into(group_coverInfo);
        groupTimestampTextInfo.setText(timestamp);
        group_nameTextInfo.setText(groupName);
        group_DescriptionTextInfo.setText(groupDetail);
        group_typeTextInfo.setText(groupType);
        group_packageTextInfo.setText(groupPackage);
        staker_max_memberInfo.setText(groupSize);

        if (pic1 != null) {
            Glide.with(getApplicationContext()).load(pic1).into(group_img1);
        }
        if (pic2 != null) {
            Glide.with(getApplicationContext()).load(pic2).into(group_img2);
        }
        if (pic3 != null) {
            Glide.with(getApplicationContext()).load(pic3).into(group_img3);
        }

        for (StakerMember stakerMember : groupObject.getStakerMember()){
            currentMember = groupObject.getStakerMember().size();
            Log.e(TAG, String.valueOf(currentMember));
            staker_current_memberInfo.setText(String.valueOf(currentMember) + "/");
            calPrice(groupObject.getGroupPrice());
            group_priceTextInfo.setText(String.valueOf(avg_price));
            if (stakerMember.isMaster()) {
                    Glide.with(getApplicationContext()).load(stakerMember.getUserPic()).into(master_profile);
                    receiverEmail = stakerMember.getUserEmail();
                    group_ownerTextInfo.setText(receiverEmail);

            }//isStakerMaster
            if (currentMember >= Integer.parseInt(groupSize)) {
                isFullTV.setVisibility(View.VISIBLE);
            } //isGroupFull
            if(stakerMember.getUserEmail().equalsIgnoreCase(currentUserEmail)){
                if (stakerMember.isMaster()){//isMaster
                    memberLayout.setVisibility(View.VISIBLE);
                } else {
                    memberLayout.setVisibility(View.GONE);
                }//isMaster
                isMemberInfo.setVisibility(View.VISIBLE);
            } else  {
                isMemberInfo.setVisibility(View.GONE);
                Query query = groupApproval.orderByChild("senderEmail").equalTo(currentUserEmail);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                        if (snapshot.child("senderEmail").getValue() == null) {
                            //ยังไม่ส่งคำขอเข้ากลุ่ม
                            //Set Submit Request Button for non-members
                            submitRequestLayout.setVisibility(View.VISIBLE);
                            Button submitRequestBtn = findViewById(R.id.submitRequestBtn);
                            submitRequestBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Member approval is pending
                                    gInfoLoading.setVisibility(View.VISIBLE);
                                    submitRequestLayout.setVisibility(View.INVISIBLE);
                                    if (currentMember <= Integer.parseInt(groupSize)) {
                                        String approveUid = groupApproval.push().getKey();
                                        Long groupTS = System.currentTimeMillis()/1000;
                                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                                        cal.setTimeInMillis(groupTS * 1000L);
                                        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                                        int typeNotify = 1;
                                        MemberApproval memberApproval = new MemberApproval(approveUid, receiverEmail, currentUserEmail, currentUserPic, groupName, date, picCover, typeNotify);
                                        groupApproval.child(approveUid).setValue(memberApproval).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "คำขอเข้ากลุ่ม " + groupName + " ของคุณได้ถูกส่งไปยังหัวหน้ากลุ่มแล้ว", Toast.LENGTH_SHORT).show();
                                                gInfoLoading.setVisibility(View.GONE);
//                                                isPendingInfo.setVisibility(View.VISIBLE);
//                                                cancelRequestLayout.setVisibility(View.VISIBLE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "คำขอเข้ากลุ่มของคุณผิดพลาด กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                                                gInfoLoading.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "กลุ่ม " + groupName + " เต็มแล้ว!", Toast.LENGTH_SHORT).show();
                                        gInfoLoading.setVisibility(View.GONE);
                                    }
                                    //gInfoLoading.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            //ส่งคำขอเข้ากลุ่มแล้ว
                            //Set Cancel Request Button for non-members
                            isPendingInfo.setVisibility(View.VISIBLE);
                            cancelRequestLayout.setVisibility(View.VISIBLE);
                            Button cancelRequestBtn = findViewById(R.id.cancelRequestBtn);
                            cancelRequestBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gInfoLoading.setVisibility(View.VISIBLE);
                                    cancelRequestLayout.setVisibility(View.INVISIBLE);
                                    isPendingInfo.setVisibility(View.INVISIBLE);

                                    //Cancel member approval
                                    if (snapshot.child("senderEmail").getValue() != null) {
                                        Log.e("CancelRequest", "CancelRequest: onDataChange");
                                        snapshot.getRef().removeValue();
                                        Toast.makeText(getApplicationContext(), "ยกเลิกคำขอของคุณแล้ว", Toast.LENGTH_SHORT).show();
                                        submitRequestLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        Log.e("CancelRequest", "CancelRequest: onDataChange " + snapshot.toString());
                                    }
                                    gInfoLoading.setVisibility(View.GONE);
                                }
                            });
                            cancelRequestLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }//isMember
        }

        MG_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupInfoActivity.this, MainActivity.class));
            }
        });

    }

    private void calPrice(float groupPrice) {
        switch (currentMember) {
            case 1:
                member = 1;
                break;
            case 2:
                member = 2;
                break;
            case 3:
                member = 3;
                break;
            case 4:
                member = 4;
                break;
            case 5:
                member = 5;
                break;
            case 6:
                member = 6;
                break;
        }
        float result = groupPrice / member;
        avg_price = round(result, 2);
    } //calPrice()

    private static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    } //BigDecimal()
}
