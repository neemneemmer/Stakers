package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stakers.utility.StakerMember;
import com.example.stakers.utility.GroupObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.stakers.R.drawable.error;

public class CreateGroupActivity extends AppCompatActivity {
    private static final String TAG = "CreateGroup";
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    private DatabaseReference groupDatabase;
    private StorageReference storageReference, ref0, ref1, ref2, ref3;
    SharedPreferences pref;
    String st_email, st_dob, st_gender, st_profilepic, groupOwnerUid;
    private Uri imageCover_Uri, imagePic1_Uri, imagePic2_Uri, imagePic3_Uri;
    ArrayAdapter<CharSequence> adapter_type, adapter_package, adapter_mem;
    Spinner spinner_type, spinner_des, spinner_mem;
    LinearLayout group_cover_btn;
    Object type_stream, des_stream, mem_stream;
    ImageView group_cover_preview,group_img1, group_img2, group_img3, backBtn;
    Drawable errorIcon;
    Button createGroupBtn;
    TextView showPrice;
    EditText groupNameET, groupDesET;
    String groupUid, group_app, group_package, group_name, group_des, pic_cover, pic_image1, pic_image2, pic_image3;
    boolean isLogin;
    int member_size;
    private float member, price;
    BigDecimal avg_price;
    RelativeLayout relLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        getSharedPreferences();

        //Init error Icon
        errorIcon = getDrawable(error);
        group_cover_btn = findViewById(R.id.group_coverBtn);
        group_img1 = findViewById(R.id.group_img1);
        group_img2 = findViewById(R.id.group_img2);
        group_img3 = findViewById(R.id.group_img3);
        groupNameET = findViewById(R.id.group_nameET);
        groupDesET = findViewById(R.id.group_desET);
        group_cover_preview = findViewById(R.id.group_coverIV);
        createGroupBtn = findViewById(R.id.createSTKGroupBtn);
        relLoading = findViewById(R.id.relLoading);
        relLoading.setVisibility(View.GONE);

        groupDatabase = FirebaseDatabase.getInstance().getReference().child("Group");

        //Set 1st spinner - Streaming App
        spinner_type = findViewById(R.id.streaming_typeSpn);
        adapter_type = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.streaming_type,
                R.layout.support_simple_spinner_dropdown_item);
        adapter_type.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_type.setAdapter(adapter_type);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { //streaming app is selected
                type_stream = spinner_type.getItemAtPosition(position);
                //group_app = type_stream.toString();
                setPackage(position); // check what is selected on PACKAGE spinner

                //Set 2nd spinner - PACKAGE
                spinner_des = findViewById(R.id.streaming_desSpn);
                adapter_package.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_des.setAdapter(adapter_package);
                spinner_des.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { //PACKAGE is selected
                        des_stream = spinner_des.getItemAtPosition(position);
                        setMember(position); // check what is selected on MEMBER spinner

                        //Set 3rd spinner - Member
                        spinner_mem = findViewById(R.id.streaming_memSpn);
                        adapter_mem.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spinner_mem.setAdapter(adapter_mem);
                        spinner_mem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mem_stream = spinner_mem.getItemAtPosition(position);
                                showPrice = findViewById(R.id.streaming_showPriceTV);
                                calPrice(); //calculate price
                                showPrice.setText(String.valueOf(avg_price));
                            } //MEMBER onItemSelected()
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                //Toast.makeText(getApplicationContext(), "กรุณาเลือกจำนวนสมาชิกที่ท่านต้องการ", Toast.LENGTH_LONG).show();
                            } //MEMBER onNothingSelected()
                        });
                    } //PACKAGE onItemSelected()
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getApplicationContext(), "กรุณาเลือกแพ็คเกจที่ท่านต้องการ", Toast.LENGTH_LONG).show();
                    } //PACKAGE onNothingSelected()
                }); //Spinner - PACKAGE
            } //TYPE onItemSelected()
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "กรุณาเลือก Streaming App ที่ท่านต้องการ", Toast.LENGTH_LONG).show();
            } //TYPE onNothingSelected()
        }); //Spinner - TYPE

        //TO SET GROUP IMAGE
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("group_images");
        //set image to group cover
        group_cover_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture(0);
                //group_cover_preview.setImageURI(imageCover_Uri);

            }
        }); //set button to select group cover's image
        //set 1st image to image1
        group_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture(1);
                //group_img1.setImageURI(imagePic1_Uri);
            }
        });
        //set 2nd image to image2
        group_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture(2);
                //group_img2.setImageURI(imagePic2_Uri);
            }
        });
        //set 3nd image to image3
        group_img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture(3);
                //group_img3.setImageURI(imagePic3_Uri);
            }
        });

        //Button
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLoading.setVisibility(View.VISIBLE);
                if (checkCreateGroupInfo()) {
                    if (imageCover_Uri != null) {
                        uploadCoverPicture(imageCover_Uri);
                    } else {
                        Toast.makeText(getApplicationContext(), "กรุณาเลือกรูปภาพหน้าปก", Toast.LENGTH_SHORT).show();
                        relLoading.setVisibility(View.GONE);
                    }

                    if (imagePic1_Uri != null) {
                        uploadFirstPicture(imagePic1_Uri);
                    } else {
                        pic_image1 = null;
                    }
                    if (imagePic2_Uri != null) {
                        uploadSecondPicture(imagePic2_Uri);
                    } else {
                        pic_image2 = null;
                    }
                    if (imagePic3_Uri != null) {
                        uploadThirdPicture(imagePic3_Uri);
                    } else {
                        pic_image3 = null;
                    }
                    createGroup(group_app, group_package, member, price, group_name, group_des, pic_cover, pic_image1, pic_image2, pic_image3, st_email);
                    Toast.makeText(CreateGroupActivity.this, "สร้างกลุ่มเสร็จสิ้น!", Toast .LENGTH_SHORT).show();
                    relLoading.setVisibility(View.GONE);
                    startActivity(new Intent(CreateGroupActivity.this, MyGroupActivity.class));
                    //startActivity() to new group info
                } else {
                    relLoading.setVisibility(View.GONE);
                    Toast.makeText(CreateGroupActivity.this, "ท่านกรอกข้อมูลไม่ครบถ้วน กรุณาตรวจสอบอีกครั้ง", Toast .LENGTH_SHORT).show();
                }
            }
        }); //Click to create group
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyGroupActivity.class));
            }
        }); //Back Button

        // Hide both the status bar.
        View decorView = getWindow().getDecorView();
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    } //onCreate()

    private void choosePicture(int type) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (type == 0) {
            startActivityForResult(intent,0);
        } else if (type == 1) {
            startActivityForResult(intent,1);
        } else if (type == 2) {
            startActivityForResult(intent,2);
        } else if (type == 3) {
            startActivityForResult(intent,3);
        }
    } //choosePicture()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageCover_Uri = data.getData();
            if (imageCover_Uri != null) {
                group_cover_preview.setImageURI(imageCover_Uri);
            }
        } else if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imagePic1_Uri = data.getData();
            if (imagePic1_Uri != null) {
                group_img1.setImageURI(imagePic1_Uri);
            }
        } else if(requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imagePic2_Uri = data.getData();
            if (imagePic2_Uri != null) {
                group_img2.setImageURI(imagePic2_Uri);
            }
        } else if(requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imagePic3_Uri = data.getData();
            if (imagePic3_Uri != null) {
                group_img3.setImageURI(imagePic3_Uri);
            }
        }
    } //onActivityResult()

    private void uploadCoverPicture(Uri uri) {
        // Code for showing progressBar while uploading
        // Defining the child of storageReference
        final String image_uid = groupDatabase.push().getKey();
        ref0 = storageReference.child(image_uid + ".jpg");
        // adding listeners on upload
        // or failure of image
        ref0.putFile(uri)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot) {
                                // Image uploaded successfully
                                // Dismiss dialog
                                storageReference.child(image_uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        pic_cover = uri.toString();
                                        Log.e(TAG, "pic_cover: " + pic_cover);
                                        groupDatabase.child(groupUid).child("pic_cover").setValue(pic_cover);
                                    }
                                });
                                //Toast.makeText(CreateGroupActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                //relLoading.setVisibility(View.GONE);
                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error, Image not uploaded
                        Toast.makeText(CreateGroupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        //relLoading.setVisibility(View.GONE);
                    }
                })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress
                                        = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                //relLoading.setVisibility(View.VISIBLE);
                            }
                        });
        //relLoading.setVisibility(View.VISIBLE);
    } //uploadCoverPicture()

    private void uploadFirstPicture(Uri uri) {
        // Code for showing progressBar while uploading
        // Defining the child of storageReference
        final String image_uid = groupDatabase.push().getKey();
        ref1 = storageReference.child(image_uid + ".jpg");
        // adding listeners on upload
        // or failure of image
        ref1.putFile(uri)
                        .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(
                                                            UploadTask.TaskSnapshot taskSnapshot) {
                                                // Image uploaded successfully
                                                // Dismiss dialog
                                                storageReference.child(image_uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        pic_image1 = uri.toString();
                                                        Log.e(TAG, "pic_image1: " + pic_image1);
                                                        groupDatabase.child(groupUid).child("pic_image1").setValue(pic_image1);
                                                    }
                                                });
                                                //Toast.makeText(CreateGroupActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                                //relLoading.setVisibility(View.GONE);
                                            }
                                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error, Image not uploaded
                                Toast.makeText(CreateGroupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                //relLoading.setVisibility(View.GONE);
                            }
                        })
                        .addOnProgressListener(
                                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                                            // Progress Listener for loading
                                            // percentage on the dialog box
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress
                                                                = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                //relLoading.setVisibility(View.VISIBLE);
                                            }
                                        });
        //relLoading.setVisibility(View.VISIBLE);
    } //uploadFirstPicture()

    private void uploadSecondPicture(Uri uri) {
        // Code for showing progressBar while uploading
        // Defining the child of storageReference
        final String image_uid = groupDatabase.push().getKey();
        ref2 = storageReference.child(image_uid + ".jpg");
        // adding listeners on upload
        // or failure of image
        ref2.putFile(uri)
                        .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(
                                                            UploadTask.TaskSnapshot taskSnapshot) {
                                                // Image uploaded successfully
                                                // Dismiss dialog
                                                storageReference.child(image_uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        pic_image2 = uri.toString();
                                                        Log.e(TAG, "pic_image2: " + pic_image2);
                                                        groupDatabase.child(groupUid).child("pic_image2").setValue(pic_image2);
                                                    }
                                                });
                                                //Toast.makeText(CreateGroupActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                                //relLoading.setVisibility(View.GONE);
                                            }
                                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error, Image not uploaded
                                Toast.makeText(CreateGroupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                //relLoading.setVisibility(View.GONE);
                            }
                        })
                        .addOnProgressListener(
                                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                                            // Progress Listener for loading
                                            // percentage on the dialog box
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress
                                                                = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                //relLoading.setVisibility(View.VISIBLE);
                                            }
                                        });
        //relLoading.setVisibility(View.VISIBLE);
    } //uploadSecondPicture()

    private void uploadThirdPicture(Uri uri) {
        // Code for showing progressBar while uploading
        // Defining the child of storageReference
        final String image_uid = groupDatabase.push().getKey();
        ref3 = storageReference.child(image_uid + ".jpg");
        // adding listeners on upload
        // or failure of image
        ref3.putFile(uri)
                        .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(
                                                            UploadTask.TaskSnapshot taskSnapshot) {
                                                // Image uploaded successfully
                                                // Dismiss dialog
                                                storageReference.child(image_uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        pic_image3 = uri.toString();
                                                        Log.e(TAG, "pic_image3: " + pic_image3);
                                                        groupDatabase.child(groupUid).child("pic_image3").setValue(pic_image3);
                                                    }
                                                });
                                                //Toast.makeText(CreateGroupActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                                //relLoading.setVisibility(View.GONE);
                                            }
                                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error, Image not uploaded
                                Toast.makeText(CreateGroupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                //relLoading.setVisibility(View.GONE);
                            }
                        })
                        .addOnProgressListener(
                                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                                            // Progress Listener for loading
                                            // percentage on the dialog box
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress
                                                                = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                //relLoading.setVisibility(View.VISIBLE);
                                            }
                                        });
        //relLoading.setVisibility(View.VISIBLE);
    } //uploadThirdPicture()

    public void createGroup(final String groupType, final String groupPackage, final float groupSize, final float groupPrice, final String groupName,
                            final String groupDetail, final String pic_cover, final String pic_image1, final String pic_image2, final String pic_image3,
                            final String userEmail) {
        groupUid = groupDatabase.push().getKey();
        Long groupTS = System.currentTimeMillis()/1000;
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(groupTS * 1000L);
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
        List<StakerMember> listMember = new ArrayList<>();
        StakerMember stakerMember = new StakerMember(true, userEmail, st_profilepic);
        listMember.add(stakerMember);
        GroupObject groupObjectInfo = new GroupObject(groupUid, date, groupType, groupPackage, groupSize, groupPrice, groupName, groupDetail, pic_cover, pic_image1, pic_image2, pic_image3, listMember);
        groupDatabase.child(groupUid).setValue(groupObjectInfo);
//        GroupTeam groupTeam = new GroupTeam(groupOwner, ownerPic);
//        groupObjectInfo.setGroupTeam(groupTeam);
    } //createGroup()

    public void getSharedPreferences() {
        //get sharedpreferences
        pref = getApplicationContext().getSharedPreferences("my_prefs", 0); // 0 - for private mode
        if (pref.contains(pref_uid)) { //check if user is login
            Log.d(TAG, "sharedPrefernces contains data!");
            isLogin = pref.getBoolean(pref_islogin, false);
            groupOwnerUid = pref.getString(pref_uid, ""); // getting String
            st_email = pref.getString(pref_email, ""); // getting String
            st_dob = pref.getString(pref_dateofbirth, ""); // getting String
            st_gender = pref.getString(pref_gender, ""); // getting String
            st_profilepic = pref.getString(pref_userimage, ""); // getting String
        }
    } //getSharedPreferences()

    public void calPrice() {
        toCalPrice();
        float result = price / member;
        avg_price = round(result, 2);
    } //calPrice()

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    } //BigDecimal()

    public void toCalPrice() {
        if (type_stream.equals("Netflix")) {
            if (des_stream.equals("Ultra HD(up to 4K) สูงสุด: 4 ท่าน")) {
                if (mem_stream.equals("4")) { // select 4 members
                    member = 4;
                    price = 419;
                } else if (mem_stream.equals("3")) { // select 3 members
                    member = 3;
                    price = 419;
                } else if (mem_stream.equals("2")) { // select 2 members
                    member = 2;
                    price = 419;
                } // Netflix has 4 members
            } else if (des_stream.equals("HD(up to 720p) สูงสุด: 2 ท่าน")) {
                member = 2;
                price = 349;
            } // Netflix has 2 members
        } else if (type_stream.equals("Viu")) {
            if (mem_stream.equals("4")) { // select 4 members
                member = 4;
                price = 119;
            } else if (mem_stream.equals("3")) { // select 3 members
                member = 3;
                price = 119;
            } else if (mem_stream.equals("2")) { // select 2 members
                member = 2;
                price = 119;
            } // Viu has 4 members
        } else if (type_stream.equals("iflix")) {
            if (mem_stream.equals("5")) { // select 5 members
                member = 5;
                price = 100;
            } else if (mem_stream.equals("4")) { // select 4 members
                member = 4;
                price = 100;
            } else if (mem_stream.equals("3")) { // select 3 members
                member = 3;
                price = 100;
            } else if (mem_stream.equals("2")) { // select 2 members
                member = 2;
                price = 100;
            } // iflix has 5 members
        } else if (type_stream.equals("Apple Music") || type_stream.equals("Spotify")) {
            if (mem_stream.equals("6")) { // select 6 members
                member = 6;
                price = 199;
            } else if (mem_stream.equals("5")) { // select 5 members
                member = 5;
                price = 199;
            } else if (mem_stream.equals("4")) { // select 4 members
                member = 4;
                price = 199;
            } else if (mem_stream.equals("3")) { // select 3 members
                member = 3;
                price = 199;
            } else if (mem_stream.equals("2")) { // select 2 members
                member = 2;
                price = 199;
            } // Apple-Spotify have 6 members
        } else if (type_stream.equals("Youtube")) {
            if (mem_stream.equals("6")) { // select 6 members
                member = 6;
                price = 259;
            } else if (mem_stream.equals("5")) { // select 5 members
                member = 5;
                price = 259;
            } else if (mem_stream.equals("4")) { // select 4 members
                member = 4;
                price = 259;
            } else if (mem_stream.equals("3")) { // select 3 members
                member = 3;
                price = 259;
            } else if (mem_stream.equals("2")) { // select 2 members
                member = 2;
                price = 259;
            } // Youtube has 6 members
        } //Type of streaming
    } //toCalPrice

    private void setPackage(int position) {
        //set package
        switch(position) {
            case 0: // Netflix's package
                adapter_package = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.netflix, R.layout.support_simple_spinner_dropdown_item);
                break;
            case 1: // Viu's package
                adapter_package = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.viu, R.layout.support_simple_spinner_dropdown_item);
                break;
            case 2: // iflix's package
                adapter_package = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.iflix, R.layout.support_simple_spinner_dropdown_item);
                break;
            case 3: // Apple Music's package
                adapter_package = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.apple, R.layout.support_simple_spinner_dropdown_item);
                break;
            case 4: // Spotify's package
                adapter_package = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.spotify, R.layout.support_simple_spinner_dropdown_item);
                break;
            case 5: // Youtube's package
                adapter_package = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.youtube, R.layout.support_simple_spinner_dropdown_item);
                break;
            default:
                //adapter_package = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.netflix, R.layout.support_simple_spinner_dropdown_item);
                break;
        }
    }

    private void setMember(int position) {
        //set member
        if (!type_stream.equals("Netflix")) {
            if (!type_stream.equals("Viu")) {
                if (!type_stream.equals("iflix")) {
                    if (type_stream.equals("Apple Music")) { // 6 members
                        adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.sixmembers, R.layout.support_simple_spinner_dropdown_item);
                    } else if (type_stream.equals("Spotify")) { // 6 members
                        adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.sixmembers, R.layout.support_simple_spinner_dropdown_item);

                    } else { // Youtube // 6 members
                        adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.sixmembers, R.layout.support_simple_spinner_dropdown_item);

                    }
                } else { // 5 members
                    adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.fivemembers, R.layout.support_simple_spinner_dropdown_item);

                }
            } else { // 4 members
                adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.fourmembers, R.layout.support_simple_spinner_dropdown_item);

            }
        } else {
            switch(position) {
                case 0: // 2 members
                    adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.twomembers, R.layout.support_simple_spinner_dropdown_item);
                    break;
                case 1: // 4 members
                    adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.fourmembers, R.layout.support_simple_spinner_dropdown_item);
                    break;
                default:
                    //adapter_mem = ArrayAdapter.createFromResource(CreateGroupActivity.this, R.array.twomembers, R.layout.support_simple_spinner_dropdown_item);
                    break;
            }
        }
    }

    public boolean checkCreateGroupInfo() {
        int check = 0;
        group_app = spinner_type.getItemAtPosition(spinner_type.getSelectedItemPosition()).toString();
        group_package = spinner_des.getItemAtPosition(spinner_des.getSelectedItemPosition()).toString();
        group_name = groupNameET.getText().toString().trim();
        group_des = groupDesET.getText().toString().trim();

        if (group_name.isEmpty()) {
            groupNameET.setError("กรุณาตั้งชื่อกลุ่ม");
            check = 1;
        }
        if (group_des.isEmpty()) {
            group_des = "เจ้าของกลุ่มไม่ได้ระบุเงื่อนไข";
        }
        if (group_app.isEmpty()) {
            Log.d(TAG, "group_app is null");
        }

        member_size = Integer.parseInt(spinner_mem.getItemAtPosition(spinner_mem.getSelectedItemPosition()).toString());

        if (st_email == null) {
            Log.d(TAG, "cannot get user email from SharedPreferences");
            check = 1;
        }

        if(check != 0){
            return false;
        }else {
            return true;
        }
    } //checkInfo()
}