package com.xh189050934.weathercast.bean;

import java.io.Serializable;
import java.util.List;

public class CityListVO implements Serializable {
    private List<City> mCityList;

    public CityListVO(List<City> cityList) {
        mCityList = cityList;
    }

    public List<City> getCityList() {
        return mCityList;
    }

    public void setCityList(List<City> cityList) {
        mCityList = cityList;
    }
}
