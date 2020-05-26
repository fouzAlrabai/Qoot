package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class history_d extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String UserID,reqID;
    Request MAGIC;
    ListView listView;


    ArrayList<Request> request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_d);
        listView=findViewById(R.id.hisDList);
        request=new ArrayList<Request>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        Query q1 = db.collection("Requests").whereEqualTo("DonatorID",UserID).whereIn("State", Arrays.asList("Cancelled","Delivered"));
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
                                MyRequestAdapter1 myRequestAdapter=new MyRequestAdapter1(history_d.this,R.layout.single_his,request);
                                listView.setAdapter(myRequestAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Request temp = (Request) parent.getItemAtPosition(position);
                                        Intent in = getIntent();
                                        in.putExtra("RequestID",temp.getID());
                                        Intent intent = new Intent(history_d.this,DonatorRequestInfo.class);
                                        intent.putExtra("Where", "history");
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
    public void opentDonatorRequset(View view){
        startActivity(new Intent(history_d.this,DonatorRequests.class));
    }
}



class MyRequestAdapter1 extends BaseAdapter {

    private Context context;
    ArrayList<Request> request;
    int layoutResourseId;


    MyRequestAdapter1(Context context,ArrayList<Request> request){
        this.request=request;
        this.context=context;
    }

    public MyRequestAdapter1(Context context, int activity_single_his, ArrayList<Request> request) {

        this.request=request;
        this.context=context;
        this.layoutResourseId=activity_single_his;


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
        View view = LayoutInflater.from(context).inflate(R.layout.single_his, null);
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


