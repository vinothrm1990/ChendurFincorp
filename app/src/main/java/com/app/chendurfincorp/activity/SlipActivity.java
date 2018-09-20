package com.app.chendurfincorp.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.andrognito.flashbar.Flashbar;
import com.app.chendurfincorp.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import com.webviewtopdf.PdfView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;

public class SlipActivity extends AppCompatActivity implements InternetConnectivityListener {

    private WebView mWebview ;
    InternetAvailabilityChecker internetAvailabilityChecker;
    Flashbar flashbar;
    String mon;
    FloatingActionButton actionButton;
    IOSDialog iosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip);

        internetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        internetAvailabilityChecker.addInternetConnectivityListener(this);

        flashbar = networkStatus();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("PAYSLIP");
        title.setTextSize(20);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actionButton = findViewById(R.id.fab);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String month = intent.getStringExtra("month");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        if (month.equalsIgnoreCase("JAN")){
            mon = "01";
        }else if (month.equalsIgnoreCase("FEB")){
            mon = "02";
        }else if (month.equalsIgnoreCase("MAR")){
            mon = "03";
        }else if (month.equalsIgnoreCase("APR")){
            mon = "04";
        }else if (month.equalsIgnoreCase("MAY")){
            mon = "05";
        }else if (month.equalsIgnoreCase("JUN")){
            mon = "06";
        }else if (month.equalsIgnoreCase("JUL")){
            mon = "07";
        }else if (month.equalsIgnoreCase("AUG")){
            mon = "08";
        }else if (month.equalsIgnoreCase("SEP")){
            mon = "09";
        }else if (month.equalsIgnoreCase("OCT")){
            mon = "10";
        }else if (month.equalsIgnoreCase("NOV")){
            mon = "11";
        }else if (month.equalsIgnoreCase("DEC")){
            mon = "12";
        }

        String monthyear = mon +"/"+ year;
        mWebview = findViewById(R.id.web_view);
        mWebview.setWebViewClient(new WebViewClient());
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.loadUrl("http://chendur.shadowws.in/view_emp_pay.php?id="+id+"&month="+monthyear);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/PDFTest/");
                final String fileName="payslip"+System.currentTimeMillis()+".pdf";

                iosDialog = new IOSDialog.Builder(SlipActivity.this)
                        .setTitle("Please Wait...")
                        .setTitleColor(getResources().getColor(R.color.white))
                        .setSpinnerColorRes(R.color.dark_gray)
                        .setCancelable(true)
                        .setSpinnerClockwise(true)
                        .build();
                iosDialog.show();

                PdfView.createWebPrintJob(SlipActivity.this, mWebview, directory, fileName, new PdfView.Callback() {

                    @Override
                    public void success(String path) {
                        iosDialog.dismiss();
                        PdfView.openPdfFile(SlipActivity.this,getString(R.string.app_name),"Do you want to open the pdf file?"+fileName,path);
                    }

                    @Override
                    public void failure() {
                        iosDialog.dismiss();

                    }
                });

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

}
