package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VolunteerViewInfo extends AppCompatActivity {
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
        setContentView(R.layout.activity_volunteer_view_info);
        name=findViewById(R.id.UserNameVv);
        car=findViewById(R.id.car);
        circleImageView=findViewById(R.id.colo);
        imageView=findViewById(R.id.UserImage);
        numVol=findViewById(R.id.Volunteered);
        averageRate=findViewById(R.id.RateV);
        mor_com=findViewById(R.id.more_com);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Bundle intent1 = getIntent().getExtras();


            //String userId=mAuth.getUid();






            //final String MyUserId = mAuth.getCurrentUser().getUid();

        if (intent1 != null) {
            final String VolID = (String) intent1.getSerializable("Volunteers");
            DocumentReference documentReference = db.collection("Volunteers").document(VolID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>(){

                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    name.setText(documentSnapshot.getString("UserName"));
                    car.setText(documentSnapshot.getString("Vehicle"));
                    //numVol.setText(documentSnapshot.getString("numVol"));
                    //averageRate.setText(documentSnapshot.getString("averageRate"));
                    getPicturePath(VolID);
                }
            });

            Query q1 = db.collection("Requests").whereEqualTo("VolnteerID",VolID).whereEqualTo("State","Delivered");
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
                                    MyReviewAdapter myReviewAdapter = new MyReviewAdapter(VolunteerViewInfo.this,R.layout.comments_list,review);
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
                    in.putExtra("Volunteers", VolID);
                    Intent intent = new Intent(VolunteerViewInfo.this, VolAllCommentView.class);
                    intent.putExtra("Volunteers", in.getStringExtra("Volunteers"));
                    startActivity(intent);

                }
            });




            /*DocumentReference documentReference2 = db.collection("profilePicture").document(VolID);
            documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>(){

                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    String photo=documentSnapshot.getString("link");
                    if(photo!=null){
                    Uri link = Uri.parse(photo);
                        Toast.makeText(VolunteerViewInfo.this, "your photo "+link , Toast.LENGTH_SHORT).show();


                    circleImageView.setImageURI(link);
                    //Picasso.with(VolunteerViewInfo.this).load(link).into(imageView);
                    }




                }
            });*/


        }


    }
    public void getPicturePath(String Vid){
        String ImageName = Vid+".png";

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference mainRef = firebaseStorage.getReference("Images");
        final File file = new File(getFilesDir(), ImageName);
        //Toast.makeText(DonatorViewInfo.this, "photo : "+file, Toast.LENGTH_SHORT).show();

        mainRef.child(ImageName).getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uri = Uri.parse(file.toString());
                    //Toast.makeText(VolunteerViewInfo.this, "photo in: "+uri, Toast.LENGTH_SHORT).show();
                    if(uri!=null){
                    circleImageView.setImageURI(uri);
                    circleImageView.requestLayout();}

                }
            }
        });
        //return uri;
    }
    public void OpenDonatorNoti(View view) {
        startActivity(new Intent(VolunteerViewInfo.this,DonatorNotifications.class));
    }
    public void OpenAllComments(View view) {
        Intent intentC = new Intent(VolunteerViewInfo.this, DonatorAllComments.class);
        //intentC.putExtra("MyUserId",intentC.getStringExtra("MyUserId"));
        startActivity(intentC);
    }
}
