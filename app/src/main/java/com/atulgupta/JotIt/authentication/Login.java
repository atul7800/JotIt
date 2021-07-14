package com.atulgupta.JotIt.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atulgupta.JotIt.MainActivity;
import com.atulgupta.JotIt.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    EditText email, loginpassword;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button loginBtn;
    TextView forgotPass, createAcc;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login to Noteskeeper");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        loginpassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);


        forgotPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);

        spinner = findViewById(R.id.progressBar3);


        showWarning();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString();
                String mLoginPassword = loginpassword.getText().toString();
                final Pattern VALID_EMAIL_ADDRESS_REGEX =
                        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mEmail);

                if (!matcher.find())
                {
                    email.setError("Invalid email");
                    Toast.makeText(Login.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }

                if (mEmail.isEmpty())
                {
                    email.setError("Required");
                    Toast.makeText(Login.this, "All fields required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mLoginPassword.isEmpty())
                {
                    email.setError("Required");
                    Toast.makeText(Login.this, "All fields required", Toast.LENGTH_SHORT).show();
                }


                //delete notes first
                spinner.setVisibility(View.VISIBLE);
                if (fAuth.getCurrentUser().isAnonymous())
                {
                    FirebaseUser user = fAuth.getCurrentUser();

                    fStore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                           Toast.makeText(Login.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Failed to delete the Anonymous Notes! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    //delete the anonymous user after deleting the anonymous notes
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "Anonymous user deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Failed to delete the anonymous user! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }


                fAuth.signInWithEmailAndPassword(mEmail,mLoginPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //animation between transition of Activities
                        overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Login Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }

    private void showWarning() {
            AlertDialog.Builder warning = new AlertDialog.Builder(this)
                    .setTitle("Are you sure ?")
                    .setMessage("Your are a temporary user, so logging in with a existing account will delete all your data.To save your data please create a new account.")
                    .setPositiveButton("Create new account", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getApplicationContext(), Register.class));
                            //animation between transition of Activities
                            overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                            finish();
                        }
                    }).setNegativeButton("It's OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });

            warning.show();

    }
}
