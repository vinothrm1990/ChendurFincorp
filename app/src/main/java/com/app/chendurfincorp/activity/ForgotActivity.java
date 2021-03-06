package com.app.chendurfincorp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

public class ForgotActivity extends AppCompatActivity implements InternetConnectivityListener {

    RelativeLayout forgotLayout;
    InternetAvailabilityChecker internetAvailabilityChecker;
    TextFieldBoxes tilPhone, tilOtp, tilPass;
    ExtendedEditText etPhone, etOtp, etPass;
    CircularProgressButton btnUpdate;
    FloatingActionButton fbBack;
    Flashbar flashbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        flashbar = networkStatus();

        tilPhone = findViewById(R.id.forgot_til_phone);
        tilOtp = findViewById(R.id.forgot_til_otp);
        tilPass = findViewById(R.id.forgot_til_pass);
        etPhone = findViewById(R.id.forgot_et_phone);
        etOtp = findViewById(R.id.forgot_et_otp);
        etPass = findViewById(R.id.forgot_et_pass);
        btnUpdate = findViewById(R.id.forgot_btn_update);
        fbBack = findViewById(R.id.forgot_btn_back);
        forgotLayout = findViewById(R.id.forgot_layout);

        fbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                Bungee.split(ForgotActivity.this);
            }
        });

        etPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                String phone = etPhone.getText().toString().trim();
                if (id== EditorInfo.IME_ACTION_NEXT){
                    if (phone.length()==0){
                        etPhone.setError("Details Required");
                    }else if (phone.length()<10) {
                        etPhone.setError("Enter Valid Number");
                    }else {
                        IOSDialog iosDialog = new IOSDialog.Builder(ForgotActivity.this)
                                .setTitle("Please Wait...")
                                .setTitleColor(getResources().getColor(R.color.black))
                                .setSpinnerColorRes(R.color.dark_gray)
                                .setCancelable(true)
                                .setSpinnerClockwise(true)
                                .build();
                        iosDialog.show();
                    }
                    return true;
                }
                return false;
            }
        });

        tilPhone.getEndIconImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString().trim();
                if (phone.length()==0){
                    etPhone.setError("Details Required");
                }else if (phone.length()<10) {
                    etPhone.setError("Enter Valid Number");
                }else {
                    IOSDialog iosDialog = new IOSDialog.Builder(ForgotActivity.this)
                            .setTitle("Please Wait...")
                            .setTitleColor(getResources().getColor(R.color.white))
                            .setSpinnerColorRes(R.color.dark_gray)
                            .setCancelable(true)
                            .setSpinnerClockwise(true)
                            .build();
                    iosDialog.show();
                }

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = etPhone.getText().toString().trim();
                String otp = etOtp.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                btnUpdate.startAnimation();
                //new update(ForgotActivity.this, phone, otp, pass).execute();
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

    private class update extends AsyncTask<String, Integer, String>{

        Context context;
        String phone, otp, pass;
        String url = Constants.BASE_URL + Constants.UPDATE;

        public update(Context context, String phone, String otp, String pass) {
            this.context = context;
            this.phone = phone;
            this.otp = otp;
            this.pass = pass;
        }

        @Override
        protected String doInBackground(String... strings) {

            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("phone", phone)
                    .add("otp", otp)
                    .add("pass", pass)
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

                        btnUpdate.stopAnimation();
                        startActivity(new Intent(ForgotActivity.this, HomeActivity.class));
                        finish();

                    } else {

                        Snackbar.make(forgotLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);
                    }
                } else {

                    Snackbar.make(forgotLayout, jonj.getString("message"), Snackbar.LENGTH_SHORT);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
