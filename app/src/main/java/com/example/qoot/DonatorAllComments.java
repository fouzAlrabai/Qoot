package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DonatorAllComments extends AppCompatActivity {

    Review MAGIC;
    ListView listView;
    ArrayList<Review> review;
    FirebaseAuth mAuth ;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_all_comments);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        final String userId = mAuth.getCurrentUser().getUid();

        listView = findViewById(R.id.list_co);
        review = new ArrayList<Review>();



            Query q1 = db.collection("Reviews").whereEqualTo("onUserID", userId);
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
                                    MyReviewAdapter2 myReviewAdapter2 = new MyReviewAdapter2(DonatorAllComments.this, R.layout.comments_list, review);
                                    listView.setAdapter(myReviewAdapter2);
                                }

                            } else {
                                Toast.makeText(DonatorAllComments.this, "Something Wrong! Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
            }

    public void OpenDonatorProfile(View view) {
        Intent intentC = new Intent(DonatorAllComments.this, DonatorProfile.class);
        startActivity(intentC);
    }
}
class MyReviewAdapter2 extends BaseAdapter {

    private Context context;
    ArrayList<Review> review;
    int layoutResourseId;


    MyReviewAdapter2(Context context,ArrayList<Review> review){
        this.review=review;
        this.context=context;
    }

    public MyReviewAdapter2(Context context, int comments_list, ArrayList<Review> review) {

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

        Commenter.setText(review.get(position).getCommenterName());
        comment.setText(review.get(position).getComment());
        rate.setRating(review.get(position).getRate());
        return view;
    }
}