package com.example.stakers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.stakers.utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class EditUserActivity extends AppCompatActivity {

    private static final String TAG = "EditUserActivity";
    private EditText rDOBET, rEmailET;

    RadioGroup radio_gender;
    RadioButton checkedRadio,male_g,female_g,no_g;
    DatePickerDialog datePicker;
    Calendar myCalendar;
    int mDay, mMonth, mYear;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private String uid, image_uid, st_profilepic, user_image_path, r_genderText, dob, email, gender, profilepic, st_email, st_password, st_dob, st_gender;
    String r_emailText, r_passText, r_con_passText, r_dateText;
    Button rCancelBtn, rSubmitBtn;
    TextView rImageError, rGenderErrTV;
    Uri imageUri;
    User user;
    ImageView user_img;
    boolean isImageAdded = false;
    RelativeLayout rcLoading;

    private FirebaseStorage storage;
    private StorageReference StorageRef, ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        rcLoading = findViewById(R.id.recLoading);
        rcLoading.setVisibility(View.GONE);

        //UI Initialization
        rEmailET = findViewById(R.id.rEmailET);
        rDOBET = findViewById(R.id.rDOBET);
        user_img = findViewById(R.id.user_img);
        male_g = findViewById(R.id.male_g);
        female_g = findViewById(R.id.female_g);
        no_g = findViewById(R.id.no_g);

        //set RadioGroup
        radio_gender = findViewById(R.id.radioGroup);

        //to set user's profile picture
        myCalendar = Calendar.getInstance();

        //Database initialization
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        uid = mUser.getUid();

        //Database Storage
        storage = FirebaseStorage.getInstance();
        StorageRef = storage.getReference().child("profile_picture");

        //selection gender
        radio_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedRadio = group.findViewById(checkedId);
                if (null != checkedRadio) {
                    switch (checkedId) {
                        case R.id.male_g: // gender: Male
                            r_genderText = "ชาย";
                            break;
                        case R.id.female_g: // gender: Female
                            r_genderText = "หญิง";
                            break;
                        case R.id.no_g: // gender: No gender
                            r_genderText = "เพศทางเลือก";
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        //get data
        mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userActivity = dataSnapshot.getValue(User.class);

                if (userActivity != null) {

                    String ImageUri = dataSnapshot.child("user_image_path").getValue().toString();
                    String email = userActivity.getEmail();
                    String dob = userActivity.getDob();

                    Glide.with(getApplicationContext()).load(ImageUri).into(user_img);
                    rEmailET.setText(email);
                    rDOBET.setText(dob);

                    //show gender from firebase
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    if (gender.equalsIgnoreCase("ชาย")){
                        male_g.setChecked(true);
                    } else if (gender.equalsIgnoreCase("หญิง")){
                        female_g.setChecked(true);
                    } else {
                        no_g.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        rDOBET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == rDOBET) {
                    // Get Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);

                    datePicker = new DatePickerDialog(EditUserActivity.this,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int day) {
                                            rDOBET.setText(day + "/" + (month + 1) + "/" + year);
                                        } //onDateSet()
                                    }, mYear, mMonth, mDay);
                    datePicker.show();
                } //if
            } //onClick()
        });

        //create route to DataUser layout
        Button back_bnt = findViewById(R.id.back_bnt);
        back_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
            }
        });

        //create route to Update
        Button submit_bnt = findViewById(R.id.submit_bnt);
        submit_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = rEmailET.getText().toString();
                final String dob = rDOBET.getText().toString();

                //checkRegisInfo();

                //check if

                if (isImageAdded != false && email != null && dob != null) {
                    uploadPicture(imageUri, email, dob, gender);
                }
            }
        });
    }

    //choosePic in Gallery
    private void choosePicture() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 0); //one can be replaced with any action code
    }//choosePicture()

    //Upload All
    private void uploadPicture(Uri imageUri, final String email, final String dob, final String gender) {
        Log.d(TAG, "Start Update");
        rcLoading.setVisibility(View.VISIBLE);

        StorageRef.child(uid + ".jpg").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageRef.child(uid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //put data to firebase
                        HashMap hashMap = new HashMap();
                        hashMap.put("email", email);
                        hashMap.put("dob", dob);
                        hashMap.put("gender", r_genderText);
                        hashMap.put("user_image_path", uri.toString());

                        //updateEmail Password in firebaseUser
                        mUser.updateEmail(email);
                        Log.d(TAG, "Updated");

                        //Update data
                        mDatabase.child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(EditUserActivity.this,UserInfoActivity.class));
                                Toast.makeText(EditUserActivity.this,"Update Data Success" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            //ProgressBar wait update
        }).addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {
                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot) {
                                double progress
                                                = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            }
                        });
    } //uploadProfile()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            //imageUri = data.getData();
            isImageAdded = true;
            user_img.setImageURI(imageUri);
        } else {
            Log.d(TAG, "requestCode error in onActivityResult()");
        }
    }//onActivityResult() check data don't null

    private boolean checkEditUser() {
        r_emailText = rEmailET.getText().toString().trim();
        //r_con_passText = r_con_passET.getText().toString().trim();
        r_dateText = rDOBET.getText().toString().trim();

        int check = 0;
        if (r_emailText.isEmpty()) {
            rEmailET.setError("กรุณาใส่อีเมลของท่าน");
            check = 1;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(r_emailText).matches()) {
                rEmailET.setError("อีเมลผิดพลาด");
                check = 1;
            } else {
            }
        } //check if email is filled

        if (r_dateText.isEmpty()) {
            rDOBET.setError("กรุณากรอกวันเกิดของท่าน");
            check = 1;
        } else {
            rDOBET.setError(null);
        } //check if date of birth is selected

        if (r_genderText == null) {
            rGenderErrTV.setError("กรุณาระบุเพศของท่าน");
            check = 1;
        } else {
            rGenderErrTV.setError(null);
        } //check if gender is selected

        if (imageUri == null) {
            rImageError.setError("กรุณาเลือกรูปภาพของท่าน");
            Toast.makeText(getApplicationContext(), "กรุณาเลือกรูปโปรไฟล์ของท่าน", Toast.LENGTH_SHORT).show();
            check = 1;
        } else {
            rImageError.setError(null);
        } //check if gender is set

        if(check != 0){
            return false;
        }else {
            return true;
        }
    }//checkRegisInfo()
}