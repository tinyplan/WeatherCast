package com.xh189050934.weathercast.utils;

import android.net.Uri;
import android.util.Log;

import com.xh189050934.weathercast.bean.City;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CityUtils {
    private static final String TAG = "CityUtils";
    private static final String CITY_URL
            = "http://api.k780.com/?app=weather.city&cou=1&appkey=47455&sign=d928b1a08b38ed2984b8f98659c1c57f&format=json";

    /**
     * 获取所有城市
     */
    public List<City> getCityList() {
        List<City> items = new ArrayList<>();

        try {
            String url = Uri.parse(CITY_URL).buildUpon().build().toString();
            String jsonString = ApiUtils.getURLString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseCity(items, jsonBody);
            System.out.println(items);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        return items;
    }

    private void parseCity(List<City> items, JSONObject jsonBody) throws JSONException {
        JSONObject data = jsonBody.getJSONObject("result").getJSONObject("datas");
        for (int i = 0,j = 0; j < 100; i++,j++) {
            JSONObject cityObject = null;
            try {
                cityObject = (JSONObject) data.get(String.valueOf(i+1));
            } catch (JSONException e) {
                j--;
                continue;
            }
            City city = new City();
            city.setId(cityObject.getString("cityid"));
            city.setName(cityObject.getString("citynm"));
            items.add(city);
        }
    }
}
