package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class VolunteerProfile extends AppCompatActivity {
    Review MAGIC ;
    TextView more_com;
     private static int numRate=0;
    TextView no_comm;
    ListView listView;
    ArrayList<Review> review;
    TextView numVol;
    TextView averageRate;
    private TextView Username;
    private ImageView Photo;
    FirebaseAuth mAuth ;
    FirebaseFirestore db;
    CircleImageView circleImageView;
    FirebaseUser user ;
    LinearLayout linearLayout;
    public static final String TAG = "VolunteerProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_profile);
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_navigation_vol);
        bottomNavigationView.setSelectedItemId(R.id.prfile_vol);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.notifi_vol:
                        startActivity(new Intent(getApplicationContext(),volunteer_notification.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.prfile_vol:
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
        numVol=findViewById(R.id.Volunteered);
        Username = findViewById(R.id.UserNameV);
        averageRate=findViewById(R.id.RateV);
        more_com=findViewById(R.id.more_com);
       // no_comm=findViewById(R.id.No_com);
        circleImageView = findViewById(R.id.UserImage);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        String userId=mAuth.getCurrentUser().getUid();
        linearLayout = findViewById(R.id.valid);


        if(!user.isEmailVerified()){
            linearLayout.setVisibility(View.VISIBLE);
            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(VolunteerProfile.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"OnFailure: Email Not Sent");
                }
            });
        }
        Query q1 = db.collection("Requests").whereEqualTo("VolnteerID",userId).whereEqualTo("State","Delivered");
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


        Query q = db.collection("Reviews").whereEqualTo("onUserID",userId);
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           int r=0;
            float sum=0;
            String num;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        numRate++;
                        r++;
                        sum= sum+ document.getLong("Rating");
                    }
                    if(r !=0){
                        sum=sum/r;}
                    num=""+sum;
                    averageRate.setText(num.substring(0,3));
                } else {
                }
            }
        });


        final String MyUserId = mAuth.getCurrentUser().getUid();
        listView = findViewById(R.id.list_Comments);
        review = new ArrayList<Review>();
        Query q2 = db.collection("Reviews").whereEqualTo("onUserID",MyUserId);
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
                                MyReviewAdapter myReviewAdapter = new MyReviewAdapter(VolunteerProfile.this,R.layout.comments_list,review);
                                listView.setAdapter(myReviewAdapter);
                            }

                        } else {
                        }
                    }

                });


       if (numRate>2){

           more_com.setVisibility(View.VISIBLE);}


        DocumentReference documentReference =db.collection("Volunteers").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Username.setText(documentSnapshot.getString("UserName"));
                numVol.setText(documentSnapshot.getString("numVol"));
                averageRate.setText(documentSnapshot.getString("averageRate"));
            }
        });

        String user = mAuth.getCurrentUser().getUid();
        download(user+".png");
    }
    private void download(String imageName) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference mainRef = firebaseStorage.getReference("Images");
        final File file = new File(getFilesDir(), imageName);
        mainRef.child(imageName).getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Uri u =Uri.parse(file.toString());
                    circleImageView.setImageURI(u);
                    circleImageView.requestLayout();
                    //Photo.getLayoutParams().height = 400;
                    //Photo.getLayoutParams().width = 400;
                } else {

                }
            }
        });
    }

    public void OpenEditProfilePage(View view){
        startActivity(new Intent(VolunteerProfile.this,EditVolunteerProfile.class));
    }

    public void OpenLogOut(View view){
       // FirebaseAuth.getInstance().signOut();
        Toast.makeText(VolunteerProfile.this, "log out Was Successful!!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(VolunteerProfile.this,LogIn.class));
    }

    public void OpenAllComments(View view) {
        Intent intentC = new Intent(VolunteerProfile.this,VolunteerAllComment.class);
        //intentC.putExtra("MyUserId",intentC.getStringExtra("MyUserId"));
        startActivity(intentC);
    }
}
