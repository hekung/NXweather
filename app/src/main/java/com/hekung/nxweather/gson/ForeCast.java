package com.hekung.nxweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wenzh on 2018/4/4.
 */

public class ForeCast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature{
        public String max;
        public String min;
    }
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
