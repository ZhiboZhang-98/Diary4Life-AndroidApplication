package com.test.diary4life.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.test.diary4life.R;

public class RegisterActivity extends AppCompatActivity {

    ImageView ImgUserProfilePhoto;
    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;

    private EditText userName, userEmail, userPassword, userPasswordCk;
    private ProgressBar regProgress;
    private Button btnReg;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.txtRegName);
        userEmail = findViewById(R.id.txtRegEmail);
        userPassword = findViewById(R.id.txtRegPassword);
        userPasswordCk = findViewById(R.id.txtRegPasswordCk);
        regProgress = findViewById(R.id.regProgressBar);
        btnReg = findViewById(R.id.btnRegister);

        regProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReg.setVisibility(View.INVISIBLE);
                regProgress.setVisibility(View.VISIBLE);
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String passwordck = userPasswordCk.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !password.equals(passwordck))
                {
                    // validation : all fields must be filled
                    // display an error message if something goes wrong

                    showMessage("Please Verify All the Fields");
                    btnReg.setVisibility(View.VISIBLE);
                    regProgress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    // create user account

                    CreateUserAccount(name, email, password);
                }
            }
        });

        ImgUserProfilePhoto = findViewById(R.id.imgRegUserPhoto);

        ImgUserProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22)
                {
                    checkAndRequestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });

    }

    private void CreateUserAccount(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    // user account created successfully
                    showMessage("Account Created Successfully !");

                    //update user's information
                    UpdateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());
                }
                else
                {
                    // account creation failed
                    showMessage("Account Creation Failed ! " + task.getException().getMessage());
                    btnReg.setVisibility(View.VISIBLE);
                    regProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void UpdateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        // upload user's profile picture to firebase storage and get url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // retrieve image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    // user info updated successfully
                                    showMessage("Register Complete");
                                    updateUI();
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        //TODO: open profile intent and wait for user to pick an image

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESTCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        }
        else
        {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null)
        {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable

            pickedImgUri = data.getData();
            ImgUserProfilePhoto.setImageURI(pickedImgUri);
        }
    }

}
