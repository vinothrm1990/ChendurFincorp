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
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
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

public class QueryActivity extends AppCompatActivity implements InternetConnectivityListener{

    InternetAvailabilityChecker internetAvailabilityChecker;
    RelativeLayout queryLayout;
    CircularProgressButton btnSubmit;
    ExtendedEditText etMessage;
    TextView tvEmpId;
    Flashbar flashbar;
    String id, name;
    IOSDialog iosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        flashbar = networkStatus();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("SUBMIT QUERIES");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Constants.pref = getApplicationContext().getSharedPreferences("CF",MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        id = Constants.pref.getString("id", "");
        name = Constants.pref.getString("name", "");

        queryLayout = findViewById(R.id.queries_layout);
        etMessage = findViewById(R.id.query_et_message);
        btnSubmit = findViewById(R.id.query_btn_submit);
        tvEmpId = findViewById(R.id.query_tv_empid);

        tvEmpId.setText(id);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etMessage.getText().toString().trim().length()==0){
                    etMessage.setError("Details Required");
                    Snackbar snack = Snackbar.make(queryLayout, "Message Feild is Empty", Snackbar.LENGTH_LONG);
                    view = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }else {
                    String myFormat = "dd/MM/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    Date dt = new Date();
                    String date = sdf.format(dt);
                    String message = etMessage.getText().toString().trim();
                    new query(QueryActivity.this, id, name, message, date).execute();
                }
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

    private class query extends AsyncTask<String, Integer, String> {

        Context context;
        String id, name, message, date;
        String url = Constants.BASE_URL + Constants.QUERIES;

        public query(Context context, String id, String name, String message, String date) {
            this.context = context;
            this.id = id;
            this.name = name;
            this.message = message;
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iosDialog = new IOSDialog.Builder(QueryActivity.this)
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
                    .add("message", message)
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

                        Snackbar snack = Snackbar.make(queryLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                        params.gravity = Gravity.BOTTOM;
                        view.setLayoutParams(params);
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.GREEN);
                        snack.show();

                    } else {
                        Snackbar snack = Snackbar.make(queryLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                        params.gravity = Gravity.BOTTOM;
                        view.setLayoutParams(params);
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.RED);
                        snack.show();
                    }
                }else {
                    Snackbar snack = Snackbar.make(queryLayout, "", Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
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
