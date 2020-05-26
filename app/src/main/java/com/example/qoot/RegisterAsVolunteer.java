package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegisterAsVolunteer extends AppCompatActivity {

    // text fields in the form
    EditText mUsername, mEmail, mPassword;
    Spinner cars;
    Button register;
    RadioGroup GenderGroup;
    RadioButton gender,male,female;

        // variables for db
    FirebaseAuth mAuth;
    FirebaseFirestore db ;
    public static final String TAG = "RegisterAsVolunteer";
    String userId,username,email,gen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_volunteer);

        //initialize textViews
        mUsername = findViewById(R.id.userText);
        mEmail = findViewById(R.id.emailText);
        mPassword = findViewById(R.id.passwordText);
        register = findViewById(R.id.button);
        GenderGroup = (RadioGroup) findViewById(R.id.radioGender);
        cars = findViewById(R.id.carDD);
        male= findViewById(R.id.male);
        female= findViewById(R.id.female);

        //initialize firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // DROP DOWN CODE
        final String[] types = new String[]{"Sedan", "SUV", "Truck"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        cars.setAdapter(adapter);


        // RADIO CODE..
        int selectedId = GenderGroup.getCheckedRadioButtonId();
        gender = (RadioButton) findViewById(selectedId);

        // checking
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 username = mUsername.getText().toString().trim();
                 email = mEmail.getText().toString().trim();
                  String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    mUsername.setError("Please Enter Your Name, It is Required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Please Enter Your Email, It is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Please Enter Password, It is Required");
                    return;
                }

                if (password.length() < 8) {
                    mPassword.setError("The Characters Must Be At Least 8 Characters ");
                    return;
                }
                if(male.isChecked())
                    gen="Male";
                if(female.isChecked())
                    gen="Female";
                //register user with authentication and firestore
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // تشيك للايميل اذا صدقي او لا
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterAsVolunteer.this, "Verification Email Has Been Sent ", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"OnFailure: Email Not Sent");
                                }
                            });
                          //  Toast.makeText(RegisterAsVolunteer.this, "Registration Was Successful!!", Toast.LENGTH_SHORT).show();
                            userId = mAuth.getCurrentUser().getUid();


                            // Donator don = new Donator(username,email,gender.getText().toString());
                            db= FirebaseFirestore.getInstance();

                            final DocumentReference documentReference=db.collection("Volunteers").document(userId);

                            String token_id= FirebaseInstanceId.getInstance().getToken();
                                    Map<String,Object> volunteer = new HashMap<>();
                            volunteer.put("UserName",username);
                            volunteer.put("Email",email);
                            volunteer.put("Gender",gen);
                            volunteer.put("PhoneNumber","05xxxxxxxx");
                            volunteer.put("Vehicle",cars.getSelectedItem().toString());
                            volunteer.put("token_id", token_id);
                            documentReference.set(volunteer).addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {
                                  //  Log.d(TAG,"OnSuccess: user profile is created for"+userId);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                   // Log.d(TAG,"OnFailure "+ e.toString());
                                }
                            });
                            DocumentReference documentReference1=db.collection("users").document(userId);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Type","Volunteer");
                            user.put("email",email);
                            user.put("token_id", token_id);
                            documentReference1.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {
                                  //  Log.d(TAG,"OnSuccess: user profile is created for"+userId);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                   // Log.d(TAG,"OnFailure "+ e.toString());
                                }
                            });



                            //db.collection("users").document(userid).set(don);

                           /* db = FirebaseFirestore.getInstance();
                            String  USER = mAuth.getCurrentUser().getUid();
                            Donator donator = new Donator(username,email,(String)gender.getText());
                            db.collection("users").document(USER).set(donator);*/

                           // startActivity(new Intent(RegisterAsDonator.this, DonatorProfile.class));
/*
                                  // create the object
                            Volunteer vol = new Volunteer(
                                    username.getText().toString()
                                    ,email.getText().toString()
                                    ,password.getText().toString()
                                    ,cars.getSelectedItem().toString() // not sure about this
                                    ,gender.getText().toString());

                                // now add this to firebase
                                fstore= FirebaseFirestore.getInstance();
                                String v = mAuth.getCurrentUser().getUid();
                                fstore.collection("users").document(v).set(vol);
                                // now go to the volunteer profile activity

                                                                // this have to change v



 */

                            startActivity(new Intent(getApplicationContext(), VolunteerProfile.class));
                        } else {
                            Toast.makeText(RegisterAsVolunteer.this, "Something Went Wrong,Try Again ! " , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    public void OpenSignupAsPage(View view) {
        startActivity(new Intent(RegisterAsVolunteer.this,SignUpAs.class));
    }

    public void OpenSignInPage(View view) {
        startActivity(new Intent(RegisterAsVolunteer.this,LogIn.class));
    }
}