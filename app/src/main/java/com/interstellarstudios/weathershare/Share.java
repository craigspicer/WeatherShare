package com.interstellarstudios.weathershare;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.SmtpApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

public class Share extends AppCompatActivity {

    private String mLocation;
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
    private FirebaseFirestore mFireBaseFireStore;
    private FirebaseAnalytics mFireBaseAnalytics;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int CONTACT_PICKER_RESULT = 1;
    private EditText mSharedUserEmailText;
    private String mSharedUserId;
    private String mCurrentUserEmail;
    private Boolean mSwitchOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Bundle bundle = getIntent().getExtras();
        mLocation = bundle.getString("location");

        //Loading shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        mSwitchOnOff = sharedPreferences.getBoolean("switchUnits", false);

        FirebaseAuth fireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseFireStore = FirebaseFirestore.getInstance();
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (fireBaseAuth.getCurrentUser() != null) {
            FirebaseUser firebaseUser = fireBaseAuth.getCurrentUser();
            mCurrentUserEmail = firebaseUser.getEmail();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedUserEmailText = findViewById(R.id.shared_user_email);
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

        TextView toolbarContacts = toolbar.findViewById(R.id.toolbar_contacts);
        toolbarContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Share.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    getPermissionToReadUserContacts();
                } else {
                    doLaunchContactPicker();
                }
            }
        });

        TextView toolbarShare = toolbar.findViewById(R.id.toolbar_share);
        toolbarShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWeather();
                sendMail();
            }
        });

        findWeather(mLocation);
    }

    public void getPermissionToReadUserContacts() {

        new AlertDialog.Builder(this)
                .setTitle("Permission needed to access Contacts")
                .setMessage("This permission is needed in order to get an email address for a selected contact. Manually enable in Settings > Apps & notifications > WeatherShare > Permissions")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                READ_CONTACTS_PERMISSIONS_REQUEST);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Share.this, "Read Contacts permission granted.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Share.this, "Read Contacts permission denied.", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void doLaunchContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICKER_RESULT && resultCode == RESULT_OK) {

            String email = "";

            Uri result = data.getData();
            String id = result.getLastPathSegment();

            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{id}, null);

            if (cursor.moveToFirst()) {
                email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
            if (cursor != null) {
                cursor.close();
            }
            if (email.length() == 0) {
                Toast.makeText(Share.this, "No email address stored for this contact.", Toast.LENGTH_LONG).show();
            } else {
                mSharedUserEmailText.setText(email);
            }
        }
    }

    private void shareWeather() {

        final Bundle analyticsBundle = new Bundle();

        final String sharedUserEmail = mSharedUserEmailText.getText().toString().trim();

        if (sharedUserEmail.trim().isEmpty()) {
            return;
        }

        DocumentReference userDetailsRef = mFireBaseFireStore.collection("User_List").document(sharedUserEmail);
        userDetailsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        UserIdModel userDetails = document.toObject(UserIdModel.class);
                        mSharedUserId = userDetails.getUserId();

                        //NOTIFICATIONS
                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("from", mCurrentUserEmail);
                        CollectionReference notificationPath = mFireBaseFireStore.collection("Users").document(mSharedUserId).collection("Public").document("Notifications").collection("Notifications");
                        notificationPath.add(notificationMessage);

                        DocumentReference sharedWeatherPath = mFireBaseFireStore.collection("Users").document(mSharedUserId).collection("Public").document("Shared");
                        sharedWeatherPath.set(new SharedWeatherModel(mLocation));

                        Toast.makeText(Share.this, "Weather report shared with and emailed to: " + sharedUserEmail, Toast.LENGTH_LONG).show();

                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                        mFireBaseAnalytics.logEvent("report_shared_and_emailed", analyticsBundle);
                    } else {
                        Toast.makeText(Share.this, "Weather report emailed to: " + sharedUserEmail, Toast.LENGTH_LONG).show();
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                        mFireBaseAnalytics.logEvent("report_share_email_only_called", analyticsBundle);
                    }
                } else {
                    Toast.makeText(Share.this, "Please ensure that there is an active network connection to share", Toast.LENGTH_LONG).show();
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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

                    //last update object
                    String lastUpdate = response.getString("dt");

                    //unix time conversion
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss (z)");
                    int lastUpdateInt = Integer.parseInt(lastUpdate);
                    long lastUpdateUnix = lastUpdateInt;
                    Date date = new java.util.Date(lastUpdateUnix*1000L);
                    String lastUpdateFinal = ("Last Updated: " + sdf.format(date));
                    int sunriseInt = Integer.parseInt(sunrise);
                    long sunriseUnix = sunriseInt;
                    Date date2 = new java.util.Date(sunriseUnix*1000L);
                    String sunriseFinal = (sdf.format(date2));
                    int sunsetInt = Integer.parseInt(sunset);
                    long sunsetUnix = sunsetInt;
                    Date date3 = new java.util.Date(sunsetUnix*1000L);
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
                    mLocationText.setText(mLocation);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(Share.this);
        queue.add(jor);
    }

    private void sendMail() {

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
                    String lastUpdated = mLastUpdatedText.getText().toString();
                    String sunrise = mSunriseText.getText().toString();
                    String sunset = mSunsetText.getText().toString();

                    ApiClient defaultClient = Configuration.getDefaultApiClient();

                    ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
                    apiKey.setApiKey("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");

                    String sharedUserEmail = mSharedUserEmailText.getText().toString().trim();

                    SmtpApi apiInstance = new SmtpApi();

                    List<SendSmtpEmailTo> emailArrayList = new ArrayList<SendSmtpEmailTo>();
                    emailArrayList.add(new SendSmtpEmailTo().email(sharedUserEmail));

                    SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
                    sendSmtpEmail.sender(new SendSmtpEmailSender().email("weathershare@interstellarstudios.co.uk").name("WeatherShare"));
                    sendSmtpEmail.to(emailArrayList);
                    sendSmtpEmail.subject("You've received a weather report from " + mCurrentUserEmail);
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
                            "                                                                    <td style=\"font-size:14px; font-family:Arial,Helvetica,sans-serif, sans-serif; color:#3c4858; line-height: 21px;\"><div><span style=\"font-size:16px;\"><strong>" + mLocation + "</strong></span></div>\n" +
                            "\n" +
                            "<div><span style=\"font-size:16px;\"><strong>Description</strong></span></div>\n" +
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
                            "<div>&nbsp;</div>\n" +
                            "\n" +
                            "<div><em>" + lastUpdated + "</em></div>\n" +
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}