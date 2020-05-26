package com.example.qoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class EditDonatorProfile extends AppCompatActivity {

    public EditText NEW_NAME;
    public EditText NEW_PHONE;

    public EditText NEW_PASSWORD;
    public EditText NEW_EMAIL;

    public ImageView NEW_IMAGE;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    String userId;
    private StorageTask UploadTask;
    StorageReference file;


    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    //  save button
    Button saveButton, Upload, Choose;

    // what user type in fields
    String s1, s2, s3 ,s4, Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donator_profile);
        NEW_IMAGE = findViewById(R.id.UserImage);
        NEW_NAME = findViewById(R.id.Name);
        NEW_PHONE = findViewById(R.id.Phone_v);
        NEW_EMAIL =findViewById(R.id.emailDonator);
        NEW_PASSWORD =findViewById(R.id.Password);
        saveButton = findViewById(R.id.button);
        Upload = findViewById(R.id.Upload);
        Choose = findViewById(R.id.choose);

        // firebase initialize
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userId=mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
//--------------------------------------------------------------------------------------------------------------------
        DocumentReference documentReference =db.collection("Donators").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                NEW_NAME.setHint(documentSnapshot.getString("UserName"));
            }
        });
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                NEW_PHONE.setHint(documentSnapshot.getString("PhoneNumber"));
            }
        });
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                NEW_EMAIL.setHint(documentSnapshot.getString("Email"));
            }
        });

        // retrieve the image
//---------------------------------------------------------------------------------------------------------------------
        //------------------------ Code for Pic------------------------------------------------------

        Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UploadTask != null && UploadTask.isInProgress()){

                    Toast.makeText(EditDonatorProfile.this,"Hold on Image is Uploading",Toast.LENGTH_SHORT).show();
                }else
                    uploadFile();

            }
        });






        //-------------------------- OverAll update --------------------------------------------------
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                s1 = NEW_NAME.getText().toString();
                s2 = NEW_PHONE.getText().toString();
                s3 =NEW_PASSWORD.getText().toString();
                s4 = NEW_EMAIL.getText().toString();

                //------ check name -------------
                if (!s1.isEmpty()) {
                    s1 = NEW_NAME.getText().toString();
                    if (s1.length() == 1) {
                        NEW_NAME.setError("Please enter a valid name length");
                        return;
                    }
                    s1 = NEW_NAME.getText().toString();
                }

                if (!s4.isEmpty()){
                    if(!isValid(s4)){
                        NEW_EMAIL.setError("Enter a valid Email");
                        return;}
                    s4 =NEW_EMAIL.getText().toString();
                }
                // ---------------- check number -------------
                if(!s2.isEmpty()) {
                    s2 = NEW_PHONE.getText().toString();
                    if (s2.length() != 10) {
                        NEW_PHONE.setError("Enter a valid phone length (10 Digits)");
                        return;
                    }
                    s2 = NEW_PHONE.getText().toString();
                    if (!s2.startsWith("05")) {
                        NEW_PHONE.setError("Enter a valid phone number (Start with 05)");
                        return;
                    }
                    s2 = NEW_PHONE.getText().toString();
                    if (containsLetters(s2)) {
                        NEW_PHONE.setError("Enter a phone number with no letters");
                        return;
                    }
                    s2 = NEW_PHONE.getText().toString();
                }//end if empty

                if (!s3.isEmpty()){
                    if (s3.length() < 8) {
                        NEW_PASSWORD.setError("Must Be At Least 8 Characters");
                        return;
                    }
                    s3 =NEW_PASSWORD.getText().toString();
                }
                s1 = NEW_NAME.getText().toString();
                s2 = NEW_PHONE.getText().toString();
                // معليش على البدائيه بس اذا عندكم حل احسن قولو
                //حلك رائع بلا دراما:)
                int counter=0;
                if (Uid != null) {
                    if (!(s1.isEmpty()) ){
                        if (s1 != null)
                            Updatename(s1);
                    }
                    else
                        counter++;
                    if (!(s2.isEmpty())) {
                        if (s2 != null)
                            UpdatePhone(s2);
                    }
                    else
                        counter++;
                    if (!s3.isEmpty() && s3 != null){
                        UpdatePassword(s3);
                    }
                    else
                        counter++;
                    if (!s4.isEmpty() && s4 != null){
                        UpdateEmail(s4);
                    }else
                        counter++;

                    if (counter == 4)
                    {
                        Toast.makeText(EditDonatorProfile.this, "No Changes on profile", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditDonatorProfile.this, DonatorProfile.class));
                    }
                    Toast.makeText(EditDonatorProfile.this, "Changes Saved successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditDonatorProfile.this, DonatorProfile.class));
                }
            }
        });
    }

    public void OpenProfileDonator(View view) {
        startActivity(new Intent(EditDonatorProfile.this,DonatorProfile.class));
    }

    // -------------------------------------------------METHODS------------------------------------------------
    private void uploadFile() {
        if (mImageUri != null){
            Upload.setClickable(true);
            file = mStorageRef.child(userId+".png");
            UploadTask = file.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditDonatorProfile.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
//                    String Link = mImageUri.toString(); //taskSnapshot.getUploadSessionUri().toString();
//                    Upload up = new Upload(userId,Link);
//                    db.collection("profilePicture").document(userId).set(up);
                }
            });
        }// ----- end if -----
        else
        {
            Toast.makeText(this,"Choose a file first",Toast.LENGTH_SHORT).show();
        }

    }
    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            NEW_IMAGE.setImageURI(mImageUri);
            NEW_IMAGE.requestLayout();
            //NEW_IMAGE.getLayoutParams().height = 400;
            //NEW_IMAGE.getLayoutParams().width = 400;
        }
    }
    public void Updatename(String name){

        //MINE -Hussa
        DocumentReference documentReference =db.collection("Donators").document(userId);
        documentReference.update("UserName",NEW_NAME.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //  Toast.makeText( EditDonatorProfile.this,"user updated",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void UpdatePassword(String pass){
        // abeer
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            user.updatePassword(pass);
        }
    }
    public void UpdateEmail(String em){
        // abeer
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            user.updateEmail(em);
        }
    }
    public void UpdatePhone (String phone){
        //Written by Hussa
        DocumentReference documentReference =db.collection("Donators").document(userId);
        documentReference.update("PhoneNumber",NEW_PHONE.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Toast.makeText( EditDonatorProfile.this,"user updated",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public final boolean containsLetters(String s) {
        boolean containsLetters = false;

        if (s != null && !s.isEmpty()) {
            for (char c : s.toCharArray()) {
                if (containsLetters = Character.isLetter(c)) {
                    break;
                }
            }
        }

        return containsLetters;
    }
    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}

class Upload {
    private String userID;
    private String Link;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String UserID, String Link) {
        userID = UserID;
        this.Link = Link;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}