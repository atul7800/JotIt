package com.atulgupta.JotIt.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity {

    EditText rUserName, rUserEmail, rUserPass,rUserCnfrmPass;
    Button synchAcc;
    TextView loginAct;
    ProgressBar progressBar;
    FirebaseAuth fireBaseAuth;
    FirebaseFirestore fireBaseFireStore;
    FirebaseUser user;
    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Create NotesKeeper account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rUserName = findViewById(R.id.userName);
        rUserEmail = findViewById(R.id.userEmail);
        rUserPass= findViewById(R.id.password);
        rUserCnfrmPass = findViewById(R.id.passwordConfirm);
        synchAcc = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);

        fireBaseAuth = FirebaseAuth.getInstance();
        fireBaseFireStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        loginAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                //animation between transition of Activities
                overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
            }
        });


        synchAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = rUserName.getText().toString();
                String userEmail = rUserEmail.getText().toString();
                String userPass = rUserPass.getText().toString();
                String userCnfrmPass = rUserCnfrmPass.getText().toString();
                final Pattern VALID_EMAIL_ADDRESS_REGEX =
                        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(userEmail);

                if (!matcher.find())
                {
                    rUserEmail.setError("Invalid email");
                    Toast.makeText(Register.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }

                if (userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty() || userCnfrmPass.isEmpty())
                {
                    rUserEmail.setError("Required");
                    rUserName.setError("Required");
                    rUserPass.setError("Required");
                    rUserCnfrmPass.setError("Required");
                    Toast.makeText(Register.this,"All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!userPass.equals(userCnfrmPass))
                {
                    //Log.d(TAG, "PASSWORD MISMATCH");
                    rUserCnfrmPass.setError("Password mismatch");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(userEmail, userPass);
                fireBaseAuth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Notes Synced", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        //saving the username of the user to the UserProfileChangeRequest object
                        FirebaseUser fBaseUser = fireBaseAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build();

                        user.updateProfile(request);

                        //once the user profile is updated here we are starting the MainActivity in order to refresh teh MainActivity bcz DireBase don't refresh automatically
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        //animation between transition of Activities
                        overridePendingTransition(R.animator.slide_up, R.animator.slide_down);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(Register.this, "Failed to sync, Try again", Toast.LENGTH_SHORT).show();
                    }
                });




                /*
                DocumentReference documentReference = fireBaseFireStore.collection("notes").document(user.getUid()).collection("logindetails").document();

                Map<String, Object> logindetails = new HashMap<>();
                logindetails.put("email", userEmail);
                logindetails.put("password", userPass);

                documentReference.set(logindetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                });


                 */

                Toast.makeText(Register.this, "Condition Passed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void login() {

        Toast.makeText(Register.this, "Login Clicked", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        //animation between transition of Activities
        overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
