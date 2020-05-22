package com.test.diary4life.Activities.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.test.diary4life.R;

public class ProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the two Firebase Variables
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView UserName = root.findViewById(R.id.txtUserName);
        TextView UserEmail = root.findViewById(R.id.txtUserEmail);
        ImageView UserProfilePicture = root.findViewById(R.id.imgUserPhoto);

        UserEmail.setText(currentUser.getEmail());
        UserName.setText(currentUser.getDisplayName());

        // use Glide to load user's profile picture
        Glide.with(this).load(currentUser.getPhotoUrl()).into(UserProfilePicture);

        return root;
    }

}
