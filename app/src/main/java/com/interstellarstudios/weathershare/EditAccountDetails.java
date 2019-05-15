package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;

public class EditAccountDetails extends AppCompatActivity {

    private EditText mFirstNameText;
    private EditText mLastNameText;
    private static final String textuUerFirstName = "userFirstName";
    private static final String textUserLastName = "userLastName";
    private String mUserFirstName;
    private String mUserLastName;
    private FirebaseAnalytics mFireBaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_details);

        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        final Bundle analyticsBundle = new Bundle();

        mFirstNameText = findViewById(R.id.editFirstName);
        mLastNameText = findViewById(R.id.editLastName);

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
                Intent i = new Intent(EditAccountDetails.this, Account.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                Toast.makeText(EditAccountDetails.this, "Details Saved", Toast.LENGTH_LONG).show();
                mFireBaseAnalytics.logEvent("name_saved", analyticsBundle);
            }
        });
        loadData();
        updateViews();
    }

    public void savePreferences() {

        SharedPreferences myPrefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();

        String userFirstName = mFirstNameText.getText().toString().trim();
        prefsEditor.putString("userFirstName", userFirstName);
        String userLastName = mLastNameText.getText().toString().trim();
        prefsEditor.putString("userLastName", userLastName);

        prefsEditor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        mUserFirstName = sharedPreferences.getString(textuUerFirstName, "");
        mUserLastName = sharedPreferences.getString(textUserLastName, "");

    }

    public void updateViews() {
        mFirstNameText.setText(mUserFirstName);
        mLastNameText.setText(mUserLastName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
