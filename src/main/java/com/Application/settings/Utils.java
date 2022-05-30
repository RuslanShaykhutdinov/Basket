package com.Application.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import io.gsonfire.GsonFireBuilder;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

public class Utils {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static final String STANDART_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static SimpleDateFormat sdf = new SimpleDateFormat(STANDART_DATE_FORMAT);

    private static Gson gson;

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

    public static ResponseEntity<String> createResponse(Object object) {
        return createResponse(object, HttpStatus.OK);
    }

    public static ResponseEntity<String> createResponse(Object object, HttpStatus status) {
        JsonParser parser = new JsonParser();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        if (object instanceof JsonElement) {
            return new ResponseEntity<>(gson().toJson(object), responseHeaders, status);
        }

        ObjectMapper mapper = new ObjectMapper();

        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            JsonElement innerRestErrorData = null;
            if (object instanceof RestError)
            {
                RestError re = (RestError)object;
                if (re.getData() != null && re.getData() instanceof JsonElement)
                {
                    innerRestErrorData = (JsonElement)re.getData();
                    re.setData( null );
                }
            }

            String jsonStr = mapper.writeValueAsString(object);
            if (innerRestErrorData != null)
            {
                JsonObject parsedObj = parser.parse( jsonStr ).getAsJsonObject();
                parsedObj.add("data", innerRestErrorData);
                jsonStr = parsedObj.toString();
            }

            return new ResponseEntity<>( jsonStr, responseHeaders, status);
        } catch (Exception e) {
        }

        //return new ResponseEntity<>(gson().toJson(object), responseHeaders, status);
        return null;
    }

    public static Gson gson() {
        if (gson != null) {
            return gson;
        }
        gson = new GsonFireBuilder().enableExposeMethodResult().createGsonBuilder()//.setPrettyPrinting()
                .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    if (src == src.longValue()) {
                        return new JsonPrimitive(src.longValue());
                    }
                    return new JsonPrimitive(src);
                }).setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return isFieldInSuperclass(f.getDeclaringClass(), f.getName());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }

                    private boolean isFieldInSuperclass(Class<?> subclass, String fieldName) {
                        Class<?> superclass = subclass.getSuperclass();
                        Field field;

                        while (superclass != null) {
                            field = getField(superclass, fieldName);

                            if (field != null) {
                                return true;
                            }

                            superclass = superclass.getSuperclass();
                        }

                        return false;
                    }

                    private Field getField(Class<?> theClass, String fieldName) {
                        try {
                            return theClass.getDeclaredField(fieldName);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                }).setDateFormat(STANDART_DATE_FORMAT).create();
        return gson;
    }
}
