package com.app.chendurfincorp.activity;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

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

        queryLayout = findViewById(R.id.queries_layout);
        etMessage = findViewById(R.id.query_et_message);
        btnSubmit = findViewById(R.id.query_btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etMessage.getText().toString().trim().length()==0){
                    etMessage.setError("Details Required");
                    Snackbar snack = Snackbar.make(queryLayout, "Message Feild is Empty", Snackbar.LENGTH_LONG);
                    view = snack.getView();
                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }else {
                    String message = etMessage.getText().toString().trim();
                    btnSubmit.startAnimation();
                    //new query(QueryActivity.this, message).execute();
                }
            }
        });

    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (!isConnected) {
            Snackbar snack = Snackbar.make(queryLayout, "Check your Internet Connection", Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.RED);
            snack.show();
        } else if (isConnected){
            Snackbar snack = Snackbar.make(queryLayout, "Connected to the Internet", Snackbar.LENGTH_SHORT);
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

    private class query extends AsyncTask<String, Integer, String> {

        Context context;
        String message;
        String url = Constants.BASE_URL + Constants.QUERIES;

        public query(Context context, String message) {
            this.context = context;
            this.message = message;
        }

        @Override
        protected String doInBackground(String... strings) {

            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("message", message)
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
                        Snackbar snack = Snackbar.make(queryLayout, "Submitted Successfully", Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.GREEN);
                        snack.show();

                    } else {
                        Snackbar snack = Snackbar.make(queryLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.YELLOW);
                        snack.show();
                    }
                }else {
                    Snackbar snack = Snackbar.make(queryLayout, jonj.getString("message"), Snackbar.LENGTH_LONG);
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
