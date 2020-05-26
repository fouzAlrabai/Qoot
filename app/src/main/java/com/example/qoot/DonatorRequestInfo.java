package com.example.qoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class DonatorRequestInfo extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView type,guests, date, time,notes, volName, state;
    LinearLayout noteLay;
    Button cancel;
    Button Rate;
    Bundle intent1;
    String ss,Hussa;
    String ABEER;
    ImageView ChatIcon;

    //  DonatorRequests r =new DonatorRequests();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_request_info);
        type = findViewById(R.id.FoodType);
        state = findViewById(R.id.requesrStatus);
        guests = findViewById(R.id.numberOfGuest);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.pickUpTime);
        notes = findViewById(R.id.note);
        volName = findViewById(R.id.volname);
        cancel=(Button) findViewById(R.id.cancel) ;
        Rate=(Button)findViewById(R.id.Rate_but);
        noteLay = (LinearLayout) findViewById(R.id.linearLayout4);
        ChatIcon = findViewById(R.id.CHAT);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        intent1 = getIntent().getExtras();

        if (intent1 != null) {
            Hussa =(String) intent1.getSerializable("Where");
            String ReqIDDD = (String) intent1.getSerializable("RequestID");
            ABEER =(String) intent1.getSerializable("RequestID");
            // String ReqIDDD = intent1.getStringExtra("RequestID");
           // Toast.makeText(DonatorRequestInfo.this, "It" + ReqIDDD, Toast.LENGTH_SHORT).show();
            DocumentReference documentReference = db.collection("Requests").document(ReqIDDD);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    type.setText(documentSnapshot.getString("TypeOfEvent"));

                    ss = (documentSnapshot.getString("State"));
                    SpannableString spannableString = new SpannableString(ss);
                    if (ss.equals("Pending")) {
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FB8C00"));
                        spannableString.setSpan(foregroundColorSpan, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        state.setText(spannableString);
                    } else if (ss.equals("Accepted")) {
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#4CAF50"));
                        spannableString.setSpan(foregroundColorSpan, 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        state.setText(spannableString);
                    } else if (ss.equals("Cancelled")) {
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#BF360C"));
                        spannableString.setSpan(foregroundColorSpan, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        state.setText(spannableString);
                    } else if (ss.equals("Delivered")) {
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0392cf"));
                        spannableString.setSpan(foregroundColorSpan, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        state.setText(spannableString);
                    } else if (ss.equals("Delivered")) {
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0392cf"));
                        spannableString.setSpan(foregroundColorSpan, 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        state.setText(spannableString);
                    }

                    guests.setText(documentSnapshot.getString("NumberOfGuests"));
                    date.setText(documentSnapshot.getString("Date"));
                    time.setText(documentSnapshot.getString("Time"));

                    String empty = "";
                    if ((documentSnapshot.getString("Note")) == empty) {
                        noteLay.setVisibility(View.GONE);
                    } else {
                        notes.setText(documentSnapshot.getString("Note"));
                    }
                    String Vol = documentSnapshot.getString("VolnteerName");
                    volName.setText(Vol);

                    if (ss.equals("Pending") || ss.equals("Accepted"))
                        cancel.setVisibility(View.VISIBLE);


                    // to display pop up
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(DonatorRequestInfo.this, cancelPopUp.class);
                            if (intent1 != null)
                                i.putExtra("RequestID", (String) intent1.getSerializable("RequestID"));

                            startActivity(i);
                        }
                    });


                    if (ss.equals("Delivered")){
                        Rate.setVisibility(View.VISIBLE);
                        ChatIcon.setVisibility(View.VISIBLE);
                    }

                        Rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(DonatorRequestInfo.this, pop_review2.class);
                            if (intent1 != null)
                                i.putExtra("RequestID", (String) intent1.getSerializable("RequestID"));

                            startActivity(i);
                        }
                    });


                    // عشان نضيف الايكون على حسب الطلب
                    switch (documentSnapshot.getString("State")) {
                        case "Pending":
                            // AddPendingIcon();
                            break;
                        case "Accepted":
                            // AddAcceptedIcon();
                            ChatIcon.setVisibility(View.VISIBLE);
                            break;
                        case "Cancelled":
                            break;
                    }
                    if(Vol!=null || Vol!="--"){
                    final String VolID = documentSnapshot.getString("VolnteerID");
                    volName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = getIntent();
                            in.putExtra("Volunteers", VolID);
                            Intent intent = new Intent(DonatorRequestInfo.this, VolunteerViewInfo.class);
                            intent.putExtra("Volunteers", in.getStringExtra("Volunteers"));
                            startActivity(intent);

                        }
                    });

                }

                }
            });
        }




    }

    public void AddPendingIcon(){

    }

    public void AddAcceptedIcon(){

    }
    public void AddCancelledIcon(){

    }

    public void OpenDonatorRequest(View view) {
//HERE
        if(Hussa != null) {
            System.out.println("HERe in not null");
            if (Hussa.equals("history")) {
                System.out.println("HERe in eq");
                startActivity(new Intent(DonatorRequestInfo.this, history_d.class));
            }
        }
        else
        startActivity(new Intent(DonatorRequestInfo.this,DonatorRequests.class));
    }

    public void OpenListOffer(View view) {
        startActivity(new Intent(DonatorRequestInfo.this,list_offers.class));
    }
    public void OpenAttachment(View view){
        Intent in = getIntent();
        in.putExtra("RequestID",ABEER);
        in.putExtra("Who","D");
        Intent intent = new Intent(DonatorRequestInfo.this, AttachmentPicture.class);
        intent.putExtra("RequestID", in.getStringExtra("RequestID"));
        intent.putExtra("Who", in.getStringExtra("Who"));
        startActivity(intent);
    }
    public void OpenChat(View view) {
        intent1 = getIntent().getExtras();
        ABEER = (String) intent1.getSerializable("Where");
        Intent intent = new Intent(DonatorRequestInfo.this, ChatPageD.class);
        if (intent1 != null)
            intent.putExtra("RequestID", (String) intent1.getSerializable("RequestID"));
        intent.putExtra("Where",ABEER);
        startActivity(intent);
    }
    public void OpenTrackDonatorRequest(View view){
        Intent in = getIntent();
        in.putExtra("RequestID",ABEER);
        in.putExtra("Who","D");
        Intent intent = new Intent(DonatorRequestInfo.this, DonatorMap.class);
        intent.putExtra("RequestID", in.getStringExtra("RequestID"));
        intent.putExtra("Who", in.getStringExtra("Who"));
        startActivity(intent);
    }

}