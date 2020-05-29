package com.example.ajtaiwan_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.ajtaiwan_test.tools.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WeatherDataActivity extends AppCompatActivity {
    private static final String TAG = "WeatherDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_data);

        TextView tvWeatherStartTime = findViewById(R.id.tvWeatherStartTime);
        TextView tvWeatherEndTime = findViewById(R.id.tvWeatherEndTime);
        TextView tvWeatherParameter = findViewById(R.id.tvWeatherParameter);

        String strIn = getIntent().getExtras().getString("weather");
        JsonObject jsonObject = new Gson().fromJson(strIn, JsonObject.class);
        JsonObject parameter = jsonObject.getAsJsonObject("parameter");
        String temperature = parameter.get("parameterName").getAsString() + "°" +
                parameter.get("parameterUnit").getAsString();

        tvWeatherStartTime.setText(jsonObject.get("startTime").getAsString());
        tvWeatherEndTime.setText(jsonObject.get("endTime").getAsString());
        tvWeatherParameter.setText(temperature);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Util.showToast(this, R.string.welcome_back);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
