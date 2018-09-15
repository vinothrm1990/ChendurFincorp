package com.app.chendurfincorp.activity;

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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import spencerstudios.com.bungeelib.Bungee;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class LoginActivity extends AppCompatActivity implements InternetConnectivityListener {

    RelativeLayout loginLayout;
    InternetAvailabilityChecker internetAvailabilityChecker;
    TextFieldBoxes tilPhone, tilpass;
    TextView tvForgot;
    ExtendedEditText etPhone, etpass;
    CircularProgressButton btnLogin;
    Flashbar flashbar;
    IOSDialog iosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        Constants.pref = getApplicationContext().getSharedPreferences("CF",MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        flashbar = networkStatus();

        loginLayout = findViewById(R.id.login_layout);
        tilPhone = findViewById(R.id.log_til_phone);
        etPhone = findViewById(R.id.log_et_phone);
        tilpass = findViewById(R.id.log_til_pass);
        etpass = findViewById(R.id.log_et_pass);
        tvForgot = findViewById(R.id.log_tv_forgot);
        btnLogin = findViewById(R.id.log_btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = etPhone.getText().toString().trim();
                String pass = etpass.getText().toString().trim();
                boolean emptyfeilds = false;

                if (phone.length()==0){
                    emptyfeilds = true;
                    tilPhone.validate();
                    etPhone.setError("Details Required");
                }if (phone.length()<10){
                    emptyfeilds = true;
                    etPhone.setError("Enter Valid Number");
                } if (pass.length()==0){
                    emptyfeilds = true;
                    tilpass.validate();
                    etpass.setError("Details Required");
                }if (emptyfeilds == false) {
                    new login(LoginActivity.this, phone, pass).execute();
                }
            }
        });
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
                Bungee.split(LoginActivity.this);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        internetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (!isConnected) {
            flashbar.show();
        } else if (isConnected){
            flashbar.dismiss();
        }
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

    private class login extends AsyncTask<String, Integer, String>{

        Context context;
        String phone, pass;
        String url = Constants.BASE_URL + Constants.LOGIN;

        public login(Context context, String phone, String pass) {
            this.context = context;
            this.phone = phone;
            this.pass = pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            iosDialog = new IOSDialog.Builder(LoginActivity.this)
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
                    .add(Constants.phone, phone)
                    .add(Constants.password, pass)
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
                        JSONObject jcat = array.getJSONObject(0);

                        Constants.editor.putBoolean("isLogged", true);
                        Constants.editor.putString(Constants.id, jcat.getString("emp_id"));
                        Constants.editor.putString(Constants.name, jcat.getString("emp_name"));
                        Constants.editor.putString(Constants.phone, jcat.getString("username"));
                        Constants.editor.putString(Constants.password, jcat.getString("password"));
                        Constants.editor.putString(Constants.category, jcat.getString("category"));
                        Constants.editor.commit();
                        Constants.editor.apply();

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    } else {

                        Snackbar.make(loginLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);
                    }
                }else {

                    Snackbar.make(loginLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
