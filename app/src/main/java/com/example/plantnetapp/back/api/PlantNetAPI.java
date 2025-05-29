package com.example.plantnetapp.back.api;


import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
    public ReturnType sendPostRequest(String endpoint, JsonObject body, @Nullable File file) throws  IOException{
        HttpURLConnection connection = null;
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "*/*");
            if (file == null){
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
            }else{
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            }

            connection.setDoOutput(true);

            if (file == null){
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            else{
                linkImageToRequest(connection,boundary, body,file, "image");
            }


            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            JsonObject responseJson = null;
            InputStream responseStream = connection.getResponseCode() >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();
            //Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (!response.toString().isEmpty()) {
                    responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                } else {
                    responseJson = new JsonObject();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            return new ReturnType(status, contentType, responseJson != null ? responseJson : new JsonObject());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public void linkImageToRequest(HttpURLConnection connection, String boundary, JsonObject body, File file, String fileFieldName) throws IOException {
        try (OutputStream outputStream = connection.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(outputStream);
             FileInputStream fileInputStream = new FileInputStream(file)) {

            // Écriture de l'en-tête du fichier
            String fileHeader = "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"images\"; filename=\"" + file.getName() + "\"\r\n" +
                    "Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\r\n\r\n";
            bos.write(fileHeader.getBytes(StandardCharsets.UTF_8));

            // Écriture du contenu du fichier
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.write("\r\n".getBytes(StandardCharsets.UTF_8));

            // Écriture de la fin
            String end = "--" + boundary + "--\r\n";
            bos.write(end.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        }
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
        if(organs != null && !organs.trim().isEmpty()){
            parameters.put("organs",organs);
        }
        request = ParameterStringBuilder.addGetParameter(request, "api-key",API_KEY,false);
        JsonObject body = new JsonObject();
        return sendPostRequest(request,body, imageFile);
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
