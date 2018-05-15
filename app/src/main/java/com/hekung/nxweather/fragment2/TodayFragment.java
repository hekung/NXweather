package com.hekung.nxweather.fragment2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hekung.nxweather.R;
import com.hekung.nxweather.activities.ShowWeatherActivity;
import com.hekung.nxweather.fragment.Fragment;
import com.hekung.nxweather.gson.ForeCast;
import com.hekung.nxweather.gson.Weather;
import com.hekung.nxweather.service.AutoUpdateService;
import com.hekung.nxweather.util.HandleWeatherUtility;
import com.hekung.nxweather.util.HttpRequest;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wenzh on 2018/5/12.
 */

public class TodayFragment extends Fragment {
    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;
    @OnClick(R.id.location_layout)
    void onChangeArea(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    protected int getContentLayoutId() {
        return R.layout.fragment_today;

    }


    @Override
    protected void initData() {
        super.initData();
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        String weatherString=preferences.getString("weather",null);
        if (weatherString!=null){
            //本地有天气JSON数据时直接解析
            Weather weather= HandleWeatherUtility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            //本地没有数据时从网络获取
           String weatherId=getActivity().getIntent().getStringExtra("weather_id");
          //  weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        //在activity中设置回调接口，下拉刷新时回调到该处
        ((ShowWeatherActivity)getActivity()).setOnRefreshCallBack(new ShowWeatherActivity.OnRefreshCallBack() {
            @Override
            public void onRefresh() {
                String weatherId=preferences.getString("weather_id",null);
                requestWeather(weatherId);
            }
        });
    }
    public void requestWeather(final String weatherId) {
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=ccbb9aeb74cc4a94baa6ddc673ec7a0d";
        HttpRequest.getAsyn(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(context,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                ((ShowWeatherActivity)getActivity()).swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=HandleWeatherUtility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ((weather!=null)&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
                            editor.putString("weather",responseText);
                            editor.putString("weather_id",weatherId);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(context, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        ((ShowWeatherActivity)getActivity()).swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }



        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime;
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (ForeCast foreCast:weather.foreCastList){
            View view= LayoutInflater.from(context).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(foreCast.date);
            infoText.setText(foreCast.more.info);
            maxText.setText(foreCast.temperature.max+"℃");
            minText.setText(foreCast.temperature.min+"℃");
            forecastLayout.addView(view);
        }
        if (weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        Intent intent=new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);
    }

}
