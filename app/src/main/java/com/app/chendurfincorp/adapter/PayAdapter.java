package com.app.chendurfincorp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.app.chendurfincorp.R;
import com.app.chendurfincorp.data.MonthList;

import java.util.List;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.MyViewHolder>{

    private Context mContext;
    private List<MonthList> monthLists;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        MonthList month = monthLists.get(position);

        holder.title.setText(month.getTitle());

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
