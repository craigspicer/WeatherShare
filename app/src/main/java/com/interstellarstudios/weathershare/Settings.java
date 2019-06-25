package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Settings extends AppCompatActivity {

    private AutoCompleteTextView mHomeLocationTextView;
    private AutoCompleteTextView mFavouriteLocation1TextView;
    private AutoCompleteTextView mFavouriteLocation2TextView;
    private AutoCompleteTextView mFavouriteLocation3TextView;
    private AutoCompleteTextView mFavouriteLocation4TextView;
    private AutoCompleteTextView mFavouriteLocation5TextView;
    private ToggleButton buttonSwitchUnits;
    private static final String switchUnits = "switchUnits";
    private static final String textHomeLocation = "homeLocation";
    private static final String textFavouriteLocation1 = "favouriteLocation1";
    private static final String textFavouriteLocation2 = "favouriteLocation2";
    private static final String textFavouriteLocation3 = "favouriteLocation3";
    private static final String textFavouriteLocation4 = "favouriteLocation4";
    private static final String textFavouriteLocation5 = "favouriteLocation5";
    private boolean switchOnOff;
    private String mHomeLocation;
    private String mFavouriteLocation1;
    private String mFavouriteLocation2;
    private String mFavouriteLocation3;
    private String mFavouriteLocation4;
    private String mFavouriteLocation5;
    private FirebaseAnalytics mFireBaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, SearchSuggestions.getSearchSuggestions(this));

        mHomeLocationTextView = findViewById(R.id.home_location);
        mHomeLocationTextView.setAdapter(adapter);
        mFavouriteLocation1TextView = findViewById(R.id.favourite_location_1);
        mFavouriteLocation1TextView.setAdapter(adapter);
        mFavouriteLocation2TextView = findViewById(R.id.favourite_location_2);
        mFavouriteLocation2TextView.setAdapter(adapter);
        mFavouriteLocation3TextView = findViewById(R.id.favourite_location_3);
        mFavouriteLocation3TextView.setAdapter(adapter);
        mFavouriteLocation4TextView = findViewById(R.id.favourite_location_4);
        mFavouriteLocation4TextView.setAdapter(adapter);
        mFavouriteLocation5TextView = findViewById(R.id.favourite_location_5);
        mFavouriteLocation5TextView.setAdapter(adapter);
        buttonSwitchUnits = findViewById(R.id.unitsSelector);

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        final Bundle analyticsBundle = new Bundle();

        ImageView saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();

                Intent i = new Intent(Settings.this, Home.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                Toast.makeText(Settings.this, "Settings Saved", Toast.LENGTH_LONG).show();
                mFireBaseAnalytics.logEvent("settings_saved", analyticsBundle);
            }
        });
        loadData();
        updateViews();
    }

    public void savePreferences() {

        SharedPreferences myPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();

        prefsEditor.putBoolean("switchUnits", buttonSwitchUnits.isChecked());

        String homeLocation = mHomeLocationTextView.getText().toString().trim();
        prefsEditor.putString("homeLocation", homeLocation);
        String favouriteLocation1 = mFavouriteLocation1TextView.getText().toString().trim();
        prefsEditor.putString("favouriteLocation1", favouriteLocation1);
        String favouriteLocation2 = mFavouriteLocation2TextView.getText().toString().trim();
        prefsEditor.putString("favouriteLocation2", favouriteLocation2);
        String favouriteLocation3 = mFavouriteLocation3TextView.getText().toString().trim();
        prefsEditor.putString("favouriteLocation3", favouriteLocation3);
        String favouriteLocation4 = mFavouriteLocation4TextView.getText().toString().trim();
        prefsEditor.putString("favouriteLocation4", favouriteLocation4);
        String favouriteLocation5 = mFavouriteLocation5TextView.getText().toString().trim();
        prefsEditor.putString("favouriteLocation5", favouriteLocation5);

        prefsEditor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(switchUnits, false);
        mHomeLocation = sharedPreferences.getString(textHomeLocation, "");
        mFavouriteLocation1 = sharedPreferences.getString(textFavouriteLocation1, "");
        mFavouriteLocation2 = sharedPreferences.getString(textFavouriteLocation2, "");
        mFavouriteLocation3 = sharedPreferences.getString(textFavouriteLocation3, "");
        mFavouriteLocation4 = sharedPreferences.getString(textFavouriteLocation4, "");
        mFavouriteLocation5 = sharedPreferences.getString(textFavouriteLocation5, "");
    }

    public void updateViews() {
        buttonSwitchUnits.setChecked(switchOnOff);
        mHomeLocationTextView.setText(mHomeLocation);
        mFavouriteLocation1TextView.setText(mFavouriteLocation1);
        mFavouriteLocation2TextView.setText(mFavouriteLocation2);
        mFavouriteLocation3TextView.setText(mFavouriteLocation3);
        mFavouriteLocation4TextView.setText(mFavouriteLocation4);
        mFavouriteLocation5TextView.setText(mFavouriteLocation5);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isDestroyed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isDestroyed();
    }
}
