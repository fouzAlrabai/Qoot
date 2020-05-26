package com.example.qoot;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.view.View;
import android.net.Uri;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import static android.content.Intent.getIntent;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class tab2 extends Fragment {
    TextView textView,location, mLocation;
    TextView dateOfPickUp;
    String selectedDate,Time,Date;
    Calendar calendar;
    int day,month,year;
    public static final int REQUEST_CODE = 11; // ???
    Spinner events;
    String DonatorName;

    /////////
    EditText mType,mNumOfGuest,mTime,mNotes;
    //FloatingActionButton submit;
    ImageView submit;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String type,numOfGuest,userId,time,date,name,reqID;
    private static final String TAG = "tab2";
    final SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy");
    Date CurrentDateObj = new Date();


    //private OnFragmentInteractionListener mListener;
    public static tab2 newInstance() {
        tab2 fragment = new tab2();
        return fragment;
    }

    public tab2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        //GPS stuff


        dateOfPickUp = (TextView) view.findViewById(R.id.pickUpDate1);

        events =(Spinner) view.findViewById(R.id.FoodType);
        final String[] eventTypes = new String[]{"Select Event Type","Wedding", "BBQ", "Small Party","Funeral","Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, eventTypes);
        events.setAdapter(adapter);


        events.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Select Event Type")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        final String currentDate = (month + "/" + day + "/" + year);
        try {
             CurrentDateObj = curFormater.parse(currentDate);
        } catch (ParseException e) {
        }


        dateOfPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create the datePickerFragment
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(tab2.this, REQUEST_CODE);
                // show the datePicker
                newFragment.show(fm, "datePicker");
            }
        });

        textView = (TextView) view.findViewById(R.id.pickUpTime);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "TimePicker");
            }

        });

       // mType = view.findViewById(R.id.FoodType);
        mNumOfGuest = view.findViewById(R.id.numberOfGuest);
        //mTime = view.findViewById(R.id.pickUpTime);
        mNotes = view.findViewById(R.id.note);
        submit = (ImageView) view.findViewById(R.id.submitReq);
       // mLocation= view.findViewById(R.id.location);
        dateOfPickUp = (TextView) view.findViewById(R.id.pickUpDate1);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type =events.getSelectedItem().toString();
                numOfGuest = mNumOfGuest.getText().toString();
                date = dateOfPickUp.getText().toString();
                time = textView.getText().toString();
              //  location = mLocation.getText().toString();

                db = FirebaseFirestore.getInstance();

                //check fields

                if (TextUtils.isEmpty(type)) {
                    mType.setError("Please Enter Your Event Type, It is Required"); /////////////// WILL BE CHANGED TO DROP DOWN !
                    return;
                }if(type.equals("Select Event Type")){
                TextView errorTextView=(TextView)events.getSelectedView();
                errorTextView.setError("");
                errorTextView.setTextColor(Color.RED);
                errorTextView.setText("Select Event Type");
                return;
            }
                if (TextUtils.isEmpty(numOfGuest)) {
                    mNumOfGuest.setError("Please Enter number Of Guests , It is Required");
                    return;
                }   if (TextUtils.isEmpty(time)) {
                    textView.setError("Please Enter Pick Up Time, It is Required");
                    return;
                }
               // if (TextUtils.isEmpty(location)) {
                 //   mLocation.setError("Please Enter Your Event Location, It is Required");
                   // return;
                //}
               // if (TextUtils.isEmpty(time)) {
                 //   textView.setError("Please choose pick up time, It is Required");
                   // return;
               // }
                if (TextUtils.isEmpty(date)) {
                    dateOfPickUp.requestFocus();
                    dateOfPickUp.setError("Please choose pick up date, It is Required");
                    return;
                }

                // check if the date is the current date or before
                try {
                    Date interedDate = curFormater.parse(date);     // convert the inserted date to a Date obj

                    if (CurrentDateObj.compareTo(interedDate) > 0) {
                        dateOfPickUp.requestFocus();
                        dateOfPickUp.setError("Please Enter a valid date, not past");
                        return;
                    } else if (CurrentDateObj.compareTo(interedDate) == 0) {
                        dateOfPickUp.setError("Please Enter a valid date, not today's date");
                        return;
                    }
                } catch (ParseException e) {
                    dateOfPickUp.setError("Error in date format");
                    return;
                }

                //check if there is no characters in this fields
                numOfGuest = mNumOfGuest.getText().toString();
                if (containsLetters(numOfGuest)) {
                    mNumOfGuest.setError("Enter numbers with no letters");
                    return;
                }
//*********************************************** DO NOT FORGET TIME YOU NEED TO CHECK THAT.************************
                // here i will send the request to database ,
                //Intent intent = getIntent();
               // Intent intent=getActivity().getIntent();
               // userId = intent.getStringExtra("user");
                // Toast.makeText( getActivity(),"user in tab"+userId,Toast.LENGTH_SHORT).show();
                mAuth = FirebaseAuth.getInstance();
                userId=mAuth.getCurrentUser().getUid();
                db = FirebaseFirestore.getInstance();

                DocumentReference VolRef=db.collection("Donators").document(userId);
                VolRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        DonatorName=documentSnapshot.getString("UserName") ;

                //String reqId = UUID.randomUUID().toString();
               /* DocumentReference documentReference = db.collection("Donators").document(userId);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        name =(String)documentSnapshot.getString("UserName");
                    }
                });*/

                // DocumentReference documentReference=db.collection("Requests").document(reqId);
                        year=calendar.get(Calendar.YEAR);
                        month=calendar.get(Calendar.MONTH)+1;
                        day=calendar.get(Calendar.DAY_OF_MONTH);
                        if(month<10)
                            Date="0"+month+"/"+day+"/"+year;
                        else
                            Date=month+"/"+day+"/"+year;
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                        Time =simpleDateFormat.format(calendar.getTime());
                Map<String,Object> request = new HashMap<>();
                request.put("TypeOfEvent",type);
                request.put("NumberOfGuests",numOfGuest);
                request.put("Time",time);
                request.put("Date",date);
                request.put("Location",location);
                request.put("Note",""+mNotes.getText().toString());
                request.put("State","Pending");
                request.put("DonatorID",userId);
                request.put("DonatorName",DonatorName);
                request.put("VolnteerID","--");
                request.put("VolnteerName","--");
                request.put("RequestID","--");
                request.put("RequestType","Scheduled");
                request.put("submetTime",Time);
                request.put("submetDate",Date);
                request.put("VolunteerCurrentLocation","--");


                db.collection("Requests")
                        .add(request)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.update("RequestID",documentReference.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    //    reqID= documentReference.getId();
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                                Intent i = new Intent(getActivity(), MapsActivity2.class);
                                i.putExtra("RequestID",documentReference.getId());
                                startActivity(i);
                                ((Activity) getActivity()).overridePendingTransition(0, 0);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Something Went Wrong,Try Again ! " , Toast.LENGTH_SHORT).show();
                                // Log.w(TAG, "Error adding document", e);
                            }
                        });
                    }
                });

               /*documentReference.set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG," Your Request Submitted Successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Something Went Wrong , Try Again ");
                    }
                });*/
            }
        });

        return view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            dateOfPickUp.setText(selectedDate);
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

}


