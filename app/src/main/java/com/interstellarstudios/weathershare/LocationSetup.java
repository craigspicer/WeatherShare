package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationSetup extends AppCompatActivity {

    private EditText mHomeLocation;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFireBaseFireStore;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setup);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseFireStore = FirebaseFirestore.getInstance();

        mHomeLocation = findViewById(R.id.home_location);

        ImageView submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeHomeLocation();

                Intent i = new Intent(LocationSetup.this, Home.class);
                startActivity(i);
            }
        });
    }

    private void writeHomeLocation() {

        String homeLocation = mHomeLocation.getText().toString();

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

        DocumentReference homeLocationPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("Main").document("Home_Location");
        homeLocationPath.set(new HomeLocationModel(homeLocation));
    }
}
