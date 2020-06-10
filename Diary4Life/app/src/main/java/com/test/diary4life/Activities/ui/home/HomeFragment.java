package com.test.diary4life.Activities.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.diary4life.Models.Story;
import com.test.diary4life.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView storyRecyclerView;
    HomeStoryViewModel homeStoryViewModel;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<Story> storyList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



/*        homePostStoryViewModel =
               ViewModelProviders.of(this).get(HomePostStoryViewModel.class);*/

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        storyRecyclerView = root.findViewById(R.id.storyRV);
        storyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        storyRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Stories");


/*        final TextView textView = root.findViewById(R.id.text_home);
       homePostStoryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
           @Override
           public void onChanged(@Nullable String s) {
               textView.setText(s);
           }
       });*/

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get List Stories from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                storyList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {

                    Story story = postsnap.getValue(Story.class);
                    storyList.add(story) ;



                }

                homeStoryViewModel = new HomeStoryViewModel(getActivity(), storyList);
                storyRecyclerView.setAdapter(homeStoryViewModel);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}