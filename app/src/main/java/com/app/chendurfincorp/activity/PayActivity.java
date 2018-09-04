package com.app.chendurfincorp.activity;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.chendurfincorp.R;
import com.app.chendurfincorp.adapter.HomeAdapter;
import com.app.chendurfincorp.adapter.PayAdapter;
import com.app.chendurfincorp.data.HomeMenu;
import com.app.chendurfincorp.data.MonthList;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;
import java.util.List;

public class PayActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker internetAvailabilityChecker;
    RelativeLayout payLayout;
    RecyclerView rvPay;
    PayAdapter payAdapter;
    List<MonthList> monthLists;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("PAY SLIP");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        payLayout = findViewById(R.id.payslip_layout);
        monthLists = new ArrayList<>();
        payAdapter = new PayAdapter(PayActivity.this, monthLists);
        rvPay = findViewById(R.id.rv_pay);
        mLayoutManager = new GridLayoutManager(PayActivity.this, 3);
        rvPay.setLayoutManager(mLayoutManager);
        rvPay.setAdapter(payAdapter);
        fetchMonth();
    }

    private void fetchMonth() {

        MonthList month = new MonthList("JAN");
        monthLists.add(month);
        month = new MonthList("FEB");
        monthLists.add(month);
        month = new MonthList("MAR");
        monthLists.add(month);
        month = new MonthList("APR");
        monthLists.add(month);
        month = new MonthList("MAY");
        monthLists.add(month);
        month = new MonthList("JUN");
        monthLists.add(month);
        month = new MonthList("JUL");
        monthLists.add(month);
        month = new MonthList("AUG");
        monthLists.add(month);
        month = new MonthList("SEP");
        monthLists.add(month);
        month = new MonthList("OCT");
        monthLists.add(month);
        month = new MonthList("NOV");
        monthLists.add(month);
        month = new MonthList("DEC");
        monthLists.add(month);

        payAdapter.notifyDataSetChanged();
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

    }
}
