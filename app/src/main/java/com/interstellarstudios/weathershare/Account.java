package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String userFirstName = sharedPreferences.getString("userFirstName", "");
        String userLastName = sharedPreferences.getString("userLastName", "");
        String userFullName = userFirstName + " " + userLastName;

        TextView userFullNameTextView = findViewById(R.id.user_full_name);
        userFullNameTextView.setText(userFullName);

        ImageView websiteLink = findViewById(R.id.website_link);
        websiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://interstellarstudios.co.uk/"));
                startActivity(browserIntent);
            }
        });

        Button termsOfService = findViewById(R.id.terms_of_service);
        termsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Account.this, TermsOfService.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        Button privacyPolicy = findViewById(R.id.privacy_policy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Account.this, PrivacyPolicy.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        Button editAccountDetails = findViewById(R.id.edit_account_button);
        editAccountDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Account.this, EditAccountDetails.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
