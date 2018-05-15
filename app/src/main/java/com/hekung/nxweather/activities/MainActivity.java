package com.hekung.nxweather.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.airsaid.pickerviewlibrary.CityPickerView;
import com.airsaid.pickerviewlibrary.listener.OnSimpleCitySelectListener;
import com.hekung.nxweather.R;
import com.hekung.nxweather.db.County;
import com.hekung.nxweather.gson.Weather;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weather=preferences.getString("weather",null);
        if (weather!=null){
            startActivity(new Intent(this, ShowWeatherActivity.class));
            finish();
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

    }

    @Override
    protected void initData() {
        super.initData();
    }
}
