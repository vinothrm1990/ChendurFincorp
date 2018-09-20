package com.app.chendurfincorp.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.andrognito.flashbar.Flashbar;
import com.app.chendurfincorp.R;
import com.app.chendurfincorp.helper.Constants;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AttendenceActivity extends AppCompatActivity implements InternetConnectivityListener {

    CompactCalendarView calendarView;
    InternetAvailabilityChecker internetAvailabilityChecker;
    Flashbar flashbar;
    RelativeLayout attendenceLayout;
    String date, status, month;
    Event days;
    TextView tvPresent, tvAbsent, tvtitle;
    List<String> present = new ArrayList<>();
    List<String> absent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        flashbar = networkStatus();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("ATTENDENCE REPORT");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Constants.pref = getApplicationContext().getSharedPreferences("CF",MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        final String id = Constants.pref.getString("id", "");

        attendenceLayout = findViewById(R.id.attendence_layout);
        calendarView = findViewById(R.id.calander_view);
        tvAbsent = findViewById(R.id.absent_tv);
        tvPresent = findViewById(R.id.present_tv);
        tvtitle = findViewById(R.id.calander_title);

        Date date = calendarView.getFirstDayOfCurrentMonth();
        month = String.valueOf(date);
        int mid = month.length() / 4;
        String[] parts = {month.substring(3, mid)};
        String mname = parts[0].trim();
        tvtitle.setText(mname);

        new attendence(this, id, mname).execute();

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                month = String.valueOf(firstDayOfNewMonth);
                int mid = month.length() / 4;
                String[] parts = {month.substring(3, mid)};
                String mname = parts[0].trim();
                tvtitle.setText(mname);
                new attendence(AttendenceActivity.this, id, mname).execute();
            }
        });

    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (!isConnected) {
           flashbar.show();
        } else if (isConnected){
           flashbar.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        internetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    private Flashbar networkStatus() {
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .titleSizeInSp(18)
                .messageSizeInSp(14)
                .title("Network Status:")
                .message("Check your Internet Connection")
                .titleColorRes(R.color.red)
                .messageColorRes(R.color.red)
                .backgroundColorRes(R.color.translucent_black)
                .showOverlay()
                .titleTypeface(Typeface.createFromAsset(getAssets(),"fonts/lato_bold.ttf"))
                .messageTypeface(Typeface.createFromAsset(getAssets(),"fonts/lato_regular.ttf"))
                .primaryActionTextTypeface(Typeface.createFromAsset(getAssets(),"fonts/lato_bold.ttf"))
                .primaryActionText("Goto")
                .primaryActionTextColorRes(R.color.black)
                .primaryActionTextSizeInSp(10)
                .primaryActionTapListener(new Flashbar.OnActionTapListener() {
                    @Override
                    public void onActionTapped(@NotNull Flashbar bar) {
                        bar.dismiss();
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .build();
    }

    private class attendence extends AsyncTask<String, Integer, String>{

        Context context;
        String id, month;
        IOSDialog iosDialog;
        String url = Constants.BASE_URL + Constants.ATTENDENCE;

        public attendence(Context context, String id, String month) {
            this.context = context;
            this.id = id;
            this.month = month;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            present.clear();
            absent.clear();
            calendarView.removeAllEvents();
            tvPresent.setText("0");
            tvAbsent.setText("0");

            iosDialog = new IOSDialog.Builder(context)
                    .setTitle("Please Wait...")
                    .setTitleColor(getResources().getColor(R.color.white))
                    .setSpinnerColorRes(R.color.dark_gray)
                    .setCancelable(true)
                    .setSpinnerClockwise(true)
                    .build();
            iosDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id", id)
                    .add("month", month)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = client.newCall(request);

            try {
                response = call.execute();

                if (response.isSuccessful()) {
                    jsonData = response.body().string();
                } else {
                    jsonData = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);

            iosDialog.dismiss();
            JSONObject jonj = null;
            try {
                if (jsonData != null) {
                    jonj = new JSONObject(jsonData);
                    if (jonj.getString("status").equalsIgnoreCase(
                            "success")) {

                        String data = jonj.getString("message");
                        JSONArray array = new JSONArray(data);
                        for(int i=0;i<array.length();i++) {
                            JSONObject jcat = array.getJSONObject(i);

                            date = jcat.getString("hdate");
                            status = jcat.getString("status");

                            if (status.equalsIgnoreCase("P")) {
                                SimpleDateFormat psdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date pdate = null;
                                try {
                                    pdate = psdf.parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long millis = pdate.getTime();
                                present.add("P");
                                days = new Event(Color.GREEN, millis, "PRESENT");
                                calendarView.addEvent(days);
                            }else if (status.equalsIgnoreCase("A")){
                                SimpleDateFormat asdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date adate = null;
                                try {
                                    adate = asdf.parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                long millis = adate.getTime();
                                absent.add("A");
                                days = new Event(Color.RED, millis, "ABSENT");
                                calendarView.addEvent(days);
                            }

                        }
                        //calendarView.addEvent(days);
                        if (present.size()!=0 || absent.size()!=0) {
                            tvPresent.setText(String.valueOf(present.size()));
                            tvAbsent.setText(String.valueOf(absent.size()));
                        }else {
                            tvPresent.setText("0");
                            tvAbsent.setText("0");
                        }
                    } else {

                        Snackbar.make(attendenceLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);
                    }
                }else {

                    //Snackbar.make(attendenceLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
