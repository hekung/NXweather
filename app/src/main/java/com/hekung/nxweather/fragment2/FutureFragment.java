package com.hekung.nxweather.fragment2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hekung.nxweather.R;
import com.hekung.nxweather.activities.MainActivity;
import com.hekung.nxweather.activities.ShowWeatherActivity;
import com.hekung.nxweather.bean.Bean;
import com.hekung.nxweather.fragment.Fragment;
import com.hekung.nxweather.gson.ForeCast;
import com.hekung.nxweather.gson.Weather;
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

public class FutureFragment extends Fragment {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //String editStartString;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.sport_text)
    TextView sportText;

    @BindView(R.id.edit_star)
    EditText editStar;
    @BindView(R.id.btn_query_starInfo)
    Button button;
    @BindView(R.id.starInfo_result_tv)
    TextView starInfoTv;
    @OnClick(R.id.btn_query_starInfo)
    void onQuery() {
        editStar.setCursorVisible(false);
        String data = "consName=" + editStar.getText() + "&type=today" + "&key=cf348a356e5e9c20140026b8eee3190b";
        String request = "http://web.juhe.cn:8080/constellation/getAll?" + data;
        HttpRequest.getAsyn(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "请求星座数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Gson gson = new Gson();
                Bean bean = new Bean();
                bean = gson.fromJson(responseText, Bean.class);
                final String receiverSummary = bean.getSummary();
               // editor.putString(editStar.getText().toString(),receiverSummary);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        starInfoTv.setText(receiverSummary);
                    }
                });
            }
        });
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_future;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();
        editStar.setText(preferences.getString("edit",""));
        starInfoTv.setText(preferences.getString("starInfo",""));
       // editStartString=editStar.getText().toString();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editor.putString("edit",editStar.getText().toString());
        editor.putString("starInfo",starInfoTv.getText().toString());
    }

    @Override
    protected void initData() {
        super.initData();

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

    private void requestWeather(final String weatherId) {
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
                          //  SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
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
        String comfort="舒适度："+weather.suggestion.comfort.info;
        String carWash="洗车指数:"+weather.suggestion.carWash.info;
        String sport="运动指数:"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        //weatherLayout.setVisibility(View.VISIBLE);

    }

}
