package com.interstellarstudios.weathershare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteConfirm extends AppCompatActivity {

    private FirebaseAuth mFireBaseAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_confirm);

        mFireBaseAuth = FirebaseAuth.getInstance();

        Button confirm_delete = findViewById(R.id.confirm_delete);
        confirm_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFireBaseAuth.getCurrentUser() != null) {
                    mUser = mFireBaseAuth.getCurrentUser();
                }

                mUser.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Intent i = new Intent(DeleteConfirm.this, Welcome.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    DeleteConfirm.this.finish();

                                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                                    Toast.makeText(DeleteConfirm.this, "Your Account has been Deleted", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
