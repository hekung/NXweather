package com.hekung.nxweather.gson;

/**
 * Created by wenzh on 2018/4/4.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
