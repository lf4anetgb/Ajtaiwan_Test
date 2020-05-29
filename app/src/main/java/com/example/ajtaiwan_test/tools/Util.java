package com.example.ajtaiwan_test.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.ajtaiwan_test.R;

public class Util {

    //連線對象
    public final static String URL_CWB_AIP = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/";
    private final static String SHARED_PREFERENCES_GUIDE_OPENED = "opened";//用於判斷是否有使用過
    private static boolean SHOW_MESSAGE = true;//用於顯示歡迎回來

    public static boolean getShowMessage() {
        return SHOW_MESSAGE;
    }

    public static void setShowMessage(boolean isClosed) {
        Util.SHOW_MESSAGE = isClosed;
    }

    //測試連線用
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ((connectivityManager != null) ? (connectivityManager.getActiveNetworkInfo()) : null);
        return ((networkInfo != null) && (networkInfo.isConnected()));
    }

    //吐司
    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    //判斷是否有使用過
    public static void isOpened(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES_GUIDE_OPENED, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("opened", false)) {
            showToast(activity.getApplicationContext(), R.string.welcome_back);
            return;
        }
        sharedPreferences.edit().putBoolean("opened", true).apply();
    }
}
