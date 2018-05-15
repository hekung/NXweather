package com.hekung.nxweather.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hekung.nxweather.R;
import com.hekung.nxweather.fragment.Fragment;
import com.hekung.nxweather.fragment2.FutureFragment;
import com.hekung.nxweather.fragment2.TodayFragment;
import com.hekung.nxweather.util.HttpRequest;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ShowWeatherActivity extends Activity implements RadioGroup.OnCheckedChangeListener{
    private TodayFragment todayFragment;
    private FutureFragment futureFragment;
    private int position;
    public static final String POSITION = "position";
    public static final int FRAGMENT_TODAY=0;
    public static final int FRAGMENT_FUTURE=1;
    @BindView(R.id.bottom_tabs)
    RadioGroup tabs;
    @BindView(R.id.btn_today)
    RadioButton btn_today;
    @BindView(R.id.bing_pic_img)
    ImageView bingImg;
    @BindView(R.id.swipe_refresh_layout)
   public SwipeRefreshLayout swipeRefreshLayout;

    private FragmentManager fragmentManager;

    private OnRefreshCallBack onRefreshCallBack;
    public interface OnRefreshCallBack{
        void onRefresh();
    }
    public void setOnRefreshCallBack(OnRefreshCallBack onRefreshCallBack){
        this.onRefreshCallBack=onRefreshCallBack;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_show_weather;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //屏幕旋转时记录位置
        outState.putInt(POSITION, position);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //屏幕恢复时取出位置
        showFragment(savedInstanceState.getInt(POSITION));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic=preferences.getString("bing_pic",null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingImg);
        }else{
            loadBingPic();
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.car_cl_choose);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshCallBack.onRefresh();
            }
        });


        fragmentManager=getSupportFragmentManager();
        tabs.setOnCheckedChangeListener(this);
        btn_today.setChecked(true);
    }
    private void loadBingPic(){
        String BingPicUrl="http://guolin.tech/api/bing_pic";
        HttpRequest.getAsyn(BingPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ShowWeatherActivity.this, "获取背景图失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(ShowWeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(ShowWeatherActivity.this).load(bingPic).into(bingImg);
                    }
                });
            }
        });

    }
    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.btn_today:
              //  changeFragment(new TodayFragment(),true);
                showFragment(FRAGMENT_TODAY);
                break;
            case R.id.btn_future:
                showFragment(FRAGMENT_FUTURE);
                //changeFragment(new FutureFragment(),true);
                break;
            default:
                break;
        }

    }
    /**
    public void changeFragment(Fragment fragment, boolean isInit){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.weather_content,fragment);
        if (!isInit){
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
     */
    public void showFragment(int index){

        FragmentTransaction ft=fragmentManager.beginTransaction();
        hideFragment(ft);

        //注意这里设置位置
        position = index;
        switch (index){
            case FRAGMENT_TODAY:
                if (todayFragment==null){
                    todayFragment=new TodayFragment();
                    ft.add(R.id.weather_content,todayFragment);
                }else {
                    ft.show(todayFragment);
                }
                break;

            case FRAGMENT_FUTURE:
                if (futureFragment==null){
                    futureFragment=new FutureFragment();
                    ft.add(R.id.weather_content,futureFragment);
                }else {
                    ft.show(futureFragment);
                }
                break;
        }
        ft.commit();
    }

    public void hideFragment(FragmentTransaction ft){
        //如果不为空，就先隐藏起来
        if (todayFragment!=null){
            ft.hide(todayFragment);
        }
        if(futureFragment!=null) {
            ft.hide(futureFragment);
        }
    }

}
