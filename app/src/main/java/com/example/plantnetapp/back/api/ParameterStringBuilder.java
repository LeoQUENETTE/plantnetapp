package com.example.plantnetapp.back.api;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            }
            result.append("=");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
            result.append("&");
        }

        String resultString = result.toString();
        return !resultString.isEmpty()
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
    public static String addGetParameter(String request, String parameterName, String value,Boolean isFirst){
        if (isFirst){
            request += "?"+parameterName+"="+value;
        }else {
            request += "&"+parameterName+"="+value;
        }
        return request;
    }
}
