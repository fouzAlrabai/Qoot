package com.example.qoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class single_offer  extends AppCompatActivity {
    public String username;
    public String away_text;
    public String vehicle_text;
    public String vehicle_type;
    public Image userimage;
    public Image Rate_image;
    public double Reat_num;
    public double away_km;
    public Button accept_offer;

    public void OpenDonatorRequestInfo(View view) {
        startActivity(new Intent(single_offer.this,DonatorRequests.class));
    }

    }

