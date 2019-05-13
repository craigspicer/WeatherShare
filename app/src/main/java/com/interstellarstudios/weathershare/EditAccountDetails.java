package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditAccountDetails extends AppCompatActivity {

    private FirebaseFirestore mFireBaseFireStore;
    private String mCurrentUserId;
    private EditText mFirstNameText;
    private EditText mLastNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_details);

        FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseFireStore = FirebaseFirestore.getInstance();

        if (mFireBaseAuth.getCurrentUser() != null) {
            mCurrentUserId = mFireBaseAuth.getCurrentUser().getUid();
        }

        mFirstNameText = findViewById(R.id.editFirstName);
        mLastNameText = findViewById(R.id.editLastName);

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });

        Button buttonDeleteAccount = findViewById(R.id.confirm_delete);
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditAccountDetails.this, DeleteAccount.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void saveDetails() {

        String userFirstName = mFirstNameText.getText().toString().trim();
        String userLastName = mLastNameText.getText().toString().trim();

        if (!TextUtils.isEmpty(userFirstName) && !TextUtils.isEmpty(userLastName)){
            DocumentReference userDetailsPath = mFireBaseFireStore.collection("Users").document(mCurrentUserId).collection("User_Details").document("This_User");
            userDetailsPath.set(new UserNamesModel(userFirstName, userLastName));

            Intent i = new Intent(EditAccountDetails.this, Account.class);
            startActivity(i);
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

            Toast.makeText(EditAccountDetails.this, "Details Saved", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditAccountDetails.this, "Please enter your first and last names", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
