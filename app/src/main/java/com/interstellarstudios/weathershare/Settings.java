package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Settings extends AppCompatActivity {

    private EditText mHomeLocationText;
    private EditText mFavouriteLocation1;
    private EditText mFavouriteLocation2;
    private EditText mFavouriteLocation3;
    private EditText mFavouriteLocation4;
    private EditText mFavouriteLocation5;
    private FirebaseFirestore mFireBaseFireStore;
    private String mCurrentUserId;
    private ToggleButton buttonSwitchUnits;
    private static final String switchUnits = "switchUnits";
    private boolean switchOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseFireStore = FirebaseFirestore.getInstance();

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

        mHomeLocationText = findViewById(R.id.home_location);
        mFavouriteLocation1 = findViewById(R.id.favourite_location_1);
        mFavouriteLocation2 = findViewById(R.id.favourite_location_2);
        mFavouriteLocation3 = findViewById(R.id.favourite_location_3);
        mFavouriteLocation4 = findViewById(R.id.favourite_location_4);
        mFavouriteLocation5 = findViewById(R.id.favourite_location_5);


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

        DocumentReference homeLocationRef = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Home_Location");
        homeLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        HomeLocationModel homeLocationModel = document.toObject(HomeLocationModel.class);
                        String location = homeLocationModel.getHomeLocation();

                        mHomeLocationText.setHint(location);
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
                        String favouriteLocation1 = favouriteLocationModel.getFavouriteLocation1();
                        String favouriteLocation2 = favouriteLocationModel.getFavouriteLocation2();
                        String favouriteLocation3 = favouriteLocationModel.getFavouriteLocation3();
                        String favouriteLocation4 = favouriteLocationModel.getFavouriteLocation4();
                        String favouriteLocation5 = favouriteLocationModel.getFavouriteLocation5();

                        mFavouriteLocation1.setText(favouriteLocation1);
                        mFavouriteLocation2.setText(favouriteLocation2);
                        mFavouriteLocation3.setText(favouriteLocation3);
                        mFavouriteLocation4.setText(favouriteLocation4);
                        mFavouriteLocation5.setText(favouriteLocation5);
                    }
                }
            }
        });
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

        String homeLocation = mHomeLocationText.getText().toString().trim();
        String favouriteLocation1 = mFavouriteLocation1.getText().toString().trim();
        String favouriteLocation2 = mFavouriteLocation2.getText().toString().trim();
        String favouriteLocation3 = mFavouriteLocation3.getText().toString().trim();
        String favouriteLocation4 = mFavouriteLocation4.getText().toString().trim();
        String favouriteLocation5 = mFavouriteLocation5.getText().toString().trim();

        if (!TextUtils.isEmpty(homeLocation)) {
            DocumentReference homeLocationPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Home_Location");
            homeLocationPath.set(new HomeLocationModel(homeLocation));
        }

        DocumentReference favouriteLocationPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Favourite_Locations");
        favouriteLocationPath.set(new FavouriteLocationModel(favouriteLocation1, favouriteLocation2, favouriteLocation3, favouriteLocation4, favouriteLocation5));

        Intent i = new Intent(Settings.this, Home.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Settings.this, Home.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
