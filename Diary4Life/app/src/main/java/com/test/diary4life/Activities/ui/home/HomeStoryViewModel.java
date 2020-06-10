package com.test.diary4life.Activities.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.diary4life.Activities.StoryDetailActivity;
import com.test.diary4life.Models.Story;
import com.test.diary4life.R;

import java.util.List;

public class HomeStoryViewModel extends RecyclerView.Adapter<HomeStoryViewModel.MyViewHolder> {

    // ----------------------------- Display Story Briefly ------------------------------------

    Context mContext;
    List<Story> mData ;


    public HomeStoryViewModel(Context mContext, List<Story> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_story_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeStoryViewModel.MyViewHolder holder, int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgStory);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgStoryProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgStory;
        ImageView imgStoryProfile;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_story_title);
            imgStory = itemView.findViewById(R.id.row_story_img);
            imgStoryProfile = itemView.findViewById(R.id.row_story_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent storyDetailActivity = new Intent(mContext, StoryDetailActivity.class);
                    int position = getAdapterPosition();

                    storyDetailActivity.putExtra("title", mData.get(position).getTitle());
                    storyDetailActivity.putExtra("storyImage", mData.get(position).getPicture());
                    storyDetailActivity.putExtra("description", mData.get(position).getDescription());
                    storyDetailActivity.putExtra("storyKey", mData.get(position).getStoryKey());
                    storyDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    // will fix this later i forgot to add user name to story object
                    //postDetailActivity.putExtra("userName",mData.get(position).getUsername);
                    long timestamp  = (long) mData.get(position).getTimeStamp();
                    storyDetailActivity.putExtra("storyDate", timestamp) ;
                    mContext.startActivity(storyDetailActivity);

                }
            });

        }

    }

}
