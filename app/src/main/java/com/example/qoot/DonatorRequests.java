package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

import android.widget.AdapterView;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;


public class DonatorRequests extends AppCompatActivity {


    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String UserID,reqID;
    Request MAGIC;
    ListView listView;
    ArrayList<Request> request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_requests);
        listView=findViewById(R.id.list_Request);
        request=new ArrayList<Request>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_navigation_don);
        bottomNavigationView.setSelectedItemId(R.id.Req_don);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.notifi_don:
                        startActivity(new Intent(getApplicationContext(),DonatorNotifications.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.prfile_don:
                        startActivity(new Intent(getApplicationContext(),DonatorProfile.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.Req_don:
                        return true;
                }
                return false;
            }
        });
        Query q1 = db.collection("Requests").whereEqualTo("DonatorID",UserID).whereIn("State", Arrays.asList("Pending","Accepted"));

        q1.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String State = document.getString("State");
                                String Event = document.getString("TypeOfEvent");
                                reqID = document.getString("RequestID");
                                String REQTYPE= document.getString("RequestType");
                                String DonatorName=document.getString("DonatorName");
                                String VolunteerName=document.getString("VolnteerName");
                                MAGIC= new Request(Event, State, mAuth.getCurrentUser().getUid(), reqID, REQTYPE,DonatorName,VolunteerName);
                                request.add(MAGIC);
                                MyRequestAdapter myRequestAdapter=new MyRequestAdapter(DonatorRequests.this,R.layout.activity_single_request,request);
                                listView.setAdapter(myRequestAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Request temp = (Request) parent.getItemAtPosition(position);
                                        Intent in = getIntent();
                                        in.putExtra("RequestID",temp.getID());
                                        Intent intent = new Intent(DonatorRequests.this,DonatorRequestInfo.class);
                                        intent.putExtra("RequestID",in.getStringExtra("RequestID"));
                                        startActivity(intent);
                                    }
                                });
                            }


                        } else {
                        }
                    }
                });

    }

    public void OpenRequestForm(View view) {
        Intent intent = new Intent(DonatorRequests.this,requestForm.class);
        startActivity(intent);
        // startActivity(new Intent(DonatorRequests.this,requestForm.class));
    }

    public void OpenDonatorHis(View view) {
        Intent intent = new Intent(DonatorRequests.this,history_d.class);
        startActivity(intent);

    }
    // ------   SAME GOES HERE -------
    //public void OpenDonaterRequestInfo(View view) {
    //  Intent intent = new Intent(DonatorRequests.this,DonatorRequestInfo.class);
    // intent.putExtra("RequestID",reqID);
    //startActivity(intent);
    //}
}

class Request {

    public String EventType;
    public String Status;
    public String UserID;
    public String ID;
    public String type;
    public String numOfGuest;
    public String time;
    public String date;
    public String DonatorName;
    public String VolunteerName;

    public Uri getPicture() {
        return picture;
    }

    public void setPicture(Uri picture) {
        this.picture = picture;
    }

    public Uri picture;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public Request(){

    }

    public Request(String type, String stat, String id, String reqID, String typeOfReq,String DonatorName,String VolunteerName){
        EventType = type;
        Status = stat;
        UserID = id;
        ID = reqID;
        this.type =typeOfReq;
        this.DonatorName=DonatorName;
        this.VolunteerName=VolunteerName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEventType() {
        return EventType;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }

    public String getVolunteerName() {
        return VolunteerName;
    }

    public void setVolunteerName(String volunteerName) {
        VolunteerName = VolunteerName;
    }

    public String getDonatorName() {
        return DonatorName;
    }

    public void setDonatorName(String DonatorName) {
        this.DonatorName = DonatorName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getTypeOfReq() {
        return type;
    }

    public void setTypeOfReq(String type) {
        this.type = type;
    }


}

class MyRequestAdapter extends BaseAdapter{

    private Context context;
    ArrayList<Request> request;
    int layoutResourseId;


    MyRequestAdapter(Context context,ArrayList<Request> request){
        this.request=request;
        this.context=context;
    }

    public MyRequestAdapter(Context context, int activity_single_request, ArrayList<Request> request) {

        this.request=request;
        this.context=context;
        this.layoutResourseId=activity_single_request;


    }

    @Override
    public int getCount() {
        return request.size();
    }

    @Override
    public Object getItem(int position) {
        return request.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_single_request, null);
        TextView eventType=(TextView) view.findViewById(R.id.EventType1);
        TextView status=(TextView) view.findViewById(R.id.status1);
        ImageView icon = view.findViewById(R.id.reqType1);
        eventType.setText(request.get(position).EventType);
        String type = request.get(position).getType();
        String ss=request.get(position).Status;

        if (request.get(position).getTypeOfReq().equals("Urgent")){
            icon.setImageResource(R.drawable.greanclock);
        }
        SpannableString spannableString=new SpannableString(ss);
        if(ss.equals("Pending")){
            ForegroundColorSpan foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#FB8C00"));
            spannableString.setSpan(foregroundColorSpan,0,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            status.setText(spannableString);
        }
        else if(ss.equals("Accepted")){
            ForegroundColorSpan foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#4CAF50"));
            spannableString.setSpan(foregroundColorSpan,0,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            status.setText(spannableString);
        }
        else if(ss.equals("Cancelled")){
            ForegroundColorSpan foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#BF360C"));
            spannableString.setSpan(foregroundColorSpan,0,9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            status.setText(spannableString);
        }else if(ss.equals("Delivered")){
            ForegroundColorSpan foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#0392cf"));
            spannableString.setSpan(foregroundColorSpan,0,9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            status.setText(spannableString);
        }

        //switch (type){
        //  case"Urgent":

        //}

        return view;
    }
}





