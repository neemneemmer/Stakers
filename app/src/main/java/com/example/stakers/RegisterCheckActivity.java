package com.example.stakers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stakers.utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class RegisterCheckActivity extends AppCompatActivity {
    private static final String TAG = "RegisterCheckActivity";
    private static final String MY_PREFS = "my_prefs";
    private static final String pref_uid = "uidKey";
    private static final String pref_email = "emailKey";
    private static final String pref_dateofbirth = "dobKey";
    private static final String pref_gender = "genderKey";
    private static final String pref_userimage = "userImageKey";
    private static final String pref_islogin = "isLoginKey";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    private StorageReference storageReference, ref;
    SharedPreferences.Editor editor;
    ImageView userProfile;
    TextView u_email, u_password, u_dob, u_gender;
    String st_email, st_password, st_dob, st_gender, st_profilepic, uid, imageUid;
    Uri uri;
    CheckBox u_term;
    Button regisBtn, conBtn, disBtn;
    SharedPreferences sharedPreferences;
    RelativeLayout rcLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_check);

        rcLoading = findViewById(R.id.recLoading);
        rcLoading.setVisibility(View.GONE);

        //UI Initialization
        userProfile = findViewById(R.id.userIV);
        u_email = findViewById(R.id.rcEmailText);
        u_password = findViewById(R.id.rcPasswordText);
        u_dob = findViewById(R.id.rcDobText);
        u_gender = findViewById(R.id.rcGenderText);
        u_term = findViewById(R.id.termChoice);
        regisBtn = findViewById(R.id.rRegisBtn);

        //Database initialization
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("profile_picture");
        //getToken();

        //Passing intent values and display them
        Intent rIntent = getIntent();
        st_email = rIntent.getStringExtra("email");
        st_password = rIntent.getStringExtra("password");
        st_dob = rIntent.getStringExtra("dateOfBirth");
        st_gender = rIntent.getStringExtra("gender");
        uri = Uri.parse(rIntent.getStringExtra("user_image"));
        userProfile.setImageURI(uri);

        u_email.setText(st_email);
        u_password.setText(st_password);
        u_dob.setText(st_dob);
        u_gender.setText(st_gender);

        regisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = u_term.isChecked();
                if (!isChecked) {
                    rcLoading.setVisibility(View.GONE);
                    Log.d(TAG, "term is unchecked");
                    final Dialog uncheckedDialog = new Dialog(RegisterCheckActivity.this);
                    uncheckedDialog.setContentView(R.layout.dialog_regis_f);
                    uncheckedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    uncheckedDialog.setCancelable(false);
                    uncheckedDialog.setCanceledOnTouchOutside(false);

                    disBtn = uncheckedDialog.findViewById(R.id.dismissBtn);
                    disBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uncheckedDialog.dismiss();
                        }
                    });
                    uncheckedDialog.show();
                } else {
                    Log.d(TAG, "term is checked");
                    uploadProfile();
                }//isChecked
            } //onClick()
        });
    } //onCreate()

    /*public void getToken(){
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<InstallationTokenResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    st_idToken = task.getResult().getToken();
//                    Log.d("Installations", "Installation auth token: " + st_idToken);
//                    mDatabase.child("Users").child(uid).setValue(st_idToken);
                } else {
                    Log.e("Installations", "Unable to get Installation auth token");
                }
            }
        });
    }*/

    public void uploadProfile() {
        Log.d(TAG, "start uploadProfile()");
        rcLoading.setVisibility(View.VISIBLE);

        // Defining the child of storageReference
        imageUid = mDatabase.push().getKey();
        ref = storageReference.child(imageUid + ".jpg");
        //ref2 = storageReference.child("profile_picture%2F" + imageUid);
        // adding listeners on upload
        // or failure of image
        ref.putFile(uri)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot)
                            {
                                // Image uploaded successfully
                                // Dismiss dialog
                                storageReference.child(imageUid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        st_profilepic = uri.toString();
                                        registerAccount(st_email, st_password, st_dob, st_gender, st_profilepic);
                                    }
                                });
                                Toast.makeText(getApplicationContext(), "Picture has been uploaded!", Toast.LENGTH_SHORT).show();
                                //start registerAccount() && setSharedPreferences() when uploadProfile() is finished;
//                                registerAccount(st_email, st_password, st_dob, st_gender, st_profilepic);
//                                setSharedPreferences();
                                //Toast.makeText(RegisterCheckActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        // Error, Image not uploaded
                        Toast.makeText(RegisterCheckActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(
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

    private void registerAccount(final String email, final String password, final String dob, final String gender, final String profilepic) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterCheckActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "start onComplete()");
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    if (user != null) {
                        uid = user.getUid();
                    }
                    User stUser = new User(email, dob, gender, profilepic);
                    mDatabase.child("Users").child(uid).setValue(stUser);
                    Log.d(TAG, "Register Account is successful!" + stUser);
                    Toast.makeText(getBaseContext(),"Register Account is successful!",Toast.LENGTH_SHORT).show();
                    setSharedPreferences();
                    logIn(email, password);
                    final Intent intent = new Intent(RegisterCheckActivity.this,MainActivity.class);
                    final Dialog registeredDialog = new Dialog(RegisterCheckActivity.this);
                    registeredDialog.setContentView(R.layout.dialog_regis_s);
                    registeredDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    registeredDialog.setCancelable(false);
                    registeredDialog.setCanceledOnTouchOutside(false);
                    conBtn = registeredDialog.findViewById(R.id.continueBtn);
                    conBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            registeredDialog.dismiss();
                            startActivity(intent);
                        }
                    });
                    registeredDialog.show();
                    Log.d(TAG, "registerAccount() && setSharedPreferences() returns true");
                } else {
                    //Log.d(TAG, "Register Account is failed!");
                    Toast.makeText(getBaseContext(),"Register Account is failed!",Toast.LENGTH_SHORT).show();
                } //check if task is successful
            } //onComplete()
        });
    }//registerAccount()

    private void setSharedPreferences() {
        // Get SharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences(MY_PREFS,
                Context.MODE_PRIVATE);
        // Save SharedPreferences
        editor = sharedPreferences.edit();
        editor.putString(pref_uid, uid);
        editor.putString(pref_email, st_email);
        editor.putString(pref_dateofbirth, st_dob);
        editor.putString(pref_gender, st_gender);
        editor.putString(pref_userimage, st_profilepic);
        editor.putBoolean(pref_islogin, true);
        editor.apply();
    }//setSharedPreferences()

    private void logIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(RegisterCheckActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Login successful!");
                    //Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    } //logIn()
}