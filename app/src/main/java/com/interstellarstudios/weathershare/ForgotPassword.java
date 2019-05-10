package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailAddress;
    private FirebaseAuth mFireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mFireBaseAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.email_address);

        ImageView sendEmailLink = findViewById(R.id.send_link_button);
        sendEmailLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLink();
            }
        });

        TextView signIn = findViewById(R.id.go_to_sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgotPassword.this, SignIn.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
    }

    private void sendLink(){

        String email = emailAddress.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(ForgotPassword.this, "Please enter your registered email address", Toast.LENGTH_LONG).show();
            return;
        }

        mFireBaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Password reset email sent", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), SignIn.class));

                            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                        } else {
                            Toast.makeText(ForgotPassword.this, "Error sending password reset email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
