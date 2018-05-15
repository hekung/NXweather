package com.hekung.nxweather.gson.weathertype;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wenzh on 2018/5/8.
 */

public class All {
    @SerializedName("resultcode")
    public  String resultcode;

    @SerializedName("reason")
    public String reason;

    @SerializedName("result")
   public List<WeatherType> weatherTypeList;

    @SerializedName("error_code")
    public int error_code;




}
