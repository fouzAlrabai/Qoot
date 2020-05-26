package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;

public class DonatorMap extends AppCompatActivity implements OnMapReadyCallback{

    // google Maps
    FusedLocationProviderClient mFusedLocationClient;
    GoogleMap mMap;
    // For Donator
    LatLng Origin;
    Marker DonatorLocationMarker;
    // for Volunteer
    LatLng Destination;
    Marker VolunteerLocationMarker;

    // firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String RequestID;
    Bundle intent;

    // 1st  get donator current location and add a marker on it
    // 2nd  submit volunteer current location in request
    // 3rd keep updating it
    // IF IT'S PENDING,DELIVERED, CANCELLED THEN TOAST A MESSAGE.
    // develop a method for updating the marker


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // init firebase dependencies
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // intent request ID from info
        intent = getIntent().getExtras();
        RequestID = (String) intent.getSerializable("RequestID");
        //---------------------------------------------------------------------------------------
//        Timer timer=new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 0, 10000);


    }

    private void SetMyCurrentLocation() {
        if (DonatorLocationMarker != null){
            DonatorLocationMarker.remove();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (mFusedLocationClient != null) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    Origin = new LatLng(location.getLatitude(),location.getLongitude());
                    DonatorLocationMarker = mMap.addMarker(new MarkerOptions().position(Origin).title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
            });
        }
    }
    private void SetMyCurrentLocation2() {
        if (RequestID != null) {
            if (DonatorLocationMarker != null) {
                DonatorLocationMarker.remove();
            }
            DocumentReference documentReference = db.collection("Requests").document(RequestID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String origin = documentSnapshot.getString("Location");
                    if (origin != null){
                        double lat = Double.parseDouble(origin.substring(0, origin.indexOf(',')));
                    double lang = Double.parseDouble(origin.substring(origin.indexOf(',') + 1));
                    Origin = new LatLng(lat, lang);
                    if (Origin == null)
                        Toast.makeText(DonatorMap.this, "YOUR LOCATION IS NULL", Toast.LENGTH_LONG).show();
                    else
                        VolunteerLocationMarker = mMap.addMarker(new MarkerOptions().position(Origin).title("Current Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }else
                        Toast.makeText(DonatorMap.this, "YOUR LOCATION IS NULL", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    private void SetVolunteerLocation() {
        if (RequestID != null){

            if (VolunteerLocationMarker != null){
                VolunteerLocationMarker.remove();
            }
            DocumentReference documentReference = db.collection("Requests").document(RequestID);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String Dest = documentSnapshot.getString("VolunteerCurrentLocation");
                    String Name = documentSnapshot.getString("VolnteerName");
                    if (!Dest.equals("--")){
                        double lat = Double.parseDouble(Dest.substring(0, Dest.indexOf(',')));
                        double lang = Double.parseDouble(Dest.substring(Dest.indexOf(',') + 1));
                    Destination = new LatLng(lat, lang);
                    if (Destination == null)
                        Toast.makeText(DonatorMap.this, "There's no Volunteers yet", Toast.LENGTH_LONG).show();
                    else
                        VolunteerLocationMarker = mMap.addMarker(new MarkerOptions().position(Destination).title(Name+"'s Location")
                                //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_walk_black_24dp)));
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        CameraPosition myPosition = new CameraPosition.Builder().target(Destination).zoom(17).bearing(90).tilt(30).build();
                        mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(myPosition));
                }
                    else
                        Toast.makeText(DonatorMap.this, "There's no Volunteers yet", Toast.LENGTH_LONG).show();
                }
            });
        }else {
         Toast.makeText(DonatorMap.this , "Request ID isn't Created",Toast.LENGTH_LONG).show();
        }


        db.collection("Requests")
                .whereEqualTo("RequestID", RequestID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    // DO NOTHING
                                    break;
                                case MODIFIED:
                                    if (VolunteerLocationMarker != null){
                                        VolunteerLocationMarker.remove();
                                    }
                                    DocumentReference documentReference = db.collection("Requests").document(RequestID);
                                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String Dest = documentSnapshot.getString("VolunteerCurrentLocation");
                                            String Name = documentSnapshot.getString("VolnteerName");
                                            if (!Dest.equals("--")){
                                                double lat = Double.parseDouble(Dest.substring(0, Dest.indexOf(',')));
                                                double lang = Double.parseDouble(Dest.substring(Dest.indexOf(',') + 1));
                                                Destination = new LatLng(lat, lang);
                                                if (Destination == null)
                                                    Toast.makeText(DonatorMap.this, "There's no Volunteers yet", Toast.LENGTH_LONG).show();
                                                else
                                                    VolunteerLocationMarker = mMap.addMarker(new MarkerOptions().position(Destination).title(Name+"'s Location")
                                                            //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_walk_black_24dp)));
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                            }
                                            else
                                                Toast.makeText(DonatorMap.this, "There's no Volunteers yet", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                case REMOVED:
                                    // DO NOTHING
                                    break;
                            }
                        }

                    }
                });




    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        SetMyCurrentLocation2();
        SetVolunteerLocation();
    }
    public void OpenDONRequestInfo(View view){
       Bundle  intents = getIntent().getExtras();
        String RRequestID = (String) intents.getSerializable("RequestID");
        Intent intent = new Intent(DonatorMap.this, DonatorRequestInfo.class);
        intent.putExtra("RequestID", RRequestID);
        startActivity(intent);

    }
}
