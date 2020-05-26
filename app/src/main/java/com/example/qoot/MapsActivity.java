package com.example.qoot;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    String userId;
    ImageView back;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    //private
    Button Choose;
    String location,req;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Choose = findViewById(R.id.pickLocation);
        mAuth = FirebaseAuth.getInstance();
        back = findViewById(R.id.imageView);

        db=FirebaseFirestore.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        Bundle extras = getIntent().getExtras();
        double x =extras.getDouble("lat");
        double y =(extras.getDouble("lon"));
        req= extras.getString("ReqId");

        location=x+","+y;

        Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mMap.getMyLocation();
                DocumentReference documentReference =db.collection("Requests").document(req);
                documentReference.update("Location",location).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MapsActivity.this, "Your Request Submitted Successfully " , Toast.LENGTH_SHORT).show();
                        //  Toast.makeText( EditVolunteerProfile.this,"user updated",Toast.LENGTH_SHORT).show();
                    }
                });

                Intent i = new Intent(MapsActivity.this, DonatorRequests.class);
                startActivity(i);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                         هنا مفروض نسوي INTENT عشان نرجع المعلومات كلها زي ما كانت
                */
                Intent i = new Intent(MapsActivity.this,requestForm.class);
                startActivity(i);
            }
        });


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //  mMap.setMyLocationEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            double x =extras.getDouble("lat");
            double y =(extras.getDouble("lon"));

            LatLng ur = new LatLng(x, y);
            mMap.addMarker(new MarkerOptions().position(ur).title(ur.toString()));

            CameraPosition myPosition = new CameraPosition.Builder()
                    .target(ur).zoom(17).bearing(90).tilt(30).build();
            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(myPosition));
          //  mMap.moveCamera(CameraUpdateFactory.newLatLng(ur));
            Choose = findViewById(R.id.pickLocation);
            mAuth = FirebaseAuth.getInstance();

            db=FirebaseFirestore.getInstance();
            userId=mAuth.getCurrentUser().getUid();

            req= extras.getString("ReqId");

            location=x+","+y;

            Choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mMap.getMyLocation();
                    DocumentReference documentReference =db.collection("Requests").document(req);
                    documentReference.update("Location",location).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText( MapsActivity.this,"Request submitted successfully",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MapsActivity.this, DonatorRequests.class);
                            startActivity(i);
                        }
                    });
                }
            });
        }

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}