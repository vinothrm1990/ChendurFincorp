package com.app.chendurfincorp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.chendurfincorp.R;
import com.app.chendurfincorp.activity.AttendenceActivity;
import com.app.chendurfincorp.activity.LeaveActivity;
import com.app.chendurfincorp.activity.PayActivity;
import com.app.chendurfincorp.activity.QueryActivity;
import com.app.chendurfincorp.data.HomeMenu;
import com.bumptech.glide.Glide;

import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private Context mContext;
    private List<HomeMenu> homeList;

    public HomeAdapter(Context mContext, List<HomeMenu> homeList) {
        this.mContext = mContext;
        this.homeList = homeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        HomeMenu menu = homeList.get(position);

        holder.title.setText(menu.getTitle());
        Glide.with(mContext).load(menu.getImage()).into(holder.image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position==0){
                    mContext.startActivity(new Intent(mContext, AttendenceActivity.class));
                    Bungee.split(mContext);
                }else if (position==1){
                    mContext.startActivity(new Intent(mContext, LeaveActivity.class));
                    Bungee.split(mContext);
                }else if (position==2){
                    mContext.startActivity(new Intent(mContext, PayActivity.class));
                    Bungee.split(mContext);
                }else if (position==3){
                    mContext.startActivity(new Intent(mContext, QueryActivity.class));
                    Bungee.split(mContext);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;
        public LinearLayout cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.home_iv);
            title = itemView.findViewById(R.id.home_tv);
            cardView = itemView.findViewById(R.id.home_cv);
        }
    }
}
