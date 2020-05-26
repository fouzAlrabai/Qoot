package com.example.qoot;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class tab3 extends Fragment {
    FirebaseAuth mAuth ;
    FirebaseFirestore db;
    String USerID;
    String RequestID;
    Request MAGIC;
    GridView gridView;
    ArrayList <Request> request;
    private Context mContext;
    String userID;
    String VolunteerName;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }
    public tab3() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_tab3, container, false);
        gridView=view.findViewById(R.id.grid_Request);
        request=new ArrayList<Request>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID =mAuth.getCurrentUser().getUid();
        Query q1 = db.collection("Requests").whereEqualTo("State","Pending").whereEqualTo("RequestType","Urgent");
        q1.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String Event = document.getString("TypeOfEvent");
                                String Time = document.getString("Time");
                                RequestID = document.getString("RequestID");
                                String REQTYPE= document.getString("RequestType");
                                String DonatorName=document.getString("DonatorName");
                                String VolunteerName=document.getString("VolnteerName");

                                MAGIC = new Request(Event, Time, USerID, RequestID, REQTYPE,DonatorName,VolunteerName);
                                request.add(MAGIC);
                                MyBrowseRequestAdapter myRequestAdapter = new MyBrowseRequestAdapter(getActivity(), R.layout.activity_browse_single_request, request);
                                gridView.setAdapter(myRequestAdapter);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                        Request temp = (Request) parent.getItemAtPosition(position);
                                        Intent in = getActivity().getIntent();
                                        in.putExtra("RequestID", temp.getID());
                                        in.putExtra("Where","tab3");
                                        Intent intent = new Intent(getActivity(), VolunteerRequestInfo.class);
                                        intent.putExtra("Where", in.getStringExtra("Where"));
                                        intent.putExtra("RequestID", in.getStringExtra("RequestID"));
                                        startActivity(intent);
                                    }
                                });

                            }
                        } else {

                        }
                    }
                });
        return view;
    }


}
class MyBrowseRequestAdapter extends BaseAdapter {
    private Context context;
    ArrayList<Request> request;
    int layoutResourseId;

    MyBrowseRequestAdapter(Context context,ArrayList<Request> request){
        this.request=request;
        this.context=context;
    }
    public MyBrowseRequestAdapter(Context context, int activity_browse_single_request, ArrayList<Request> request)
    {
        this.request=request;
        this.context=context;
        this.layoutResourseId=activity_browse_single_request;
        int size=getCount();
        // Toast.makeText(context,"size :", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(context).inflate(R.layout.activity_browse_single_request, null);
        TextView eventType=(TextView) view.findViewById(R.id.EventType1);
        TextView status=(TextView) view.findViewById(R.id.status1);
        eventType.setText(request.get(position).EventType);
        status.setText(request.get(position).Status);
        return view;
    }
}