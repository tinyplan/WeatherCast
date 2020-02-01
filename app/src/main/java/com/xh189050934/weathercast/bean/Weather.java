package com.xh189050934.weathercast.bean;

import org.json.JSONException;
import org.json.JSONObject;

public interface Weather {
    void parseJSON(JSONObject json) throws JSONException;
}
