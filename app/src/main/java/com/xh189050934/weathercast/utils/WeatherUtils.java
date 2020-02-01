package com.xh189050934.weathercast.utils;

import android.net.Uri;
import android.util.Log;

import com.xh189050934.weathercast.bean.DailyWeather;
import com.xh189050934.weathercast.bean.HourWeather;
import com.xh189050934.weathercast.bean.Life;
import com.xh189050934.weathercast.bean.NowWeather;
import com.xh189050934.weathercast.bean.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherUtils {
    private static final String TAG = "WeatherUtils";
    private static final String WEATHER_URL = "http://v1.alapi.cn/api/weather";

    //查询类型
    public static final String NOW = "/now";
    public static final String FORECAST = "/forecast";
    public static final String HOURLY = "/hourly";
    public static final String LIFE = "/life";

    /**
     * 获取天气
     *
     * @param cityName 城市名称
     * @param type     查询类型
     */
    public List<Weather> getWeather(String cityName, String type) {
        List<Weather> items = new ArrayList<>();

        try {
            String url = Uri.parse(WEATHER_URL + type)
                    .buildUpon()
                    .appendQueryParameter("location", cityName)
                    .build().toString();
            String jsonString = ApiUtils.getURLString(url);
            /*Log.i(TAG, "Received JSON: " + jsonString);*/
            JSONObject jsonBody = new JSONObject(jsonString);
            switch (type) {
                case NOW:
                    parseNowWeather(items, jsonBody);
                    Log.i(TAG, "Get Nowadays Success!!!");
                    break;
                case FORECAST:
                    parseDailyWeather(items, jsonBody);
                    Log.i(TAG, "Get DailyWeather Success!!!");
                    break;
                case HOURLY:
                    parseHourlyWeather(items, jsonBody);
                    break;
                case LIFE:
                    parseLifeStyle(items, jsonBody);
                    break;
                default:
                    throw new IllegalArgumentException("type参数类型不匹配!!!");
            }
            System.out.println(items);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        return items;
    }

    /**
     * 解析实况天气
     *
     * @param items    天气列表
     * @param jsonBody json数据(整体)
     *                 此方法解析后List中只有1条当天的天气数据
     */
    private void parseNowWeather(List<Weather> items, JSONObject jsonBody) throws JSONException {
        JSONObject responseData = jsonBody.getJSONObject("data");
        JSONObject now = responseData.getJSONObject("now");
        NowWeather weather = new NowWeather();
        weather.parseJSON(now);
        items.add(weather);
    }

    /**
     * 解析接下来7天的天气预报
     */
    private void parseDailyWeather(List<Weather> items, JSONObject jsonBody) throws JSONException {
        JSONObject responseData = jsonBody.getJSONObject("data");
        JSONArray weathers = responseData.getJSONArray("daily_forecast");
        DailyWeather daily;
        for (int i = 0; i < weathers.length(); i++) {
            //获取其中一天的天气对象
            JSONObject onDay = weathers.getJSONObject(i);
            daily = new DailyWeather();
            daily.parseJSON(onDay);
            items.add(daily);
        }
    }

    /**
     * 解析逐小时预报的天气
     */
    private void parseHourlyWeather(List<Weather> items, JSONObject jsonBody) throws JSONException {
        JSONArray hourlyList = jsonBody.getJSONObject("data").getJSONArray("hourly");
        HourWeather weather;
        for (int i = 0; i < hourlyList.length(); i++) {
            JSONObject hour = hourlyList.getJSONObject(i);
            weather = new HourWeather();
            weather.parseJSON(hour);
            items.add(weather);
        }
    }

    /**
     * 解析生活指数
     */
    private void parseLifeStyle(List<Weather> items, JSONObject jsonBody) throws JSONException {
        JSONArray lifeList = jsonBody.getJSONObject("data").getJSONArray("lifestyle");
        Life life;
        for (int i = 0; i < lifeList.length(); i++) {
            JSONObject lifeObj = lifeList.getJSONObject(i);
            life = new Life();
            life.parseJSON(lifeObj);
            items.add(life);
        }
    }

}
