package com.example.plantnetapp.back.api;


import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
public class PlantNetAPI {
    private static PlantNetAPI INSTANCE = null;
    private final String URL_NAME = "https://my-api.plantnet.org/";
    private final String API_KEY = "2b10JCvsWMcjpFIjVGoD3HloO";
    private PlantNetAPI() {
        try{
            checkStatus();
        }catch (IOException e){
            INSTANCE = null;
        }
    }
    public static PlantNetAPI createInstance() throws IOException {
        if (INSTANCE == null){
            INSTANCE = new PlantNetAPI();
        }
        return INSTANCE;
    }
    private ReturnType createRequest(Map<String, String> parameters, String urlServiceName) throws IOException {
        URL url = new URL(URL_NAME+urlServiceName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();
        ReturnType requestResult = getRequestResult(con, "POST");
        con.disconnect();
        return requestResult;
    }
    private ReturnType createRequest(String urlServiceName) throws IOException {
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
        StringBuilder response = new StringBuilder();
        JsonElement jsonElement = null;
        JsonObject jsonObject = new JsonObject();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

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
        return createRequest("v2/_status");
    }
    public ReturnType languages() throws IOException {
        return createRequest("v2/languages?api-key="+API_KEY);
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
        return createRequest(request);
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
        return createRequest(request);
    }
    public ReturnType subscription()throws IOException{
        String request = "v2/subscription";
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,true);
        return createRequest(request);
    }
    public ReturnType identify(String projectName,String[] imagesUrl)throws IOException{
        String request = "v2/identify/"+projectName;
        if (projectName == null || projectName.trim().isEmpty()){
            throw new IOException("ERROR : No value passed for the project name");
        }
        return createRequest(request);
    }
    public ReturnType identify(String projectName)throws IOException{
        String request = "v2/identify/"+projectName;
        if (projectName == null || projectName.trim().isEmpty()){
            throw new IOException("ERROR : No value passed for the project name");
        }
        return createRequest(request);
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
        return createRequest(request);
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
        return createRequest(request);
    }
    public ReturnType speciesByProject(String projectName)throws IOException{
        String request = "v2/projects/"+projectName+"/species";
        if (projectName == null || projectName.trim().isEmpty()){
            throw new IOException("ERROR : No value passed for the project name");
        }
        return createRequest(request);
    }
}
