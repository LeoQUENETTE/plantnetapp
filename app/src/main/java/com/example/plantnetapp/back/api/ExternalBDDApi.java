package com.example.plantnetapp.back.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.example.plantnetapp.back.entity.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ExternalBDDApi {
    private static ExternalBDDApi INSTANCE = null;
    private final String URL_NAME = "https://simpleapi-1u9p.onrender.com/api/";
    private ExternalBDDApi() {
        try{
            ReturnType apiStatus = checkStatus();
            if (apiStatus.status != 200){
                INSTANCE = null;
            }
        }catch (IOException e){
            INSTANCE = null;
        }
    }

    public static ExternalBDDApi createInstance() {
        if (INSTANCE == null){
            INSTANCE = new ExternalBDDApi();
        }
        return INSTANCE;
    }
    public ReturnType checkStatus() throws IOException{
        return sendHeadRequest("health");
    }

    public ReturnType sendHeadRequest(String endpoint) throws  IOException{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            return new ReturnType(status, contentType, null);
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
    public ReturnType sendPostRequest(String endpoint, JsonObject body) throws  IOException{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            JsonObject responseJson = null;
            //Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
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
            }
            return new ReturnType(status, contentType, responseJson != null ? responseJson : new JsonObject());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
    public ReturnType sendDeleteRequest(String endpoint, JsonObject body) throws  IOException{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            JsonObject responseJson = null;
            //Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
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
            }
            return new ReturnType(status, contentType, responseJson != null ? responseJson : new JsonObject());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
    public ReturnType sendGetRequest(String endpoint) throws  IOException{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(URL_NAME + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            int status = connection.getResponseCode();
            String contentType = connection.getContentType();

            JsonObject responseJson = null;
            //Read response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
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
            }
            return new ReturnType(status, contentType, responseJson != null ? responseJson : new JsonObject());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    public ReturnType login(String username, String pswrd){

        try {
            JsonObject body = new JsonObject();
            body.addProperty("username", username);
            body.addProperty("pswrd", pswrd);
            ReturnType response = sendPostRequest("user/get",body);
            if (response.status != 200){
                return null;
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            return null;
        }
    }

    public ReturnType addUser(User user){
        try {
            JsonObject body = new JsonObject();
            body.addProperty("username", user.login);
            body.addProperty("pswrd", user.mdp);
            body.addProperty("email",user.mail);
            body.addProperty("firstname",user.firstName);
            body.addProperty("lastname",user.lastName);
            body.addProperty("phone",user.phone);
            ReturnType response = sendPostRequest("user/add",body);
            if (response.status != 201){
                return null;
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            return null;
        }
    }

    public ReturnType deleteUser(String id){
        try {
                JsonObject body = new JsonObject();
            body.addProperty("id", id);
            ReturnType response = sendDeleteRequest("user/delete",body);
            if (response.status != 200){
                return null;
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            return null;
        }
    }

    public ReturnType getPlantCollection(String userID, String collectionName){
        try {
            JsonObject body = new JsonObject();
            body.addProperty("userID", userID);
            body.addProperty("name", collectionName);
            ReturnType response = sendPostRequest("user/getPlantCollection",body);
            if (response.status != 200){
                return null;
            }
            System.out.println(response);
            return response;
        }catch (IOException e){
            return null;
        }
    }
}
