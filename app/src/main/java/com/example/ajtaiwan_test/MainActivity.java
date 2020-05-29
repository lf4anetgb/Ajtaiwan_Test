package com.example.ajtaiwan_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ajtaiwan_test.tools.CommunicationTask;
import com.example.ajtaiwan_test.tools.Util;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private RecyclerView weatherRecyclerView;

    private CommunicationTask communicationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.isOpened(this);

        weatherRecyclerView = findViewById(R.id.weatherRecyclerView);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Util.networkConnected(this)) {
            Util.showToast(this, R.string.not_connected);
            return;
        }

        //設定連線參數
        StringBuffer url = new StringBuffer(Util.URL_CWB_AIP)
                .append("F-C0032-001")//功能
                .append("?Authorization=").append("CWB-93B3E51D-0A72-4F91-A371-5B841EDA4CCC")//key
                .append("&").append("format=JSON")//回傳格式
                .append("&").append("locationName=%E8%87%BA%E5%8C%97%E5%B8%82")//地區
                .append("&").append("elementName=MinT");//天氣因子
        communicationTask = new CommunicationTask(url.toString());
        JsonObject jsonObject = null;

        try {
            String jsonIn = communicationTask.execute().get();
            Log.d(TAG, "jsonIn: " + jsonIn);
            jsonObject = new Gson().fromJson(jsonIn, JsonObject.class);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (jsonObject == null) {
            Util.showToast(this, R.string.no_data);
            return;
        }

        //抓取所需要的值
        JsonArray jsonArray = jsonObject.getAsJsonObject("records").getAsJsonArray("location")
                .get(0).getAsJsonObject().getAsJsonArray("weatherElement")
                .get(0).getAsJsonObject().getAsJsonArray("time");

        weatherRecyclerView.setAdapter(new RecyclerViewAdapter(jsonArray));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //重生時判斷是因為按Home鍵而重生，或是從其他頁面回來的
        //如其他頁面回來的就不顯示
        if (Util.getShowMessage()) {
            Util.showToast(this, R.string.welcome_back);
            return;
        }
        Util.setShowMessage(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (communicationTask != null) {
            communicationTask.cancel(true);
            communicationTask = null;
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter {
        private JsonArray data;

        RecyclerViewAdapter(JsonArray data) {
            this.data = data;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;//用於切分成Layout
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0: {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_list_data, parent, false);
                    return new ViewHolder_listData(view);
                }

                case 1: {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_list_image, parent, false);
                    return new ViewHolder_listImage(view);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (position % 2 == 0) {
                final JsonObject jsonObject = data.get(position / 2).getAsJsonObject();

                JsonObject parameter = jsonObject.getAsJsonObject("parameter");
                String temperature = parameter.get("parameterName").getAsString() + "°" +
                        parameter.get("parameterUnit").getAsString();

                ViewHolder_listData viewHolder_listData = (ViewHolder_listData) holder;
                viewHolder_listData.tvItemStartTime.setText(jsonObject.get("startTime").getAsString());
                viewHolder_listData.tvItemEndTime.setText(jsonObject.get("endTime").getAsString());
                viewHolder_listData.tvItemParameter.setText(temperature);

                viewHolder_listData.cvWeather.setOnClickListener(v -> {
                    Util.setShowMessage(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("weather", jsonObject.toString());
                    Intent intent = new Intent(MainActivity.this, WeatherDataActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size() * 2;
        }

        private class ViewHolder_listData extends RecyclerView.ViewHolder {
            private CardView cvWeather;
            private TextView tvItemStartTime, tvItemEndTime, tvItemParameter;

            ViewHolder_listData(@NonNull View itemView) {
                super(itemView);
                cvWeather = itemView.findViewById(R.id.cvWeather);
                tvItemStartTime = itemView.findViewById(R.id.tvItemStartTime);
                tvItemEndTime = itemView.findViewById(R.id.tvItemEndTime);
                tvItemParameter = itemView.findViewById(R.id.tvItemParameter);
            }
        }

        private class ViewHolder_listImage extends RecyclerView.ViewHolder {
            ViewHolder_listImage(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}