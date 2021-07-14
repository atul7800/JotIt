package com.atulgupta.JotIt.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.atulgupta.JotIt.MainActivity;
import com.atulgupta.JotIt.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {

    Intent data;
    EditText editNoteTitle, editNoteContent;
    ProgressBar progressBar1;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = firebaseFirestore.getInstance();
        progressBar1 = findViewById(R.id.progressBar1);

        data = getIntent();

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        editNoteTitle = findViewById(R.id.editNoteTitle);
        editNoteContent = findViewById(R.id.editNoteContent);
        editNoteTitle.setText(noteTitle);
        editNoteContent.setText(noteContent);

        FloatingActionButton saveEditedNote = findViewById(R.id.saveEditedNote);
        saveEditedNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), EditNote.class);
                i.putExtra("title", data.getStringExtra("title"));
                i.putExtra("content", data.getStringExtra("content"));
                startActivity(i);
                //animation between transition of Activities
                overridePendingTransition(R.animator.slide_up, R.animator.slide_down);

                String ntitle =editNoteTitle.getText().toString();
                String nContent = editNoteContent.getText().toString();

                if (ntitle.isEmpty() || nContent.isEmpty()) {
                    Toast.makeText(EditNote.this, "Cant save please fill all the required fields", Toast.LENGTH_LONG).show();
                    return;
                }

                progressBar1.setVisibility(View.VISIBLE);

                //save the note to firebase
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));

                Map<String, Object> note = new HashMap<>();
                note.put("title", ntitle);
                note.put("content", nContent);

                documentReference.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditNote.this, "Note Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //animation between transition of Activities
                        overridePendingTransition(R.animator.slide_up, R.animator.slide_down);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditNote.this, "Failed, Note not added to firebase", Toast.LENGTH_SHORT).show();
                        progressBar1.setVisibility(View.INVISIBLE);
                        onBackPressed();
                    }
                });
            }
        });

    }
}
