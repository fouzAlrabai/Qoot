
package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class history_v extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String UserID,reqID;
    Request MAGIC;
    ListView listView;


    ArrayList<Request> request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_v);
        listView=findViewById(R.id.hisVList);
        request=new ArrayList<Request>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        Query q1 = db.collection("Requests").whereEqualTo("VolnteerID",UserID).whereIn("State", Arrays.asList("Cancelled","Delivered"));
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
                                MyRequestAdapter1 myRequestAdapter=new MyRequestAdapter1(history_v.this,R.layout.single_his,request);
                                listView.setAdapter(myRequestAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Request temp = (Request) parent.getItemAtPosition(position);
                                        Intent in = getIntent();
                                        in.putExtra("RequestID",temp.getID());
                                        Intent intent = new Intent(history_v.this,VolunteerRequestInfo.class);
                                        intent.putExtra("RequestID",in.getStringExtra("RequestID"));
                                        intent.putExtra("Where", "history");
                                        startActivity(intent);
                                    }
                                });
                            }


                        } else {
                        }
                    }
                });

    }
    public void openVRequest(View view){
        startActivity(new Intent(history_v.this,VolunteerRequests.class));
    }
}
