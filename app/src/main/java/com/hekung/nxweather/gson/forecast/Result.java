package com.hekung.nxweather.gson.forecast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wenzh on 2018/5/8.
 */

public class Result {
    @SerializedName("sk")
    public CurrentWeather currentWeather;


    @SerializedName("today")
    public TodayWeather todayWeather;

    @SerializedName("future")
    public List<FutureWeather> futureWeatherList;


}
