package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class volunteer_notification extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String UserID, reqID;
    Notification notify;
    ListView listViewNoti;
    ArrayList<Notification> notificarion;
    MyNotificationsAdapter myNotificationsAdapter;
    ArrayList<Uri> userIDS;


    //Abeer
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_notification);
        listViewNoti=findViewById(R.id.list_Requestnoti);
        notificarion = new ArrayList<Notification>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        userIDS=new ArrayList<Uri>();

        //BottomNavigationViewStart
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_navigation_vol);
        bottomNavigationView.setSelectedItemId(R.id.notifi_vol);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.prfile_vol:
                        startActivity(new Intent(getApplicationContext(),VolunteerProfile.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.notifi_vol:
                        return true;

                    case R.id.Req_vol:
                        startActivity(new Intent(getApplicationContext(),VolunteerRequests.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.browse_vol:
                        startActivity(new Intent(getApplicationContext(),AllRequests.class));
                        overridePendingTransition(0,0);
                        return false;


                }
                return false;
            }
        });

        //BottomNavigationViewEnd

        Query q1 = db.collection("users").document(UserID).collection("Notification");
        q1.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String Comment = document.getString("Comment");
                                final String Date= document.getString("Date");
                                final float Rate1 = document.getLong("Rate");
                                //final float Rate1=Float.parseFloat(rate);
                                final String Time = document.getString("Time");
                                final String from = document.getString("from");
                                final String typeOfEvent = document.getString("typeOfEvent");
                                final String typeOfNoti = document.getString("typeOfNoti");
                                final String Msg;
                                if(Comment.equals("--")){
                                    Msg=typeOfNoti+" Your "+typeOfEvent+" Request";
                                }else{
                                    Msg=typeOfNoti+" You";
                                }


                                DocumentReference query = db.collection("users").document(from);

                                query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                String from_Type = documentSnapshot.getString("Type");
                                                //Toast.makeText(context, "The Type Is: " + from_Type, Toast.LENGTH_SHORT).show();
                                                DocumentReference query2 = db.collection(from_Type + "s").document(from);
                                                query2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot documentSnapshot = task.getResult();
                                                            if (documentSnapshot.exists()) {
                                                                String from_name = documentSnapshot.getString("UserName");
                                                                //Toast.makeText(context, "The Name Is: " + from_name, Toast.LENGTH_SHORT).show();
                                                                notify = new Notification(Comment,Date,Rate1,Time, from, typeOfEvent, typeOfNoti, Msg,from_name);
                                                                notificarion.add(notify);
                                                                myNotificationsAdapter = new MyNotificationsAdapter(volunteer_notification.this, R.layout.activity_single_notification, notificarion);
                                                                listViewNoti.setAdapter(myNotificationsAdapter);
                                                            } else {
                                                                String from_name = " ";
                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                String from_Type = " ";
                                            }
                                        }
                                    }
                                });



                                listViewNoti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        final Notification temp = (Notification) parent.getItemAtPosition(position);
                                        String VolID = temp.getFrom();
                                        Intent in = getIntent();
                                        in.putExtra("Donators", VolID);
                                        Intent intent = new Intent(volunteer_notification.this, DonatorViewInfo.class);
                                        intent.putExtra("Donators", in.getStringExtra("Donators"));
                                        startActivity(intent);
                                    }
                                });
                            }

                        } else {
                        }
                    }

                });


    }
}

class MyNotificationsVolAdapter extends BaseAdapter {

    private Context context;
    ArrayList<Notification> notificarion;
    int layoutResourseId;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CircleImageView circleImageView;
    Uri uri;






    MyNotificationsVolAdapter(Context context, ArrayList<Notification> notificarion) {
        this.notificarion = notificarion;
        this.context = context;
    }

    public MyNotificationsVolAdapter(Context context, int activity_single_notification,ArrayList<Notification> notificarion) {
        this.notificarion = notificarion;
        this.context = context;
        this.layoutResourseId = activity_single_notification;
    }


    @Override
    public int getCount() {
        return notificarion.size();
    }

    @Override
    public Object getItem(int position) {
        return notificarion.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getViewTypeCount(){
        return 2;
    }

    public int getItemViewType(int position){
        if(notificarion!=null)
            return 0;
        else return 1;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // =============================================initialization===============================================================
        View view = LayoutInflater.from(context).inflate(R.layout.activity_single_notification, null);
        String Comment =notificarion.get(position).getComment();
        float Rate =notificarion.get(position).getRate();
        final String from =notificarion.get(position).getFrom();
        String TypeOfEvent=notificarion.get(position).getEvent_Type();
        String typeOfNotify=notificarion.get(position).getNotifiarion_Type();
        String from_name=notificarion.get(position).getFrom_name();
        String msg=notificarion.get(position).getMessage();
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(from_name+" ");
        //===================================Request===================================
        if(!typeOfNotify.equals("Review")) {
            if (typeOfNotify.equals("Pending")) {
                SpannableString state1 = new SpannableString(typeOfNotify);
                state1.setSpan(new ForegroundColorSpan(Color.parseColor("#FB8C00")), 0, typeOfNotify.length(), 0);
                builder.append(state1);
            } else if (typeOfNotify.equals("Accepted")) {
                SpannableString state1 = new SpannableString(typeOfNotify);
                state1.setSpan(new ForegroundColorSpan(Color.parseColor("#4CAF50")), 0, typeOfNotify.length(), 0);
                builder.append(state1);
            } else if (typeOfNotify.equals("Cancelled")) {
                SpannableString state1 = new SpannableString(typeOfNotify);
                state1.setSpan(new ForegroundColorSpan(Color.parseColor("#BF360C")), 0, typeOfNotify.length(), 0);
                builder.append(state1);
            } else if (typeOfNotify.equals("Delivered")) {
                SpannableString state1 = new SpannableString(typeOfNotify);
                state1.setSpan(new ForegroundColorSpan(Color.parseColor("#0392cf")), 0, typeOfNotify.length(), 0);
                builder.append(state1);
            }
            SpannableString TypeOfEvent1 = new SpannableString(TypeOfEvent);
            StyleSpan boldStyle=new StyleSpan(Typeface.BOLD);
            TypeOfEvent1.setSpan(boldStyle,0,TypeOfEvent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(" Your " + TypeOfEvent1 + " Request");
            // ==================================Review========================================
        }else{

        }

        TextView volunteerName = (TextView) view.findViewById(R.id.requests);
        circleImageView = (CircleImageView) view.findViewById(R.id.colo1);
        volunteerName.setText(builder, TextView.BufferType.SPANNABLE);

        return view;
    }

}
