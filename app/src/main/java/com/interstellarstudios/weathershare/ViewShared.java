package com.interstellarstudios.weathershare;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
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

public class ViewShared extends AppCompatActivity {

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
    private Boolean mSwitchOnOff;
    private String mLocation;
    private String mCurrentUserId;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shared);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        mSwitchOnOff = sharedPreferences.getBoolean("switchUnits", false);

        FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFireBaseFireStore = FirebaseFirestore.getInstance();

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

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

        mProgressDialog.setMessage("Refreshing");
        mProgressDialog.show();

        DocumentReference receivedLocationPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Public").document("Shared");
        receivedLocationPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        SharedWeatherModel sharedWeatherModel = document.toObject(SharedWeatherModel.class);
                        mLocation = sharedWeatherModel.getLocation();

                        findWeather(mLocation);
                        findForecast(mLocation);
                    }
                }
            }
        });
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
                    } else if (weatherIdInt == 800) {
                        layout.setBackgroundResource(R.mipmap.clear);
                    } else if (weatherIdInt >= 801 && weatherIdInt <= 803) {
                        layout.setBackgroundResource(R.mipmap.clouds);
                    } else if (weatherIdInt == 804) {
                        layout.setBackgroundResource(R.mipmap.overcast);
                    }

                    //final strings
                    String locationFinal = (city + ", " + country);
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
                    mLocationText.setText(locationFinal);
                    mDescriptionText.setText(description);
                    mHumidityText.setText(humidityFinal);
                    mPressureText.setText(PressureFinal);
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(ViewShared.this);
        queue.add(jor);
    }

    private void findForecast(String city) {

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
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMMM yyyy");

                    //list array
                    JSONArray listArray = response.getJSONArray("list");

                    //index 0 of the list array
                    JSONObject arrayObject0 = listArray.getJSONObject(11);

                    //date time
                    String dateTime0 = arrayObject0.getString("dt");

                    //main object inside list array
                    JSONObject mainObject0 = arrayObject0.getJSONObject("main");
                    String tempForecast0 = String.valueOf(mainObject0.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray0 = arrayObject0.getJSONArray("weather");
                    JSONObject weatherObject0 = weatherArray0.getJSONObject(0);
                    String description0 = weatherObject0.getString("description");

                    //converting dt
                    int dateTimeInt0 = Integer.parseInt(dateTime0);
                    long dateTimeUnix0 = dateTimeInt0;
                    Date date0 = new java.util.Date(dateTimeUnix0 * 1000L);
                    String dateTimeFinal0 = (sdf.format(date0));

                    //final strings
                    String temperatureForecastMetric0 = (tempForecast0 + "°C");
                    String temperatureForecastImperial0 = (tempForecast0 + "°F");

                    //index 1 of the list array
                    JSONObject arrayObject1 = listArray.getJSONObject(18);

                    //date time
                    String dateTime1 = arrayObject1.getString("dt");

                    //main object inside list array
                    JSONObject mainObject1 = arrayObject1.getJSONObject("main");
                    String tempForecast1 = String.valueOf(mainObject1.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray1 = arrayObject1.getJSONArray("weather");
                    JSONObject weatherObject1 = weatherArray1.getJSONObject(0);
                    String description1 = weatherObject1.getString("description");

                    //converting dt
                    int dateTimeInt1 = Integer.parseInt(dateTime1);
                    long dateTimeUnix1 = dateTimeInt1;
                    Date date1 = new java.util.Date(dateTimeUnix1 * 1000L);
                    String dateTimeFinal1 = (sdf.format(date1));

                    //final strings
                    String temperatureForecastMetric1 = (tempForecast1 + "°C");
                    String temperatureForecastImperial1 = (tempForecast1 + "°F");
                    //index 2 of the list array
                    JSONObject arrayObject2 = listArray.getJSONObject(25);

                    //date time
                    String dateTime2 = arrayObject2.getString("dt");

                    //main object inside list array
                    JSONObject mainObject2 = arrayObject2.getJSONObject("main");
                    String tempForecast2 = String.valueOf(mainObject2.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray2 = arrayObject2.getJSONArray("weather");
                    JSONObject weatherObject2 = weatherArray2.getJSONObject(0);
                    String description2 = weatherObject2.getString("description");

                    //converting dt
                    int dateTimeInt2 = Integer.parseInt(dateTime2);
                    long dateTimeUnix2 = dateTimeInt2;
                    Date date2 = new java.util.Date(dateTimeUnix2 * 1000L);
                    String dateTimeFinal2 = (sdf.format(date2));

                    //final strings
                    String temperatureForecastMetric2 = (tempForecast2 + "°C");
                    String temperatureForecastImperial2 = (tempForecast2 + "°F");

                    //index 3 of the list array
                    JSONObject arrayObject3 = listArray.getJSONObject(32);

                    //date time
                    String dateTime3 = arrayObject3.getString("dt");

                    //main object inside list array
                    JSONObject mainObject3 = arrayObject3.getJSONObject("main");
                    String tempForecast3 = String.valueOf(mainObject3.getInt("temp"));

                    //weather object inside list array
                    JSONArray weatherArray3 = arrayObject3.getJSONArray("weather");
                    JSONObject weatherObject3 = weatherArray3.getJSONObject(0);
                    String description3 = weatherObject3.getString("description");

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

                    //converting dt
                    int dateTimeInt4 = Integer.parseInt(dateTime4);
                    long dateTimeUnix4 = dateTimeInt4;
                    Date date4 = new java.util.Date(dateTimeUnix4 * 1000L);
                    String dateTimeFinal4 = (sdf.format(date4));

                    //final strings
                    String temperatureForecastMetric4 = (tempForecast4 + "°C");
                    String temperatureForecastImperial4 = (tempForecast4 + "°F");

                    mDay1Text.setText(dateTimeFinal0);
                    mDay2Text.setText(dateTimeFinal1);
                    mDay3Text.setText(dateTimeFinal2);
                    mDay4Text.setText(dateTimeFinal3);
                    mDay5Text.setText(dateTimeFinal4);
                    mDescription1Text.setText(description0);
                    mDescription2Text.setText(description1);
                    mDescription3Text.setText(description2);
                    mDescription4Text.setText(description3);
                    mDescription5Text.setText(description4);

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
        RequestQueue queue = Volley.newRequestQueue(ViewShared.this);
        queue.add(jor);
    }
}
