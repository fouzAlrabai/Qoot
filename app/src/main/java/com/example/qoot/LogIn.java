package com.example.qoot;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class LogIn extends AppCompatActivity {
    EditText userEmail,userPassword;
    Button loginbtn;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId;
    String[] name = new String[2];
    TextView tes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        userEmail = findViewById(R.id.emailText);
        userPassword = findViewById(R.id.passwordText);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loginbtn = findViewById(R.id.LogInBTN);
        // tes = findViewById(R.id.textView3);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    userEmail.setError("Please Enter Your Email, It Is Required");
                    return;
                }
                else if(TextUtils.isEmpty(password)){
                    userPassword.setError("Please Enter Password, It Is Required");
                    return;
                }
                else if(password.length() < 8){
                    userPassword.setError("The Characters Must Be At Least 8 Characters");
                    return;
                }
                else if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    userEmail.setError("Please Enter Your Email and Password, It Is Required");
                    return;
                }
                else if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))) {

                    // authenticate the user

                    fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(LogIn.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();


                                String token_id= FirebaseInstanceId.getInstance().getToken();
                                        String current_id=fAuth.getCurrentUser().getUid();

                                        Map<String,Object> tokenMap=new HashMap<>();
                                        tokenMap.put("token_id", token_id);
                                        db.collection("users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });


                                //to open the right profile
                                whichOne();


                                //startActivity(new Intent(LogIn.this,  حطو الكلاس المناسب.class));
                            }else {



                                Toast.makeText(LogIn.this, "Something Went Wrong,Try Again ! " , Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }

            }
        });

    }

    public void whichOne(){
        userId=fAuth.getCurrentUser().getUid();
        DocumentReference documentReference =db.collection("users").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String t =documentSnapshot.getString("Type")+"";

                goOn(t);

            }
        });
    }

    public void goOn(String t){

        if(t.equals("Donator")){
            DocumentReference documentReference =db.collection("Donators").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    name[0] =documentSnapshot.getString("UserName");
                }
            });
            Intent intent = new Intent(LogIn.this, DonatorRequests.class);
           // intent.putExtra("user", userId);
          // intent.putExtra("Name", name); بس خليت ذا كومنت ما سويت شيء - عبير
            startActivity(intent);
           // startActivity(new Intent(LogIn.this, DonatorRequests.class));
        }
        if(t.equals("Volunteer")) {
            DocumentReference documentReference =db.collection("Volunteers").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    name[0] =documentSnapshot.getString("UserName");
                }
            });
            Intent intent = new Intent(LogIn.this, VolunteerRequests.class);
        //    intent.putExtra("user", userId);
           // intent.putExtra("Name", name);
            startActivity(intent);
            //startActivity(new Intent(LogIn.this, VolunteerProfile.class));
        }

    }
    public void OpenSignupAsPage(View view) {
        startActivity(new Intent(LogIn.this,SignUpAs.class));
    }

    public void openForPassPage(View view) {
        startActivity(new Intent(LogIn.this,ForgotPassword.class));
    }

    public void OpenSignupPage(View view) {
        //for Test
        //  startActivity(new Intent(LogIn.this,profile.class));
    }

}

