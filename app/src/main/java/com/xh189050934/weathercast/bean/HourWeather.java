package com.xh189050934.weathercast.bean;

import com.xh189050934.weathercast.utils.DateFormattor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class HourWeather implements Weather {

    private String tmp;
    private String cond_code;
    private String cond_txt;
    private String time;

    @Override
    public void parseJSON(JSONObject json) throws JSONException{
        this.setTmp(json.getString("tmp"));
        this.setCond_code(json.getString("cond_code"));
        this.setCond_txt(json.getString("cond_txt"));
        try {
            String time = DateFormattor.parseToHour(json.getString("time"));
            this.setTime(time);
        } catch (ParseException e) {
            System.out.println("format error!!!");
        }
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getCond_code() {
        return cond_code;
    }

    public void setCond_code(String cond_code) {
        this.cond_code = cond_code;
    }

    public String getCond_txt() {
        return cond_txt;
    }

    public void setCond_txt(String cond_txt) {
        this.cond_txt = cond_txt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
