package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonatorViewInfo extends AppCompatActivity {
    Review MAGIC ;
    ListView listView;
    ArrayList<Review> review;
    TextView numVol;
    TextView averageRate;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView car,name,mor_com;
    CircleImageView circleImageView;
    ImageView imageView;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_view_info);
        name = findViewById(R.id.UserNameDv);
        //car = findViewById(R.id.car);
        circleImageView = findViewById(R.id.colo);
        numVol=findViewById(R.id.Donations);
        averageRate=findViewById(R.id.RateD);
        mor_com=findViewById(R.id.more_com);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Bundle intent1 = getIntent().getExtras();

        if (intent1 != null) {
            final String VolID = (String) intent1.getSerializable("Donators");
            DocumentReference documentReference = db.collection("Donators").document(VolID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    name.setText(documentSnapshot.getString("UserName"));
                    getPicturePath(VolID);
                }
            });

            Query q1 = db.collection("Requests").whereEqualTo("DonatorID",VolID).whereEqualTo("State","Delivered");
            q1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                int Vol=0;
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Vol += 1; }
                        numVol.setText(""+Vol);
                    } else {
                    }
                }
            });

            Query q = db.collection("Reviews").whereEqualTo("onUserID",VolID);
            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                int numRate=0;
                float sum=0;
                String num;
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            numRate++;
                            sum= sum+ document.getLong("Rating");
                        }
                        if(numRate !=0){
                            sum=sum/numRate;}
                        num=""+sum;
                        averageRate.setText(num.substring(0,3));
                    } else {
                    }
                }
            });

            listView = findViewById(R.id.list_Comments);
            review = new ArrayList<Review>();
            Query q2 = db.collection("Reviews").whereEqualTo("onUserID",VolID);
            q2.limit(3).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String VolName = document.getString("CommenterName");
                                    String comment = document.getString("Comment");
                                    float rate = document.getLong("Rating");

                                    MAGIC = new Review(VolName, comment, rate);
                                    review.add(MAGIC);
                                    MyReviewAdapter myReviewAdapter = new MyReviewAdapter(DonatorViewInfo.this,R.layout.comments_list,review);
                                    listView.setAdapter(myReviewAdapter);
                                }

                            } else {
                            }
                        }

                    });

           mor_com.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = getIntent();
                    in.putExtra("Donators", VolID);
                    Intent intent = new Intent(DonatorViewInfo.this, DonViewAllComment.class);
                    intent.putExtra("Donators", in.getStringExtra("Donators"));
                    startActivity(intent);

                }
            });

        }
    }

        public void getPicturePath(String Vid){
            String ImageName = Vid+".png";

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference mainRef = firebaseStorage.getReference("Images");
            final File file = new File(getFilesDir(), ImageName);

            mainRef.child(ImageName).getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        uri = Uri.parse(file.toString());
                        if(uri!=null){
                        circleImageView.setImageURI(uri);
                        circleImageView.requestLayout();}

                    }
                }
            });
        }


    public void OpenVolunteerNoti(View view) {

            startActivity(new Intent(DonatorViewInfo.this,volunteer_notification.class));

    }

    public void OpenAllComments(View view) {
        Intent intentC = new Intent(DonatorViewInfo.this, DonatorAllComments.class);
        //intentC.putExtra("MyUserId",intentC.getStringExtra("MyUserId"));
        startActivity(intentC);
    }
}
