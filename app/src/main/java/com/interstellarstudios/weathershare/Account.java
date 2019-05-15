package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Account extends AppCompatActivity {

    private FirebaseAuth mFireBaseAuth;
    private FirebaseFirestore mFireBaseFireStore;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseFireStore = FirebaseFirestore.getInstance();

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

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

        Button logOut = findViewById(R.id.log_out_button);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> userToken = new HashMap<>();
                userToken.put("User_Token_ID", "");

                DocumentReference userTokenDocumentPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("User_Details").document("User_Token");
                userTokenDocumentPath.set(userToken);

                mFireBaseAuth.signOut();

                Intent i = new Intent(Account.this, Welcome.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                Account.this.finish();

                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                Toast.makeText(Account.this, "You have been signed out", Toast.LENGTH_LONG).show();
            }
        });

        if (mFireBaseAuth.getCurrentUser() != null) {
            TextView userEmail = findViewById(R.id.user_email_address);
            FirebaseUser mUser = mFireBaseAuth.getCurrentUser();
            userEmail.setText(mUser.getEmail());
        }

        DocumentReference signUpDetailsPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("User_Details").document("Sign_Up_Details");
        signUpDetailsPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        UserDetailsModel userDetailsModel = document.toObject(UserDetailsModel.class);
                        String signUpDate = userDetailsModel.getSignUpDate();

                        TextView signUp = findViewById(R.id.member_since);
                        String setSignUp = "Member Since: " + signUpDate;
                        signUp.setText(setSignUp);
                    }
                }
            }
        });

        DocumentReference userDetailsPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("User_Details").document("This_User");
        userDetailsPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        UserNamesModel userNamesModel = document.toObject(UserNamesModel.class);
                        String firstName = userNamesModel.getFirstName();
                        String lastName = userNamesModel.getLastName();

                        TextView userFullName = findViewById(R.id.user_full_name);
                        String fullName = firstName + " " + lastName;
                        userFullName.setText(fullName);
                    }
                }
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
