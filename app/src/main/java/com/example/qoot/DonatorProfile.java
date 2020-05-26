package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.File;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonatorProfile extends AppCompatActivity {

    TextView Username;
    ImageView Photo;
    private static int nuRate=0;
    TextView numDona ;
    TextView TextRate;
    TextView more_com;
    TextView no_comm;
    LinearLayout linearLayout;
    ConstraintLayout root ;
    Review MAGIC ;
    ArrayList<Review> review;
    ListView listView;
    // eventually we will add comments and ratings as well
    FirebaseAuth mAuth ;
    FirebaseFirestore db;
    StorageReference mfStore;
    FirebaseUser user ;
    CircleImageView circleImageView;
    public static final String TAG = "DonatorProfile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_profile);
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_navigation_don);
        bottomNavigationView.setSelectedItemId(R.id.prfile_don);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.notifi_don:
                        startActivity(new Intent(getApplicationContext(),DonatorNotifications.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.prfile_don:
                        return true;

                    case R.id.Req_don:
                        startActivity(new Intent(getApplicationContext(),DonatorRequests.class));
                        overridePendingTransition(0,0);
                        return false;
                }
                return false;
            }
        });
        numDona=findViewById(R.id.Donations);
        TextRate=findViewById(R.id.RateD);
        Username = findViewById(R.id.UserNameD);
        more_com = findViewById(R.id.more_com);
        // no_comm = findViewById(R.id.No_com);
        circleImageView = findViewById(R.id.UserImage);
        linearLayout = findViewById(R.id.valid);
        root = findViewById(R.id.rootProfile);
        mAuth = FirebaseAuth.getInstance();
        mfStore = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        //Donations = findViewById(R.id.Donations);
        final String userId=mAuth.getCurrentUser().getUid();
        user = mAuth.getCurrentUser();

        Query q1 = db.collection("Requests").whereEqualTo("DonatorID",userId).whereEqualTo("State","Delivered");
        q1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            int don=0;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        don += 1; }
                    numDona.setText(""+don);
                } else {
                }
            }
        });

        Query q = db.collection("Reviews").whereEqualTo("onUserID",userId);
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             int R=0;
            float sum=0;
            String num;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        nuRate++;
                        R++;
                        sum= sum+ document.getLong("Rating");
                    }
                    if(R !=0){
                        sum=sum/R;}
                    num=""+sum;
                    TextRate.setText(num.substring(0,3));
                } else {
                }
            }
        });





        if(!user.isEmailVerified()){
            root.removeView(linearLayout);
            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DonatorProfile.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG,"OnFailure: Email Not Sent");
                }
            });
        }

        //// comment section
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
                                MyReviewAdapter myReviewAdapter = new MyReviewAdapter(DonatorProfile.this,R.layout.comments_list,review);
                                listView.setAdapter(myReviewAdapter);
                            }

                        } else {
                        }
                    }

                });

        //  if (!numDona.equals("0"))
        //   no_comm.setVisibility(View.VISIBLE);

        if (nuRate>2){
            more_com.setVisibility(View.VISIBLE);}
            /* count donations

        Query q2 = db.collection("Requests").whereEqualTo("DonatorID",userId).whereEqualTo("State","Delivered");
        q2.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    donation++;
                            }
                        }
                    }
                });
            //Donations.setText(donation);
        */
        ///// END of comments section
        DocumentReference documentReference =db.collection("Donators").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Username.setText(documentSnapshot.getString("UserName"));
                numDona.setText(documentSnapshot.getString("numDona"));
                TextRate.setText(documentSnapshot.getString("TextRate"));

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
                //Toast.makeText(DonatorProfile.this, "photo : "+file, Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(DonatorProfile.this,EditDonatorProfile.class));
    }
    public void OpenLogOut(View view) {
        Toast.makeText(DonatorProfile.this, "log out Was Successful!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DonatorProfile.this,LogIn.class);
        startActivity(intent);
    }

    public void OpenAllComments(View view) {
        Intent intentC = new Intent(DonatorProfile.this, DonatorAllComments.class);
        //intentC.putExtra("MyUserId",intentC.getStringExtra("MyUserId"));
        startActivity(intentC);
    }

}

class Review{
    String commenterName;
    String comment;
    float rate;
    Timestamp Date_t;


    public Review(String commenterName, String comment, float rate) {
        this.commenterName = commenterName;
        this.comment = comment;
        this.rate = rate;

    }

    public String getCommenterName() {
        return commenterName;
    }
    public Timestamp getDate_t() { return Date_t; }
    public void setDate_t(String comment) {
        this.Date_t = Date_t;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
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

class MyReviewAdapter extends BaseAdapter {

    private Context context;
    ArrayList<Review> review;
    int layoutResourseId;


    MyReviewAdapter(Context context,ArrayList<Review> review){
        this.review=review;
        this.context=context;
    }

    public MyReviewAdapter(Context context, int comments_list, ArrayList<Review> review) {

        this.review=review;
        this.context=context;
        this.layoutResourseId = comments_list;
    }

    @Override
    public int getCount() {
        return review.size();
    }

    @Override
    public Object getItem(int position) {
        return review.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_list, null);

        TextView Commenter =(TextView) view.findViewById(R.id.commenter_name);
        TextView comment =(TextView) view.findViewById(R.id.tv_desc_review);
        RatingBar rate = (RatingBar)view.findViewById(R.id.rate_star);
        /*
        RatingBar.setText(review.get(position).EventType);
        String type = review.get(position).getType();
        String ss=review.get(position).Status;
        */
        Commenter.setText(review.get(position).getCommenterName());
        comment.setText(review.get(position).getComment());
        rate.setRating(review.get(position).getRate());
        return view;
    }
}