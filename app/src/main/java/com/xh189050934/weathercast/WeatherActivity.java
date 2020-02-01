package com.xh189050934.weathercast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xh189050934.weathercast.bean.City;

import java.util.List;

public class WeatherActivity extends SingleFragmentActivity implements WeatherFragment.CallBacks {
    public static final String EXTRA_CITY = "com.xh189050934.weathercast.WeatherActivity.city";

    private City mCity;

    @Override
    protected Fragment createFragment() {
        return WeatherFragment.newInstance();
    }

    public static Intent newIntent(Context context, City city) {
        Intent intent = new Intent(context, WeatherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CITY, city);
        intent.putExtra("data", bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBundleExtra("data") != null) {
            mCity = (City) getIntent().getBundleExtra("data").get(EXTRA_CITY);
        } else {
            CityLab cityLab = CityLab.get(this);
            List<City> cityList = cityLab.getCities();
            //默认为杭州
            if (cityList.isEmpty()) {
                mCity = new City("101210101", "杭州");
            } else {
                //表首城市
                mCity = cityList.get(0);
            }
        }
        System.out.println(mCity);
    }

    @Override
    public String getCityName() {
        return mCity.getName();
    }
}
