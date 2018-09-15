package com.app.chendurfincorp.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
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
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.adapters.CalendarPageAdapter;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AttendenceActivity extends AppCompatActivity implements InternetConnectivityListener {

    CalendarView calendarView;
    InternetAvailabilityChecker internetAvailabilityChecker;
    Flashbar flashbar;
    Calendar pcalendar, acalendar;
    RelativeLayout attendenceLayout;
    String date, status, month;
    Date pdate, adate;
    List<EventDay> days = new ArrayList<>();
    TextView tvPresent, tvAbsent;
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
        title.setText("ATTENDENCE ENTRY");
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
        calendarView = findViewById(R.id.calendar);
        tvAbsent = findViewById(R.id.absent_tv);
        tvPresent = findViewById(R.id.present_tv);

        calendarView.showCurrentMonthPage();

        final Calendar calendar = Calendar.getInstance();
        Formatter formatter = new Formatter();
        String formattermonth = String.valueOf(formatter.format(Locale.getDefault(), "%tB", calendar));
        month = formattermonth.substring(0,3);

        new attendence(this, id).execute();
        //new present(this, id, month).execute();
        //new absent(this, id, month).execute();


        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {

                String id = Constants.pref.getString("id", "");
                new attendence(AttendenceActivity.this, id).execute();

            }
        });
        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {

                Calendar cal = Calendar.getInstance();
                Formatter formatter = new Formatter();
                String formattermonth = String.valueOf(formatter.format(String.valueOf(cal), "%tB"));
                String nmonth = formattermonth.substring(0,3);
                new present(AttendenceActivity.this, id, nmonth).execute();
                new absent(AttendenceActivity.this, id, nmonth).execute();
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
        String id;
        IOSDialog iosDialog;
        String url = Constants.BASE_URL + Constants.ATTENDENCE;

        public attendence(Context context, String id) {
            this.context = context;
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iosDialog = new IOSDialog.Builder(context)
                    .setTitle("Please Wait...")
                    .setTitleColor(getResources().getColor(R.color.black))
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
                                //present.add(date);
                                SimpleDateFormat psdf = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    pdate = psdf.parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                pcalendar = Calendar.getInstance();
                                pcalendar.setTime(pdate);
                                present.add("P");
                                days.add(new EventDay(pcalendar, R.drawable.ic_present));
                            }else if (status.equalsIgnoreCase("A")){
                                //absent.add(date);
                                SimpleDateFormat asdf = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    adate = asdf.parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                acalendar = Calendar.getInstance();
                                acalendar.setTime(adate);
                                absent.add("A");
                                days.add(new EventDay(acalendar, R.drawable.ic_absent));

                            }

                        }
                        calendarView.setEvents(days);
                        if (present.size()!=0 && absent.size()!=0) {
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

                    Snackbar.make(attendenceLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class present extends AsyncTask<String, Integer, String>{

        Context context;
        String id, month;
        IOSDialog iosDialog;
        String url = Constants.BASE_URL + Constants.PRESENT;

        public present(Context context, String id, String month) {
            this.context = context;
            this.id = id;
            this.month = month;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iosDialog = new IOSDialog.Builder(context)
                    .setTitle("Please Wait...")
                    .setTitleColor(getResources().getColor(R.color.black))
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
                            "present")) {

                        String numofdays = jonj.getString("days");
                        tvPresent.setText(present.size());

                    } else if (jonj.getString("status").equalsIgnoreCase(
                            "empty")){

                        String numofdays = jonj.getString("days");
                        tvPresent.setText(numofdays);

                    }
                }else {

                    Snackbar.make(attendenceLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class absent extends AsyncTask<String, Integer, String>{

        Context context;
        String id, month;
        IOSDialog iosDialog;
        String url = Constants.BASE_URL + Constants.ABSENT;

        public absent(Context context, String id, String month) {
            this.context = context;
            this.id = id;
            this.month = month;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iosDialog = new IOSDialog.Builder(context)
                    .setTitle("Please Wait...")
                    .setTitleColor(getResources().getColor(R.color.black))
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
                            "absent")) {

                        String numofdays = jonj.getString("days");
                        tvPresent.setText(numofdays);

                    } else if (jonj.getString("status").equalsIgnoreCase(
                            "empty")){

                        String numofdays = jonj.getString("days");
                        tvPresent.setText(numofdays);

                    }
                }else {

                    Snackbar.make(attendenceLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
