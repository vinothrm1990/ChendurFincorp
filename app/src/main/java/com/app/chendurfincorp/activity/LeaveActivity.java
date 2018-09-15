package com.app.chendurfincorp.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrognito.flashbar.Flashbar;
import com.app.chendurfincorp.R;
import com.app.chendurfincorp.helper.Constants;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    Flashbar flashbar;
    String id, name;
    IOSDialog iosDialog;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        flashbar = networkStatus();

        Constants.pref = getApplicationContext().getSharedPreferences("CF",MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        id = Constants.pref.getString("id", "");
        name = Constants.pref.getString("name", "");

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

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date dt = new Date();
        final String date = sdf.format(dt);

        new leavestatus(this, id, date).execute();

        leaveLayout = findViewById(R.id.leave_layout);
        etReason = findViewById(R.id.leave_et_reason);
        tvEmpId = findViewById(R.id.leave_tv_empid);
        tvStartDate = findViewById(R.id.leave_tv_sdate);
        tvEndDate = findViewById(R.id.leave_tv_edate);
        btnSubmit = findViewById(R.id.leave_btn_submit);

        tvEmpId.setText(id);
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
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }else if (etReason.getText().toString().trim().length()==0){
                    etReason.setError("Details Required");
                    Snackbar snack = Snackbar.make(leaveLayout, "Reason Feild is Empty", Snackbar.LENGTH_LONG);
                    view = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }else {

                    String sDate = tvStartDate.getText().toString().trim();
                    String eDate = tvEndDate.getText().toString().trim();
                    String reason = etReason.getText().toString().trim();

                    new leave(LeaveActivity.this, id, name, sDate, eDate, reason, date).execute();
                }
            }
        });

    }

    private void setStartDateFormat() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvStartDate.setText(sdf.format(scalendar.getTime()));

    }

    private void setEndDateFormat() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvEndDate.setText(sdf.format(ecalendar.getTime()));

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

    private class leave extends AsyncTask<String, Integer, String>{

        Context context;
        String id, name, sdate, edate, reason, date;
        String url = Constants.BASE_URL + Constants.LEAVE;

        public leave(Context context, String id, String name, String sdate, String edate, String reason, String date) {
            this.context = context;
            this.id = id;
            this.name = name;
            this.sdate = sdate;
            this.edate = edate;
            this.reason = reason;
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iosDialog = new IOSDialog.Builder(LeaveActivity.this)
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
                    .add("name", name)
                    .add("sdate", sdate)
                    .add("edate", edate)
                    .add("reason", reason)
                    .add("date", date)
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

                        Snackbar snack = Snackbar.make(leaveLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.GREEN);
                        snack.show();

                        String sdate = tvStartDate.getText().toString().trim();
                        String edate = tvEndDate.getText().toString().trim();
                        String reason = etReason.getText().toString().trim();

                        tvStartDate.setText(sdate);
                        tvStartDate.setEnabled(false);
                        tvEndDate.setText(edate);
                        tvEndDate.setEnabled(false);
                        etReason.setText(reason);
                        etReason.setEnabled(false);
                        btnSubmit.setVisibility(View.GONE);

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

    private class leavestatus extends AsyncTask<String, Integer, String>{

        Context context;
        String id, date;
        String url = Constants.BASE_URL + Constants.LEAVESTATUS;

        public leavestatus(Context context, String id, String date) {
            this.context = context;
            this.id = id;
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iosDialog = new IOSDialog.Builder(LeaveActivity.this)
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
                    .add("date", date)
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
                            "hold")) {

                        Snackbar snack = Snackbar.make(leaveLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.YELLOW);
                        snack.show();

                        statusPendingDialog();

                        String sdate = jonj.getString("start_date");
                        String edate = jonj.getString("end_date");
                        String reason = jonj.getString("query");

                        tvStartDate.setText(sdate);
                        tvStartDate.setEnabled(false);
                        tvEndDate.setText(edate);
                        tvEndDate.setEnabled(false);
                        etReason.setText(reason);
                        etReason.setEnabled(false);
                        btnSubmit.setVisibility(View.GONE);

                    } else if (jonj.getString("status").equalsIgnoreCase(
                            "decline")){

                        Snackbar snack = Snackbar.make(leaveLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();

                        statusDeclineDialog();

                        String sdate = jonj.getString("start_date");
                        String edate = jonj.getString("end_date");
                        String reason = jonj.getString("query");

                        tvStartDate.setText(sdate);
                        tvStartDate.setEnabled(false);
                        tvEndDate.setText(edate);
                        tvEndDate.setEnabled(false);
                        etReason.setText(reason);
                        etReason.setEnabled(false);
                        btnSubmit.setVisibility(View.GONE);

                    }
                    else if (jonj.getString("status").equalsIgnoreCase(
                            "approve")){

                        Snackbar snack = Snackbar.make(leaveLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.GREEN);
                        snack.show();

                        statusApproveDialog();

                        String sdate = jonj.getString("start_date");
                        String edate = jonj.getString("end_date");
                        String reason = jonj.getString("query");

                        tvStartDate.setText(sdate);
                        tvStartDate.setEnabled(false);
                        tvEndDate.setText(edate);
                        tvEndDate.setEnabled(false);
                        etReason.setText(reason);
                        etReason.setEnabled(false);
                        btnSubmit.setVisibility(View.GONE);

                    }
                    else if (jonj.getString("status").equalsIgnoreCase(
                            "empty")){

                        Snackbar snack = Snackbar.make(leaveLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.GREEN);
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

    private void statusPendingDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.status_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        TextView textView = dialogView.findViewById(R.id.tv_status);
        textView.setTextColor(getResources().getColor(R.color.yellow));
        textView.setText("Request Pending");
        ImageView imageView = dialogView.findViewById(R.id.iv_close);

        alertDialog = dialogBuilder.create();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void statusDeclineDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.status_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        TextView textView = dialogView.findViewById(R.id.tv_status);
        textView.setTextColor(getResources().getColor(R.color.red));
        textView.setText("Request Declined");
        ImageView imageView = dialogView.findViewById(R.id.iv_close);

        alertDialog = dialogBuilder.create();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void statusApproveDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.status_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        TextView textView = dialogView.findViewById(R.id.tv_status);
        textView.setTextColor(getResources().getColor(R.color.green));
        textView.setText("Request Approved");
        ImageView imageView = dialogView.findViewById(R.id.iv_close);

        alertDialog = dialogBuilder.create();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

}
