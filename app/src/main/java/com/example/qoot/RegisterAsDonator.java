package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


// i study 444
public class RegisterAsDonator extends AppCompatActivity {
    EditText mUsername, mEmail, mPassword;
    Button register;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    RadioGroup GenderGroup;
    RadioButton gender,male,female;

    String username,email,gen;
    public static final String TAG = "RegisterAsDonator";
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_donator);
        mUsername = findViewById(R.id.userText);
        mEmail = findViewById(R.id.emailText);
        mPassword = findViewById(R.id.passwordText);
        register = findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        GenderGroup = (RadioGroup) findViewById(R.id.radioGender);
        male= findViewById(R.id.male);
        female= findViewById(R.id.female);

        // RADIO CODE..
        int selectedId = GenderGroup.getCheckedRadioButtonId();
        gender = (RadioButton) findViewById(selectedId);
        //Toast.makeText(RegisterAsDonator.this, gender.getText(), Toast.LENGTH_SHORT).show();
        /*if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), checking.class));
            finish();
        }*/

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username= mUsername.getText().toString();
                email = mEmail.getText().toString().trim();

                String password = mPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    mUsername.setError("Please Enter Your Name, It is Required");
                    return;

                }if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Please Enter Your Email, It Is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Please Enter Password, It Is Required");
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
                //register user
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // تشيك للايميل اذا صدقي او لا
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterAsDonator.this, "Verification Email Has Been Sent ", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"OnFailure: Email Not Sent");
                                }
                            });
// NEED TO WAIT FOR Abby
                            userId = mAuth.getCurrentUser().getUid();
                          //  Toast.makeText(RegisterAsDonator.this, "Registration Was Successful!!", Toast.LENGTH_SHORT).show();

                            // Donator don = new Donator(username,email,gender.getText().toString());
                            db= FirebaseFirestore.getInstance();
                            final DocumentReference documentReference=db.collection("Donators").document(userId);


                                    String token_id= FirebaseInstanceId.getInstance().getToken();
                                    Map<String,Object> donators = new HashMap<>();
                                    donators.put("UserName",username);
                                    donators.put("Email",email);
                                    donators.put("Gender",gen);
                                    donators.put("PhoneNumber","05xxxxxxxx");
                                    donators.put("token_id", token_id);
                                    documentReference.set(donators).addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Log.d(TAG,"OnSuccess: user profile is created for"+userId);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //  Log.d(TAG,"OnFailure "+ e.toString());
                                        }
                                    });


                            DocumentReference documentReference1=db.collection("users").document(userId);
                            Map<String,Object> user = new HashMap<>();
                            user.put("Type","Donator");
                            user.put("email",email);
                            user.put("token_id", token_id);
                            documentReference1.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                                @Override
                                public void onSuccess(Void aVoid) {
                                //    Log.d(TAG,"OnSuccess: user profile is created for"+userId);
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


                            startActivity(new Intent(RegisterAsDonator.this, DonatorProfile.class));
                        } else {
                            Toast.makeText(RegisterAsDonator.this, "Something Went Wrong,Try Again !  " , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    /*
    public void  saveUSer(){
        db = FirebaseFirestore.getInstance();
        String  USER = mAuth.getCurrentUser().getUid();
       // DocumentReference dRef = db.collection("Donators").document(USER);
        Donator donator = new Donator()

       Map<String, Object> Donator = new HashMap<>();
        Donator.put("Name", username);
        Donator.put("Email",email);
        Donator.put("PhoneNumber","0500000000");
        Donator.put("Password",password);
        dRef.set(Donator).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"onSuccess: Added user successfully!");
            }
        });
    }*/

    public void OpenSignInPage(View view) {
        startActivity(new Intent(RegisterAsDonator.this,LogIn.class));
    }

    public void OpenSignupAsPage(View view) {
        startActivity(new Intent(RegisterAsDonator.this,SignUpAs.class));
    }



}


