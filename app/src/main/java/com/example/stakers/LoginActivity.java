package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stakers.utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    FirebaseAuth mAuth;
    private static final String MY_PREFS = "my_prefs";
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    private DatabaseReference mDatabase;
    SharedPreferences sharedpreferences;
    Button loginBtn;
    EditText emailET, passET;
    String st_email, st_password, st_dob, st_gender, st_profilepic, uid;
    TextView forgetPassTV, registerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passwordET);

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkInfo
                if (checkLoginInfo()) {
                    Log.d(TAG, "checkLoginInfo() returns true");
                    logIn();
                } else {
                    Log.d(TAG, "checkLoginInfo() returns false");
                }//check if checkLoginInfo() return true
            }
        });
        //forget password activity button
        forgetPassTV = findViewById(R.id.forgetPassTV);
        forgetPassTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPassActivity.class));
            }
        });
        //register activity button
        registerTV = findViewById(R.id.regisTV);
        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private boolean checkLoginInfo() {
        st_email = emailET.getText().toString().trim();
        st_password = passET.getText().toString().trim();

        int check = 0;

        if (st_email.isEmpty()) {
            emailET.setError("กรุณาใส่อีเมลของท่าน");
            check = 1;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(st_email).matches()) {
                emailET.setError("อีเมลผิดพลาด");
                check = 1;
            } else {
                emailET.setError(null);
            }
        } //check if email is filled

        if (st_password.isEmpty()) {
            passET.setError("กรุณาใส่รหัสผ่าน");
            check = 1;
        } else if (st_password.length()<6) {
            passET.setError("กรุณาใส่รหัสผ่านให้ครบ 6 ตัวอักษร");
            check = 1;
        } else {
            passET.setError(null);
        }
        //check if password if filled

        return check == 0;
    }//checkLoginInfo()

    private boolean logIn() {
        Log.d(TAG, "start logIn()");
        mAuth.signInWithEmailAndPassword(st_email, st_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        uid = user.getUid();
                    }
                    Log.d(TAG, "Login Successful!");
                    Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                    if (sharedPreferncesLogin(uid)) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                } else {
                    Log.d(TAG, "Login Failed");
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }//if task is successful
            }
        });
        return true;
    } //logIn()

    private boolean sharedPreferncesLogin(final String uid) {
        // Get SharedPreferences
        sharedpreferences = getApplicationContext().getSharedPreferences(MY_PREFS,
                Context.MODE_PRIVATE);
        mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User userSharedPreferences = dataSnapshot.getValue(User.class);
                    if(userSharedPreferences != null){
                        st_email = userSharedPreferences.getEmail();
                        st_dob = userSharedPreferences.getDob();
                        st_gender = userSharedPreferences.getGender();
                        st_profilepic = userSharedPreferences.getUser_image_path();
                        // Set data to SharedPreferences
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(pref_uid, uid);
                        editor.putString(pref_email, st_email);
                        editor.putString(pref_dateofbirth, st_dob);
                        editor.putString(pref_gender, st_gender);
                        editor.putString(pref_userimage, st_profilepic);
                        editor.putBoolean(pref_islogin, true);
                        editor.apply();
                    }else{
                        Log.d(TAG, "Can't connect to database");
                    }
                } else {
                    Log.e(TAG, "Not found: " + uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }
}//LoginActivity