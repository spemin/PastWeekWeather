package com.pastweather.pastweather;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class DailyWeather implements JsonDeserializer<DailyWeather.Daily> {
    double latitude;
    double longitude;
    String timezone;
    int offset;
    Daily daily;

    @Override
    public Daily deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return null;
    }

    public static class Daily {
        DayWeather[] data;
    }

    public static class DayWeather {
        long time;
        String summary; //  "partly cloudy starting in the evening"
        String icon; // partly-cloudy-night, corresponds to image type
        double precipIntensity;
        double temperatureHigh;    //: 86.4
        double temperatureLow; //:63.77
        double humidity;    //: 0.22
        double windSpeed;   //: 0.28
        long sunsetTime; //:
        long sunriseTime;    //:

        public boolean parseString(String jsonData) {
            return true;
        }
    }

    public static DailyWeather getObjectFromString(String jonsIn) {
        DailyWeather dailyWeather = new Gson().fromJson(jonsIn, DailyWeather.class);
        return dailyWeather;
    }
}
