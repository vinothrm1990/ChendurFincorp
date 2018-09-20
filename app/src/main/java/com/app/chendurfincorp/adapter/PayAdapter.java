package com.app.chendurfincorp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chendurfincorp.R;
import com.app.chendurfincorp.activity.SlipActivity;
import com.app.chendurfincorp.data.MonthList;
import com.app.chendurfincorp.helper.Constants;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.MyViewHolder>{

    private Context mContext;
    private List<MonthList> monthLists;
    MonthList month;

    public PayAdapter(Context mContext, List<MonthList> monthLists) {
        this.mContext = mContext;
        this.monthLists = monthLists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        month = monthLists.get(position);

        holder.title.setText(month.getTitle());

        Constants.pref = mContext.getSharedPreferences("CF",0);
        Constants.editor = Constants.pref.edit();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = Constants.pref.getString("id", "");
                String mon = monthLists.get(position).getTitle();

                Intent intent = new Intent(mContext, SlipActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("month", mon);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return monthLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_pay);
            cardView = itemView.findViewById(R.id.cv_pay);
        }
    }

}
