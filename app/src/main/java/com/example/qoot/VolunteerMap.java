package com.example.qoot;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class VolunteerMap extends FragmentActivity implements OnMapReadyCallback {
    Bundle intent1;
    GoogleMap mMap;
    String ReqIDDD;
    FirebaseFirestore db;
    String location;
    double lat, lang;
    ImageView back;
    String Abeer;

    //--- abeer-----
    FusedLocationProviderClient mFusedLocationClient;
    Location ADDRESS;
    Marker OriginMarker; // no need for Destination marker since the destination will be fixed
    View locationButton;
    LatLng Origin;
    LatLng Destination;
    ArrayList<LatLng> Positions ;

    String State;
    String VolunteerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Positions = new ArrayList<>();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        db = FirebaseFirestore.getInstance();
        intent1 = getIntent().getExtras();
        back = findViewById(R.id.back);

        if (intent1 != null) {
            ReqIDDD = (String) intent1.getSerializable("RequestID");
            Abeer = (String) intent1.getSerializable("Where");

            //----------------------------- abeer-----------------------------------
            mMap.setMyLocationEnabled(true);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            final View mMapView = findViewById(R.id.map);
            locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 1800, 180, 0);

            if (mFusedLocationClient != null) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull final Task<Location> task) {
                                ADDRESS = task.getResult();
                                lat = ADDRESS.getLatitude();
                                lang = ADDRESS.getLongitude();
                                Origin = new LatLng(lat, lang);
                               mMap.addMarker(new MarkerOptions().position(Origin).title(Origin.toString()));
                                // reset the markers
                                if (OriginMarker != null) {
                                  //  Positions.clear(); // not sure about this
                                }
                                DocumentReference documentReference = db.collection("Requests").document(ReqIDDD);
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        location=documentSnapshot.getString("Location") ;
                                        lat=Double.parseDouble(location.substring(0,location.indexOf(',')));
                                        lang=Double.parseDouble(location.substring(location.indexOf(',')+1));
                                        //NEW CODE
                                        VolunteerID = documentSnapshot.getString("VolnteerID");
                                        State = documentSnapshot.getString("State");

                                        Timer timer = new Timer();
                                          timer.scheduleAtFixedRate(new TimerTask() {
                                               @Override
                                               public void run() {

                                                   intent1 = getIntent().getExtras();

                                                   if (intent1 != null) {
                                                       ReqIDDD = (String) intent1.getSerializable("RequestID");
                                                       Abeer = (String) intent1.getSerializable("Where");
                                                   }
                                                   DocumentReference documentReference = db.collection("Requests").document(ReqIDDD);
                                                   documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                       @Override
                                                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                           VolunteerID = documentSnapshot.getString("VolnteerID");
                                                           State = documentSnapshot.getString("State");
                                                           if (VolunteerID != null) {
                                                               if (VolunteerID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                       && State.equals("Accepted") && !State.equals("Pending")
                                                                       && !State.equals("Delivered") && !State.equals("Cancelled")) {
                                                                   UpdateVolunteerLocation(true);
                                                               } else
                                                                   UpdateVolunteerLocation(false);
                                                           }
                                                       }
                                                   });
                                               }
                                               }, 0, 100000);
                                        LatLng loc = new LatLng(lat, lang);
                                        Destination = loc;
                                        mMap.addMarker(new MarkerOptions().position(loc).title(loc.toString()));
                                        CameraPosition myPosition = new CameraPosition.Builder()
                                                .target(loc).zoom(10).bearing(90).tilt(30).build();
                                        mMap.animateCamera(
                                                CameraUpdateFactory.newCameraPosition(myPosition));
                                    // THERE IS THIS THING
                                        Positions.add(Origin);
                                        Positions.add(Destination);
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(Origin);
                                        if (Positions.get(0).equals(Origin)) {
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                        }
                                        markerOptions.position(Destination);
                                        if (Positions.get(1).equals(Destination)){
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        }
                                        mMap.addMarker(markerOptions);
                                        //TODO: request direction from the api and create the URL for the HTTP request
//                                       if (Positions.size() == 2){
//                                        // make url
//                                    String url = getRequestUrl(Positions.get(0),Positions.get(1));
//                                    TaskRequestDirection taskRequestDirection =new TaskRequestDirection();
//                                    taskRequestDirection.execute(url);
//                                             }// end of IF
                                    }
                                });
                              //  GetRequestLocation();

////                                //TODO: request direction from the api and create the URL for the HTTP request
//                                if (Positions.size() == 2){
//                                        // make url
//                                    String url = getRequestUrl(Positions.get(0),Positions.get(1));
//                                    TaskRequestDirection taskRequestDirection =new TaskRequestDirection();
//                                    taskRequestDirection.execute(url);
//                                }
                               // OriginMarker = mMap.addMarker(new MarkerOptions().position(Origin).title(Origin.toString()));
//                                CameraPosition myPosition = new CameraPosition.Builder().target(Origin).zoom(17).bearing(90).tilt(30).build();
//                                mMap.animateCamera(
//                                        CameraUpdateFactory.newCameraPosition(myPosition));
                                // this might not be needed but i'll keep it in case
                                locationButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Location x = task.getResult();
                                        lat = x.getLatitude();
                                        lang = x.getLongitude();
                                        LatLng UpdatedOrigin = new LatLng(lat, lang);
                                        setCurrentLocationAgain(UpdatedOrigin);
                                    }
                                });
                            }
                        });
            }
            //------------------------------ end of abeer ------------------------------
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VolunteerMap.this, VolunteerRequestInfo.class);
                i.putExtra("RequestID", ReqIDDD);
                i.putExtra("Where", Abeer);
                startActivity(i);
            }
        });
    }

    private void UpdateVolunteerLocation(boolean Decide) {

        Bundle intent1 = getIntent().getExtras();
        final String ReqID = (String) intent1.getSerializable("RequestID");
        FusedLocationProviderClient mFusedLocationClient;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (Decide == true) {
            if (mFusedLocationClient != null) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            String LOCATION = "" + location.getLatitude() + "," + location.getLongitude();
                            DocumentReference documentReference = db.collection("Requests").document(ReqID);
                            documentReference.update("VolunteerCurrentLocation", LOCATION).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }

                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "your location is Unabled !", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }else if (Decide == false){
            DocumentReference documentReference = db.collection("Requests").document(ReqID);
            documentReference.update("VolunteerCurrentLocation", "--").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
    }

    private String getRequestUrl(LatLng origin, LatLng destination) {
        String valueOfOrigin = "origin="+origin.latitude+","+origin.longitude;
        String valueOfDestination = "destination="+destination.latitude+","+destination.longitude;
        String sensor="sensor=false";
        String mode="mode=driving";//-----------------------------------might change
        String param = valueOfOrigin+"&"+valueOfDestination+"&"+sensor+"&"+mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;

    return  url;
    }

    private String requestDirection(String reqURL) throws IOException {
        String responseString ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection=null;
        try{
            URL url = new URL(reqURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line="";
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }catch(MalformedURLException e){
            Toast.makeText(VolunteerMap.this, "Connection failed",Toast.LENGTH_LONG);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();

        }
    return responseString;
    }


/*
                           هنا ميثودات عادية
 */

    private void setCurrentLocationAgain(LatLng UpdatedOrigin) {
        if (OriginMarker != null)
            mMap.clear();
        OriginMarker = mMap.addMarker(new MarkerOptions().position(UpdatedOrigin).title(UpdatedOrigin.toString()));
//        CameraPosition myPosition = new CameraPosition.Builder().target(UpdatedOrigin).zoom(17).bearing(90).tilt(30).build();
//        mMap.animateCamera(
//                CameraUpdateFactory.newCameraPosition(myPosition));
    }
    class TaskRequestDirection extends AsyncTask<String,Void, String>{


        @Override
        protected String doInBackground(String... strings)
        {String responseString="";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //TODO: Parse Json File here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);

        }
    }

    public class TaskParser extends AsyncTask<String,Void,List<List<HashMap<String,String>>>>{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes= directionsParser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            // get list routes
            ArrayList points  = null;
            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions =  new PolylineOptions();
                for (HashMap<String, String> point : path){
                    double lat  = Double.parseDouble(point.get("lat"));
                    double lng  = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat,lng));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.GREEN);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null){
                mMap.addPolyline(polylineOptions);
            }else{

                //TODO: when Direction is not found doooo this
                Toast.makeText(getApplicationContext(),"There's no route yet polylineOption is null",Toast.LENGTH_LONG).show();
            }

        }
    }
}


