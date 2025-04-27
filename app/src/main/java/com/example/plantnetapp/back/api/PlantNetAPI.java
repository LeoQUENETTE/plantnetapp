package com.example.plantnetapp.back.api;


import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlantNetAPI {
    private static PlantNetAPI INSTANCE = null;
    private final String URL_NAME = "https://my-api.plantnet.org/";
    private final String API_KEY = "2b10JCvsWMcjpFIjVGoD3HloO";
    private final int NB_IDENTIFICATION_MAX = 500;
    private PlantNetAPI() {
        try{
            ReturnType apiStatus = checkStatus();
            if (apiStatus.status != 200 || !apiStatus.values.get("status").toString().equals("ok")){
                INSTANCE = null;
            }
        }catch (IOException e){
            INSTANCE = null;
        }
    }
    public static PlantNetAPI createInstance() {
        if (INSTANCE == null){
            INSTANCE = new PlantNetAPI();
        }
        return INSTANCE;
    }
    public boolean isIdentificationPossible() throws IOException {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String todayDateGoodFormat = dateFormat.format(currentDate);
        ReturnType response = dailyQuota(todayDateGoodFormat);
        JsonObject count = response.values.get("count").getAsJsonObject();
        int nbIdentificationMade = count.get("identify").getAsInt();
        return nbIdentificationMade < NB_IDENTIFICATION_MAX;
    }
    public ReturnType createRequestPost(String urlStr, Map<String, Object> params) throws Exception {
        // Prepare the parameter string in "key=value&key2=value2" format
        StringBuilder postData = new StringBuilder();
        String boundary = "----Boundary" + System.currentTimeMillis();
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
                postData.append('=');
                postData.append(URLEncoder.encode(param.getValue().toString(), StandardCharsets.UTF_8));
            }
        }
        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
        // Create and configure the HTTP connection
        URL url = new URL(URL_NAME + urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true); // Allows sending body data
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

        // Send the request body
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postDataBytes);
        }

        // For demonstration: read the response code
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        return getRequestResult(conn,"POST");
    }
    private ReturnType createRequestGet(String urlServiceName) throws IOException {
        URL url = new URL(URL_NAME+urlServiceName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setRequestProperty("Content-Type", "application/json");
        ReturnType requestResult = getRequestResult(con, "GET");
        con.disconnect();
        return requestResult;
    }
    private ReturnType getRequestResult(HttpURLConnection con, String requestType) throws IOException {
        int status = con.getResponseCode();
        String type;
        String inputLine;
        BufferedReader in;
        StringBuilder response = new StringBuilder();
        JsonElement jsonElement;
        JsonObject jsonObject = new JsonObject();
        if (status == 200){
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }else{
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        jsonElement = JsonParser.parseString(response.toString());
        if (jsonElement.isJsonObject()){
            jsonObject = jsonElement.getAsJsonObject();
        }else if (jsonElement.isJsonArray()){
            jsonObject.add("data",jsonElement);
        }
        if (status == 200){
            type = requestType;
        }else{
            type = "Error : " + jsonObject.get("message").toString();
        }
        return new ReturnType(status, type, jsonObject);
    }

    public ReturnType checkStatus() throws IOException {
        return createRequestGet("v2/_status");
    }
    public ReturnType languages() throws IOException {
        return createRequestGet("v2/languages?api-key="+API_KEY);
    }
    public ReturnType projects(@Nullable String lang, @Nullable Float lat,@Nullable Float lon,@Nullable String type) throws Exception {
        String request = "v2/projects";
        int nbParameter = 0;
        if (lang != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "lang", lang, true);
        }
        if (lat != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "lat", lat.toString(), nbParameter == 1);
        }
        if (lon != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "lon", lon.toString(), nbParameter == 1);
        }
        if (type != null && (!type.equals("kt") && !type.equals("legacy"))){
            throw new Exception("ERROR : The type can only be kt legacy or null");
        }else if (type != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "type", type, nbParameter == 1);
        }
        nbParameter += 1;
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,nbParameter == 1);
        return createRequestGet(request);
    }
    public ReturnType species(@Nullable String lang, @Nullable String type, int pageSize, @Nullable String prefix) throws Exception {
        String request = "v2/species";
        int nbParameter = 0;
        if (lang != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "lang", lang, true);
        }
        if (type != null && (!type.equals("kt") && !type.equals("legacy"))){
            throw new Exception("ERROR : The type can only be kt legacy or null");
        }else if (type != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "type", type, nbParameter == 1);
        }
        if (pageSize != 0){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "pageSize", Integer.toString(pageSize), nbParameter == 1);
        }
        if (prefix != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "prefix", prefix, nbParameter == 1);
        }
        nbParameter += 1;
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,nbParameter == 1);
        return createRequestGet(request);
    }
    public ReturnType subscription()throws IOException{
        String request = "v2/subscription";
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,true);
        return createRequestGet(request);
    }
    public ReturnType identify(@Nullable String projectName,String[] imagesUrl)throws IOException{
        if (projectName == null || projectName.trim().isEmpty()){
            projectName = "all";
        }
        if (isIdentificationPossible()){
            throw new IOException("ERROR : No identification left");
        }
        String request = "v2/identify/"+projectName;
        return createRequestGet(request);
    }
    public ReturnType identify(File imageFile,
            @Nullable String projectName, @Nullable Boolean includeRelatedImage,
            @Nullable Boolean noReject, int nbResults,
            @Nullable String lang, @Nullable String type, @Nullable String organs) throws Exception
    {
        if (!isIdentificationPossible()){
            throw new IOException("ERROR : No identification left");
        }
        if (projectName == null || projectName.trim().isEmpty()){
            projectName = "all";
        }
        String request = "v2/identify/"+projectName;
        if (includeRelatedImage == null){
            includeRelatedImage =false;
        }
        request = ParameterStringBuilder.addGetParameter(request, "include-related-images", includeRelatedImage.toString(), true);
        if (noReject==null){
            noReject = false;
        }
        request = ParameterStringBuilder.addGetParameter(request, "no-reject", noReject.toString(), false);
        if (nbResults > 0){
            request = ParameterStringBuilder.addGetParameter(request, "nb-results", noReject.toString(), false);
        }
        if (lang != null && !lang.trim().isEmpty()){
            request = ParameterStringBuilder.addGetParameter(request, "lang", lang, false);
        }
        if (type != null && (!type.equals("kt") && !type.equals("legacy"))){
            throw new IOException("ERROR : The type can only be kt legacy or null");
        }else if (type != null){
            request = ParameterStringBuilder.addGetParameter(request, "type", type, false);
        }
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("images",imageFile);
        if(organs != null && !organs.trim().isEmpty()){
            parameters.put("organs",organs);
        }
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,false);
        return createRequestPost(request,parameters);
    }
    public ReturnType dailyQuota(@Nullable String day)throws IOException{
        String request = "v2/quota/daily";
        int nbParameter = 0;
        if (day != null){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "day",day,true);
        }
        nbParameter += 1;
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,nbParameter == 1);
        return createRequestGet(request);
    }
    public ReturnType historyQuota(int year)throws IOException{
        String request = "v2/quota/history";
        int nbParameter = 0;
        if (year != 0){
            nbParameter += 1;
            request = ParameterStringBuilder.addGetParameter(request, "year", String.valueOf(year),true);
        }
        nbParameter += 1;
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,nbParameter == 1);
        return createRequestGet(request);
    }
    public ReturnType speciesByProject(String projectName)throws IOException{
        String request = "v2/projects/"+projectName+"/species";
        if (projectName == null || projectName.trim().isEmpty()){
            throw new IOException("ERROR : No value passed for the project name");
        }
        return createRequestGet(request);
    }
}
