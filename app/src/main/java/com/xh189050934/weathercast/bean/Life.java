package com.xh189050934.weathercast.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class Life implements Weather{
    private String type;
    private String brf;
    private String txt;

    @Override
    public void parseJSON(JSONObject json) throws JSONException {
        this.setType(json.getString("type"));
        this.setBrf(json.getString("brf"));
        this.setTxt(json.getString("txt"));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrf() {
        return brf;
    }

    public void setBrf(String brf) {
        this.brf = brf;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    @Override
    public String toString() {
        return "Life{" +
                "type='" + type + '\'' +
                ", brf='" + brf + '\'' +
                ", txt='" + txt + '\'' +
                '}';
    }
}
