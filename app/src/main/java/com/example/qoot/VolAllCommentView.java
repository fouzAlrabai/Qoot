package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VolAllCommentView extends AppCompatActivity {

    Review MAGIC;
    ListView listView;
    ArrayList<Review> review;
    FirebaseAuth mAuth ;
    FirebaseFirestore db;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_all_comment_view);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.list_co);
        review = new ArrayList<Review>();
        imageView=findViewById(R.id.imageView);

        Bundle intent1 = getIntent().getExtras();

        if (intent1 != null) {
            final String VolID = (String) intent1.getSerializable("Volunteers");

            Query q1 = db.collection("Reviews").whereEqualTo("onUserID", VolID);

            q1.get()
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
                                    MyReviewAdapter2 myReviewAdapter2 = new MyReviewAdapter2(VolAllCommentView.this, R.layout.comments_list, review);
                                    listView.setAdapter(myReviewAdapter2);
                                }

                            } else {
                                Toast.makeText(VolAllCommentView.this, "Something Wrong! Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = getIntent();
                    in.putExtra("Volunteers", VolID);
                    Intent intent = new Intent(VolAllCommentView.this, VolunteerViewInfo.class);
                    intent.putExtra("Volunteers", in.getStringExtra("Volunteers"));
                    startActivity(intent);

                }
            });

        }
    }

}
