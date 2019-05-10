package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Settings extends AppCompatActivity {

    private TextView mHomeLocation;
    private TextView mFavouriteLocation1;
    private TextView mFavouriteLocation2;
    private TextView mFavouriteLocation3;
    private TextView mFavouriteLocation4;
    private TextView mFavouriteLocation5;
    private TextView mFavouriteLocation6;
    private TextView mFavouriteLocation7;
    private TextView mFavouriteLocation8;
    private TextView mFavouriteLocation9;
    private TextView mFavouriteLocation10;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFireBaseFireStore;
    private String mCurrentUserId;
    private ToggleButton buttonSwitchUnits;
    private static final String switchUnits = "switchUnits";
    private boolean switchOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseFireStore = FirebaseFirestore.getInstance();

        mHomeLocation = findViewById(R.id.home_location);
        mFavouriteLocation1 = findViewById(R.id.favourite_location_1);
        mFavouriteLocation2 = findViewById(R.id.favourite_location_2);
        mFavouriteLocation3 = findViewById(R.id.favourite_location_3);
        mFavouriteLocation4 = findViewById(R.id.favourite_location_4);
        mFavouriteLocation5 = findViewById(R.id.favourite_location_5);
        mFavouriteLocation6 = findViewById(R.id.favourite_location_6);
        mFavouriteLocation7 = findViewById(R.id.favourite_location_7);
        mFavouriteLocation8 = findViewById(R.id.favourite_location_8);
        mFavouriteLocation9 = findViewById(R.id.favourite_location_9);
        mFavouriteLocation10 = findViewById(R.id.favourite_location_10);


        ImageView saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        buttonSwitchUnits = findViewById(R.id.unitsSelector);
        buttonSwitchUnits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    savePreferences();
                } else {
                    savePreferences();
                }
            }
        });

        loadData();
        updateViews();
    }

    public void savePreferences() {
        SharedPreferences myPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean("switchUnits", buttonSwitchUnits.isChecked());
        prefsEditor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(switchUnits, false);
    }

    public void updateViews() {
        buttonSwitchUnits.setChecked(switchOnOff);
    }

    private void saveSettings() {

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

        String homeLocation = mHomeLocation.getText().toString().trim();
        String favouriteLocation1 = mFavouriteLocation1.getText().toString().trim();
        String favouriteLocation2 = mFavouriteLocation2.getText().toString().trim();
        String favouriteLocation3 = mFavouriteLocation3.getText().toString().trim();
        String favouriteLocation4 = mFavouriteLocation4.getText().toString().trim();
        String favouriteLocation5 = mFavouriteLocation5.getText().toString().trim();
        String favouriteLocation6 = mFavouriteLocation6.getText().toString().trim();
        String favouriteLocation7 = mFavouriteLocation7.getText().toString().trim();
        String favouriteLocation8 = mFavouriteLocation8.getText().toString().trim();
        String favouriteLocation9 = mFavouriteLocation9.getText().toString().trim();
        String favouriteLocation10 = mFavouriteLocation10.getText().toString().trim();

        if (!TextUtils.isEmpty(homeLocation)) {
            DocumentReference homeLocationPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Home_Location");
            homeLocationPath.set(new HomeLocationModel(homeLocation));
        }

        DocumentReference favouriteLocationPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Favourite_Locations");
        favouriteLocationPath.set(new FavouriteLocationModel(favouriteLocation1, favouriteLocation2, favouriteLocation3, favouriteLocation4, favouriteLocation5, favouriteLocation6, favouriteLocation7, favouriteLocation8, favouriteLocation9, favouriteLocation10));

        Intent i = new Intent(Settings.this, Home.class);
        startActivity(i);

        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
