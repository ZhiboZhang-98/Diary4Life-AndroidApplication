package com.test.diary4life.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.diary4life.Models.Reflection;
import com.test.diary4life.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StoryDetailActivity extends AppCompatActivity {

    ImageView imgStory, imgUserStory, imgCurrentUser;
    TextView txtStoryDesc, txtStoryDateName, txtStoryTitle;
    EditText editTextReflection;
    Button btnAddReflection;

    String StoryKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    RecyclerView RvReflection;
    HomeAddReflectionViewModel homeAddReflectionViewModel;

    List<Reflection> listReflection;

    static String COMMENT_KEY = "Reflection" ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);


        // set the Status Bar to transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        // Initialize Views
        RvReflection = findViewById(R.id.rv_reflection);
        imgStory =findViewById(R.id.story_detail_img);
        imgUserStory = findViewById(R.id.story_detail_user_img);
        imgCurrentUser = findViewById(R.id.story_detail_currentuser_img);

        txtStoryTitle = findViewById(R.id.story_detail_title);
        txtStoryDesc = findViewById(R.id.story_detail_desc);
        txtStoryDateName = findViewById(R.id.story_detail_date_name);

        editTextReflection = findViewById(R.id.story_detail_reflection);
        btnAddReflection = findViewById(R.id.story_detail_add_reflection_btn);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        // add Reflection button onclick listener
        btnAddReflection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddReflection.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(StoryKey).push();
                String comment_content = editTextReflection.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();
                Reflection reflection = new Reflection(comment_content, uid, uimg, uname);

                commentReference.setValue(reflection).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Reflection Added");
                        editTextReflection.setText("");
                        btnAddReflection.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Fail to Add Reflection : " + e.getMessage());
                    }
                });



            }
        });


        // bind all data into those views
        // 1ST : get story data
        // 2ND : send story detail data to this activity
        // 3RD : get story data

        String storyImage = getIntent().getExtras().getString("storyImage") ;
        Glide.with(this).load(storyImage).into(imgStory);

        String storyTitle = getIntent().getExtras().getString("title");
        txtStoryTitle.setText(storyTitle);

        String userstoryImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userstoryImage).into(imgUserStory);

        String storyDescription = getIntent().getExtras().getString("description");
        txtStoryDesc.setText(storyDescription);

        // set story user image

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        // get story id
        StoryKey = getIntent().getExtras().getString("storyKey");

        String date = timestampToString(getIntent().getExtras().getLong("storyDate"));
        txtStoryDateName.setText(date);


        // Initialize Recyclerview Reflection
        iniRvReflection();


    }

    private void iniRvReflection() {

        RvReflection.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference reflectionRef = firebaseDatabase.getReference(COMMENT_KEY).child(StoryKey);
        reflectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listReflection = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Reflection reflection = snap.getValue(Reflection.class);
                    listReflection.add(reflection) ;

                }

                homeAddReflectionViewModel = new HomeAddReflectionViewModel(getApplicationContext(), listReflection);
                RvReflection.setAdapter(homeAddReflectionViewModel);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }


    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;


    }

}