package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText forgotPassEmail;
    Button resetbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        forgotPassEmail=(EditText)findViewById(R.id.emailForgot);
        resetbtn=(Button)findViewById(R.id.resetPassword);
        mAuth = FirebaseAuth.getInstance();

        resetbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               String useremail= forgotPassEmail.getText().toString().trim();

                if(TextUtils.isEmpty(useremail)){
                    Toast.makeText(ForgotPassword.this, "Please Enter Your Email, It Is Required", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(ForgotPassword.this,"Password rest email sent",Toast.LENGTH_SHORT).show();
                             finish();
                             startActivity(new Intent(ForgotPassword.this,LogIn.class));
                         }else {
                             Toast.makeText(ForgotPassword.this,"Error in sending password rest email",Toast.LENGTH_SHORT).show();
                         }
                        }
                    }) ;
                }

            }
        });

    }

    public void OpenSignInPage(View view) {
        startActivity(new Intent(ForgotPassword.this,LogIn.class));
    }
}
