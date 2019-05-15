package com.interstellarstudios.weathershare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import hotchemi.android.rate.AppRate;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.SmtpApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACCESS_LOCATION_PERMISSIONS_REQUEST = 1;
    private TextView mTemperatureText;
    private TextView mLocationText;
    private TextView mDescriptionText;
    private TextView mHumidityText;
    private TextView mPressureText;
    private TextView mMinTempText;
    private TextView mMaxTempText;
    private TextView mWindSpeedText;
    private TextView mWindDegreesText;
    private TextView mSunriseText;
    private TextView mSunsetText;
    private TextView mIndexUV;
    private TextView mDay1Text;
    private TextView mDay2Text;
    private TextView mDay3Text;
    private TextView mDay4Text;
    private TextView mDay5Text;
    private TextView mDescription1Text;
    private TextView mDescription2Text;
    private TextView mDescription3Text;
    private TextView mDescription4Text;
    private TextView mDescription5Text;
    private TextView mTempForecast1;
    private TextView mTempForecast2;
    private TextView mTempForecast3;
    private TextView mTempForecast4;
    private TextView mTempForecast5;
    private AutoCompleteTextView mSearchField;
    private EditText mSharedUserEmailText;
    private String mSharedUserEmail;
    private String mHomeLocation;
    private String mLocationFinal;
    private String mSearchLocation;
    private String mFavouriteLocation1;
    private String mFavouriteLocation2;
    private String mFavouriteLocation3;
    private String mFavouriteLocation4;
    private String mFavouriteLocation5;
    private Boolean mSwitchOnOff;
    private FirebaseAnalytics mFireBaseAnalytics;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AppRate.with(this)
                .setInstallDays(7)
                .setLaunchTimes(5)
                .setRemindInterval(2)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
        //AppRate.with(this).showRateDialog(this);

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissionToAccessLocation();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        mSwitchOnOff = sharedPreferences.getBoolean("switchUnits", false);
        mHomeLocation = sharedPreferences.getString("homeLocation", "");
        mFavouriteLocation1 = sharedPreferences.getString("favouriteLocation1", "");
        mFavouriteLocation2 = sharedPreferences.getString("favouriteLocation2", "");
        mFavouriteLocation3 = sharedPreferences.getString("favouriteLocation3", "");
        mFavouriteLocation4 = sharedPreferences.getString("favouriteLocation4", "");
        mFavouriteLocation5 = sharedPreferences.getString("favouriteLocation5", "");

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        final Bundle analyticsBundle = new Bundle();

        mSharedUserEmailText = findViewById(R.id.shared_user_email);
        mTemperatureText = findViewById(R.id.temperatureText);
        mLocationText = findViewById(R.id.locationText);
        mDescriptionText = findViewById(R.id.descriptionText);
        mHumidityText = findViewById(R.id.humidityText);
        mPressureText = findViewById(R.id.pressureText);
        mMinTempText = findViewById(R.id.minTempText);
        mMaxTempText = findViewById(R.id.maxTempText);
        mWindSpeedText = findViewById(R.id.windSpeedText);
        mWindDegreesText = findViewById(R.id.windDegreesText);
        mSunriseText = findViewById(R.id.sunriseText);
        mSunsetText = findViewById(R.id.sunsetText);
        mIndexUV = findViewById(R.id.indexUVtext);
        mProgressDialog = new ProgressDialog(this);

        mDay1Text = findViewById(R.id.day1);
        mDay2Text = findViewById(R.id.day2);
        mDay3Text = findViewById(R.id.day3);
        mDay4Text = findViewById(R.id.day4);
        mDay5Text = findViewById(R.id.day5);
        mDescription1Text = findViewById(R.id.description1);
        mDescription2Text = findViewById(R.id.description2);
        mDescription3Text = findViewById(R.id.description3);
        mDescription4Text = findViewById(R.id.description4);
        mDescription5Text = findViewById(R.id.description5);
        mTempForecast1 = findViewById(R.id.tempForecast1);
        mTempForecast2 = findViewById(R.id.tempForecast2);
        mTempForecast3 = findViewById(R.id.tempForecast3);
        mTempForecast4 = findViewById(R.id.tempForecast4);
        mTempForecast5 = findViewById(R.id.tempForecast5);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView navDrawerMenu = findViewById(R.id.navDrawerMenu);
        navDrawerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        ImageView searchIcon = findViewById(R.id.searchButton);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchLocation = mSearchField.getText().toString();
                findWeather(mSearchLocation, "", "");
                findForecast(mSearchLocation, "", "");
            }
        });

        mSearchField = findViewById(R.id.searchField);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, SearchSuggestions.getSearchSuggestions(this));
        mSearchField.setAdapter(adapter);

        mSearchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = mSearchField.getText().toString();
                findWeather(selectedLocation, "", "");
                findForecast(selectedLocation, "", "");
            }
        });

        mSearchField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            mSearchLocation = mSearchField.getText().toString();
                            findWeather(mSearchLocation, "", "");
                            findForecast(mSearchLocation, "", "");
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        ImageView shareIcon = findViewById(R.id.share_icon);
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
                Toast.makeText(Home.this, "Weather report emailed to: " + mSharedUserEmail, Toast.LENGTH_LONG).show();
                mFireBaseAnalytics.logEvent("weather_report_emailed", analyticsBundle);
            }
        });

        mSharedUserEmailText = findViewById(R.id.shared_user_email);
        mSharedUserEmailText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            sendMail();
                            Toast.makeText(Home.this, "Weather report emailed to: " + mSharedUserEmail, Toast.LENGTH_LONG).show();
                            mFireBaseAnalytics.logEvent("weather_report_emailed", analyticsBundle);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        final Menu menu = navigationView.getMenu();
        MenuItem navHomeLocation = menu.findItem(R.id.nav_home_location);
        navHomeLocation.setTitle(mHomeLocation);
        MenuItem favourite_location1 = menu.findItem(R.id.favourite_location1);
        favourite_location1.setTitle(mFavouriteLocation1);
        MenuItem favourite_location2 = menu.findItem(R.id.favourite_location2);
        favourite_location2.setTitle(mFavouriteLocation2);
        MenuItem favourite_location3 = menu.findItem(R.id.favourite_location3);
        favourite_location3.setTitle(mFavouriteLocation3);
        MenuItem favourite_location4 = menu.findItem(R.id.favourite_location4);
        favourite_location4.setTitle(mFavouriteLocation4);
        MenuItem favourite_location5 = menu.findItem(R.id.favourite_location5);
        favourite_location5.setTitle(mFavouriteLocation5);

        mProgressDialog.setMessage("Refreshing");
        mProgressDialog.show();

        GPStracker gpsTracker = new GPStracker(getApplicationContext());
        Location location = gpsTracker.getLocation();
        if (location != null) {

            double latitudeDouble = location.getLatitude();
            double longitudeDouble = location.getLongitude();
            String latitude = Double.toString(latitudeDouble);
            String longitude = Double.toString(longitudeDouble);

            findWeather("", latitude, longitude);
            findForecast("", latitude, longitude);

        } else {

            findWeather(mHomeLocation, "", "");
            findForecast(mHomeLocation, "", "");
        }
    }

    public void getPermissionToAccessLocation() {

        new AlertDialog.Builder(this)
                .setTitle("Permission needed to access Location")
                .setMessage("This permission is needed in order to get weather data for your current location. Manually enable in Settings > Apps & notifications > WeatherShare > Permissions.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                ACCESS_LOCATION_PERMISSIONS_REQUEST);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_PERMISSIONS_REQUEST);
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
            findWeather(mHomeLocation, "", "");
            findForecast(mHomeLocation, "", "");
        } else if (id == R.id.nav_current_location) {

            GPStracker gpsTracker = new GPStracker(getApplicationContext());
            Location location = gpsTracker.getLocation();
            if (location != null) {
                double latitudeDouble = location.getLatitude();
                double longitudeDouble = location.getLongitude();
                String latitude = Double.toString(latitudeDouble);
                String longitude = Double.toString(longitudeDouble);

                findWeather("", latitude, longitude);
                findForecast("", latitude, longitude);
            } else {
                Toast.makeText(Home.this, "Waiting for location. Please try again.", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.favourite_location1) {
            findWeather(mFavouriteLocation1, "", "");
            findForecast(mFavouriteLocation1, "", "");
        } else if (id == R.id.favourite_location2) {
            findWeather(mFavouriteLocation2, "", "");
            findForecast(mFavouriteLocation2, "", "");
        } else if (id == R.id.favourite_location3) {
            findWeather(mFavouriteLocation3, "", "");
            findForecast(mFavouriteLocation3, "", "");
        } else if (id == R.id.favourite_location4) {
            findWeather(mFavouriteLocation4, "", "");
            findForecast(mFavouriteLocation4, "", "");
        } else if (id == R.id.favourite_location5) {
            findWeather(mFavouriteLocation5, "", "");
            findForecast(mFavouriteLocation5, "", "");
        } else if (id == R.id.account) {
            Intent i = new Intent(Home.this, Account.class);
            startActivity(i);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (id == R.id.settings) {
            Intent j = new Intent(Home.this, Settings.class);
            startActivity(j);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void findWeather(String city, String latitude, String longitude) {

        String mWeatherURL;

        if (city.equals("")) {
            String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";
            String LAT_LON = "lat=" + latitude + "&lon=" + longitude;
            String API_KEY = "&appid=157187733bb90119ccc38f4d8d1f6da7";
            String unitsURL;

            if (mSwitchOnOff) {
                unitsURL = "&units=imperial";
            } else {
                unitsURL = "&units=metric";
            }
            mWeatherURL = BASE_URL + LAT_LON + API_KEY + unitsURL;
        } else {
            String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
            String API_KEY = "&appid=157187733bb90119ccc38f4d8d1f6da7";
            String unitsURL;

            if (mSwitchOnOff) {
                unitsURL = "&units=imperial";
            } else {
                unitsURL = "&units=metric";
            }
            mWeatherURL = BASE_URL + city + API_KEY + unitsURL;
        }

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, mWeatherURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //coord object
                    JSONObject coordObject = response.getJSONObject("coord");
                    String latitudeFinal = String.valueOf(coordObject.getString("lat"));
                    String longitudeFinal = String.valueOf(coordObject.getInt("lon"));

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
                    double windSpeedDouble = Double.parseDouble(windSpeed);
                    int windDegreesInt = Integer.parseInt(windDegrees);

                    //name object
                    String city = response.getString("name");

                    //unix time conversion
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss (z)");
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
                    } else if (weatherIdInt >= 762 && weatherIdInt <= 781) {
                        layout.setBackgroundResource(R.mipmap.mist);
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
                    mSunriseText.setText(sunriseFinal);
                    mSunsetText.setText(sunsetFinal);

                    if (mSwitchOnOff) {
                        //converting wind speed
                        double windSpeedConvert = windSpeedDouble * 1.15078;
                        double windSpeedDoubleRounded = Math.round(windSpeedConvert * 10) / 10.0;
                        String finalWindSpeedImperial = windSpeedDoubleRounded + "mph";

                        mWindSpeedText.setText(finalWindSpeedImperial);
                        mMinTempText.setText(temperatureMinFinalImperial);
                        mMaxTempText.setText(temperatureMaxFinalImperial);
                        mTemperatureText.setText(temperatureFinalImperial);
                    } else {
                        //converting wind speed
                        double windSpeedConvert = windSpeedDouble * 3.6;
                        double windSpeedDoubleRounded = Math.round(windSpeedConvert * 10) / 10.0;
                        String finalWindSpeedMetric = windSpeedDoubleRounded + "kph";

                        mWindSpeedText.setText(finalWindSpeedMetric);
                        mMinTempText.setText(temperatureMinFinalMetric);
                        mMaxTempText.setText(temperatureMaxFinalMetric);
                        mTemperatureText.setText(temperatureFinalMetric);
                    }

                    findIndexUV(latitudeFinal, longitudeFinal);

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

    private void findIndexUV(String latitude, String longitude) {

        String BASE_URL = "https://api.openweathermap.org/data/2.5/uvi?";
        String API_KEY = "appid=157187733bb90119ccc38f4d8d1f6da7";
        String LAT_LON = "&lat=" + latitude + "&lon=" + longitude;
        String FINAL_URL = BASE_URL + API_KEY + LAT_LON;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, FINAL_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    double indexUV = response.getDouble("value");

                    if (indexUV >= 0 && indexUV <= 2) {
                        mIndexUV.setText("UV Index: Low");
                    } else if (indexUV >= 3 && indexUV <= 5) {
                        mIndexUV.setText("UV Index: Moderate");
                    } else if (indexUV >= 6 && indexUV <= 7) {
                        mIndexUV.setText("UV Index: High");
                    } else if (indexUV >= 8 && indexUV <= 10) {
                        mIndexUV.setText("UV Index: Very High");
                    } else {
                        mIndexUV.setText("UV Index: Extreme");
                    }

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

    private void findForecast(String city, String latitude, String longitude) {

        String mForecastURL;

        if (city.equals("")) {
            String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?";
            String LAT_LON = "lat=" + latitude + "&lon=" + longitude;
            String API_KEY = "&appid=157187733bb90119ccc38f4d8d1f6da7";
            String unitsURL;

            if (mSwitchOnOff) {
                unitsURL = "&units=imperial";
            } else {
                unitsURL = "&units=metric";
            }
            mForecastURL = BASE_URL + LAT_LON + API_KEY + unitsURL;
        } else {
            String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?q=";
            String API_KEY = "&appid=157187733bb90119ccc38f4d8d1f6da7";
            String unitsURL;

            if (mSwitchOnOff) {
                unitsURL = "&units=imperial";
            } else {
                unitsURL = "&units=metric";
            }
            mForecastURL = BASE_URL + city + API_KEY + unitsURL;
        }

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, mForecastURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy");

                    //list array
                    JSONArray listArray = response.getJSONArray("list");

                    //index 0 of the list array
                    JSONObject arrayObject0 = listArray.getJSONObject(7);

                    //date time
                    String dateTime0 = arrayObject0.getString("dt");

                    //main object inside list array
                    JSONObject mainObject0 = arrayObject0.getJSONObject("main");
                    String tempForecast0 = String.valueOf(mainObject0.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray0 = arrayObject0.getJSONArray("weather");
                    JSONObject weatherObject0 = weatherArray0.getJSONObject(0);
                    String description0 = weatherObject0.getString("description");
                    String forecastId0 = weatherObject0.getString("id");
                    int forecastIdInt0 = Integer.parseInt(forecastId0);

                    //converting dt
                    int dateTimeInt0 = Integer.parseInt(dateTime0);
                    long dateTimeUnix0 = dateTimeInt0;
                    Date date0 = new java.util.Date(dateTimeUnix0 * 1000L);
                    String dateTimeFinal0 = (sdf.format(date0));

                    //final strings
                    String temperatureForecastMetric0 = (tempForecast0 + "°C");
                    String temperatureForecastImperial0 = (tempForecast0 + "°F");

                    //index 1 of the list array
                    JSONObject arrayObject1 = listArray.getJSONObject(15);

                    //date time
                    String dateTime1 = arrayObject1.getString("dt");

                    //main object inside list array
                    JSONObject mainObject1 = arrayObject1.getJSONObject("main");
                    String tempForecast1 = String.valueOf(mainObject1.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray1 = arrayObject1.getJSONArray("weather");
                    JSONObject weatherObject1 = weatherArray1.getJSONObject(0);
                    String description1 = weatherObject1.getString("description");
                    String forecastId1 = weatherObject1.getString("id");
                    int forecastIdInt1 = Integer.parseInt(forecastId1);

                    //converting dt
                    int dateTimeInt1 = Integer.parseInt(dateTime1);
                    long dateTimeUnix1 = dateTimeInt1;
                    Date date1 = new java.util.Date(dateTimeUnix1 * 1000L);
                    String dateTimeFinal1 = (sdf.format(date1));

                    //final strings
                    String temperatureForecastMetric1 = (tempForecast1 + "°C");
                    String temperatureForecastImperial1 = (tempForecast1 + "°F");
                    //index 2 of the list array
                    JSONObject arrayObject2 = listArray.getJSONObject(23);

                    //date time
                    String dateTime2 = arrayObject2.getString("dt");

                    //main object inside list array
                    JSONObject mainObject2 = arrayObject2.getJSONObject("main");
                    String tempForecast2 = String.valueOf(mainObject2.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray2 = arrayObject2.getJSONArray("weather");
                    JSONObject weatherObject2 = weatherArray2.getJSONObject(0);
                    String description2 = weatherObject2.getString("description");
                    String forecastId2 = weatherObject2.getString("id");
                    int forecastIdInt2 = Integer.parseInt(forecastId2);

                    //converting dt
                    int dateTimeInt2 = Integer.parseInt(dateTime2);
                    long dateTimeUnix2 = dateTimeInt2;
                    Date date2 = new java.util.Date(dateTimeUnix2 * 1000L);
                    String dateTimeFinal2 = (sdf.format(date2));

                    //final strings
                    String temperatureForecastMetric2 = (tempForecast2 + "°C");
                    String temperatureForecastImperial2 = (tempForecast2 + "°F");

                    //index 3 of the list array
                    JSONObject arrayObject3 = listArray.getJSONObject(31);

                    //date time
                    String dateTime3 = arrayObject3.getString("dt");

                    //main object inside list array
                    JSONObject mainObject3 = arrayObject3.getJSONObject("main");
                    String tempForecast3 = String.valueOf(mainObject3.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray3 = arrayObject3.getJSONArray("weather");
                    JSONObject weatherObject3 = weatherArray3.getJSONObject(0);
                    String description3 = weatherObject3.getString("description");
                    String forecastId3 = weatherObject3.getString("id");
                    int forecastIdInt3 = Integer.parseInt(forecastId3);

                    //converting dt
                    int dateTimeInt3 = Integer.parseInt(dateTime3);
                    long dateTimeUnix3 = dateTimeInt3;
                    Date date3 = new java.util.Date(dateTimeUnix3 * 1000L);
                    String dateTimeFinal3 = (sdf.format(date3));

                    //final strings
                    String temperatureForecastMetric3 = (tempForecast3 + "°C");
                    String temperatureForecastImperial3 = (tempForecast3 + "°F");

                    //index 4 of the list array
                    JSONObject arrayObject4 = listArray.getJSONObject(39);

                    //date time
                    String dateTime4 = arrayObject4.getString("dt");

                    //main object inside list array
                    JSONObject mainObject4 = arrayObject4.getJSONObject("main");
                    String tempForecast4 = String.valueOf(mainObject4.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray4 = arrayObject4.getJSONArray("weather");
                    JSONObject weatherObject4 = weatherArray4.getJSONObject(0);
                    String description4 = weatherObject4.getString("description");
                    String forecastId4 = weatherObject4.getString("id");
                    int forecastIdInt4 = Integer.parseInt(forecastId4);

                    //converting dt
                    int dateTimeInt4 = Integer.parseInt(dateTime4);
                    long dateTimeUnix4 = dateTimeInt4;
                    Date date4 = new java.util.Date(dateTimeUnix4 * 1000L);
                    String dateTimeFinal4 = (sdf.format(date4));

                    //final strings
                    String temperatureForecastMetric4 = (tempForecast4 + "°C");
                    String temperatureForecastImperial4 = (tempForecast4 + "°F");

                    //change forecast icon
                    ImageView day1Icon = findViewById(R.id.day1Icon);
                    if (forecastIdInt0 >= 200 && forecastIdInt0 <= 232) {
                        day1Icon.setImageResource(R.drawable.icon_thunderstorm);
                    } else if (forecastIdInt0 >= 300 && forecastIdInt0 <= 321) {
                        day1Icon.setImageResource(R.drawable.icon_drizzle);
                    } else if (forecastIdInt0 >= 500 && forecastIdInt0 <= 531) {
                        day1Icon.setImageResource(R.drawable.icon_rain);
                    } else if (forecastIdInt0 >= 600 && forecastIdInt0 <= 622) {
                        day1Icon.setImageResource(R.drawable.icon_snow);
                    } else if (forecastIdInt0 >= 701 && forecastIdInt0 <= 781) {
                        day1Icon.setImageResource(R.drawable.icon_mist);
                    } else if (forecastIdInt0 == 800) {
                        day1Icon.setImageResource(R.drawable.icon_clear);
                    } else if (forecastIdInt0 >= 801 && forecastIdInt0 <= 803) {
                        day1Icon.setImageResource(R.drawable.icon_clouds);
                    } else if (forecastIdInt0 == 804) {
                        day1Icon.setImageResource(R.drawable.icon_overcast);
                    }

                    //change forecast icon
                    ImageView day2Icon = findViewById(R.id.day2Icon);
                    if (forecastIdInt1 >= 200 && forecastIdInt1 <= 232) {
                        day2Icon.setImageResource(R.drawable.icon_thunderstorm);
                    } else if (forecastIdInt1 >= 300 && forecastIdInt1 <= 321) {
                        day2Icon.setImageResource(R.drawable.icon_drizzle);
                    } else if (forecastIdInt1 >= 500 && forecastIdInt1 <= 531) {
                        day2Icon.setImageResource(R.drawable.icon_rain);
                    } else if (forecastIdInt1 >= 600 && forecastIdInt1 <= 622) {
                        day2Icon.setImageResource(R.drawable.icon_snow);
                    } else if (forecastIdInt1 >= 701 && forecastIdInt1 <= 781) {
                        day2Icon.setImageResource(R.drawable.icon_mist);
                    } else if (forecastIdInt1 == 800) {
                        day2Icon.setImageResource(R.drawable.icon_clear);
                    } else if (forecastIdInt1 >= 801 && forecastIdInt1 <= 803) {
                        day2Icon.setImageResource(R.drawable.icon_clouds);
                    } else if (forecastIdInt1 == 804) {
                        day2Icon.setImageResource(R.drawable.icon_overcast);
                    }

                    //change forecast icon
                    ImageView day3Icon = findViewById(R.id.day3Icon);
                    if (forecastIdInt2 >= 200 && forecastIdInt2 <= 232) {
                        day3Icon.setImageResource(R.drawable.icon_thunderstorm);
                    } else if (forecastIdInt2 >= 300 && forecastIdInt2 <= 321) {
                        day3Icon.setImageResource(R.drawable.icon_drizzle);
                    } else if (forecastIdInt2 >= 500 && forecastIdInt2 <= 531) {
                        day3Icon.setImageResource(R.drawable.icon_rain);
                    } else if (forecastIdInt2 >= 600 && forecastIdInt2 <= 622) {
                        day3Icon.setImageResource(R.drawable.icon_snow);
                    } else if (forecastIdInt2 >= 701 && forecastIdInt2 <= 781) {
                        day3Icon.setImageResource(R.drawable.icon_mist);
                    } else if (forecastIdInt2 == 800) {
                        day3Icon.setImageResource(R.drawable.icon_clear);
                    } else if (forecastIdInt2 >= 801 && forecastIdInt2 <= 803) {
                        day3Icon.setImageResource(R.drawable.icon_clouds);
                    } else if (forecastIdInt2 == 804) {
                        day3Icon.setImageResource(R.drawable.icon_overcast);
                    }

                    //change forecast icon
                    ImageView day4Icon = findViewById(R.id.day4Icon);
                    if (forecastIdInt3 >= 200 && forecastIdInt3 <= 232) {
                        day4Icon.setImageResource(R.drawable.icon_thunderstorm);
                    } else if (forecastIdInt3 >= 300 && forecastIdInt3 <= 321) {
                        day4Icon.setImageResource(R.drawable.icon_drizzle);
                    } else if (forecastIdInt3 >= 500 && forecastIdInt3 <= 531) {
                        day4Icon.setImageResource(R.drawable.icon_rain);
                    } else if (forecastIdInt3 >= 600 && forecastIdInt3 <= 622) {
                        day4Icon.setImageResource(R.drawable.icon_snow);
                    } else if (forecastIdInt3 >= 701 && forecastIdInt3 <= 781) {
                        day4Icon.setImageResource(R.drawable.icon_mist);
                    } else if (forecastIdInt3 == 800) {
                        day4Icon.setImageResource(R.drawable.icon_clear);
                    } else if (forecastIdInt3 >= 801 && forecastIdInt3 <= 803) {
                        day4Icon.setImageResource(R.drawable.icon_clouds);
                    } else if (forecastIdInt3 == 804) {
                        day4Icon.setImageResource(R.drawable.icon_overcast);
                    }

                    //change forecast icon
                    ImageView day5Icon = findViewById(R.id.day5Icon);
                    if (forecastIdInt4 >= 200 && forecastIdInt4 <= 232) {
                        day5Icon.setImageResource(R.drawable.icon_thunderstorm);
                    } else if (forecastIdInt4 >= 300 && forecastIdInt4 <= 321) {
                        day5Icon.setImageResource(R.drawable.icon_drizzle);
                    } else if (forecastIdInt4 >= 500 && forecastIdInt4 <= 531) {
                        day5Icon.setImageResource(R.drawable.icon_rain);
                    } else if (forecastIdInt4 >= 600 && forecastIdInt4 <= 622) {
                        day5Icon.setImageResource(R.drawable.icon_snow);
                    } else if (forecastIdInt4 >= 701 && forecastIdInt4 <= 781) {
                        day5Icon.setImageResource(R.drawable.icon_mist);
                    } else if (forecastIdInt4 == 800) {
                        day5Icon.setImageResource(R.drawable.icon_clear);
                    } else if (forecastIdInt4 >= 801 && forecastIdInt4 <= 803) {
                        day5Icon.setImageResource(R.drawable.icon_clouds);
                    } else if (forecastIdInt4 == 804) {
                        day5Icon.setImageResource(R.drawable.icon_overcast);
                    }

                    mDay1Text.setText(dateTimeFinal0);
                    mDay2Text.setText(dateTimeFinal1);
                    mDay3Text.setText(dateTimeFinal2);
                    mDay4Text.setText(dateTimeFinal3);
                    mDay5Text.setText(dateTimeFinal4);
                    mDescription1Text.setText(description0.toUpperCase());
                    mDescription2Text.setText(description1.toUpperCase());
                    mDescription3Text.setText(description2.toUpperCase());
                    mDescription4Text.setText(description3.toUpperCase());
                    mDescription5Text.setText(description4.toUpperCase());

                    if (mSwitchOnOff) {
                        mTempForecast1.setText(temperatureForecastImperial0);
                        mTempForecast2.setText(temperatureForecastImperial1);
                        mTempForecast3.setText(temperatureForecastImperial2);
                        mTempForecast4.setText(temperatureForecastImperial3);
                        mTempForecast5.setText(temperatureForecastImperial4);
                    } else {
                        mTempForecast1.setText(temperatureForecastMetric0);
                        mTempForecast2.setText(temperatureForecastMetric1);
                        mTempForecast3.setText(temperatureForecastMetric2);
                        mTempForecast4.setText(temperatureForecastMetric3);
                        mTempForecast5.setText(temperatureForecastMetric4);
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

    private void sendMail() {

        mSharedUserEmail = mSharedUserEmailText.getText().toString().trim();
        if (mSharedUserEmail.trim().isEmpty()) {
            return;
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String temperature = mTemperatureText.getText().toString();
                    String minTemp = mMinTempText.getText().toString();
                    String maxTemp = mMaxTempText.getText().toString();
                    String description = mDescriptionText.getText().toString().toUpperCase();
                    String humidity = mHumidityText.getText().toString();
                    String pressure = mPressureText.getText().toString();
                    String windSpeed = mWindSpeedText.getText().toString();
                    String windDirection = mWindDegreesText.getText().toString();
                    String sunrise = mSunriseText.getText().toString();
                    String sunset = mSunsetText.getText().toString();
                    String indexUV = mIndexUV.getText().toString();

                    String day1 = mDay1Text.getText().toString();
                    String day2 = mDay2Text.getText().toString();
                    String day3 = mDay3Text.getText().toString();
                    String day4 = mDay4Text.getText().toString();
                    String day5 = mDay5Text.getText().toString();
                    String descrDay1 = mDescription1Text.getText().toString().toUpperCase();
                    String descrDay2 = mDescription2Text.getText().toString().toUpperCase();
                    String descrDay3 = mDescription3Text.getText().toString().toUpperCase();
                    String descrDay4 = mDescription4Text.getText().toString().toUpperCase();
                    String descrDay5 = mDescription5Text.getText().toString().toUpperCase();
                    String tempDay1 = mTempForecast1.getText().toString();
                    String tempDay2 = mTempForecast2.getText().toString();
                    String tempDay3 = mTempForecast3.getText().toString();
                    String tempDay4 = mTempForecast4.getText().toString();
                    String tempDay5 = mTempForecast5.getText().toString();

                    ApiClient defaultClient = Configuration.getDefaultApiClient();

                    ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
                    apiKey.setApiKey("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");

                    SmtpApi apiInstance = new SmtpApi();

                    List<SendSmtpEmailTo> emailArrayList = new ArrayList<>();
                    emailArrayList.add(new SendSmtpEmailTo().email(mSharedUserEmail));

                    SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
                    sendSmtpEmail.sender(new SendSmtpEmailSender().email("weathershare@interstellarstudios.co.uk").name("WeatherShare"));
                    sendSmtpEmail.to(emailArrayList);
                    sendSmtpEmail.subject("You've Received a Shared Weather Report");
                    sendSmtpEmail.htmlContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" /><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><meta name=\"x-apple-disable-message-reformatting\" /><meta name=\"apple-mobile-web-app-capable\" content=\"yes\" /><meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black\" /><meta name=\"format-detection\" content=\"telephone=no\" /><title></title><style type=\"text/css\">\n" +
                            "        /* Resets */\n" +
                            "        .ReadMsgBody { width: 100%; background-color: #ebebeb;}\n" +
                            "        .ExternalClass {width: 100%; background-color: #ebebeb;}\n" +
                            "        .ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {line-height:100%;}\n" +
                            "        a[x-apple-data-detectors]{\n" +
                            "            color:inherit !important;\n" +
                            "            text-decoration:none !important;\n" +
                            "            font-size:inherit !important;\n" +
                            "            font-family:inherit !important;\n" +
                            "            font-weight:inherit !important;\n" +
                            "            line-height:inherit !important;\n" +
                            "        }        \n" +
                            "        body {-webkit-text-size-adjust:none; -ms-text-size-adjust:none;}\n" +
                            "        body {margin:0; padding:0;}\n" +
                            "        .yshortcuts a {border-bottom: none !important;}\n" +
                            "        .rnb-del-min-width{ min-width: 0 !important; }\n" +
                            "\n" +
                            "        /* Add new outlook css start */\n" +
                            "        .templateContainer{\n" +
                            "            max-width:590px !important;\n" +
                            "            width:auto !important;\n" +
                            "        }\n" +
                            "        /* Add new outlook css end */\n" +
                            "\n" +
                            "        /* Image width by default for 3 columns */\n" +
                            "        img[class=\"rnb-col-3-img\"] {\n" +
                            "        max-width:170px;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* Image width by default for 2 columns */\n" +
                            "        img[class=\"rnb-col-2-img\"] {\n" +
                            "        max-width:264px;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* Image width by default for 2 columns aside small size */\n" +
                            "        img[class=\"rnb-col-2-img-side-xs\"] {\n" +
                            "        max-width:180px;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* Image width by default for 2 columns aside big size */\n" +
                            "        img[class=\"rnb-col-2-img-side-xl\"] {\n" +
                            "        max-width:350px;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* Image width by default for 1 column */\n" +
                            "        img[class=\"rnb-col-1-img\"] {\n" +
                            "        max-width:550px;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* Image width by default for header */\n" +
                            "        img[class=\"rnb-header-img\"] {\n" +
                            "        max-width:590px;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* Ckeditor line-height spacing */\n" +
                            "        .rnb-force-col p, ul, ol{margin:0px!important;}\n" +
                            "        .rnb-del-min-width p, ul, ol{margin:0px!important;}\n" +
                            "\n" +
                            "        /* tmpl-2 preview */\n" +
                            "        .rnb-tmpl-width{ width:100%!important;}\n" +
                            "\n" +
                            "        /* tmpl-11 preview */\n" +
                            "        .rnb-social-width{padding-right:15px!important;}\n" +
                            "\n" +
                            "        /* tmpl-11 preview */\n" +
                            "        .rnb-social-align{float:right!important;}\n" +
                            "\n" +
                            "        /* Ul Li outlook extra spacing fix */\n" +
                            "        li{mso-margin-top-alt: 0; mso-margin-bottom-alt: 0;}        \n" +
                            "\n" +
                            "        /* Outlook fix */\n" +
                            "        table {mso-table-lspace:0pt; mso-table-rspace:0pt;}\n" +
                            "    \n" +
                            "        /* Outlook fix */\n" +
                            "        table, tr, td {border-collapse: collapse;}\n" +
                            "\n" +
                            "        /* Outlook fix */\n" +
                            "        p,a,li,blockquote {mso-line-height-rule:exactly;} \n" +
                            "\n" +
                            "        /* Outlook fix */\n" +
                            "        .msib-right-img { mso-padding-alt: 0 !important;}\n" +
                            "\n" +
                            "        @media only screen and (min-width:590px){\n" +
                            "        /* mac fix width */\n" +
                            "        .templateContainer{width:590px !important;}\n" +
                            "        }\n" +
                            "\n" +
                            "        @media screen and (max-width: 360px){\n" +
                            "        /* yahoo app fix width \"tmpl-2 tmpl-10 tmpl-13\" in android devices */\n" +
                            "        .rnb-yahoo-width{ width:360px !important;}\n" +
                            "        }\n" +
                            "\n" +
                            "        @media screen and (max-width: 380px){\n" +
                            "        /* fix width and font size \"tmpl-4 tmpl-6\" in mobile preview */\n" +
                            "        .element-img-text{ font-size:24px !important;}\n" +
                            "        .element-img-text2{ width:230px !important;}\n" +
                            "        .content-img-text-tmpl-6{ font-size:24px !important;}\n" +
                            "        .content-img-text2-tmpl-6{ width:220px !important;}\n" +
                            "        }\n" +
                            "\n" +
                            "        @media screen and (max-width: 480px) {\n" +
                            "        td[class=\"rnb-container-padding\"] {\n" +
                            "        padding-left: 10px !important;\n" +
                            "        padding-right: 10px !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* force container nav to (horizontal) blocks */\n" +
                            "        td.rnb-force-nav {\n" +
                            "        display: inherit;\n" +
                            "        }\n" +
                            "        }\n" +
                            "\n" +
                            "        @media only screen and (max-width: 600px) {\n" +
                            "\n" +
                            "        /* center the address &amp; social icons */\n" +
                            "        .rnb-text-center {text-align:center !important;}\n" +
                            "\n" +
                            "        /* force container columns to (horizontal) blocks */\n" +
                            "        td.rnb-force-col {\n" +
                            "        display: block;\n" +
                            "        padding-right: 0 !important;\n" +
                            "        padding-left: 0 !important;\n" +
                            "        width:100%;\n" +
                            "        }\n" +
                            "\n" +
                            "        table.rnb-container {\n" +
                            "         width: 100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        table.rnb-btn-col-content {\n" +
                            "        width: 100% !important;\n" +
                            "        }\n" +
                            "        table.rnb-col-3 {\n" +
                            "        /* unset table align=\"left/right\" */\n" +
                            "        float: none !important;\n" +
                            "        width: 100% !important;\n" +
                            "\n" +
                            "        /* change left/right padding and margins to top/bottom ones */\n" +
                            "        margin-bottom: 10px;\n" +
                            "        padding-bottom: 10px;\n" +
                            "        /*border-bottom: 1px solid #eee;*/\n" +
                            "        }\n" +
                            "\n" +
                            "        table.rnb-last-col-3 {\n" +
                            "        /* unset table align=\"left/right\" */\n" +
                            "        float: none !important;\n" +
                            "        width: 100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        table[class~=\"rnb-col-2\"] {\n" +
                            "        /* unset table align=\"left/right\" */\n" +
                            "        float: none !important;\n" +
                            "        width: 100% !important;\n" +
                            "\n" +
                            "        /* change left/right padding and margins to top/bottom ones */\n" +
                            "        margin-bottom: 10px;\n" +
                            "        padding-bottom: 10px;\n" +
                            "        /*border-bottom: 1px solid #eee;*/\n" +
                            "        }\n" +
                            "\n" +
                            "        table.rnb-col-2-noborder-onright {\n" +
                            "        /* unset table align=\"left/right\" */\n" +
                            "        float: none !important;\n" +
                            "        width: 100% !important;\n" +
                            "\n" +
                            "        /* change left/right padding and margins to top/bottom ones */\n" +
                            "        margin-bottom: 10px;\n" +
                            "        padding-bottom: 10px;\n" +
                            "        }\n" +
                            "\n" +
                            "        table.rnb-col-2-noborder-onleft {\n" +
                            "        /* unset table align=\"left/right\" */\n" +
                            "        float: none !important;\n" +
                            "        width: 100% !important;\n" +
                            "\n" +
                            "        /* change left/right padding and margins to top/bottom ones */\n" +
                            "        margin-top: 10px;\n" +
                            "        padding-top: 10px;\n" +
                            "        }\n" +
                            "\n" +
                            "        table.rnb-last-col-2 {\n" +
                            "        /* unset table align=\"left/right\" */\n" +
                            "        float: none !important;\n" +
                            "        width: 100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        table.rnb-col-1 {\n" +
                            "        /* unset table align=\"left/right\" */\n" +
                            "        float: none !important;\n" +
                            "        width: 100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        img.rnb-col-3-img {\n" +
                            "        /**max-width:none !important;**/\n" +
                            "        width:100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        img.rnb-col-2-img {\n" +
                            "        /**max-width:none !important;**/\n" +
                            "        width:100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        img.rnb-col-2-img-side-xs {\n" +
                            "        /**max-width:none !important;**/\n" +
                            "        width:100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        img.rnb-col-2-img-side-xl {\n" +
                            "        /**max-width:none !important;**/\n" +
                            "        width:100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        img.rnb-col-1-img {\n" +
                            "        /**max-width:none !important;**/\n" +
                            "        width:100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        img.rnb-header-img {\n" +
                            "        /**max-width:none !important;**/\n" +
                            "        width:100% !important;\n" +
                            "        margin:0 auto;\n" +
                            "        }\n" +
                            "\n" +
                            "        img.rnb-logo-img {\n" +
                            "        /**max-width:none !important;**/\n" +
                            "        width:100% !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        td.rnb-mbl-float-none {\n" +
                            "        float:inherit !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        .img-block-center{text-align:center !important;}\n" +
                            "\n" +
                            "        .logo-img-center\n" +
                            "        {\n" +
                            "            float:inherit !important;\n" +
                            "        }\n" +
                            "\n" +
                            "        /* tmpl-11 preview */\n" +
                            "        .rnb-social-align{margin:0 auto !important; float:inherit !important;}\n" +
                            "\n" +
                            "        /* tmpl-11 preview */\n" +
                            "        .rnb-social-center{display:inline-block;}\n" +
                            "\n" +
                            "        /* tmpl-11 preview */\n" +
                            "        .social-text-spacing{margin-bottom:0px !important; padding-bottom:0px !important;}\n" +
                            "\n" +
                            "        /* tmpl-11 preview */\n" +
                            "        .social-text-spacing2{padding-top:15px !important;}\n" +
                            "\n" +
                            "    }</style><!--[if gte mso 11]><style type=\"text/css\">table{border-spacing: 0; }table td {border-collapse: separate;}</style><![endif]--><!--[if !mso]><!--><style type=\"text/css\">table{border-spacing: 0;} table td {border-collapse: collapse;}</style> <!--<![endif]--><!--[if gte mso 15]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]--><!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]--></head><body>\n" +
                            "\n" +
                            "<table border=\"0\" align=\"center\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" class=\"main-template\" bgcolor=\"#f9fafc\" style=\"background-color: rgb(249, 250, 252);\">\n" +
                            "\n" +
                            "    <tbody><tr style=\"display:none !important; font-size:1px; mso-hide: all;\"><td></td><td></td></tr><tr>\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "        <!--[if gte mso 9]>\n" +
                            "                        <table align=\"center\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"590\" style=\"width:590px;\">\n" +
                            "                        <tr>\n" +
                            "                        <td align=\"center\" valign=\"top\" width=\"590\" style=\"width:590px;\">\n" +
                            "                        <![endif]-->\n" +
                            "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"templateContainer\" style=\"max-width:590px!important; width: 590px;\">\n" +
                            "        <tbody><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <div>\n" +
                            "                \n" +
                            "                <table class=\"rnb-del-min-width rnb-tmpl-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:590px;\" name=\"Layout_9\" id=\"Layout_9\">\n" +
                            "                    \n" +
                            "                    <tbody><tr>\n" +
                            "                        <td class=\"rnb-del-min-width\" valign=\"top\" align=\"center\" style=\"min-width: 590px;\">\n" +
                            "                            <table width=\"100%\" cellpadding=\"0\" border=\"0\" bgcolor=\"#f9fafc\" align=\"center\" cellspacing=\"0\" style=\"background-color: rgb(249, 250, 252);\">\n" +
                            "                                <tbody><tr>\n" +
                            "                                    <td height=\"10\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                </tr>\n" +
                            "                                <tr>\n" +
                            "                                    <td align=\"center\" height=\"20\" style=\"font-family:Arial,Helvetica,sans-serif; color:#666666;font-size:13px;font-weight:normal;text-align: center;\">\n" +
                            "                                        <span style=\"color: rgb(102, 102, 102); text-decoration: underline;\">\n" +
                            "                                            <a target=\"_blank\" href=\"{{ mirror }}\" style=\"text-decoration: underline; color: rgb(102, 102, 102);\">View in browser</a></span>\n" +
                            "                                    </td>\n" +
                            "                                </tr>\n" +
                            "                                <tr>\n" +
                            "                                    <td height=\"10\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                </tr>\n" +
                            "                            </tbody></table>\n" +
                            "                        </td>\n" +
                            "                    </tr>\n" +
                            "                </tbody></table>\n" +
                            "                \n" +
                            "            </div></td>\n" +
                            "    </tr><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <div>\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"width:100%;\">\n" +
                            "                <tr>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <td valign=\"top\" width=\"590\" style=\"width:590px;\">\n" +
                            "                <![endif]-->\n" +
                            "                <table class=\"rnb-del-min-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:590px;\" name=\"Layout_8\" id=\"Layout_8\">\n" +
                            "                <tbody><tr>\n" +
                            "                    <td class=\"rnb-del-min-width\" align=\"center\" valign=\"top\" style=\"min-width:590px;\">\n" +
                            "                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-container\" bgcolor=\"#ffffff\" style=\"background-color: rgb(255, 255, 255); border-radius: 0px; padding-left: 20px; padding-right: 20px; border-collapse: separate;\">\n" +
                            "                            <tbody><tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td valign=\"top\" class=\"rnb-container-padding\" align=\"left\">\n" +
                            "                                    <table width=\"100%\" cellpadding=\"0\" border=\"0\" align=\"center\" cellspacing=\"0\">\n" +
                            "                                        <tbody><tr>\n" +
                            "                                            <td valign=\"top\" align=\"center\">\n" +
                            "                                                <table cellpadding=\"0\" border=\"0\" align=\"center\" cellspacing=\"0\" class=\"logo-img-center\"> \n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td valign=\"middle\" align=\"center\" style=\"line-height: 0px;\">\n" +
                            "                                                            <div style=\"border-top:0px None #000;border-right:0px None #000;border-bottom:0px None #000;border-left:0px None #000;display:inline-block; \" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><div><img width=\"550\" vspace=\"0\" hspace=\"0\" border=\"0\" alt=\"Note-ify\" style=\"float: left;max-width:550px;display:block;\" class=\"rnb-logo-img\" src=\"http://img.mailinblue.com/2190383/images/rnb/original/5cd4237f27351d65c77e45f2.jpg\"></div></div></td>\n" +
                            "                                                    </tr>\n" +
                            "                                                </tbody></table>\n" +
                            "                                                </td>\n" +
                            "                                        </tr>\n" +
                            "                                    </tbody></table></td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                        </tbody></table>\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </tbody></table>\n" +
                            "            <!--[if mso]>\n" +
                            "                </td>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                </tr>\n" +
                            "                </table>\n" +
                            "                <![endif]-->\n" +
                            "            \n" +
                            "        </div></td>\n" +
                            "    </tr><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <div>\n" +
                            "            \n" +
                            "                <!--[if mso]>\n" +
                            "                <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"width:100%;\">\n" +
                            "                <tr>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <td valign=\"top\" width=\"590\" style=\"width:590px;\">\n" +
                            "                <![endif]-->\n" +
                            "                <table class=\"rnb-del-min-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:100%;\" name=\"Layout_7\">\n" +
                            "                <tbody><tr>\n" +
                            "                    <td class=\"rnb-del-min-width\" align=\"center\" valign=\"top\">\n" +
                            "                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-container\" bgcolor=\"#ffffff\" style=\"background-color: rgb(255, 255, 255); padding-left: 20px; padding-right: 20px; border-collapse: separate; border-radius: 0px; border-bottom: 0px none rgb(200, 200, 200);\">\n" +
                            "\n" +
                            "                                        <tbody><tr>\n" +
                            "                                            <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                        </tr>\n" +
                            "                                        <tr>\n" +
                            "                                            <td valign=\"top\" class=\"rnb-container-padding\" align=\"left\">\n" +
                            "\n" +
                            "                                                <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-columns-container\">\n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td class=\"rnb-force-col\" valign=\"top\" style=\"padding-right: 0px;\">\n" +
                            "\n" +
                            "                                                            <table border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"left\" class=\"rnb-col-1\">\n" +
                            "\n" +
                            "                                                                <tbody><tr>\n" +
                            "                                                                    <td style=\"font-size:14px; font-family:Arial,Helvetica,sans-serif, sans-serif; color:#3c4858; line-height: 21px;\"><div><u><span style=\"font-size:16px;\"><strong>" + mLocationFinal + "</strong></span></u></div>\n" +
                            "\n" +
                            "<div><span style=\"font-size:16px;\">" + description + "</span></div>\n" +
                            "\n" +
                            "<div><br>\n" +
                            "Current Temperature: " + temperature + "</div>\n" +
                            "\n" +
                            "<div>Minimum Temperature: " + minTemp + "</div>\n" +
                            "\n" +
                            "<div>Maximum Temperature: " + maxTemp + "</div>\n" +
                            "\n" +
                            "<div>Humidity: " + humidity + "</div>\n" +
                            "\n" +
                            "<div>Pressure: " + pressure + "</div>\n" +
                            "\n" +
                            "<div>Wind Speed: " + windSpeed + "</div>\n" +
                            "\n" +
                            "<div>Wind Direction: " + windDirection + "</div>\n" +
                            "\n" +
                            "<div>Sunrise Time: " + sunrise + "</div>\n" +
                            "\n" +
                            "<div>Sunset Time: " + sunset + "</div>\n" +
                            "\n" +
                            "<div>" + indexUV + "</div>\n" +
                            "\n" +
                            "<div>&nbsp;</div>\n" +
                            "\n" +
                            "<div><span style=\"font-size:14px;\"><b>5 Day Forecast:</b></span></div>\n" +
                            "\n" +
                            "<div>&nbsp;</div>\n" +
                            "\n" +
                            "<div>" + day1 + " - " + descrDay1 + " - " + tempDay1 + "</div>\n" +
                            "\n" +
                            "<div>" + day2 + " - " + descrDay2 + " - " + tempDay2 + "</div>\n" +
                            "\n" +
                            "<div>" + day3 + " - " + descrDay3 + " - " + tempDay3 + "</div>\n" +
                            "\n" +
                            "<div>" + day4 + " - " + descrDay4 + " - " + tempDay4 + "</div>\n" +
                            "\n" +
                            "<div>" + day5 + " - " + descrDay5 + " - " + tempDay5 + "</div>\n" +
                            "</td>\n" +
                            "                                                                </tr>\n" +
                            "                                                                </tbody></table>\n" +
                            "\n" +
                            "                                                            </td></tr>\n" +
                            "                                                </tbody></table></td>\n" +
                            "                                        </tr>\n" +
                            "                                        <tr>\n" +
                            "                                            <td height=\"20\" style=\"font-size:1px; line-height:0px\">&nbsp;</td>\n" +
                            "                                        </tr>\n" +
                            "                                    </tbody></table>\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </tbody></table><!--[if mso]>\n" +
                            "                </td>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                </tr>\n" +
                            "                </table>\n" +
                            "                <![endif]-->\n" +
                            "\n" +
                            "            </div></td>\n" +
                            "    </tr><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <div>\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"width:100%;\">\n" +
                            "                <tr>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <td valign=\"top\" width=\"590\" style=\"width:590px;\">\n" +
                            "                <![endif]-->\n" +
                            "                <table class=\"rnb-del-min-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:100%;\" name=\"Layout_6\" id=\"Layout_6\">\n" +
                            "                <tbody><tr>\n" +
                            "                    <td class=\"rnb-del-min-width\" align=\"center\" valign=\"top\">\n" +
                            "                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-container\" bgcolor=\"#ffffff\" style=\"max-width: 100%; min-width: 100%; table-layout: fixed; background-color: rgb(255, 255, 255); border-radius: 0px; border-collapse: separate; padding-left: 20px; padding-right: 20px;\">\n" +
                            "                            <tbody><tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td valign=\"top\" class=\"rnb-container-padding\" align=\"left\">\n" +
                            "\n" +
                            "                                    <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-columns-container\">\n" +
                            "                                        <tbody><tr>\n" +
                            "                                            <td class=\"rnb-force-col\" width=\"550\" valign=\"top\" style=\"padding-right: 0px;\">\n" +
                            "                                                <table border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" class=\"rnb-col-1\" width=\"550\">\n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td width=\"100%\" class=\"img-block-center\" valign=\"top\" align=\"center\">\n" +
                            "                                                            <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                                <tbody>\n" +
                            "                                                                    <tr>\n" +
                            "                                                                        <td width=\"100%\" valign=\"top\" align=\"center\" class=\"img-block-center\">\n" +
                            "\n" +
                            "                                                                        <table style=\"display: inline-block;\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                                            <tbody><tr>\n" +
                            "                                                                                <td>\n" +
                            "                                                                        <div style=\"border-top:0px None #000;border-right:0px None #000;border-bottom:0px None #000;border-left:0px None #000;display:inline-block;\">\n" +
                            "                                                                            <div><a target=\"_blank\" href=\"https://play.google.com/store/apps/details?id=com.interstellarstudios.weathershare\">\n" +
                            "                                                                            <img ng-if=\"col.img.source != 'url'\" width=\"200\" border=\"0\" hspace=\"0\" vspace=\"0\" alt=\"\" class=\"rnb-col-1-img\" src=\"http://img.mailinblue.com/2190383/images/rnb/original/5c27674ccf29bcec2a435996.png\" style=\"vertical-align: top; max-width: 200px; float: left;\"></a></div><div style=\"clear:both;\"></div>\n" +
                            "                                                                            </div></td>\n" +
                            "                                                                            </tr>\n" +
                            "                                                                        </tbody></table>\n" +
                            "\n" +
                            "                                                                    </td>\n" +
                            "                                                                    </tr>\n" +
                            "                                                                </tbody>\n" +
                            "                                                                </table></td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td height=\"10\" class=\"col_td_gap\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td style=\"font-size:14px; font-family:Arial,Helvetica,sans-serif, sans-serif; color:#3c4858; line-height: 21px;\">\n" +
                            "                                                            <div><div style=\"text-align: center;\">Download the free App now.</div>\n" +
                            "</div>\n" +
                            "                                                        </td>\n" +
                            "                                                    </tr>\n" +
                            "                                                    </tbody></table>\n" +
                            "\n" +
                            "                                                </td></tr>\n" +
                            "                                    </tbody></table></td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                        </tbody></table>\n" +
                            "\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </tbody></table><!--[if mso]>\n" +
                            "                </td>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                </tr>\n" +
                            "                </table>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "            </div></td>\n" +
                            "    </tr><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <div>\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"width:100%;\">\n" +
                            "                <tr>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <td valign=\"top\" width=\"590\" style=\"width:590px;\">\n" +
                            "                <![endif]-->\n" +
                            "                <table class=\"rnb-del-min-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:100%;\" name=\"Layout_5\" id=\"Layout_5\">\n" +
                            "                <tbody><tr>\n" +
                            "                    <td class=\"rnb-del-min-width\" align=\"center\" valign=\"top\">\n" +
                            "                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-container\" bgcolor=\"#ffffff\" style=\"max-width: 100%; min-width: 100%; table-layout: fixed; background-color: rgb(255, 255, 255); border-radius: 0px; border-collapse: separate; padding-left: 20px; padding-right: 20px;\">\n" +
                            "                            <tbody><tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td valign=\"top\" class=\"rnb-container-padding\" align=\"left\">\n" +
                            "\n" +
                            "                                    <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-columns-container\">\n" +
                            "                                        <tbody><tr>\n" +
                            "                                            <td class=\"rnb-force-col\" width=\"263\" valign=\"top\" style=\"padding-right: 20px;\">\n" +
                            "                                                <table border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" class=\"rnb-col-2\" width=\"263\">\n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td width=\"100%\" class=\"img-block-center\" valign=\"top\" align=\"left\">\n" +
                            "                                                            <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                            <tbody>\n" +
                            "                                                                <tr>\n" +
                            "                                                                    <td width=\"100%\" valign=\"top\" align=\"left\" class=\"img-block-center\">\n" +
                            "                                                                        <table style=\"display: inline-block;\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                                            <tbody><tr>\n" +
                            "                                                                                <td>\n" +
                            "                                                                                    <div style=\"border-top:1px Solid #9c9c9c;border-right:1px Solid #9c9c9c;border-bottom:1px Solid #9c9c9c;border-left:1px Solid #9c9c9c;display:inline-block;\"><div><img border=\"0\" width=\"263\" hspace=\"0\" vspace=\"0\" alt=\"\" class=\"rnb-col-2-img\" src=\"http://img.mailinblue.com/2190383/images/rnb/original/5c4392438696e366516c5d85.jpg\" style=\"vertical-align: top; max-width: 300px; float: left;\"></div><div style=\"clear:both;\"></div>\n" +
                            "                                                                                    </div>\n" +
                            "                                                                            </td>\n" +
                            "                                                                            </tr>\n" +
                            "                                                                        </tbody></table>\n" +
                            "\n" +
                            "                                                                    </td>\n" +
                            "                                                                </tr>\n" +
                            "                                                            </tbody>\n" +
                            "                                                        </table></td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td height=\"10\" class=\"col_td_gap\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td style=\"font-size:14px; font-family:Arial,Helvetica,sans-serif, sans-serif; color:#3c4858; line-height: 21px;\">\n" +
                            "                                                            <div><div>All of your locations automatically synced to the Cloud. Stored securely with Google Firebase.</div>\n" +
                            "</div>\n" +
                            "                                                        </td>\n" +
                            "                                                    </tr>\n" +
                            "                                                    </tbody></table>\n" +
                            "\n" +
                            "                                                </td><td class=\"rnb-force-col\" width=\"263\" valign=\"top\" style=\"padding-right: 0px;\">\n" +
                            "                                                <table border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" class=\"rnb-last-col-2\" width=\"263\">\n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td width=\"100%\" class=\"img-block-center\" valign=\"top\" align=\"left\">\n" +
                            "                                                            <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                            <tbody>\n" +
                            "                                                                <tr>\n" +
                            "                                                                    <td width=\"100%\" valign=\"top\" align=\"left\" class=\"img-block-center\">\n" +
                            "                                                                        <table style=\"display: inline-block;\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                                            <tbody><tr>\n" +
                            "                                                                                <td>\n" +
                            "                                                                                    <div style=\"border-top:1px Solid #9c9c9c;border-right:1px Solid #9c9c9c;border-bottom:1px Solid #9c9c9c;border-left:1px Solid #9c9c9c;display:inline-block;\"><div><img border=\"0\" width=\"263\" hspace=\"0\" vspace=\"0\" alt=\"\" class=\"rnb-col-2-img\" src=\"http://img.mailinblue.com/2190383/images/rnb/original/5c4392438696e3662461432d.jpg\" style=\"vertical-align: top; max-width: 300px; float: left;\"></div><div style=\"clear:both;\"></div>\n" +
                            "                                                                                    </div>\n" +
                            "                                                                            </td>\n" +
                            "                                                                            </tr>\n" +
                            "                                                                        </tbody></table>\n" +
                            "\n" +
                            "                                                                    </td>\n" +
                            "                                                                </tr>\n" +
                            "                                                            </tbody>\n" +
                            "                                                        </table></td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td height=\"10\" class=\"col_td_gap\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td style=\"font-size:14px; font-family:Arial,Helvetica,sans-serif, sans-serif; color:#3c4858; line-height: 21px;\">\n" +
                            "                                                            <div><div>All of your reports on all of your devices. Share reports instantly via email and device-to-device.</div>\n" +
                            "</div>\n" +
                            "                                                        </td>\n" +
                            "                                                    </tr>\n" +
                            "                                                    </tbody></table>\n" +
                            "\n" +
                            "                                                </td></tr>\n" +
                            "                                    </tbody></table></td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                        </tbody></table>\n" +
                            "\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </tbody></table><!--[if mso]>\n" +
                            "                </td>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                </tr>\n" +
                            "                </table>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "            </div></td>\n" +
                            "    </tr><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <div>\n" +
                            "                \n" +
                            "                <!--[if mso 15]>\n" +
                            "                <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"width:100%;\">\n" +
                            "                <tr>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso 15]>\n" +
                            "                <td valign=\"top\" width=\"590\" style=\"width:590px;\">\n" +
                            "                <![endif]-->\n" +
                            "                <table class=\"rnb-del-min-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:100%;\" name=\"Layout_11\" id=\"Layout_11\">\n" +
                            "                <tbody><tr>\n" +
                            "                    <td class=\"rnb-del-min-width\" align=\"center\" valign=\"top\">\n" +
                            "                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-container\" bgcolor=\"#ffffff\" style=\"max-width: 100%; min-width: 100%; table-layout: fixed; background-color: rgb(255, 255, 255); border-radius: 0px; border-collapse: separate; padding: 20px;\">\n" +
                            "                            <tbody><tr>\n" +
                            "                                <td valign=\"top\" class=\"rnb-container-padding\" align=\"left\">\n" +
                            "\n" +
                            "                                    <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-columns-container\">\n" +
                            "                                        <tbody><tr>\n" +
                            "\n" +
                            "                                            <td class=\"rnb-force-col img-block-center\" valign=\"top\" width=\"180\" style=\"padding-right: 20px;\">\n" +
                            "\n" +
                            "                                                <table border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" class=\"rnb-col-2-noborder-onright\" width=\"180\">\n" +
                            "\n" +
                            "\n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td width=\"100%\" style=\"line-height: 0px;\" class=\"img-block-center\" valign=\"top\" align=\"left\">\n" +
                            "                                                            <div style=\"border-top:0px none #000;border-right:0px None #000;border-bottom:0px None #000;border-left:0px None #000;display:inline-block;\"><div><a target=\"_blank\" href=\"https://weathershare.interstellarstudios.co.uk\"><img ng-if=\"col.img.source != 'url'\" alt=\"\" border=\"0\" hspace=\"0\" vspace=\"0\" width=\"180\" style=\"vertical-align:top; float: left; max-width:270px !important; \" class=\"rnb-col-2-img-side-xl\" src=\"http://img.mailinblue.com/2190383/images/rnb/original/5c4b80730d48fbeb3c5c753d.png\"></a></div><div style=\"clear:both;\"></div></div></td>\n" +
                            "                                                    </tr>\n" +
                            "                                                    </tbody></table>\n" +
                            "                                                </td><td class=\"rnb-force-col\" valign=\"top\">\n" +
                            "\n" +
                            "                                                <table border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" width=\"350\" align=\"left\" class=\"rnb-last-col-2\">\n" +
                            "\n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td style=\"font-size:24px; font-family:Arial,Helvetica,sans-serif; color:#3c4858; text-align:left;\">\n" +
                            "                                                            <span style=\"color:#3c4858; \"><strong><span style=\"font-size:18px;\">Website</span></strong></span></td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td height=\"10\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td class=\"rnb-mbl-float-none\" style=\"font-size:14px; font-family:Arial,Helvetica,sans-serif;color:#3c4858;float:right;width:350px; line-height: 21px;\"><div>Need some information? Check out our website:&nbsp;<a href=\"https://weathershare.interstellarstudios.co.uk\" style=\"text-decoration: underline; color: rgb(52, 153, 219);\">https://weathershare.interstellarstudios.co.uk</a><a href=\"https://noteify.interstellarstudios.co.uk/\" style=\"text-decoration: underline; color: rgb(52, 153, 219);\">\u200B</a></div>\n" +
                            "</td>\n" +
                            "                                                    </tr>\n" +
                            "                                                    </tbody></table>\n" +
                            "                                                </td>\n" +
                            "\n" +
                            "                                            </tr></tbody></table></td>\n" +
                            "                            </tr>\n" +
                            "                        </tbody></table>\n" +
                            "\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </tbody></table>\n" +
                            "            <!--[if mso 15]>\n" +
                            "                </td>\n" +
                            "                <![endif]-->\n" +
                            "\n" +
                            "                <!--[if mso 15]>\n" +
                            "                </tr>\n" +
                            "                </table>\n" +
                            "                <![endif]-->\n" +
                            "            \n" +
                            "        </div></td>\n" +
                            "    </tr><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <div>\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" style=\"width:100%;\">\n" +
                            "                <tr>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                <td valign=\"top\" width=\"590\" style=\"width:590px;\">\n" +
                            "                <![endif]-->\n" +
                            "                <table class=\"rnb-del-min-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:100%;\" name=\"Layout_12\" id=\"Layout_12\">\n" +
                            "                <tbody><tr>\n" +
                            "                    <td class=\"rnb-del-min-width\" align=\"center\" valign=\"top\">\n" +
                            "                        <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-container\" bgcolor=\"#ffffff\" style=\"max-width: 100%; min-width: 100%; table-layout: fixed; background-color: rgb(255, 255, 255); border-radius: 0px; border-collapse: separate; padding-left: 20px; padding-right: 20px;\">\n" +
                            "                            <tbody><tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td valign=\"top\" class=\"rnb-container-padding\" align=\"left\">\n" +
                            "\n" +
                            "                                    <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"rnb-columns-container\">\n" +
                            "                                        <tbody><tr>\n" +
                            "                                            <td class=\"rnb-force-col\" width=\"550\" valign=\"top\" style=\"padding-right: 0px;\">\n" +
                            "                                                <table border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" class=\"rnb-col-1\" width=\"550\">\n" +
                            "                                                    <tbody><tr>\n" +
                            "                                                        <td width=\"100%\" class=\"img-block-center\" valign=\"top\" align=\"center\">\n" +
                            "                                                            <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                                <tbody>\n" +
                            "                                                                    <tr>\n" +
                            "                                                                        <td width=\"100%\" valign=\"top\" align=\"center\" class=\"img-block-center\">\n" +
                            "\n" +
                            "                                                                        <table style=\"display: inline-block;\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n" +
                            "                                                                            <tbody><tr>\n" +
                            "                                                                                <td>\n" +
                            "                                                                        <div style=\"border-top:0px None #000;border-right:0px None #000;border-bottom:0px None #000;border-left:0px None #000;display:inline-block;\">\n" +
                            "                                                                            <div><a target=\"_blank\" href=\"https://github.com/craigspicer\">\n" +
                            "                                                                            <img ng-if=\"col.img.source != 'url'\" width=\"200\" border=\"0\" hspace=\"0\" vspace=\"0\" alt=\"\" class=\"rnb-col-1-img\" src=\"http://img.mailinblue.com/2190383/images/rnb/original/5cd3fccc27351d028e2b7a1b.png\" style=\"vertical-align: top; max-width: 200px; float: left;\"></a></div><div style=\"clear:both;\"></div>\n" +
                            "                                                                            </div></td>\n" +
                            "                                                                            </tr>\n" +
                            "                                                                        </tbody></table>\n" +
                            "\n" +
                            "                                                                    </td>\n" +
                            "                                                                    </tr>\n" +
                            "                                                                </tbody>\n" +
                            "                                                                </table></td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td height=\"10\" class=\"col_td_gap\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                                                    </tr><tr>\n" +
                            "                                                        <td style=\"font-size:14px; font-family:Arial,Helvetica,sans-serif, sans-serif; color:#3c4858; line-height: 21px;\">\n" +
                            "                                                            <div><div style=\"text-align: center;\">© 2019 WeatherShare. All Rights Reserved.</div>\n" +
                            "</div>\n" +
                            "                                                        </td>\n" +
                            "                                                    </tr>\n" +
                            "                                                    </tbody></table>\n" +
                            "\n" +
                            "                                                </td></tr>\n" +
                            "                                    </tbody></table></td>\n" +
                            "                            </tr>\n" +
                            "                            <tr>\n" +
                            "                                <td height=\"20\" style=\"font-size:1px; line-height:0px;\">&nbsp;</td>\n" +
                            "                            </tr>\n" +
                            "                        </tbody></table>\n" +
                            "\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </tbody></table><!--[if mso]>\n" +
                            "                </td>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "                <!--[if mso]>\n" +
                            "                </tr>\n" +
                            "                </table>\n" +
                            "                <![endif]-->\n" +
                            "                \n" +
                            "            </div></td>\n" +
                            "    </tr><tr>\n" +
                            "\n" +
                            "        <td align=\"center\" valign=\"top\">\n" +
                            "\n" +
                            "            <table class=\"rnb-del-min-width\" width=\"100%\" cellpadding=\"0\" border=\"0\" cellspacing=\"0\" style=\"min-width:590px;\" name=\"Layout_4701\" id=\"Layout_4701\">\n" +
                            "                <tbody><tr>\n" +
                            "                    <td class=\"rnb-del-min-width\" valign=\"top\" align=\"center\" style=\"min-width:590px;\">\n" +
                            "                        <table width=\"100%\" cellpadding=\"0\" border=\"0\" height=\"38\" cellspacing=\"0\">\n" +
                            "                            <tbody><tr>\n" +
                            "                                <td valign=\"top\" height=\"38\">\n" +
                            "                                    <img width=\"20\" height=\"38\" style=\"display:block; max-height:38px; max-width:20px;\" alt=\"\" src=\"http://img.mailinblue.com/new_images/rnb/rnb_space.gif\">\n" +
                            "                                </td>\n" +
                            "                            </tr>\n" +
                            "                        </tbody></table>\n" +
                            "                    </td>\n" +
                            "                </tr>\n" +
                            "            </tbody></table>\n" +
                            "            </td>\n" +
                            "    </tr></tbody></table>\n" +
                            "            <!--[if gte mso 9]>\n" +
                            "                        </td>\n" +
                            "                        </tr>\n" +
                            "                        </table>\n" +
                            "                        <![endif]-->\n" +
                            "                        </td>\n" +
                            "        </tr>\n" +
                            "        </tbody></table>\n" +
                            "\n" +
                            "</body></html>");
                    try {
                        CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
