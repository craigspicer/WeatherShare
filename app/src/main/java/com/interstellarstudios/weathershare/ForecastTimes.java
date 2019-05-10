package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ForecastTimes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Boolean mSwitchOnOff;
    private TextView index0Text;
    private TextView index1Text;
    private TextView index2Text;
    private TextView index3Text;
    private TextView index4Text;
    private TextView index5Text;
    private TextView index6Text;
    private TextView index7Text;
    private TextView index8Text;
    private TextView index9Text;
    private TextView index10Text;
    private TextView index11Text;
    private TextView index12Text;
    private TextView index13Text;
    private TextView index14Text;
    private TextView index15Text;
    private TextView index16Text;
    private TextView index17Text;
    private TextView index18Text;
    private TextView index19Text;
    private TextView index20Text;
    private TextView index21Text;
    private TextView index22Text;
    private TextView index23Text;
    private TextView index24Text;
    private TextView index25Text;
    private TextView index26Text;
    private TextView index27Text;
    private TextView index28Text;
    private TextView index29Text;
    private TextView index30Text;
    private TextView index31Text;
    private TextView index32Text;
    private TextView index33Text;
    private String mHomeLocation;
    private String favouriteLocation1;
    private String favouriteLocation2;
    private String favouriteLocation3;
    private String favouriteLocation4;
    private String favouriteLocation5;
    private String favouriteLocation6;
    private String favouriteLocation7;
    private String favouriteLocation8;
    private String favouriteLocation9;
    private String favouriteLocation10;
    private String mCurrentUserId;
    private TextView mLocationText;
    private String mLocationFinal;
    private String mTimeInterval0;
    private String mTimeInterval1;
    private String mTimeInterval2;
    private String mTimeInterval3;
    private String mTimeInterval4;
    private String mTimeInterval5;
    private String mTimeInterval6;
    private String mTimeInterval7;
    private String mTimeInterval8;
    private String mTimeInterval9;
    private String mTimeInterval10;
    private String mTimeInterval11;
    private String mTimeInterval12;
    private String mTimeInterval13;
    private String mTimeInterval14;
    private String mTimeInterval15;
    private String mTimeInterval16;
    private String mTimeInterval17;
    private String mTimeInterval18;
    private String mTimeInterval19;
    private String mTimeInterval20;
    private String mTimeInterval21;
    private String mTimeInterval22;
    private String mTimeInterval23;
    private String mTimeInterval24;
    private String mTimeInterval25;
    private String mTimeInterval26;
    private String mTimeInterval27;
    private String mTimeInterval28;
    private String mTimeInterval29;
    private String mTimeInterval30;
    private String mTimeInterval31;
    private String mTimeInterval32;
    private String mTimeInterval33;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(ForecastTimes.this, Home.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_account:
                    Intent j = new Intent(ForecastTimes.this, Account.class);
                    startActivity(j);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_times);

        FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFireBaseFireStore = FirebaseFirestore.getInstance();

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

        //Loading shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        mSwitchOnOff = sharedPreferences.getBoolean("switchUnits", false);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        index0Text = findViewById(R.id.index0);
        index0Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 0);
                i.putExtra("time interval", mTimeInterval0);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index1Text = findViewById(R.id.index1);
        index1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 1);
                i.putExtra("time interval", mTimeInterval1);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index2Text = findViewById(R.id.index2);
        index2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 2);
                i.putExtra("time interval", mTimeInterval2);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index3Text = findViewById(R.id.index3);
        index3Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 3);
                i.putExtra("time interval", mTimeInterval3);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index4Text = findViewById(R.id.index4);
        index4Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 4);
                i.putExtra("time interval", mTimeInterval4);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index5Text = findViewById(R.id.index5);
        index5Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 5);
                i.putExtra("time interval", mTimeInterval5);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index6Text = findViewById(R.id.index6);
        index6Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 6);
                i.putExtra("time interval", mTimeInterval6);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index7Text = findViewById(R.id.index7);
        index7Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 7);
                i.putExtra("time interval", mTimeInterval7);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index8Text = findViewById(R.id.index8);
        index8Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 8);
                i.putExtra("time interval", mTimeInterval8);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index9Text = findViewById(R.id.index9);
        index9Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 9);
                i.putExtra("time interval", mTimeInterval9);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index10Text = findViewById(R.id.index10);
        index10Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 10);
                i.putExtra("time interval", mTimeInterval10);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index11Text = findViewById(R.id.index11);
        index11Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 11);
                i.putExtra("time interval", mTimeInterval11);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index12Text = findViewById(R.id.index12);
        index12Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 12);
                i.putExtra("time interval", mTimeInterval12);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index13Text = findViewById(R.id.index13);
        index13Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 13);
                i.putExtra("time interval", mTimeInterval13);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index14Text = findViewById(R.id.index14);
        index14Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 14);
                i.putExtra("time interval", mTimeInterval14);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index15Text = findViewById(R.id.index15);
        index15Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 15);
                i.putExtra("time interval", mTimeInterval15);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index16Text = findViewById(R.id.index16);
        index16Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 16);
                i.putExtra("time interval", mTimeInterval16);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index17Text = findViewById(R.id.index17);
        index17Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 17);
                i.putExtra("time interval", mTimeInterval17);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index18Text = findViewById(R.id.index18);
        index18Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 18);
                i.putExtra("time interval", mTimeInterval18);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index19Text = findViewById(R.id.index19);
        index19Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 19);
                i.putExtra("time interval", mTimeInterval19);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index20Text = findViewById(R.id.index20);
        index20Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 20);
                i.putExtra("time interval", mTimeInterval20);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index21Text = findViewById(R.id.index21);
        index21Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 21);
                i.putExtra("time interval", mTimeInterval21);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index22Text = findViewById(R.id.index22);
        index22Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 22);
                i.putExtra("time interval", mTimeInterval22);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index23Text = findViewById(R.id.index23);
        index23Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 23);
                i.putExtra("time interval", mTimeInterval23);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index24Text = findViewById(R.id.index24);
        index24Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 24);
                i.putExtra("time interval", mTimeInterval24);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index25Text = findViewById(R.id.index25);
        index25Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 25);
                i.putExtra("time interval", mTimeInterval25);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index26Text = findViewById(R.id.index26);
        index26Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 26);
                i.putExtra("time interval", mTimeInterval26);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index27Text = findViewById(R.id.index27);
        index27Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 27);
                i.putExtra("time interval", mTimeInterval27);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index28Text = findViewById(R.id.index28);
        index28Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 28);
                i.putExtra("time interval", mTimeInterval28);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index29Text = findViewById(R.id.index29);
        index29Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 29);
                i.putExtra("time interval", mTimeInterval29);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index30Text = findViewById(R.id.index30);
        index30Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 30);
                i.putExtra("time interval", mTimeInterval30);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index31Text = findViewById(R.id.index31);
        index31Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 31);
                i.putExtra("time interval", mTimeInterval31);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index32Text = findViewById(R.id.index32);
        index32Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 32);
                i.putExtra("time interval", mTimeInterval32);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        index33Text = findViewById(R.id.index33);
        index33Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForecastTimes.this, WeatherForecast.class);
                i.putExtra("location", mLocationFinal);
                i.putExtra("index", 33);
                i.putExtra("time interval", mTimeInterval33);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mLocationText = findViewById(R.id.selected_location);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.drawer_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        final Menu menu = navigationView.getMenu();
        MenuItem navHomeLocation = menu.findItem(R.id.nav_home_location);
        navHomeLocation.setTitle("Home Location");

        DocumentReference homeLocationRef = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Home_Location");
        homeLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        HomeLocationModel homeLocationModel = document.toObject(HomeLocationModel.class);
                        mHomeLocation = homeLocationModel.getHomeLocation();

                        getForecastTimes(mHomeLocation);
                    }
                }
            }
        });

        DocumentReference favouriteLocationsRef = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Favourite_Locations");
        favouriteLocationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        FavouriteLocationModel favouriteLocationModel = document.toObject(FavouriteLocationModel.class);
                        favouriteLocation1 = favouriteLocationModel.getFavouriteLocation1();
                        favouriteLocation2 = favouriteLocationModel.getFavouriteLocation2();
                        favouriteLocation3 = favouriteLocationModel.getFavouriteLocation3();
                        favouriteLocation4 = favouriteLocationModel.getFavouriteLocation4();
                        favouriteLocation5 = favouriteLocationModel.getFavouriteLocation5();
                        favouriteLocation6 = favouriteLocationModel.getFavouriteLocation6();
                        favouriteLocation7 = favouriteLocationModel.getFavouriteLocation7();
                        favouriteLocation8 = favouriteLocationModel.getFavouriteLocation8();
                        favouriteLocation9 = favouriteLocationModel.getFavouriteLocation9();
                        favouriteLocation10 = favouriteLocationModel.getFavouriteLocation10();

                        MenuItem navLocation2 = menu.findItem(R.id.nav_location2);
                        navLocation2.setTitle(favouriteLocation1);
                        MenuItem navLocation3 = menu.findItem(R.id.nav_location3);
                        navLocation3.setTitle(favouriteLocation2);
                        MenuItem navLocation4 = menu.findItem(R.id.nav_location4);
                        navLocation4.setTitle(favouriteLocation3);
                        MenuItem navLocation5 = menu.findItem(R.id.nav_location5);
                        navLocation5.setTitle(favouriteLocation4);
                        MenuItem navLocation6 = menu.findItem(R.id.nav_location6);
                        navLocation6.setTitle(favouriteLocation5);
                        MenuItem navLocation7 = menu.findItem(R.id.nav_location7);
                        navLocation7.setTitle(favouriteLocation6);
                        MenuItem navLocation8 = menu.findItem(R.id.nav_location8);
                        navLocation8.setTitle(favouriteLocation7);
                        MenuItem navLocation9 = menu.findItem(R.id.nav_location9);
                        navLocation9.setTitle(favouriteLocation8);
                        MenuItem navLocation10 = menu.findItem(R.id.nav_location10);
                        navLocation10.setTitle(favouriteLocation9);
                        MenuItem navLocation11 = menu.findItem(R.id.nav_location11);
                        navLocation11.setTitle(favouriteLocation10);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(ForecastTimes.this, Settings.class);
            startActivity(i);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home_location) {
            getForecastTimes(mHomeLocation);
        } else if (id == R.id.nav_location2) {
            getForecastTimes(favouriteLocation1);
        } else if (id == R.id.nav_location3) {
            getForecastTimes(favouriteLocation2);
        } else if (id == R.id.nav_location4) {
            getForecastTimes(favouriteLocation3);
        } else if (id == R.id.nav_location5) {
            getForecastTimes(favouriteLocation4);
        } else if (id == R.id.nav_location6) {
            getForecastTimes(favouriteLocation5);
        } else if (id == R.id.nav_location7) {
            getForecastTimes(favouriteLocation6);
        } else if (id == R.id.nav_location8) {
            getForecastTimes(favouriteLocation7);
        } else if (id == R.id.nav_location9) {
            getForecastTimes(favouriteLocation8);
        } else if (id == R.id.nav_location10) {
            getForecastTimes(favouriteLocation9);
        } else if (id == R.id.nav_location11) {
            getForecastTimes(favouriteLocation10);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getForecastTimes(String city){

        String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?q=";
        String API_KEY = "&appid=157187733bb90119ccc38f4d8d1f6da7";
        String unitsURL;

        if (mSwitchOnOff) {
            unitsURL = "&units=imperial";
        } else {
            unitsURL = "&units=metric";
        }

        String FINAL_URL = BASE_URL + city + API_KEY + unitsURL;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, FINAL_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //city object
                    JSONObject cityObject = response.getJSONObject("city");
                    String city = String.valueOf(cityObject.getString("name"));
                    String country = String.valueOf(cityObject.getString("country"));
                    mLocationFinal = city + ", " + country;
                    mLocationText.setText(mLocationFinal);

                    //list array
                    JSONArray listArray = response.getJSONArray("list");

                    JSONObject arrayObject0 = listArray.getJSONObject(0);
                    mTimeInterval0 = arrayObject0.getString("dt_txt");
                    index0Text.setText(mTimeInterval0);

                    JSONObject arrayObject1 = listArray.getJSONObject(1);
                    mTimeInterval1 = arrayObject1.getString("dt_txt");
                    index1Text.setText(mTimeInterval1);

                    JSONObject arrayObject2 = listArray.getJSONObject(2);
                    mTimeInterval2 = arrayObject2.getString("dt_txt");
                    index2Text.setText(mTimeInterval2);

                    JSONObject arrayObject3 = listArray.getJSONObject(3);
                    mTimeInterval3 = arrayObject3.getString("dt_txt");
                    index3Text.setText(mTimeInterval3);

                    JSONObject arrayObject4 = listArray.getJSONObject(4);
                    mTimeInterval4 = arrayObject4.getString("dt_txt");
                    index4Text.setText(mTimeInterval4);

                    JSONObject arrayObject5 = listArray.getJSONObject(5);
                    mTimeInterval5 = arrayObject5.getString("dt_txt");
                    index5Text.setText(mTimeInterval5);

                    JSONObject arrayObject6 = listArray.getJSONObject(6);
                    mTimeInterval6 = arrayObject6.getString("dt_txt");
                    index6Text.setText(mTimeInterval6);

                    JSONObject arrayObject7 = listArray.getJSONObject(7);
                    mTimeInterval7 = arrayObject7.getString("dt_txt");
                    index7Text.setText(mTimeInterval7);

                    JSONObject arrayObject8 = listArray.getJSONObject(8);
                    mTimeInterval8 = arrayObject8.getString("dt_txt");
                    index8Text.setText(mTimeInterval8);

                    JSONObject arrayObject9 = listArray.getJSONObject(9);
                    mTimeInterval9 = arrayObject9.getString("dt_txt");
                    index9Text.setText(mTimeInterval9);

                    JSONObject arrayObject10 = listArray.getJSONObject(10);
                    mTimeInterval10 = arrayObject10.getString("dt_txt");
                    index10Text.setText(mTimeInterval10);

                    JSONObject arrayObject11 = listArray.getJSONObject(11);
                    mTimeInterval11 = arrayObject11.getString("dt_txt");
                    index11Text.setText(mTimeInterval11);

                    JSONObject arrayObject12 = listArray.getJSONObject(12);
                    mTimeInterval12 = arrayObject12.getString("dt_txt");
                    index12Text.setText(mTimeInterval12);

                    JSONObject arrayObject13 = listArray.getJSONObject(13);
                    mTimeInterval13 = arrayObject13.getString("dt_txt");
                    index13Text.setText(mTimeInterval13);

                    JSONObject arrayObject14 = listArray.getJSONObject(14);
                    mTimeInterval14 = arrayObject14.getString("dt_txt");
                    index14Text.setText(mTimeInterval14);

                    JSONObject arrayObject15 = listArray.getJSONObject(15);
                    mTimeInterval15 = arrayObject15.getString("dt_txt");
                    index15Text.setText(mTimeInterval15);

                    JSONObject arrayObject16 = listArray.getJSONObject(16);
                    mTimeInterval16 = arrayObject16.getString("dt_txt");
                    index16Text.setText(mTimeInterval16);

                    JSONObject arrayObject17 = listArray.getJSONObject(17);
                    mTimeInterval17 = arrayObject17.getString("dt_txt");
                    index17Text.setText(mTimeInterval17);

                    JSONObject arrayObject18 = listArray.getJSONObject(18);
                    mTimeInterval18 = arrayObject18.getString("dt_txt");
                    index18Text.setText(mTimeInterval18);

                    JSONObject arrayObject19 = listArray.getJSONObject(19);
                    mTimeInterval19 = arrayObject19.getString("dt_txt");
                    index19Text.setText(mTimeInterval19);

                    JSONObject arrayObject20 = listArray.getJSONObject(20);
                    mTimeInterval20 = arrayObject20.getString("dt_txt");
                    index20Text.setText(mTimeInterval20);

                    JSONObject arrayObject21 = listArray.getJSONObject(21);
                    mTimeInterval21 = arrayObject21.getString("dt_txt");
                    index21Text.setText(mTimeInterval21);

                    JSONObject arrayObject22 = listArray.getJSONObject(22);
                    mTimeInterval22 = arrayObject22.getString("dt_txt");
                    index22Text.setText(mTimeInterval22);

                    JSONObject arrayObject23 = listArray.getJSONObject(23);
                    mTimeInterval23 = arrayObject23.getString("dt_txt");
                    index23Text.setText(mTimeInterval23);

                    JSONObject arrayObject24 = listArray.getJSONObject(24);
                    mTimeInterval24 = arrayObject24.getString("dt_txt");
                    index24Text.setText(mTimeInterval24);

                    JSONObject arrayObject25 = listArray.getJSONObject(25);
                    mTimeInterval25 = arrayObject25.getString("dt_txt");
                    index25Text.setText(mTimeInterval25);

                    JSONObject arrayObject26 = listArray.getJSONObject(26);
                    mTimeInterval26 = arrayObject26.getString("dt_txt");
                    index26Text.setText(mTimeInterval26);

                    JSONObject arrayObject27 = listArray.getJSONObject(27);
                    mTimeInterval27 = arrayObject27.getString("dt_txt");
                    index27Text.setText(mTimeInterval27);

                    JSONObject arrayObject28 = listArray.getJSONObject(28);
                    mTimeInterval28 = arrayObject28.getString("dt_txt");
                    index28Text.setText(mTimeInterval28);

                    JSONObject arrayObject29 = listArray.getJSONObject(29);
                    mTimeInterval29 = arrayObject29.getString("dt_txt");
                    index29Text.setText(mTimeInterval29);

                    JSONObject arrayObject30 = listArray.getJSONObject(30);
                    mTimeInterval30 = arrayObject30.getString("dt_txt");
                    index30Text.setText(mTimeInterval30);

                    JSONObject arrayObject31 = listArray.getJSONObject(31);
                    mTimeInterval31 = arrayObject31.getString("dt_txt");
                    index31Text.setText(mTimeInterval31);

                    JSONObject arrayObject32 = listArray.getJSONObject(32);
                    mTimeInterval32 = arrayObject32.getString("dt_txt");
                    index32Text.setText(mTimeInterval32);

                    JSONObject arrayObject33 = listArray.getJSONObject(33);
                    mTimeInterval33 = arrayObject33.getString("dt_txt");
                    index33Text.setText(mTimeInterval33);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(ForecastTimes.this);
        queue.add(jor);
    }
}
