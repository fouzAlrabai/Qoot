package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import javax.annotation.Nullable;

public class AttachmentPicture extends AppCompatActivity {

    //Widget
    ImageView picture;
    TextView no;
    //Intent
    Bundle intent1;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    //vars
    String RequestID;
    String ImageName;
    //Deciding Variable
    String Type;
    String Abeer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_picture);

        picture = findViewById(R.id.ATTACHED);
        no = findViewById(R.id.NO_attach);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        intent1 = getIntent().getExtras();

        if (intent1 != null) {
            RequestID = (String) intent1.getSerializable("RequestID");
            Type = (String) intent1.getSerializable("Who");
            Abeer = (String) intent1.getSerializable("Where");
            DocumentReference documentReference = db.collection("Requests").document(RequestID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    ImageName = documentSnapshot.getString("Photo");
                    if(ImageName != null) {
                        if (!ImageName.equals("--")) {
                            download(ImageName);
                        } else if (ImageName.equals("--") || ImageName.equals(null)) {
                            picture.setVisibility(View.GONE);
                            no.setVisibility(View.VISIBLE);
                        }
                    }else {
                        picture.setVisibility(View.GONE);
                        no.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
    public void OpenRequestInfo(View view) {
        switch (Type) {
            case"D":
            Intent in = getIntent();
                in.putExtra("RequestID", RequestID);
                in.putExtra("Who",Type);
                in.putExtra("Where",Abeer);
            Intent intent = new Intent(AttachmentPicture.this, DonatorRequestInfo.class);
                intent.putExtra("RequestID", in.getStringExtra("RequestID"));
                intent.putExtra("Who", in.getStringExtra("Who"));
                intent.putExtra("Where", in.getStringExtra("Where"));
            startActivity(intent);
            break;
            case"V":
                Intent in2 = getIntent();
                    in2.putExtra("Who",Type);
                    in2.putExtra("Where",Abeer);
                    in2.putExtra("RequestID", RequestID);
                Intent intent2 = new Intent(AttachmentPicture.this, VolunteerRequestInfo.class);
                    intent2.putExtra("RequestID", in2.getStringExtra("RequestID"));
                    intent2.putExtra("Who", in2.getStringExtra("Who"));
                    intent2.putExtra("Where", in2.getStringExtra("Where"));
                startActivity(intent2);
                break;
        }
    }
    private void download(String imageName) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference mainRef = firebaseStorage.getReference("UrgentRequest");
        final File file = new File(getFilesDir(), imageName);
        mainRef.child(imageName).getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Uri u = Uri.parse(file.toString());
                    picture.setImageURI(u);
                } else {

                }
            }
        });
    }
}