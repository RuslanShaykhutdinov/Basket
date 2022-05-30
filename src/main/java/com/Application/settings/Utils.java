package com.Application.settings;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;

public class Utils {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static String getSafeString(JsonObject jo, String name, String defVal) {
        try {
            return jo.get(name).getAsString();
        } catch (Exception e) {
            return defVal;
        }
    }
    public static Integer getSafeInt(JsonObject jo, String name, Integer defVal) {
        try {
            return jo.get(name).getAsInt();
        } catch (Exception e) {
            return defVal;
        }
    }

    public static Long getSafeLong(JsonObject jo, String name, Long defVal) {
        try {
            return jo.get(name).getAsLong();
        } catch (Exception e) {
            return defVal;
        }
    }
}
