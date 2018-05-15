package com.hekung.nxweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hekung.nxweather.db.City;
import com.hekung.nxweather.db.County;
import com.hekung.nxweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by wenzh on 2018/5/9.
 */

public class HandleAreaUtility {
    static Gson gson=new Gson();
    //解析和处理服务器返回的各省数据
    public static boolean handleProvinceResponse(String response){

        if (!TextUtils.isEmpty(response)){
           try {
                JSONArray allProvinces=new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
        /**
          try {
              List<Province> provinceList=gson.fromJson(response,new TypeToken<List<Province>>(){}.getType());
              for (Province province:provinceList){
                  province.save();
              }
              return true;
          }catch (Exception e){
              e.printStackTrace();
          }

        return false;*/

    }
    //解析和处理服务器返回的各市的数据
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){

            try {
                JSONArray allCitys=new JSONArray(response);
                for (int i=0;i<allCitys.length();i++){
                    JSONObject cityObject=allCitys.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
            /**
            try {
                List<City> cityList=gson.fromJson(response,new TypeToken<List<City>>(){}.getType());
                for (City city:cityList){
                    city.setProvinceId(provinceId);
                   city.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return false;*/
    }
    //解析和处理服务器返回的各县的数据
    public static boolean handleCountyResponse(String response,int cityId){

        try {
            JSONArray allCounties=new JSONArray(response);
            for (int i=0;i<allCounties.length();i++){
                JSONObject countyObject=allCounties.getJSONObject(i);
                County county=new County();
                county.setCountyName(countyObject.getString("name"));
                county.setWeatherId(countyObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
        /**
        try {
            List<County> countyList=gson.fromJson(response,new TypeToken<List<County>>(){}.getType());
            for (County county:countyList){
                county.setCityId(cityId);
                county.save();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;*/

    }


}
