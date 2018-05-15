package com.hekung.nxweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hekung.nxweather.R;
import com.hekung.nxweather.activities.MainActivity;
import com.hekung.nxweather.activities.ShowWeatherActivity;
import com.hekung.nxweather.db.City;
import com.hekung.nxweather.db.County;
import com.hekung.nxweather.db.Province;
import com.hekung.nxweather.fragment2.TodayFragment;
import com.hekung.nxweather.util.HandleAreaUtility;
import com.hekung.nxweather.util.HttpRequest;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class ChooseAreaFragment extends Fragment {
    private int currentLevel;
    public static  final int LEVEL_PROVINCE=0;
    public static  final int LEVEL_CITY=1;
    public static  final int LEVEL_COUNTY=2;

   // private ArrayAdapter<String> adapter;
    private MyAdapter adapter;
    private List<String> dataList=new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;

    private ProgressDialog progressDialog;

    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.back_button)
    Button backButton;
    //@BindView(R.id.listview)
    //ListView listView;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Override
    protected int getContentLayoutId() {
        return R.layout.choose_area;
    }

    @Override
    protected void initWidget(View rootView) {
        super.initWidget(rootView);
       // adapter=new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item,dataList);
     //   listView.setAdapter(adapter);

       LinearLayoutManager manager=new LinearLayoutManager(context);
       manager.setOrientation(LinearLayoutManager.VERTICAL);
       recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        super.initData();

       adapter=new MyAdapter(dataList);
       recyclerView.setAdapter(adapter);
        /**
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        */

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        Intent intent=new Intent(getActivity(),ShowWeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof ShowWeatherActivity){
                        TodayFragment todayFragment=(TodayFragment)getParentFragment();
                        todayFragment.drawerLayout.closeDrawers();
                        ((ShowWeatherActivity) getActivity()).swipeRefreshLayout.setRefreshing(true);
                        todayFragment.requestWeather(weatherId);

                    }

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());

            }
            adapter.notifyDataSetChanged();

            //listView.setSelection(0);

            currentLevel=LEVEL_PROVINCE;
        }else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
        //    listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int  provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }

    }
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
       //     listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    private void queryFromServer(String address,final String type) {
        //显示进度对话框
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
        HttpRequest.getAsyn(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog!=null){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                boolean result=false;
                if ("province".equals(type)){
                    result=HandleAreaUtility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result=HandleAreaUtility.handleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=HandleAreaUtility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog!=null){
                                progressDialog.dismiss();
                            }
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });

                }
            }
        });

    }
}
