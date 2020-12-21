package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    CircularImageView userImage;
    Uri imageUri;
    TextView rImageError, rGenderErrTV;
    EditText r_emailET, r_passET, r_con_passET, r_dateET;
    String r_emailText, r_passText, r_con_passText, r_dateText, r_genderText;
    RadioGroup radio_gender;
    RadioButton checkedRadio;
    Button rCancelBtn, rSubmitBtn;
    DatePickerDialog datePicker;
    Calendar myCalendar;
    int mDay, mMonth, mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //set EditText
        r_emailET = findViewById(R.id.rEmailET);
        r_passET = findViewById(R.id.rPassET);
        r_con_passET = findViewById(R.id.rConPassET);
        r_dateET = findViewById(R.id.rDOBET);
        rImageError = findViewById(R.id.imageErrorTV);
        rGenderErrTV = findViewById(R.id.rGenderErrTV);

        //set ImageView
        userImage = findViewById(R.id.userIV);
        // Set Border
        //userImage.setBorderWidth(5f);
        // or with gradient
        //userImage.setBorderColorStart(R.color.colorPrimary);
        //userImage.setBorderColorEnd(R.color.colorPrimaryDark);
        //userImage.setBorderColorDirection(CircularImageView.GradientDirection.TOP_TO_BOTTOM);

        //set RadioGroup
        radio_gender = findViewById(R.id.radioGenderGroup);

        //to set user's profile picture
        myCalendar = Calendar.getInstance();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        r_dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == r_dateET) {
                    // Get Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);

                    datePicker = new DatePickerDialog(RegisterActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            r_dateET.setText(day + "/" + (month + 1) + "/" + year);
                        } //onDateSet()
                    }, mYear, mMonth, mDay);
                    datePicker.show();
                } //if
            } //onClick()
        });

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

        rCancelBtn = findViewById(R.id.rCancelBtn);
        rCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        rSubmitBtn = findViewById(R.id.rSubmitBtn);
        rSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if all fill is formed
                if (checkRegisInfo()) {
                    passingData();
                } /*else {
                    choosePicture(); //select picture first
                }*/
            }
        });
    }//onCreate()

    private void choosePicture() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto,0); //one can be replaced with any action code
    }//choosePicture()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            //imageUri = data.getData();
            userImage.setImageURI(imageUri);
        } else {
            Log.d(TAG, "requestCode error in onActivityResult()");
        }
    }//onActivityResult()

    private boolean checkRegisInfo() {
        r_emailText = r_emailET.getText().toString().trim();
        r_passText = r_passET.getText().toString().trim();
        r_con_passText = r_con_passET.getText().toString().trim();
        r_dateText = r_dateET.getText().toString().trim();

        int check = 0;
        if (r_emailText.isEmpty()) {
            r_emailET.setError("กรุณาใส่อีเมลของท่าน");
            check = 1;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(r_emailText).matches()) {
                r_emailET.setError("อีเมลผิดพลาด");
                check = 1;
            } else {
                r_passET.setError(null);
            }
        } //check if email is filled

        if(r_passText.isEmpty()|| r_con_passText.isEmpty()){
            if(r_passText.isEmpty()){r_passET.setError("กรุณากรอกรหัสผ่าน");
                check = 1; }
            if(r_con_passText.isEmpty()){r_con_passET.setError("กรุณายืนยันรหัสผ่าน");
                check = 1; }
        }else if(!r_passText.equalsIgnoreCase(r_con_passText)){
            if(!r_passText.equalsIgnoreCase(r_con_passText)){
                r_passET.setError("รหัสผ่านไม่ตรงกัน");
                r_con_passET.setError("รหัสผ่านไม่ตรงกัน");
                check = 1;
            }else{
                r_passET.setError(null);
                r_con_passET.setError(null);
            }
        }else if(r_con_passET.length()<6){
            if(r_con_passET.length()<6){
                r_passET.setError("รหัสผ่านต้องมี 6 ตัวอักษรขึ้นไป");
                r_con_passET.setError("รหัสผ่านยืนยันต้องมี 6 ตัวอักษรขึ้นไป");
                check = 1;
            }else{
                r_passET.setError(null);
                r_con_passET.setError(null);
            }
        }//check if password is filled and matched

        if (r_dateText.isEmpty()) {
            r_dateET.setError("กรุณากรอกวันเกิดของท่าน");
            check = 1;
        } else {
            r_dateET.setError(null);
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

    private void passingData() {
        Log.d(TAG, "start registerAccount()");
        Intent rIntent = new Intent(getApplicationContext(), RegisterCheckActivity.class);
        rIntent.putExtra("email", r_emailText);
        rIntent.putExtra("password", r_passText);
        rIntent.putExtra("dateOfBirth", r_dateText);
        rIntent.putExtra("gender", r_genderText);
        rIntent.putExtra("user_image", imageUri.toString());
        startActivity(rIntent);
    }//registerAccountchecked()

}//RegisterActivity