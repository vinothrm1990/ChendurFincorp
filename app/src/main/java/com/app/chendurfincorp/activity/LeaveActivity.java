package com.app.chendurfincorp.activity;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.chendurfincorp.R;
import com.app.chendurfincorp.helper.Constants;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class LeaveActivity extends AppCompatActivity implements InternetConnectivityListener{

    InternetAvailabilityChecker internetAvailabilityChecker;
    RelativeLayout leaveLayout;
    CircularProgressButton btnSubmit;
    TextView tvEmpId, tvStartDate, tvEndDate;
    ExtendedEditText etReason;
    Calendar scalendar, ecalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("LEAVE REQUEST");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leaveLayout = findViewById(R.id.leave_layout);
        etReason = findViewById(R.id.leave_et_reason);
        tvEmpId = findViewById(R.id.leave_tv_empid);
        tvStartDate = findViewById(R.id.leave_tv_sdate);
        tvEndDate = findViewById(R.id.leave_tv_edate);
        btnSubmit = findViewById(R.id.leave_btn_submit);

        scalendar = Calendar.getInstance();
        ecalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener sdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                scalendar.set(Calendar.YEAR, year);
                scalendar.set(Calendar.MONTH, monthOfYear);
                scalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setStartDateFormat();
            }

        };
        final DatePickerDialog.OnDateSetListener edate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                ecalendar.set(Calendar.YEAR, year);
                ecalendar.set(Calendar.MONTH, monthOfYear);
                ecalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setEndDateFormat();
            }

        };
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(LeaveActivity.this, sdate, scalendar
                        .get(Calendar.YEAR), scalendar.get(Calendar.MONTH),
                        scalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(LeaveActivity.this, edate, ecalendar
                        .get(Calendar.YEAR), ecalendar.get(Calendar.MONTH),
                        ecalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvStartDate.getText().toString().trim().equalsIgnoreCase("starting date") ||
                        tvEndDate.getText().toString().trim().equalsIgnoreCase("ending date")){
                    Snackbar snack = Snackbar.make(leaveLayout, "Date Feilds are Empty", Snackbar.LENGTH_LONG);
                    view = snack.getView();
                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }else if (etReason.getText().toString().trim().length()==0){
                    etReason.setError("Details Required");
                    Snackbar snack = Snackbar.make(leaveLayout, "Reason Feild is Empty", Snackbar.LENGTH_LONG);
                    view = snack.getView();
                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }else {
                    String sDate = tvStartDate.getText().toString().trim();
                    String eDate = tvEndDate.getText().toString().trim();
                    String reason = etReason.getText().toString().trim();
                    btnSubmit.startAnimation();
                    //new leave(LeaveActivity.this, sDate, eDate, reason).execute();
                }
            }
        });

    }

    private void setStartDateFormat() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvStartDate.setText(sdf.format(scalendar.getTime()));

    }

    private void setEndDateFormat() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvEndDate.setText(sdf.format(ecalendar.getTime()));

    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (!isConnected) {
            Snackbar snack = Snackbar.make(leaveLayout, "Check your Internet Connection", Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.RED);
            snack.show();
        } else if (isConnected){
            Snackbar snack = Snackbar.make(leaveLayout, "Connected to the Internet", Snackbar.LENGTH_SHORT);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.GREEN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        internetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    private class leave extends AsyncTask<String, Integer, String>{

        Context context;
        String sdate, edate, reason;
        String url = Constants.BASE_URL + Constants.LEAVE;

        public leave(Context context, String sdate, String edate, String reason) {
            this.context = context;
            this.sdate = sdate;
            this.edate = edate;
            this.reason = reason;
        }

        @Override
        protected String doInBackground(String... strings) {

            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("sdate", sdate)
                    .add("edate", edate)
                    .add("reason", reason)
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

            JSONObject jonj = null;
            try {
                if (jsonData != null) {
                    jonj = new JSONObject(jsonData);
                    if (jonj.getString("status").equalsIgnoreCase(
                            "success")) {

                        String data = jonj.getString("message");
                        JSONArray array = new JSONArray(data);
                        JSONObject jcat = array.getJSONObject(0);

                        btnSubmit.stopAnimation();
                        Snackbar snack = Snackbar.make(leaveLayout, "Submitted Successfully", Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.GREEN);
                        snack.show();

                    } else {
                        Snackbar snack = Snackbar.make(leaveLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.YELLOW);
                        snack.show();
                    }
                }else {
                    Snackbar snack = Snackbar.make(leaveLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.YELLOW);
                    snack.show();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
