package com.example.qoot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.regex.Pattern;

public class EditVolunteerProfile extends AppCompatActivity {

    Spinner cars;
    public EditText NEW_NAME;
    public EditText NEW_PHONE;
    public EditText NEW_EMAIL;
    public EditText NEW_PASSWORD;
    public ImageView NEW_IMAGE;
    private StorageReference mStorageRef;
    String userId;
    private StorageTask UploadTask;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String actualCar;
    private Uri mImageUri;
    //  save button
    Button saveButton, Upload, Choose;
    private static final int PICK_IMAGE_REQUEST = 1;
    // what user type in fields
    String s1, s2, s3,s4,Uid;
    //String[]  types = new String[]{",","Small", "Medium", "Truck","None"};
    String[]  types = new String[]{"Sedan", "SUV", "Truck"};
    String[]  types1 = new String[]{".", "Medium", "Truck","None"};
    String urCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_volunteer_profile);

        // DROP DOWN CODE
        cars = findViewById(R.id.carDD);

       /* final String[] types = new String[]{"Default","Small", "Medium", "Truck","None"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        cars.setAdapter(adapter);*/
        // I think here we need to fetch the type from DB.. not like the above




        NEW_NAME = findViewById(R.id.Name);
        NEW_PHONE = findViewById(R.id.Phone_v);
        NEW_EMAIL = findViewById(R.id.EmailV);
        NEW_IMAGE =findViewById(R.id.UserImage);
        NEW_PASSWORD = findViewById(R.id.Pass);
        saveButton = findViewById(R.id.button);
        Upload = findViewById(R.id.Upload);
        Choose = findViewById(R.id.choose);

        // firebase initialize
        mAuth = FirebaseAuth.getInstance();

        db=FirebaseFirestore.getInstance();
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userId=mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");


        // ---------------------- setting Hints -----------------------

        DocumentReference documentReference =db.collection("Volunteers").document(Uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                NEW_NAME.setHint(documentSnapshot.getString("UserName"));
            }
        });
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                NEW_PHONE.setHint(documentSnapshot.getString("PhoneNumber"));
            }
        });
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                NEW_EMAIL.setHint(documentSnapshot.getString("Email"));
            }
        });


        /* the last one
        DocumentReference documentReference =db.collection("Volunteers").document(Uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                types[0] = (String)documentSnapshot.getString("Vehicle")+",";
                magic.setText(documentSnapshot.getString("Vehicle"));
                urCar=magic.getText().toString() ;


            }
        });*/
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                types1[0] = (String)documentSnapshot.getString("Vehicle")+"";
               // urCar = (String)documentSnapshot.getString("Vehicle")+"";
             //   magic.setText(documentSnapshot.getString("Vehicle"));
                int index =-1  ;
                for(int i=0;i<types.length;i++){
                   // Toast.makeText( EditVolunteerProfile.this,""+i,Toast.LENGTH_SHORT).show();
                    if(types1[0].equals(types[i]) ){
                        index = i;
                       // Toast.makeText( EditVolunteerProfile.this,"inside 1 if",Toast.LENGTH_LONG).show();
                        if(index != -1) {
                            String temp = types[0];
                            types[0] = types[index];
                            types[index] = temp;
                          //  Toast.makeText( EditVolunteerProfile.this,"inside 2 if",Toast.LENGTH_LONG).show();
                            return;
                        }// end if 2
                    }//end if
                }
            }
        });


        //  actualCar = ReadCar();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        cars.setAdapter(adapter);




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
                    Toast.makeText(EditVolunteerProfile.this,"Hold on Image is Uploading",Toast.LENGTH_SHORT).show();
                }else
                    uploadFile();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //take from user
                s1 = NEW_NAME.getText().toString();
                s2 = NEW_PHONE.getText().toString();
                s3 = NEW_PASSWORD.getText().toString();
                s4 = NEW_EMAIL.getText().toString();
                // read the actual car
                actualCar = ReadCar();
                int counter=0;
                //------ check name -------------
              if (!s1.isEmpty()) {
                  if (s1.length() == 1) {
                      NEW_NAME.setError("Please enter a valid name length");
                      return;
                  }
              }
              if (!s4.isEmpty()){
                  if (!isValid(s4)){
                      NEW_EMAIL.setError("Enter a valid Email");
                      return; }
              }
                // ---------------- check number -------------
                if(!s2.isEmpty()) {
                    if (s2.length() != 10) {
                        NEW_PHONE.setError("Enter a valid phone (10 Digits)");
                        return;
                    }
                    if (!s2.startsWith("05")) {
                        NEW_PHONE.setError("Enter a valid phone (Start with 05)");
                        return;
                    }
                    if (containsLetters(s2)) {
                        NEW_PHONE.setError("Enter a phone number with no letters");
                        return;
                    }
                }//end if empty
                // ------------------ check email --------------------
                if (!s3.isEmpty()){
                    if (s3.length() < 8) {
                        NEW_PASSWORD.setError("Must Be At Least 8 Characters");
                        return;
                    }
                }


                //check on car
               /* if (cars.getSelectedItem().toString().equals(actualCar)) { // if same
                    Toast.makeText(EditVolunteerProfile.this, "No Changes on Vehicle", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                /*if (cars.getSelectedItem().toString().equals(null)) { // not sure about this
                    return;
                }*/
                if (!(cars.getSelectedItem().toString().equals(types[0]))) { // if NOT same then UPDATE
                    UpdateVehicle(cars.getSelectedItem().toString());

                }else{
                        counter++;
                }

                s1 = NEW_NAME.getText().toString();
                s2 = NEW_PHONE.getText().toString();
                s3 = NEW_PASSWORD.getText().toString();
                s4 = NEW_EMAIL.getText().toString();
                // معليش على البدائيه بس اذا عندكم حل احسن قولو
                //حلك رائع بلا دراما:)

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
                    if (!s3.isEmpty()){
                        UpdatePassword(s3);
                    }
                    else
                        counter++;
                    if (!s4.isEmpty()){
                        UpdateEmail(s4);
                    }
                    else
                        counter++;

                    if (counter == 5)
                    {
                        Toast.makeText(EditVolunteerProfile.this, "No Changes on profile", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditVolunteerProfile.this, VolunteerProfile.class));
                    }
                    Toast.makeText(EditVolunteerProfile.this, "Changes Saved successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(EditVolunteerProfile.this, VolunteerProfile.class));
                }
            }
        });
    }

    private String ReadCar() {

        DocumentReference documentReference =db.collection("Volunteers").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                actualCar= documentSnapshot.getString("Vehicle");
               // magic.setText(documentSnapshot.getString("Vehicle"));
               // urCar =(String)magic.getText().toString();
            }
        });

 return actualCar;
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
        }
    }
    private void uploadFile() {
        if (mImageUri != null){
            Upload.setClickable(true);

            StorageReference file = mStorageRef.child(userId
                    +".png");
            UploadTask = file.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditVolunteerProfile.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this,"Choose a file first",Toast.LENGTH_SHORT).show();
        }

    }
    public void OpenProfile(View view) {
        startActivity(new Intent(EditVolunteerProfile.this,VolunteerProfile.class));
    }
    public void Updatename(String name){

        //MINE -Hussa
        DocumentReference documentReference =db.collection("Volunteers").document(userId);
        documentReference.update("UserName",NEW_NAME.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              //  Toast.makeText( EditVolunteerProfile.this,"user updated",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void UpdatePhone (String phone){

        //Written by Hussa
        DocumentReference documentReference =db.collection("Volunteers").document(userId);
        documentReference.update("PhoneNumber",NEW_PHONE.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText( EditVolunteerProfile.this,"user updated",Toast.LENGTH_SHORT).show();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            user.updateEmail(em);
        }
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

    public void UpdateVehicle(String newVehicle){
        DocumentReference documentReference =db.collection("Volunteers").document(userId);
        documentReference.update("Vehicle",newVehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText( EditVolunteerProfile.this,"Vehicle updated",Toast.LENGTH_SHORT).show();
            }
        });
    }


}




