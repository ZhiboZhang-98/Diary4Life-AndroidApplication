package com.test.diary4life.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.test.diary4life.Models.Story;
import com.test.diary4life.R;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Dialog popAddStory;

    ImageView popupUserImage, popupStoryImage, popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;

    private Uri pickedImgUri = null;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the two Firebase Variables
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Popup
        iniPopup();
        setupPopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popAddStory.show();

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // change <- sign to three dashes
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_settings, R.id.nav_about, R.id.nav_signout)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        updateNavHeader();

    }

    private void setupPopupImageClick() {

        popupStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // when user click the image ---> open the gallery
                // must check if the app have the access to user's files before open the gallery
                // reference to "RegisterActivity"

                checkAndRequestForPermission();

            }
        });

    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(HomeActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();

            }

            else
            {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            // granted permission to access user's gallery
            openGallery();

    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    // when user picked an image ...
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData() ;
            popupStoryImage.setImageURI(pickedImgUri);

        }


    }

    private void iniPopup() {

        popAddStory = new Dialog(this);
        popAddStory.setContentView(R.layout.popup_add_story);
        popAddStory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddStory.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddStory.getWindow().getAttributes().gravity = Gravity.TOP;

        // Initialize all Popup Widgets
        popupUserImage = popAddStory.findViewById(R.id.popup_user_image);
        popupStoryImage = popAddStory.findViewById(R.id.popup_img);
        popupTitle = popAddStory.findViewById(R.id.popup_title);
        popupDescription = popAddStory.findViewById(R.id.popup_description);
        popupAddBtn = popAddStory.findViewById(R.id.popup_add);
        popupClickProgress = popAddStory.findViewById(R.id.popup_progressBar);

        // load current user Profile picture
        Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(popupUserImage);

        // Add Diary onclick Listener
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // validation : check all input fields "Title", "Description", and "Image"
                if (!popupTitle.getText().toString().isEmpty() && !popupDescription.getText().toString().isEmpty() && pickedImgUri != null)
                {

                    // no empty or null fields
                    // TODO : Create Diary Object and Add it to Firebase Database
                    // upload user's picked Image
                    // access Firebase Storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("story_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());

                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();
                                    // create story Object
                                    Story story = new Story(popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownlaodLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString());

                                    // Add story to firebase database

                                    addStory(story);



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // something goes wrong uploading picture

                                    showMessage(e.getMessage());
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);



                                }
                            });


                        }
                    });

                }
                else
                {

                    showMessage("Please Verify All Inputs Fields and Choose an Image");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);

                }

            }
        });

    }

    private void addStory(Story story) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Stories").push();

        // get story unique ID and update story key
        String key = myRef.getKey();
        story.setStoryKey(key);

        // add story data to firebase database
        myRef.setValue(story).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Story Added Successfully");
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddStory.dismiss();

                // clear all text views and move the cursor to title
                popupTitle.setText(null);
                popupDescription.setText(null);
                popupStoryImage.setImageResource(0);
                //popupTitle.setCursorVisible(true);
                popupTitle.requestFocus();
            }
        });

    }

    private void showMessage(String message) {

        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void updateNavHeader()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_userName);
        TextView navUserEmail = headerView.findViewById(R.id.nav_userEmail);
        ImageView navUserProfilePicture = headerView.findViewById(R.id.nav_imgUserPhoto);

        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        // use Glide to load user's profile picture
        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserProfilePicture);

    }

}
