package com.hekung.nxweather.util.util2;

import com.google.gson.Gson;
import com.hekung.nxweather.gson.forecast.ALL;
import com.hekung.nxweather.gson.forecast.FutureWeather;
import com.hekung.nxweather.gson.weathertype.All;
import com.hekung.nxweather.gson.weathertype.WeatherType;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wenzh on 2018/5/12.
 */

public class WeatherUtility {
    static Gson gson=new Gson();
    public static void handleWeatherType(String response){
        All all=gson.fromJson(response,All.class);
        List<WeatherType> weatherTypeList=all.weatherTypeList;
        for (WeatherType weatherType:weatherTypeList){
            weatherType.save();
        }
    }
    //处理天气数据
    public static ALL handleAll(String response){
        ALL all=gson.fromJson(response,ALL.class);

        return all;
    }
}
