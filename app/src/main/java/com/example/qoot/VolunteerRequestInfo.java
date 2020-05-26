package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

public class VolunteerRequestInfo extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView type,guests,date, time,notes, DonName,state;
    Button Acceptbtn,cancel;
    String userID;
    LinearLayout noteLay;
    String VolunteerName,donID;
    CheckBox checkDelivered;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    Bundle intent1;
    String dateCheck,currentDate,typeR,timeCheck,currenttime;

    ImageView chat;
    String ABEER;
    String ABEER2;
    SpannableString spannableString;

    String VolunteerID;
    String CurrentState;
    GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_request_info);

        type = findViewById(R.id.FoodType);
        state = findViewById(R.id.requesrStatus);
        guests = findViewById(R.id.numberOfGuest);
      //  location = findViewById(R.id.location);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.pickUpTime);
        notes = findViewById(R.id.note);
        DonName = findViewById(R.id.volname);
        Acceptbtn = findViewById(R.id.offers);
        noteLay = (LinearLayout) findViewById(R.id.linearLayout4);
        checkDelivered = (CheckBox)findViewById(R.id.checkDel);
        chat = findViewById(R.id.ChatIcon);
        cancel =(Button)findViewById(R.id.cancel) ;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        intent1 = getIntent().getExtras();
        userID =mAuth.getCurrentUser().getUid();


        if (intent1 != null) {
            final String ReqIDDD = (String) intent1.getSerializable("RequestID");
            ABEER2 = (String) intent1.getSerializable("Where");
            //to show location for vol
//            location.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i =new Intent(VolunteerRequestInfo.this,VolunteerMap.class);
//                    i.putExtra("RequestID",ReqIDDD);
//                    i.putExtra("Where",ABEER2);
//                    startActivity(i);
//                }
//            });
            ABEER = (String) intent1.getSerializable("RequestID");
            ABEER2 = (String) intent1.getSerializable("Where");


            DocumentReference documentReference1 = db.collection("Requests").document(ReqIDDD);
            documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                    VolunteerID = documentSnapshot.getString("VolnteerID");
                    CurrentState = documentSnapshot.getString("State");
//                    Toast.makeText(getApplicationContext(),"this vol "+VolunteerID,Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(),"this state "+CurrentState,Toast.LENGTH_LONG).show();
                }
            });

                // String ReqIDDD = intent1.getStringExtra("RequestID");
                DocumentReference documentReference = db.collection("Requests").document(ReqIDDD);
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        final String TOE=documentSnapshot.getString("TypeOfEvent");
                        type.setText(TOE);
                        String ss = (documentSnapshot.getString("State"));
                        typeR = documentSnapshot.getString("RequestType");
                        donID=documentSnapshot.getString("DonatorID");
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
                        }

                        guests.setText(documentSnapshot.getString("NumberOfGuests"));
                     //   location.setText(documentSnapshot.getString("Location"));
                        date.setText(documentSnapshot.getString("Date"));
                        time.setText(documentSnapshot.getString("Time"));

                        String empty = "";
                        if ((documentSnapshot.getString("Note")) == empty) {
                            noteLay.setVisibility(View.GONE);
                        } else {
                            notes.setText(documentSnapshot.getString("Note"));
                        }
                        String Don=documentSnapshot.getString("DonatorName");
                        DonName.setText(Don);

                        //cancel
                        // checkDate
                        Calendar now = Calendar.getInstance();
                        dateCheck = date.getText().toString();

                        //int  y= Integer.parseInt(dateCheck.substring(dateCheck.indexOf('/',6)+1));
                        currentDate = (now.get(Calendar.MONTH) + 1)
                                + "/"
                                + now.get(Calendar.DAY_OF_MONTH)
                                + "/"
                                + now.get(Calendar.YEAR);
                        //check for schedule
                        if (ss.equals("Accepted") && typeR.equals("Scheduled")){
                            int  m = Integer.parseInt(dateCheck.substring(0,dateCheck.indexOf('/')));
                            int  d= Integer.parseInt(dateCheck.substring(dateCheck.indexOf('/')+1,dateCheck.indexOf('/',4)));
                            if ((now.get(Calendar.MONTH)+1)>= m && now.get(Calendar.DAY_OF_MONTH)<d  )
                                cancel.setVisibility(View.VISIBLE);}
                        //check for Urgent
                        now.add(Calendar.HOUR, 2);
                        // SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                        // format.format(now.getTime());
                        currenttime = now.get(Calendar.HOUR_OF_DAY)
                                + ":"
                                + now.get(Calendar.MINUTE);

                        timeCheck = documentSnapshot.getString("Time");
                        String str = timeCheck.replaceAll("\\s+", "");


                        String k=str.substring(0,(str.indexOf(':')));


                     //   System.out.println("**************k************"+k+"len"+k.length());
                       // System.out.println("**************************"+timeCheck+"len"+timeCheck.length());
                        if (ss.equals("Accepted") && typeR.equals("Urgent")){
                            int hour =0;
                            int nowh=0;
                            if(timeCheck != null){
                             hour = Integer.parseInt(k);
                              //  hour=13;

                            if(now.get(Calendar.HOUR_OF_DAY)>12){
                                nowh=now.get(Calendar.HOUR_OF_DAY);
                                nowh-=12;
                            }else
                                nowh=now.get(Calendar.HOUR_OF_DAY);

                            timeCheck = documentSnapshot.getString("Time");
                           // int min = Integer.parseInt(timeCheck.substring(timeCheck.indexOf(':') + 1));
                                }
                            //Toast.makeText( VolunteerRequestInfo.this,"c="+hour,Toast.LENGTH_SHORT).show();
                           // Toast.makeText( VolunteerRequestInfo.this,"now="+now.get(Calendar.HOUR_OF_DAY),Toast.LENGTH_SHORT).show();

                            // Toast.makeText( VolunteerRequestInfo.this,"c="+dateCheck+"t="+currentDate,Toast.LENGTH_SHORT).show();
                        if (dateCheck.equals(currentDate))
                            if (nowh < hour)
                                cancel.setVisibility(View.VISIBLE);
                    }
                        // to display pop up
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(VolunteerRequestInfo.this,VolunteerCancel.class);
                                if(intent1 != null)
                                    i.putExtra("RequestID",(String) intent1.getSerializable("RequestID"));

                                startActivity(i);
                            }
                        });
                        //end of cancel
                        // Delivered checkbox..
                        checkDelivered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                if(isChecked) {
                                    checkDelivered.setEnabled(false);
                                    String ReqIDDD = (String) intent1.getSerializable("RequestID");
                                    DocumentReference documentReference2 = db.collection("Requests").document(ReqIDDD);
                                    documentReference2.update("State","Delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                           //Toast.makeText( VolunteerRequestInfo.this,"updated successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent pop = new Intent(VolunteerRequestInfo.this, PopReview.class);
                                    if(intent1!= null)
                                        pop.putExtra("RequestID",(String) intent1.getSerializable("RequestID"));

                                    startActivity(pop);
                                }else{
                                    // nothing
                                }
                                Calendar calendar = Calendar.getInstance();
                                String Date;
                                int year=calendar.get(Calendar.YEAR);
                                int month=calendar.get(Calendar.MONTH)+1;
                                int day=calendar.get(Calendar.DAY_OF_MONTH);
                                if(month<10)
                                    Date="0"+month+"/"+day+"/"+year;
                                else
                                    Date=month+"/"+day+"/"+year;
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                                String Time =simpleDateFormat.format(calendar.getTime());
                                Map<String,Object> notificationMessage=new HashMap<>();
                                notificationMessage.put("from",userID);
                                notificationMessage.put("typeOfNoti","Delivered");
                                notificationMessage.put("typeOfEvent",TOE);
                                notificationMessage.put("Comment","--");
                                notificationMessage.put("Rate",0);
                                notificationMessage.put("Time",Time);
                                notificationMessage.put("Date",Date);
                                db.collection("users/"+donID+"/Notification").add(notificationMessage);
                            }
                        });




                        switch (documentSnapshot.getString("State")){
                            case "Delivered":
                                chat.setVisibility(View.VISIBLE);
                                checkDelivered.setVisibility(View.GONE);
                                Acceptbtn.setVisibility(View.GONE);
                              //  cancel.setVisibility(View.GONE);
                                break;
                            case "Pending":
                                chat.setVisibility(View.GONE);
                                checkDelivered.setVisibility(View.GONE);
                                Acceptbtn.setVisibility(View.VISIBLE);
                              //  cancel.setVisibility(View.GONE);
                                break;
                            case "Accepted":
                                chat.setVisibility(View.VISIBLE);
                                checkDelivered.setVisibility(View.VISIBLE);
                                Acceptbtn.setVisibility(View.GONE);
                              //  cancel.setVisibility(View.VISIBLE);
                                break;
                            case"Cancelled":
                                chat.setVisibility(View.GONE);
                                checkDelivered.setVisibility(View.GONE);
                                Acceptbtn.setVisibility(View.GONE);
                              //  cancel.setVisibility(View.GONE);
                                break;
                        }
                        if(Don!=null || Don!="--" ) {
                            final String VolID = documentSnapshot.getString("DonatorID");
                            DonName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent in = getIntent();
                                    in.putExtra("Donators", VolID);
                                    Intent intent = new Intent(VolunteerRequestInfo.this, DonatorViewInfo.class);
                                    intent.putExtra("Donators", in.getStringExtra("Donators"));
                                    startActivity(intent);

                                }
                            });

                        }


                        if (documentSnapshot.getString("State").equals("Delivered"))
                        {
                            checkDelivered.setVisibility(View.GONE);}


                        if (documentSnapshot.getString("State").equals("Pending"))
                        {
                            checkDelivered.setVisibility(View.GONE);
                            Acceptbtn.setVisibility(View.VISIBLE);
                            Acceptbtn.setOnClickListener(new View.OnClickListener() {
                            Bundle intent1 = getIntent().getExtras();
                            String ReqIDDD = (String) intent1.getSerializable("RequestID");
                            DocumentReference documentReference = db.collection("Requests").document(ReqIDDD);



                                @Override
                            public void onClick(View view) {
                                documentReference.update("State", "Accepted").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Toast.makeText(VolunteerRequestInfo.this,"state changed",Toast.LENGTH_SHORT).show();
                                    }

                                });

                                DocumentReference VolRef=db.collection("Volunteers").document(userID);
                                VolRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        VolunteerName=documentSnapshot.getString("UserName") ;
                                        documentReference.update("VolnteerName", VolunteerName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }

                                        });
                                    }
                                });

                                documentReference.update("VolnteerID", userID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                       // Toast.makeText(VolunteerRequestInfo.this, "changed vol id", Toast.LENGTH_SHORT).show();
                                        Intent i2 = new Intent(VolunteerRequestInfo.this, VolunteerRequests.class);
                                        startActivity(i2);
                                    }
                                });
                                Calendar calendar = Calendar.getInstance();
                                String Date;
                                int year=calendar.get(Calendar.YEAR);
                                int month=calendar.get(Calendar.MONTH)+1;
                                int day=calendar.get(Calendar.DAY_OF_MONTH);
                                if(month<10)
                                    Date="0"+month+"/"+day+"/"+year;
                                else
                                    Date=month+"/"+day+"/"+year;
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                                String Time =simpleDateFormat.format(calendar.getTime());

                                Map<String,Object> notificationMessage=new HashMap<>();
                                notificationMessage.put("from",userID);
                                notificationMessage.put("typeOfNoti","Accepted");
                                notificationMessage.put("typeOfEvent",TOE);
                                notificationMessage.put("Comment","--");
                                notificationMessage.put("Rate",0);
                                notificationMessage.put("Time",Time);
                                notificationMessage.put("Date",Date);
                                db.collection("users/"+donID+"/Notification").add(notificationMessage);

                            }// end of accept button
                        });
                    }
                    }


                });
    }
    }


    public void OpenVolunteerRequests(View view) {
        switch (ABEER2){
            case"Requests":
                Intent intent = new Intent(VolunteerRequestInfo.this, VolunteerRequests.class);
                startActivity(intent);
                break;
            case"tab3":
                Intent intent2 = new Intent(VolunteerRequestInfo.this, AllRequests.class);
                startActivity(intent2);
                break;
            case"tab4":
                Intent intent3 = new Intent(VolunteerRequestInfo.this, AllRequests.class);
                startActivity(intent3);
                ;
                break;
            case "history":
                Intent intent4 = new Intent(VolunteerRequestInfo.this, history_v.class);
                startActivity(intent4);


        }
    }
    public void OpenAttachment(View view){
        Intent in = getIntent();
        in.putExtra("RequestID",ABEER);
        in.putExtra("Who","V");
        in.putExtra("Where",ABEER2);
        Intent intent = new Intent(VolunteerRequestInfo.this, AttachmentPicture.class);
        intent.putExtra("RequestID", in.getStringExtra("RequestID"));
        intent.putExtra("Who", in.getStringExtra("Who"));
        intent.putExtra("Where", in.getStringExtra("Where"));
        startActivity(intent);
    }

    public void OpenTrackDonatorRequest(View view){
        Intent in = getIntent();
        in.putExtra("RequestID",ABEER);
        in.putExtra("Who","V");
        in.putExtra("Where",ABEER2);
        Intent intent = new Intent(VolunteerRequestInfo.this, VolunteerMap.class);
        intent.putExtra("RequestID", in.getStringExtra("RequestID"));
        intent.putExtra("Who", in.getStringExtra("Who"));
        intent.putExtra("Where", in.getStringExtra("Where"));
        startActivity(intent);

    }
    public void OpenChat(View view) {
        intent1 = getIntent().getExtras();
        ABEER2 = (String) intent1.getSerializable("Where");
        Intent intent = new Intent(VolunteerRequestInfo.this, ChatPageV.class);
        if (intent1 != null)
            intent.putExtra("RequestID", (String) intent1.getSerializable("RequestID"));
            intent.putExtra("Where",ABEER2);

        startActivity(intent);
    }

}
