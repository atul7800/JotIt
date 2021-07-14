package com.atulgupta.JotIt.note;

import android.os.Bundle;

import com.atulgupta.JotIt.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNotes extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    EditText noteTitle, noteContent;
    ProgressBar progressBar;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        noteTitle = findViewById(R.id.addNoteTitle);
        noteContent = findViewById(R.id.addNoteContent);
        progressBar = findViewById(R.id.progressBar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ntitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();

                if (ntitle.isEmpty() || nContent.isEmpty()) {
                    Toast.makeText(AddNotes.this, "Cant save please fill all required fields", Toast.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //save the note to firebase
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document();

                Map<String, Object> note = new HashMap<>();
                note.put("title", ntitle);
                note.put("content", nContent);

                documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddNotes.this, "Note Added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNotes.this, "Failed, Note not added to firebase", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.close_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close)
        {
            Toast.makeText(this, "Not saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}


