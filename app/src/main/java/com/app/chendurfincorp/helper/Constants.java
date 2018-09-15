package com.app.chendurfincorp.helper;

import android.content.SharedPreferences;

public class Constants {

    //sharedPreference
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    //baseUrl
    public static String BASE_URL = "http://chendur.shadowws.in/jsons/";

    //keywords
    public static String id="id";
    public static String phone="phone";
    public static String name="name";
    public static String password="password";
    public static String category="category";

    //pageUrl
    public static String LOGIN= "login.php?";
    public static String LEAVE= "leave.php?";
    public static String QUERIES= "query.php?";
    public static String UPDATE= "update.php?";
    public static String ATTENDENCE= "attendence.php?";
    public static String LEAVESTATUS= "leavestatus.php?";
    public static String PRESENT= "presentdays.php?";
    public static String ABSENT= "absentdays.php?";
}
