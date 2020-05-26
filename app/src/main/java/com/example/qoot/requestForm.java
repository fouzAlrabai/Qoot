package com.example.qoot;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class requestForm extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tab1, tab2;
    public pageAdapter pagerAdapter;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);

        //*********************************bottom nav
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_navigation_vol);
        bottomNavigationView.setSelectedItemId(R.id.browse_vol);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.notifi_vol:
                        startActivity(new Intent(getApplicationContext(),volunteer_notification.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.browse_vol:
                        return true;

                    case R.id.Req_vol:
                        startActivity(new Intent(getApplicationContext(),VolunteerRequests.class));
                        overridePendingTransition(0,0);
                        return false;

                    case R.id.prfile_vol:
                        startActivity(new Intent(getApplicationContext(),VolunteerProfile.class));
                        overridePendingTransition(0,0);
                        return false;


                }
                return false;
            }
        });



        //********************************bottom nav
      //  mAuth = FirebaseAuth.getInstance();
       // db = FirebaseFirestore.getInstance();
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tab1 = (TabItem) findViewById(R.id.Tab1);
        tab2 = (TabItem) findViewById(R.id.Tab2);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
      pagerAdapter = new pageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
      viewPager.setAdapter(pagerAdapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.clock2);
        tabLayout.getTabAt(1).setIcon(R.drawable.schedule2);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    viewPager.setCurrentItem(tab.getPosition());
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    viewPager.setCurrentItem(tab.getPosition());
                    pagerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void openUrgentForm(View view) {
        Intent intent = new Intent(requestForm.this,tab1.class);
        startActivity(intent);
    }

    public void openScheduleForm(View view) {
        startActivity(new Intent(requestForm.this,tab2.class));
    }

     public void OpenDonatorRequest(View view) {
       //  Intent intent1 = getIntent();
        // String userId = intent1.getStringExtra("user");
         Intent intent = new Intent(requestForm.this,DonatorRequests.class);
         //intent.putExtra("user", userId);
        // startActivity(new Intent(requestForm.this,DonatorRequests.class));
         startActivity(intent);
     }
 }