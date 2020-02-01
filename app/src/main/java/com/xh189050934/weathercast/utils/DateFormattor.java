package com.xh189050934.weathercast.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DateFormattor {
    private static final String format = "HH:mm";
    private static final String format1 = "yyyy-MM-dd hh:mm";
    private static HashMap<String,String> typeMap;

    public DateFormattor() {
        typeMap = new HashMap<>();
        typeMap.put("comf","舒适指数");
        typeMap.put("drsg","穿衣指数");
        typeMap.put("flu","感冒指数");
        typeMap.put("sport","运动指数");
        typeMap.put("trav","旅游指数");
        typeMap.put("uv","紫外线指数");
        typeMap.put("cw","洗车指数");
        typeMap.put("air","空气指数");
    }

    private static Long parseToStamp(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format1);
        return sdf.parse(dateString).getTime();
    }

    public static String parseToHour(String dateString) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Long stamp = parseToStamp(dateString);
        return sdf.format(new Date(stamp));
    }

    public String swtichType(String type){
        return typeMap.get(type);
    }
}
