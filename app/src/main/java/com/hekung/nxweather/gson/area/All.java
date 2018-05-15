package com.hekung.nxweather.gson.area;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by wenzh on 2018/5/8.
 */

public class All extends DataSupport {
    @SerializedName("resultcode")
    private String resultcode;

    @SerializedName("reason")
    private String reason;

    @SerializedName("result")
    private List<Area> areaList;

    @SerializedName("error_code")
    private int error_code;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }


    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Area> getAreaList() {
        return areaList;
    }

    public void setResultList(List<Area> areaList) {
        this.areaList = areaList;
    }


}
