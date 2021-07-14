package com.atulgupta.JotIt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.atulgupta.JotIt.authentication.Login;
import com.atulgupta.JotIt.authentication.Register;
import com.atulgupta.JotIt.model.Note;
import com.atulgupta.JotIt.note.AddNotes;
import com.atulgupta.JotIt.note.EditNote;
import com.atulgupta.JotIt.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView notelists;
    com.atulgupta.JotIt.model.Adapter adapter;
    FirebaseFirestore fstore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fstore = FirebaseFirestore.getInstance();

        Query query = fstore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);

        //query notes > uid > mynotes
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final int contentsBackgroundColorCode = getRandomColor();
                noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(contentsBackgroundColorCode, null));
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("color", contentsBackgroundColorCode);
                        i.putExtra("noteId", docId);
                        v.getContext().startActivity(i);
                        //animation between transition of Activities
                        overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                    }
                });


                ImageView menuIcon =  noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(), EditNote.class);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                i.putExtra("noteId", docId);
                                startActivity(i);
                                //animation between transition of Activities
                                overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docRef = fstore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);

                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //note deletion successful
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error in deleting note !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        menu.show();
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view_layout, parent, false);

                return new NoteViewHolder(view);
            }
        };


        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        navigationView = findViewById(R.id.navigationView);

        notelists = findViewById(R.id.noteList);

        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        notelists.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notelists.setAdapter(noteAdapter);


        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

        if (user.isAnonymous())
        {
            userEmail.setVisibility(View.GONE);
            userName.setText("Temporary User");
        } else {
            userEmail.setText(user.getEmail());
            userName.setText(user.getDisplayName());
        }


        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(view.getContext(), AddNotes.class));
                //animation between transition of Activities
                overridePendingTransition(R.animator.slide_up, R.animator.slide_down);

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId())
        {
            case R.id.addnotes:
                startActivity(new Intent(this, AddNotes.class));
                //animation between transition of Activities
                overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                break;

            case R.id.sync:
                if (user.isAnonymous())
                {
                    startActivity(new Intent(this, Login.class));
                    //animation between transition of Activities
                    overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                } else{
                    Toast.makeText(MainActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.email:
                email();
                Toast.makeText(this, "Email Working Fine", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                checkUser();
                break;

            case R.id.privacypolicy:
                privacyPolicy();
                break;

            case R.id.termandconditions:
                termsAndConditions();
                break;

            case R.id.shareapp:
                shareApp();
                break;

            case R.id.rating:
                rateApp();
                break;

            default:
                Toast.makeText(this, "It's Working Fine", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void email() {

        String[] TO = {"someone@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Toast.makeText(MainActivity.this, "Email sent...", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            // google play

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Oops!something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp() {
        try{
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String shareBody = "Best App for taking notes \nAccess you notes anytime anywhere \n \nhttps://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID;
            myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(myIntent, "Share using"));
        }catch (Exception e) {
            Toast.makeText(MainActivity.this, "Oops! something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void termsAndConditions() {
    }

    private void privacyPolicy() {
    }

    private void checkUser()
    {
        //if the user is Anonymous or not
        if (user.isAnonymous())
        {
            displayAlert();

        } else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Splash.class));
            //animation between transition of Activities
            overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
            finish();
        }
    }

    private void displayAlert()
    {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setMessage("Your are Logged in with a temporary account logging out will delete all your data. To save your data please Loging with an account.")
                .setPositiveButton("Synch Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       startActivity(new Intent(getApplicationContext(), Register.class));
                        //animation between transition of Activities
                        overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete all the note of the anonymous user

                        Toast.makeText(MainActivity.this, "user annonymous deleted", Toast.LENGTH_SHORT).show();

                        //delete the anonymous user
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), Splash.class));
                                //animation between transition of Activities
                                overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
                                finish();
                            }
                        });
                    }
                });

        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.settings)
//        {
//            Toast.makeText(this, "Setting Menu is clicked", Toast.LENGTH_SHORT).show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            view = itemView;
            mCardView = itemView.findViewById(R.id.noteCard);

        }
    }

    private int getRandomColor()
    {
        List<Integer> colorscode = new ArrayList<>();
        colorscode.add(R.color.blue);
        colorscode.add(R.color.skyblue);
        colorscode.add(R.color.lightPurple);
        colorscode.add(R.color.yellow);
        colorscode.add(R.color.lightGreen);
        colorscode.add(R.color.pink);
        colorscode.add(R.color.gray);
        colorscode.add(R.color.notgreen);
        colorscode.add(R.color.red);
        colorscode.add(R.color.greenlight);

        Random randomcolor  = new Random();
        int number = randomcolor.nextInt(colorscode.size());

        return colorscode.get(number);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (noteAdapter != null)
        {
            noteAdapter.stopListening();
        }

    }
}
