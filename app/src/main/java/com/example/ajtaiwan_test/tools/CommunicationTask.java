package com.example.ajtaiwan_test.tools;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CommunicationTask extends AsyncTask<String, Integer, String> {
    private final static String TAG = "CommunicationTask";
    private String url;

    public CommunicationTask(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {
        return getRemoteData();
    }

    private String getRemoteData() {
        StringBuilder inStr = new StringBuilder();

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            //此隱含式寫法可自動關閉con
            try (AutoCloseable conc = () -> con.disconnect()) {
                //允許資料進出
                con.setDoInput(true);
                con.setDoOutput(false);

                //設定HTTP參數
                con.setChunkedStreamingMode(0);
                con.setUseCaches(false);
                con.setRequestMethod("GET");
                con.setRequestProperty("charset", "UTF-8");

                //讀取回傳，如=200時將資料讀取近來使用
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            inStr.append(line);
                        }
                    }
                } else {
                    Log.d(TAG, "回傳CODE: " + responseCode);
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "格式錯誤：" + e.toString());
        } catch (IOException e) {
            Log.d(TAG, "IO錯誤：" + e.toString());
        } catch (Exception e) {
            Log.d(TAG, "倒大楣啦！錯誤：" + e.toString());
        }

        return inStr.toString();
    }
}
