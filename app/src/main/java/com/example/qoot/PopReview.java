package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class PopReview extends Activity {

    EditText Name;
    EditText Comment;
    RatingBar Rate;
    Button send;

    //Button notNow;
    TextView close;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userID, ReqIDDD, on_user, myName,Date,Time;
    String name, comment;
    double rate;
    Bundle myIntent;
    Calendar calendar;
    Timestamp DT;
    int day,month,year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_review);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        // curve
        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -40;
        getWindow().setAttributes(params);

        getWindow().setLayout((int)(width*.85),(int)(height*.5));


        //Name = findViewById(R.id.et_name);
        Comment = findViewById(R.id.review);
        Rate = findViewById(R.id.rate_star);
        send = findViewById(R.id.btn_send_review);
        //notNow = findViewById(R.id.btn_not_now);
        close = findViewById(R.id.close_X);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //name = Name.getText().toString();
                comment = Comment.getText().toString();
                rate = ((double) Rate.getRating());


                /*if (TextUtils.isEmpty(name)) {
                    Name.setError("Please Enter Your Name");
                    return;
                }*/
                if (TextUtils.isEmpty(comment)) {
                    Comment.setError("Please Enter Your Comments");
                    return;
                }

                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                myIntent = getIntent().getExtras();
                if (myIntent != null) {
                    ReqIDDD = (String) myIntent.getSerializable("RequestID");
                }

                DocumentReference reqid = db.collection("Requests").document(ReqIDDD);
                reqid.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        on_user = documentSnapshot.getString("DonatorID");
                        myName = documentSnapshot.getString("VolnteerName");
                        String TOE=documentSnapshot.getString("TypeOfEvent");
                        calendar = Calendar.getInstance();
                        DT=Timestamp.now();
                        year=calendar.get(Calendar.YEAR);
                        month=calendar.get(Calendar.MONTH)+1;
                        day=calendar.get(Calendar.DAY_OF_MONTH);
                        if(month<10)
                            Date="0"+month+"/"+day+"/"+year;
                        else
                            Date=month+"/"+day+"/"+year;
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                        Time =simpleDateFormat.format(calendar.getTime());



                /// First try to add in DB
                //Review MyReview = new Review(on_user,name,comment,rate);
                //DocumentReference documentReference = db.collection("Reviews").document(userID);

                Map<String, Object> review = new HashMap<>();
                review.put("CommenterID", userID);
                review.put("CommenterName", myName);
                review.put("onUserID", on_user);
                review.put("Comment", comment);
                review.put("Rating", rate);
                review.put("RequestId",ReqIDDD);
                review.put("Date",Date);
                review.put("Time",Time);
                review.put("Date_t",DT);

                        Map<String,Object> notificationMessage=new HashMap<>();
                        notificationMessage.put("from",userID);
                        notificationMessage.put("typeOfNoti","Review");
                        notificationMessage.put("typeOfEvent",TOE);
                        notificationMessage.put("Comment",comment);
                        notificationMessage.put("Rate",rate);
                        notificationMessage.put("Time",Time);
                        notificationMessage.put("Date",Date);
                        db.collection("users/"+on_user+"/Notification").add(notificationMessage);
                //review.put("Timestamb", FieldValue.serverTimestamp());

                /// second try to add in DB
                db.collection("Reviews")
                        .add(review)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText( PopReview.this,"Thank You!",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PopReview.this, "Something Went Wrong,Try Again ! " , Toast.LENGTH_SHORT).show();
                            }
                        });
                        /// First try to add in DB
                        //Review MyReview = new Review(on_user,name,comment,rate);
                        //DocumentReference documentReference = db.collection("Reviews").document(userID);
/*
                        Map<String, Object> review = new HashMap<>();
                        review.put("CommenterID", userID);
                        review.put("onUserID", on_user);
                        review.put("Comment", comment);
                        review.put("Rating", rate);
                        review.put("RequestId",ReqIDDD);

                        /// second try to add in DB
                        db.collection("Reviews")
                                .add(review)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText( PopReview.this,"Thank You!",Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PopReview.this, "Something Went Wrong,Try Again ! " , Toast.LENGTH_SHORT).show();
                                    }
                                }); */
                    }
                });

                Intent i = new Intent(PopReview.this,VolunteerRequests.class);
                startActivity(i);
            }
        });


/*
                /// third try to add in DB
                DocumentReference documentReference1 = db.collection("Reviews").document(userID);
                Map<String, Object> review = new HashMap<>();
                review.put("Commenter", name);
                review.put("onUserID", on_user);
                review.put("Comment", comment);
                review.put("Rating", rate);
                review.put("RequestId",ReqIDDD);
                documentReference1.set(review).addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.d(TAG,"OnFailure "+ e.toString());
                    }
                });

            }
        });*/
    }
}



/*
class Review{
    String onUserID;
    String commenter;
    String comment;
    float rate;

    public Review(String onUserID, String commenter, String comment, float rate) {
        this.onUserID = onUserID;
        this.commenter = commenter;
        this.comment = comment;
        this.rate = rate;
    }

    public Review(String onUserID) {
        this.onUserID = onUserID;
    }


    public String getOnUserID() {
        return onUserID;
    }

    public void setOnUserID(String onUserID) {
        this.onUserID = onUserID;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
*/
