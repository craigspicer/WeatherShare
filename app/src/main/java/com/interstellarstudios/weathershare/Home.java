package com.interstellarstudios.weathershare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import hotchemi.android.rate.AppRate;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTemperatureText;
    private TextView mLocationText;
    private TextView mDescriptionText;
    private TextView mHumidityText;
    private TextView mPressureText;
    private TextView mMinTempText;
    private TextView mMaxTempText;
    private TextView mLastUpdatedText;
    private TextView mWindSpeedText;
    private TextView mWindDegreesText;
    private TextView mSunriseText;
    private TextView mSunsetText;
    private String mCurrentUserId;
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
    private Boolean mSwitchOnOff;
    private String mLocationFinal;
    private ProgressDialog mProgressDialog;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_forecast:
                    Intent i = new Intent(Home.this, ForecastTimes.class);
                    i.putExtra("location", mLocationFinal);
                    startActivity(i);
                    return true;
                case R.id.navigation_account:
                    Intent j = new Intent(Home.this, Account.class);
                    startActivity(j);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //app rate
        AppRate.with(this)
                .setInstallDays(7)
                .setLaunchTimes(5)
                .setRemindInterval(2)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
        //AppRate.with(this).showRateDialog(this);

        //Loading shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        mSwitchOnOff = sharedPreferences.getBoolean("switchUnits", false);

        FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFireBaseFireStore = FirebaseFirestore.getInstance();

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mTemperatureText = findViewById(R.id.temperatureText);
        mLocationText = findViewById(R.id.locationText);
        mDescriptionText = findViewById(R.id.descriptionText);
        mHumidityText = findViewById(R.id.humidityText);
        mPressureText = findViewById(R.id.pressureText);
        mMinTempText = findViewById(R.id.minTempText);
        mMaxTempText = findViewById(R.id.maxTempText);
        mLastUpdatedText = findViewById(R.id.lastUpdatedText);
        mWindSpeedText = findViewById(R.id.windSpeedText);
        mWindDegreesText = findViewById(R.id.windDegreesText);
        mSunriseText = findViewById(R.id.sunriseText);
        mSunsetText = findViewById(R.id.sunsetText);
        mProgressDialog = new ProgressDialog(this);

        ImageView shareImage = findViewById(R.id.share_button);
        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Share.class);
                i.putExtra("location", mLocationFinal);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        TextView shareText = findViewById(R.id.share_textview);
        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Share.class);
                i.putExtra("location", mLocationFinal);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

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

        mProgressDialog.setMessage("Refreshing");
        mProgressDialog.show();

        DocumentReference homeLocationRef = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Home_Location");
        homeLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        HomeLocationModel homeLocationModel = document.toObject(HomeLocationModel.class);
                        mHomeLocation = homeLocationModel.getHomeLocation();

                        findWeather(mHomeLocation);
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
            Intent i = new Intent(Home.this, Settings.class);
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
            findWeather(mHomeLocation);
        } else if (id == R.id.nav_location2) {
            findWeather(favouriteLocation1);
        } else if (id == R.id.nav_location3) {
            findWeather(favouriteLocation2);
        } else if (id == R.id.nav_location4) {
            findWeather(favouriteLocation3);
        } else if (id == R.id.nav_location5) {
            findWeather(favouriteLocation4);
        } else if (id == R.id.nav_location6) {
            findWeather(favouriteLocation5);
        } else if (id == R.id.nav_location7) {
            findWeather(favouriteLocation6);
        } else if (id == R.id.nav_location8) {
            findWeather(favouriteLocation7);
        } else if (id == R.id.nav_location9) {
            findWeather(favouriteLocation8);
        } else if (id == R.id.nav_location10) {
            findWeather(favouriteLocation9);
        } else if (id == R.id.nav_location11) {
            findWeather(favouriteLocation10);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void findWeather(String city) {

        String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
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
                    //sys object
                    JSONObject sysObject = response.getJSONObject("sys");
                    String country = String.valueOf(sysObject.getString("country"));
                    String sunrise = String.valueOf(sysObject.getInt("sunrise"));
                    String sunset = String.valueOf(sysObject.getInt("sunset"));

                    //weather array
                    JSONArray jsonArray = response.getJSONArray("weather");
                    JSONObject arrayObject = jsonArray.getJSONObject(0);
                    String description = arrayObject.getString("description");
                    String weatherId = arrayObject.getString("id");
                    int weatherIdInt = Integer.parseInt(weatherId);

                    //main object
                    JSONObject mainObject = response.getJSONObject("main");
                    String temp = String.valueOf(mainObject.getInt("temp"));
                    String humidity = String.valueOf(mainObject.getInt("humidity"));
                    String pressure = String.valueOf(mainObject.getInt("pressure"));
                    String tempMin = String.valueOf(mainObject.getInt("temp_min"));
                    String tempMax = String.valueOf(mainObject.getInt("temp_max"));

                    //wind object
                    JSONObject windObject = response.getJSONObject("wind");
                    String windSpeed = String.valueOf(windObject.getDouble("speed"));
                    String windDegrees = String.valueOf(windObject.getInt("deg"));
                    int windDegreesInt = Integer.parseInt(windDegrees);

                    //clouds object
                    //JSONObject cloudsObject = response.getJSONObject("clouds");
                    //String clouds = String.valueOf(cloudsObject.getInt("all"));

                    //name object
                    String city = response.getString("name");

                    //last update object
                    String lastUpdate = response.getString("dt");

                    //unix time conversion
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss (z)");
                    int lastUpdateInt = Integer.parseInt(lastUpdate);
                    long lastUpdateUnix = lastUpdateInt;
                    Date date = new java.util.Date(lastUpdateUnix * 1000L);
                    String lastUpdateFinal = ("Last Updated: " + sdf.format(date));
                    int sunriseInt = Integer.parseInt(sunrise);
                    long sunriseUnix = sunriseInt;
                    Date date2 = new java.util.Date(sunriseUnix * 1000L);
                    String sunriseFinal = (sdf.format(date2));
                    int sunsetInt = Integer.parseInt(sunset);
                    long sunsetUnix = sunsetInt;
                    Date date3 = new java.util.Date(sunsetUnix * 1000L);
                    String sunsetFinal = (sdf.format(date3));

                    //wind direction
                    if (windDegreesInt >= 23 && windDegreesInt <= 68) {
                        mWindDegreesText.setText("NE");
                    } else if (windDegreesInt >= 69 && windDegreesInt <= 114) {
                        mWindDegreesText.setText("E");
                    } else if (windDegreesInt >= 114 && windDegreesInt <= 159) {
                        mWindDegreesText.setText("SE");
                    } else if (windDegreesInt >= 159 && windDegreesInt <= 204) {
                        mWindDegreesText.setText("S");
                    } else if (windDegreesInt >= 204 && windDegreesInt <= 249) {
                        mWindDegreesText.setText("SW");
                    } else if (windDegreesInt >= 249 && windDegreesInt <= 294) {
                        mWindDegreesText.setText("W");
                    } else if (windDegreesInt >= 294 && windDegreesInt <= 339) {
                        mWindDegreesText.setText("NW");
                    } else {
                        mWindDegreesText.setText("N");
                    }

                    //change background image
                    ConstraintLayout layout = findViewById(R.id.container2);
                    if (weatherIdInt >= 200 && weatherIdInt <= 232) {
                        layout.setBackgroundResource(R.mipmap.thunderstorm);
                    } else if (weatherIdInt >= 300 && weatherIdInt <= 321) {
                        layout.setBackgroundResource(R.mipmap.rain);
                    } else if (weatherIdInt >= 500 && weatherIdInt <= 531) {
                        layout.setBackgroundResource(R.mipmap.rain);
                    } else if (weatherIdInt >= 600 && weatherIdInt <= 622) {
                        layout.setBackgroundResource(R.mipmap.snow);
                    } else if (weatherIdInt >= 701 && weatherIdInt <= 721) {
                        layout.setBackgroundResource(R.mipmap.mist);
                    } else if (weatherIdInt == 731) {
                        layout.setBackgroundResource(R.mipmap.sand);
                    } else if (weatherIdInt == 741) {
                        layout.setBackgroundResource(R.mipmap.mist);
                    } else if (weatherIdInt >= 751 && weatherIdInt <= 761) {
                        layout.setBackgroundResource(R.mipmap.sand);
                    } else if (weatherIdInt == 800) {
                        layout.setBackgroundResource(R.mipmap.clear);
                    } else if (weatherIdInt >= 801 && weatherIdInt <= 803) {
                        layout.setBackgroundResource(R.mipmap.clouds);
                    } else if (weatherIdInt == 804) {
                        layout.setBackgroundResource(R.mipmap.overcast);
                    }

                    //final strings
                    mLocationFinal = (city + ", " + country);
                    String humidityFinal = (humidity + "%");
                    String PressureFinal = (pressure + "hPa");
                    String windSpeedFinalMetric = (windSpeed + "kph");
                    String windSpeedFinalImperial = (windSpeed + "mph");
                    String temperatureFinalMetric = (temp + "°C");
                    String temperatureFinalImperial = (temp + "°F");
                    String temperatureMinFinalMetric = (tempMin + "°C");
                    String temperatureMinFinalImperial = (tempMin + "°F");
                    String temperatureMaxFinalMetric = (tempMax + "°C");
                    String temperatureMaxFinalImperial = (tempMax + "°F");

                    //set views
                    mLocationText.setText(mLocationFinal);
                    mDescriptionText.setText(description);
                    mHumidityText.setText(humidityFinal);
                    mPressureText.setText(PressureFinal);
                    mLastUpdatedText.setText(lastUpdateFinal);
                    mSunriseText.setText(sunriseFinal);
                    mSunsetText.setText(sunsetFinal);

                    if (mSwitchOnOff) {
                        mWindSpeedText.setText(windSpeedFinalImperial);
                        mMinTempText.setText(temperatureMinFinalImperial);
                        mMaxTempText.setText(temperatureMaxFinalImperial);
                        mTemperatureText.setText(temperatureFinalImperial);
                    } else {
                        mWindSpeedText.setText(windSpeedFinalMetric);
                        mMinTempText.setText(temperatureMinFinalMetric);
                        mMaxTempText.setText(temperatureMaxFinalMetric);
                        mTemperatureText.setText(temperatureFinalMetric);
                    }

                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(Home.this);
        queue.add(jor);
    }
}
