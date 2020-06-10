package com.test.diary4life.Activities.ui.home;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.diary4life.Models.Reflection;
import com.test.diary4life.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeAddReflectionViewModel extends RecyclerView.Adapter<HomeAddReflectionViewModel.ReflectionViewHolder> {

    // ------------------------ Add Additional Reflection Message -----------------------------

    private Context mContext;
    private List<Reflection> mData;


    public HomeAddReflectionViewModel(Context mContext, List<Reflection> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ReflectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_reflection,parent,false);
        return new ReflectionViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ReflectionViewHolder holder, int position) {

        Glide.with(mContext).load(mData.get(position).getUimg()).into(holder.img_user);
        holder.tv_name.setText(mData.get(position).getUname());
        holder.tv_content.setText(mData.get(position).getContent());
        holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ReflectionViewHolder extends RecyclerView.ViewHolder{

        ImageView img_user;
        TextView tv_name, tv_content, tv_date;

        public ReflectionViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.reflection_user_img);
            tv_name = itemView.findViewById(R.id.reflection_username);
            tv_content = itemView.findViewById(R.id.reflection_content);
            tv_date = itemView.findViewById(R.id.reflection_date);
        }
    }



    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;


    }

}