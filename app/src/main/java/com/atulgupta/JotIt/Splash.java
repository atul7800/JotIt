package com.atulgupta.JotIt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fauth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (fauth.getCurrentUser() != null)
                {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    //animation between transition of Activities
                    overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                    finish();
                } else{
                    fauth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Splash.this, "Signed in Anonymously", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            //animation between transition of Activities
                            overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Splash.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }

            }
        },2000);
    }
}
